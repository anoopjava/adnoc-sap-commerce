package com.adnoc.service.order.payment.transaction;

import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;

/**
 * The interface Adnoc payment initiate request creation strategy.
 */
public interface AdnocPaymentInitiateRequestCreationStrategy
{
    /**
     * Create payment initiate request adnoc payment initiate request data.
     *
     * @param adnocPaymentInitiateRequestData the adnoc payment initiate request data
     * @param paymentFlow                     the payment flow
     * @return the adnoc payment initiate request data
     */
    AdnocPaymentInitiateRequestData createPaymentInitiateRequest(AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData, String paymentFlow);
}
