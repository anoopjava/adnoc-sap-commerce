package com.adnoc.service.actions.quote;

import com.adnoc.service.constants.AdnocserviceConstants;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.actions.GenerateEmailAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.task.RetryLaterException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* When customer Re_Submits the Quote this file will notify the CsAdmin (CS Cockpit) Quote status should be BUYER_OFFER(Approved) state*/

public class AdnocQuoteRequestResubmissionEmailAction extends GenerateEmailAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuoteRequestResubmissionEmailAction.class);

    private ConfigurationService configurationService;
    private EmailService emailService;

    @Override
    public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException
    {
        final Transition transition = super.executeAction(businessProcessModel);
        if (transition == Transition.OK)
        {
            final List<String> toEmails = generateEmails(getConfigurationService().getConfiguration().getString(AdnocserviceConstants.ADMIN_TOADDRESS));
            final List<EmailAddressModel> toAddresses = createEmailsAddressForEmail(toEmails);
            if (CollectionUtils.isEmpty(toAddresses))
            {
                LOG.error("appEvent=AdnocQuoteResubmissionEmailValidation, No valid admin email addresses generated..");
                return Transition.NOK;
            }

            final EmailMessageModel emailMessage = businessProcessModel.getEmails().iterator().next();
            emailMessage.setToAddresses(toAddresses);
            modelService.save(emailMessage);
        }
        return transition;
    }

    private List<String> generateEmails(final String emails)
    {
        final List<String> emailAddress = new ArrayList<>();
        if (StringUtils.isNotBlank(emails))
        {
            final String[] values = emails.split(",");
            emailAddress.addAll(Arrays.asList(values));
        }
        return emailAddress;
    }

    private List<EmailAddressModel> createEmailsAddressForEmail(final List<String> emails)
    {
        final List<EmailAddressModel> emailAddressModelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(emails))
        {
            for (final String email : emails)
            {
                LOG.info("appEvent=AdnocQuoteResubmissionEmailAddressCreation, processing email address: {}", email);
                final EmailAddressModel emailAddressModel = getEmailService().getOrCreateEmailAddressForEmail(email, email.split("@")[0]);
                emailAddressModelList.add(emailAddressModel);
            }
        }
        return emailAddressModelList;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    protected EmailService getEmailService()
    {
        return emailService;
    }

    public void setEmailService(final EmailService emailService)
    {
        this.emailService = emailService;
    }
}
