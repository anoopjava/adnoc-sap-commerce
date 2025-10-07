package com.adnoc.service.user.creditlimit;

import com.adnoc.facades.company.request.data.AdnocCreditLimitRequestData;
import com.adnoc.facades.company.response.data.AdnocCreditLimitResponseData;
import com.adnoc.facades.creditsimulation.data.AdnocCreditSimulationRequestData;
import com.adnoc.facades.creditsimulation.data.AdnocCreditSimulationResponseData;

/**
 * The interface Adnoc credit limit service.
 */
public interface AdnocCreditLimitService
{
    /**
     * Gets credit limit response.
     *
     * @param adnocCreditLimitRequestData the adnoc credit limit request data
     * @return the credit limit response
     */
    AdnocCreditLimitResponseData getCreditLimitResponse(AdnocCreditLimitRequestData adnocCreditLimitRequestData);

    /**
     * Gets credit Simulation response.
     *
     * @param adnocCreditSimulationRequestData the adnoc credit siumulation request data
     * @return the credit Simulation response
     */
    AdnocCreditSimulationResponseData getCreditSimulationResponse(AdnocCreditSimulationRequestData adnocCreditSimulationRequestData);
}
