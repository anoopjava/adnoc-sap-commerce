package com.adnoc.facades.payment.strategies;

import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;

/**
 * The interface Initiate payment strategy.
 */
public interface AdnocInitiatePaymentStrategy
{
    /**
     * Initiate payment adnoc payment initiate response data.
     *
     * @param adnocPaymentInitiateRequestData the adnoc payment initiate request data
     * @return the adnoc payment initiate response data
     */
    AdnocPaymentInitiateResponseData initiatePayment(AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData,String paymentFlow);
}
