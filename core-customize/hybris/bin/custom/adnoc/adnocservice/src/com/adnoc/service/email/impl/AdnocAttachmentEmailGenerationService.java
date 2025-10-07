package com.adnoc.service.email.impl;

import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailGenerationService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.adnoc.service.event.QuoteSellerApprovalSubmitEventListener.QUOTE_EMAIL_ATTACHMENT;

public class AdnocAttachmentEmailGenerationService extends DefaultEmailGenerationService
{
    private static final Logger LOG = LogManager.getLogger(AdnocAttachmentEmailGenerationService.class);

    private ProcessParameterHelper processParameterHelper;

    @Override
    public EmailMessageModel generate(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel)
    {

        ServicesUtil.validateParameterNotNull(emailPageModel, "EmailPageModel cannot be null");

        Assert.isInstanceOf(EmailPageTemplateModel.class, emailPageModel.getMasterTemplate(),
                "MasterTemplate associated with EmailPageModel should be EmailPageTemplate");

        final EmailPageTemplateModel emailPageTemplateModel = (EmailPageTemplateModel) emailPageModel.getMasterTemplate();
        final RendererTemplateModel bodyRenderTemplate = emailPageTemplateModel.getHtmlTemplate();

        Assert.notNull(bodyRenderTemplate, "HtmlTemplate associated with MasterTemplate of EmailPageModel cannot be null");

        final RendererTemplateModel subjectRenderTemplate = emailPageTemplateModel.getSubject();
        Assert.notNull(subjectRenderTemplate, "Subject associated with MasterTemplate of EmailPageModel cannot be null");

        final EmailMessageModel emailMessageModel;

        final AbstractEmailContext<BusinessProcessModel> emailContext = getEmailContextFactory().create(businessProcessModel,
                emailPageModel, bodyRenderTemplate);

        if (emailContext == null)
        {
            LOG.error("appEvent=AdnocEmailGeneration, Failed to create email context for businessProcess [{}]", businessProcessModel);
            throw new IllegalStateException("appEvent=AdnocEmailGeneration, Failed to create email context for businessProcess [" + businessProcessModel + "]");
        }
        else
        {
            if (!validate(emailContext))
            {
                LOG.error("appEvent=AdnocEmailGeneration, Email context for businessProcess [{}] is not valid", businessProcessModel);
                throw new IllegalStateException("appEvent=AdnocEmailGeneration, Email context for businessProcess [" + businessProcessModel + "] is not valid: "
                        + ReflectionToStringBuilder.toString(emailContext));
            }

            final StringWriter subject = new StringWriter();
            getRendererService().render(subjectRenderTemplate, emailContext, subject);

            final StringWriter body = new StringWriter();
            getRendererService().render(bodyRenderTemplate, emailContext, body);

            emailMessageModel = createEmailMessage(subject.toString(), body.toString(), emailContext, businessProcessModel);

            if (LOG.isDebugEnabled())
            {
                LOG.debug("appEvent=AdnocEmailGeneration, Email Subject: {}", emailMessageModel.getSubject());
                LOG.debug("appEvent=AdnocEmailGeneration, Email Body: {}", emailMessageModel.getBody());
            }

        }
        return emailMessageModel;
    }

    protected EmailMessageModel createEmailMessage(final String emailSubject, final String emailBody,
                                                   final AbstractEmailContext<BusinessProcessModel> emailContext, final BusinessProcessModel businessProcessModel)
    {
        List<EmailAttachmentModel> emailAttachments = null;
        final BusinessProcessParameterModel processParameterByName = processParameterHelper.getProcessParameterByName(businessProcessModel, QUOTE_EMAIL_ATTACHMENT);
        if (Objects.nonNull(processParameterByName)
                && processParameterByName.getValue() instanceof final EmailAttachmentModel emailAttachmentModel)
        {
            emailAttachments = new ArrayList<>();
            emailAttachments.add(emailAttachmentModel);
            LOG.info("appEvent=AdnocEmailGeneration, Attachment successfully generated for processParameterByName={}", processParameterByName);
        }
        final List<EmailAddressModel> toEmails = new ArrayList<>();
        final EmailAddressModel toAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getToEmail(),
                emailContext.getToDisplayName());
        toEmails.add(toAddress);
        final EmailAddressModel fromAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getFromEmail(),
                emailContext.getFromDisplayName());
        return getEmailService().createEmailMessage(toEmails, new ArrayList<>(),
                new ArrayList<>(), fromAddress, emailContext.getFromEmail(), emailSubject, emailBody, emailAttachments);
    }

    protected ProcessParameterHelper getProcessParameterHelper()
    {
        return processParameterHelper;
    }

    public void setProcessParameterHelper(final ProcessParameterHelper processParameterHelper)
    {
        this.processParameterHelper = processParameterHelper;
    }
}
