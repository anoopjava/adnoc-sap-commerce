package com.adnoc.service.process.sms.actions;

import com.adnoc.service.rest.integration.SmsRequestData;
import com.adnoc.service.sms.AdnocSmsService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractAdnocB2bGenerateSmsAction extends AbstractSimpleDecisionAction
{
    private static final Logger LOG = LogManager.getLogger(AbstractAdnocB2bGenerateSmsAction.class);

    private ConfigurationService configurationService;
    private AdnocSmsService adnocSmsService;

    public void prepareAndSendSmsAction(final String messageBodyCode, final String mobileNumber, final String... placeHolders)
    {
        LOG.info("appEvent=PrepareAndSendSms, preparing to send SMS to:{}", mobileNumber);
        if (StringUtils.isBlank(mobileNumber))
        {
            return;
        }
        final SmsRequestData smsRequestData = new SmsRequestData();
        smsRequestData.setMessage_body(String.format(getConfigurationService().getConfiguration().getString(messageBodyCode), placeHolders));
        smsRequestData.setMobile_numbers(List.of(mobileNumber));
        smsRequestData.setSender_name(getConfigurationService().getConfiguration().getString("adnoc.b2b.sms.sender.name"));
        smsRequestData.setSystem_key(getConfigurationService().getConfiguration().getString("adnoc.b2b.sms.system.key"));
        LOG.debug("appEvent=PrepareAndSendSms, prepared SmsRequestData:{}", smsRequestData);

        getAdnocSmsService().sendSms(smsRequestData);
        LOG.info("appEvent=PrepareAndSendSms, SMS sent to {}", mobileNumber);
    }

    public String getSalutationWithFirstName(B2BCustomerModel b2BCustomerModel)
    {
        final String titleName = defaultIfNullOrBlank(b2BCustomerModel.getTitle(), TitleModel::getName);
        final String firstName = defaultIfNullOrBlank(b2BCustomerModel, B2BCustomerModel::getFirstName);
        String salutationWithFirstName = titleName+" "+firstName;
        return salutationWithFirstName;
    }

    public <T> String defaultIfNullOrBlank(final T obj, final Function<T, String> mapper)
    {
        return Optional.ofNullable(obj).map(mapper).map(name -> StringUtils.defaultIfBlank(name, StringUtils.EMPTY))
                .orElse(StringUtils.EMPTY);
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    protected AdnocSmsService getAdnocSmsService()
    {
        return adnocSmsService;
    }

    public void setAdnocSmsService(final AdnocSmsService adnocSmsService)
    {
        this.adnocSmsService = adnocSmsService;
    }
}
