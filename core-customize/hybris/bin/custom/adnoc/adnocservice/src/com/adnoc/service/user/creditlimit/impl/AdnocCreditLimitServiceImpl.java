package com.adnoc.service.user.creditlimit.impl;

import com.adnoc.facades.company.request.data.AdnocCreditLimitRequestData;
import com.adnoc.facades.company.response.data.AdnocCreditLimitResponseData;
import com.adnoc.facades.creditsimulation.data.AdnocCreditSimulationRequestData;
import com.adnoc.facades.creditsimulation.data.AdnocCreditSimulationResponseData;
import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import com.adnoc.service.user.creditlimit.AdnocCreditLimitService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocCreditLimitServiceImpl implements AdnocCreditLimitService
{
    private static final Logger LOG = LogManager.getLogger(AdnocCreditLimitServiceImpl.class);

    public static final String ADNOC_CREDITLIMIT_GATEWAY_DESTINATION = "adnocCreditLimitDestination";
    public static final String ADNOC_CREDITLIMIT_DESTINATION_TARGET = "adnoc-credit_limit-destination-target";

    public static final String ADNOC_CREDIT_SIMULATION_DESTINATION = "adnocCreditSimulationDestination";
    public static final String ADNOC_CREDIT_SIMULATION_DESTINATION_TARGET = "adnoc-creditsimulation-destination-target";

    private AdnocRestIntegrationService adnocRestIntegrationService;

    @Override
    public AdnocCreditLimitResponseData getCreditLimitResponse(final AdnocCreditLimitRequestData adnocCreditLimitRequestData)
    {
        LOG.info("appEvent=AdnocCreditLimit, get credit limit request for: {}", adnocCreditLimitRequestData.getB2bUnitUid());
        final AdnocCreditLimitResponseData adnocCreditLimitResponseData = getAdnocRestIntegrationService().restIntegration(ADNOC_CREDITLIMIT_GATEWAY_DESTINATION, ADNOC_CREDITLIMIT_DESTINATION_TARGET, adnocCreditLimitRequestData, AdnocCreditLimitResponseData.class);
        return adnocCreditLimitResponseData;
    }

    @Override
    public AdnocCreditSimulationResponseData getCreditSimulationResponse(AdnocCreditSimulationRequestData adnocCreditSimulationRequestData)
    {
        LOG.info("appEvent=AdnocCreditSimulation, get credit simulation request for: {}", adnocCreditSimulationRequestData.getPayer());
        final AdnocCreditSimulationResponseData adnocCreditSimulationResponseData = getAdnocRestIntegrationService().restIntegration(ADNOC_CREDIT_SIMULATION_DESTINATION, ADNOC_CREDIT_SIMULATION_DESTINATION_TARGET, adnocCreditSimulationRequestData, AdnocCreditSimulationResponseData.class);
        return adnocCreditSimulationResponseData;
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
