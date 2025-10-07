package com.adnoc.service.order.action;

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
import java.util.Optional;
import java.util.stream.Collectors;

/* When customer Submits the Return Order this file will notify the CsAdmin (CS Cockpit) Return Order status should be ******* state*/

public class AdnocOrderReplicationFailedEmailAction extends GenerateEmailAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderReplicationFailedEmailAction.class);

    private ConfigurationService configurationService;
    private EmailService emailService;

    @Override
    public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException
    {
        final Transition transition = super.executeAction(businessProcessModel);
        if (transition == Transition.OK)
        {
            final List<EmailAddressModel> toAddresses = Optional.of(getConfigurationService().getConfiguration().getString(AdnocserviceConstants.IT_SUPPORT_TOADDRESS))
                    .map(this::generateEmails)
                    .map(this::createEmailsAddressForEmail)
                    .orElseGet(ArrayList::new);
            if (CollectionUtils.isEmpty(toAddresses))
            {
                LOG.error("appEvent=emails,No valid email addresses created for the admin.");
                return Transition.NOK;
            }

            final EmailMessageModel emailMessage = businessProcessModel.getEmails().iterator().next();
            emailMessage.setToAddresses(toAddresses);
            getModelService().save(emailMessage);
        }
        return transition;
    }

    private List<String> generateEmails(final String emails)
    {
        return StringUtils.isNotBlank(emails) ? Arrays.asList(emails.split(",")) : new ArrayList<>();
    }

    private List<EmailAddressModel> createEmailsAddressForEmail(final List<String> emails)
    {
        return CollectionUtils.isEmpty(emails) ? new ArrayList<>() : emails.stream()
                .map(email -> {
                    LOG.info("appEvent=email,processing email address: {}", email);
                    return getEmailService().getOrCreateEmailAddressForEmail(email, email.split("@")[0]);
                })
                .collect(Collectors.toList());
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
