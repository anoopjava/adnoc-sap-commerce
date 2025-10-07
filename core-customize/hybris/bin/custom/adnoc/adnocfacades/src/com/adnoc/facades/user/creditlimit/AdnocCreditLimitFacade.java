package com.adnoc.facades.user.creditlimit;

import com.adnoc.facades.company.request.data.AdnocCreditLimitRequestData;
import com.adnoc.facades.company.response.data.AdnocCreditLimitResponseData;

/**
 * The interface Adnoc credit limit facade.
 */
public interface AdnocCreditLimitFacade
{
    /**
     * Gets credit limit details.
     *
     * @param adnocCreditLimitRequestData the adnoc credit limit request data
     * @return the credit limit details
     */
    AdnocCreditLimitResponseData getCreditLimitDetails(AdnocCreditLimitRequestData adnocCreditLimitRequestData);

    /**
     * Credit simulation check boolean.
     *
     * @param payerId the payer id
     * @return the boolean
     */
    boolean creditSimulationCheck(String payerId);
}
