package com.adnoc.service.sms;

import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import com.adnoc.service.rest.integration.SmsRequestData;
import com.adnoc.service.rest.integration.SmsResponseData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocSmsServiceImpl implements AdnocSmsService
{
    private static final Logger LOG = LogManager.getLogger(AdnocSmsServiceImpl.class);
    public static final String ADNOC_SMS_GATEWAY_DESTINATION = "adnocSmsGatewayDestination";
    public static final String ADNOC_SMSGATEWAY_DESTINATION_TARGET = "adnoc-smsgateway-destination-target";

    private AdnocRestIntegrationService adnocRestIntegrationService;

    @Override
    public SmsResponseData sendSms(final SmsRequestData smsRequestData)
    {
        LOG.info("appEvent=AdnocSmsService,start sending sms to: {}", smsRequestData.getMobile_numbers());
        try
        {
            return getAdnocRestIntegrationService().restIntegration(ADNOC_SMS_GATEWAY_DESTINATION, ADNOC_SMSGATEWAY_DESTINATION_TARGET, smsRequestData, SmsResponseData.class);
        }
        catch (final Exception exception)
        {
            LOG.error("An exception occurred while sending the sms with cause:{}", ExceptionUtils.getRootCauseMessage(exception));
        }
        return null;
    }

    protected AdnocRestIntegrationService getAdnocRestIntegrationService()

    {
        return adnocRestIntegrationService;
    }

    public void setAdnocRestIntegrationService(final AdnocRestIntegrationService adnocRestIntegrationService)
    {
        this.adnocRestIntegrationService = adnocRestIntegrationService;
    }
}
