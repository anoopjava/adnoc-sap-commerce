package com.adnoc.facades.user.creditlimit.impl;

import com.adnoc.facades.company.request.data.AdnocCreditLimitRequestData;
import com.adnoc.facades.company.response.data.AdnocCreditLimitResponseData;
import com.adnoc.facades.creditsimulation.data.AdnocCreditSimulationRequestData;
import com.adnoc.facades.creditsimulation.data.AdnocCreditSimulationResponseData;
import com.adnoc.facades.creditsimulation.data.ECheckConfirmation;
import com.adnoc.facades.creditsimulation.data.MessageType;
import com.adnoc.facades.user.creditlimit.AdnocCreditLimitFacade;
import com.adnoc.service.exception.AdnocS4HanaException;
import com.adnoc.service.user.creditlimit.AdnocCreditLimitService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocCreditLimitFacadeImpl implements AdnocCreditLimitFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocCreditLimitFacadeImpl.class);

    public static final String ADNOC_CREDITSIMULATION_CREDITSEGMENT = "adnoc.creditsimulation.creditsegment.code";
    public static final String ADNOC_CREDITSIMULATION_CHECK_MOCK_ENABLED = "adnoc.creditsimulation.check.mock.enabled";

    private AdnocCreditLimitService adnocCreditLimitService;
    private CommonI18NService commonI18NService;
    private ConfigurationService configurationService;


    @Override
    public AdnocCreditLimitResponseData getCreditLimitDetails(final AdnocCreditLimitRequestData adnocCreditLimitRequestData)
    {
        LOG.info("appEvent=AdnocCreditLimit, get credit limit details for B2B Unit UID: {}", adnocCreditLimitRequestData.getB2bUnitUid());
        final AdnocCreditLimitResponseData adnocCreditLimitResponseData;
        try
        {
            adnocCreditLimitResponseData = adnocCreditLimitService.getCreditLimitResponse(adnocCreditLimitRequestData);
            if (Objects.nonNull(adnocCreditLimitResponseData)
                    && Objects.nonNull(adnocCreditLimitResponseData.getB2BCreditLimit())
                    && StringUtils.equals(adnocCreditLimitResponseData.getB2BCreditLimit().getMsgType(), "S"))
            {
                return adnocCreditLimitResponseData;
            }
        }
        catch (final Exception exception)
        {
            LOG.error("appEvent=AdnocCreditLimit {}", ExceptionUtils.getRootCauseMessage(exception));
            throw new AdnocS4HanaException("Due to some technical issue, credit limit is unavailable. Please try again later.");
        }
        throw new AdnocS4HanaException(adnocCreditLimitResponseData.getB2BCreditLimit().getMessage());
    }

    @Override
    public boolean creditSimulationCheck(final String payerId)
    {
        LOG.info("appEvent=AdnocCreditLimit, get payer ID: {}", payerId);
        final AdnocCreditSimulationResponseData adnocCreditSimulationResponseData;
        final Configuration configuration = getConfigurationService().getConfiguration();
        if (configuration.getBoolean(ADNOC_CREDITSIMULATION_CHECK_MOCK_ENABLED, false))
        {
            return true;
        }
        final AdnocCreditSimulationRequestData adnocCreditSimulationRequestData = new AdnocCreditSimulationRequestData();
        adnocCreditSimulationRequestData.setPayer(payerId);
        adnocCreditSimulationRequestData.setCurrency(getCommonI18NService().getCurrentCurrency().getIsocode());
        adnocCreditSimulationRequestData.setCreditSegment(configuration.getString(ADNOC_CREDITSIMULATION_CREDITSEGMENT));
        try
        {
            adnocCreditSimulationResponseData = adnocCreditLimitService.getCreditSimulationResponse(adnocCreditSimulationRequestData);

            if (validateAdnocCreditSimulationResponse(adnocCreditSimulationResponseData))
            {
                return (Objects.equals(adnocCreditSimulationResponseData.getMessageType(), MessageType.S)
                        && Objects.equals(adnocCreditSimulationResponseData.getECheckConfirmation(), ECheckConfirmation.T));
            }
        }
        catch (final Exception exception)
        {
            LOG.error("appEvent=AdnocCreditLimit {}", ExceptionUtils.getRootCauseMessage(exception));
            throw new AdnocS4HanaException("Due to some technical issue, credit simulation check is unavailable. Please try again later.");
        }
        throw new AdnocS4HanaException("Adnoc Credit simulation is not valid: " + (Objects.nonNull(adnocCreditSimulationResponseData) ? adnocCreditSimulationResponseData.toString() : "Response data is null"));
    }

    private boolean validateAdnocCreditSimulationResponse(final AdnocCreditSimulationResponseData adnocCreditSimulationResponseData)
    {
        return (Objects.nonNull(adnocCreditSimulationResponseData) && Objects.nonNull(adnocCreditSimulationResponseData.getECheckConfirmation()) && Objects.nonNull(adnocCreditSimulationResponseData.getMessageType()));
    }

    protected AdnocCreditLimitService getAdnocCreditLimitService()
    {
        return adnocCreditLimitService;
    }

    public void setAdnocCreditLimitService(final AdnocCreditLimitService adnocCreditLimitService)
    {
        this.adnocCreditLimitService = adnocCreditLimitService;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }
}
