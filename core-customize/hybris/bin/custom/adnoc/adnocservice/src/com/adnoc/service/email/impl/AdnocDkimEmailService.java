package com.adnoc.service.email.impl;


import com.adnoc.service.email.util.AdnocSMTPDKIMMessage;
import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdnocDkimEmailService extends DefaultEmailService
{
    private static final Logger LOG = LogManager.getLogger(AdnocDkimEmailService.class);

    private AdnocDkimSigner adnocDkimSigner;

    @Override
    public boolean send(final EmailMessageModel emailMessageModel)
    {
        if (Objects.isNull(emailMessageModel))
        {
            throw new IllegalArgumentException("emailMessageModel must not be null");
        }

        final boolean sendEnabled = getConfigurationService().getConfiguration().getBoolean(DefaultEmailService.EMAILSERVICE_SEND_ENABLED_CONFIG_KEY, true);
        if (!sendEnabled)
        {
            LOG.warn("appEvent=AdnocSendEmail,Could not send e-mail pk [{}] subject [{}]", emailMessageModel.getPk(), emailMessageModel.getSubject());
            LOG.info("appEvent=AdnocSendEmail, Email sending has been disabled. Check the config property 'emailservice.send.enabled'");
            return true;
        }
        try
        {
            final HtmlEmail email = getPerConfiguredEmail();
            email.setCharset(StandardCharsets.UTF_8.name());
            final List<EmailAddressModel> toAddressesList = emailMessageModel.getToAddresses();
            final EmailAddressModel fromEmailAddress = emailMessageModel.getFromAddress();
            email.setFrom(fromEmailAddress.getEmailAddress(), nullifyEmpty(fromEmailAddress.getDisplayName()));
            setAddresses(emailMessageModel, email, toAddressesList);
            addReplyTo(emailMessageModel, email);
            email.setSubject(emailMessageModel.getSubject());
            email.setHtmlMsg(getBody(emailMessageModel));
            final List<EmailAttachmentModel> attachments = emailMessageModel.getAttachments();
            if (CollectionUtils.isNotEmpty(attachments) && !processAttachmentsSuccessful(email, attachments))
            {
                LOG.info("appEvent=AdnocSendEmail, Attachment succesfully created attachment={}", attachments);
                return false;
            }
            // setting logs
            final String fromAddress = email.getFromAddress().getAddress();
            final String toAddresses = email.getToAddresses().stream().map(InternetAddress::getAddress).collect(Collectors.joining(","));
            final String replyToAddresses = email.getReplyToAddresses().stream().map(InternetAddress::getAddress).collect(Collectors.joining(","));
            final String subject = email.getSubject();
            LOG.info("appEvent=AdnocSendEmail, Email prepared with info From={}, To={}, ReplyTo={}, Subject={}.",
                    fromAddress, toAddresses, replyToAddresses, subject);

            // Prepare and convert HtmlEmail to MimeMessage
            email.buildMimeMessage();
            final MimeMessage unsignedMimeMessage = email.getMimeMessage();
            final MimeMessage dkimSignedMessage = signEmailWithDkim(unsignedMimeMessage, emailMessageModel);
            if (dkimSignedMessage == null)
            {
                return false;
            }
            final String messageID = sendMimeMessage(dkimSignedMessage);
            emailMessageModel.setSent(true);
            emailMessageModel.setSentMessageID(messageID);
            emailMessageModel.setSentDate(new Date());
            emailMessageModel.setAttachments(attachments);
            getModelService().save(emailMessageModel);
            return true;
        }
        catch (final EmailException emailException)
        {
            LOG.warn("appEvent=AdnocSendEmail, Could not send e-mail pk [{}] subject [{}]", emailMessageModel.getPk(), emailMessageModel.getSubject());
            LOG.error("appEvent=AdnocSendEmail, An exception occurred {}.", ExceptionUtils.getRootCauseMessage(emailException));
            throw new RuntimeException(emailException);
        }
    }

    /**
     * Signs the email message using DKIM authentication.
     *
     * @param unsignedMimeMessage The email message to sign
     * @param emailMessageModel   The email message model containing metadata
     * @return true if signing was successful, false otherwise
     */
    protected MimeMessage signEmailWithDkim(final MimeMessage unsignedMimeMessage, final EmailMessageModel emailMessageModel)
    {
        try
        {
            final AdnocSMTPDKIMMessage dkimMessage = new AdnocSMTPDKIMMessage(unsignedMimeMessage, adnocDkimSigner);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dkimMessage.writeTo(baos, null);
            final String dkimHeader = getAdnocDkimSigner().sign(dkimMessage);
            LOG.info("appEvent=AdnocSendEmail, AllHeaderLines={}.", Collections.list(dkimMessage.getAllHeaderLines()));
            LOG.info("appEvent=AdnocSendEmail, AllHeaders={}.", Collections.list(dkimMessage.getAllHeaders()));
            final String messageContent = baos.toString(StandardCharsets.UTF_8);
            LOG.info("appEvent=AdnocSendEmail, Signed Email Message:\n{}", messageContent);
            return dkimMessage;
        }
        catch (final Exception exception)
        {
            LOG.warn("appEvent=AdnocSendEmail, Could not send e-mail pk [{}] subject [{}]",
                    emailMessageModel.getPk(), emailMessageModel.getSubject());
            LOG.error("appEvent=AdnocSendEmail, An exception occurred {}.",
                    ExceptionUtils.getRootCauseMessage(exception));
            return null;
        }
    }

    public String sendMimeMessage(final MimeMessage dkimSignedMessage) throws EmailException
    {
        Objects.requireNonNull(dkimSignedMessage, "MimeMessage has not been created yet");
        try
        {
            Transport.send(dkimSignedMessage);
            return dkimSignedMessage.getMessageID();
        }
        catch (final Exception e)
        {
            throw new EmailException("Sending the email to the following server failed :" + e.getMessage(), e);
        }
    }


    protected AdnocDkimSigner getAdnocDkimSigner()
    {
        return adnocDkimSigner;
    }

    public void setAdnocDkimSigner(final AdnocDkimSigner adnocDkimSigner)
    {
        this.adnocDkimSigner = adnocDkimSigner;
    }
}
