package com.adnoc.facades.payment;

import com.adnoc.facades.payment.card.data.AdnocPaymentResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentBankFinalizationRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentBankFinalizationResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;
import com.adnoc.service.model.BankPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

/**
 * Facade interface for handling ADNOC payment operations including payment initiation
 * and retrieval for both checkout and direct payment flows.
 */
public interface AdnocPaymentFacade
{
    /**
     * Initiate checkout payment adnoc payment initiate response data.
     *
     * @param adnocPaymentInitiateRequestData the adnoc payment initiate request data
     * @return the adnoc payment initiate response data
     */
    AdnocPaymentInitiateResponseData initiateCheckoutPayment(AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData);

    /**
     * Initiate payment adnoc payment initiate response data.
     *
     * @param adnocPaymentInitiateRequestData the adnoc payment initiate request data
     * @param paymentFlow                     the type of payment flow to be initiated (e.g., "DIRECT", "HOSTED")
     * @return the adnoc payment initiate response data
     */
    AdnocPaymentInitiateResponseData initiatePayment(AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData, String paymentFlow);

    /**
     * Retrieve checkout payment adnoc payment response data.
     *
     * @param resultIndicator the result indicator
     * @return the adnoc payment response data
     */
    AdnocPaymentResponseData retrieveCheckoutPayment(String resultIndicator);

    /**
     * Retrieve payment adnoc payment response data.
     *
     * @param resultIndicator the result indicator
     * @return the adnoc payment response data
     */
    AdnocPaymentResponseData retrievePayment(String resultIndicator);

    /**
     * Finalize checkout bank payment adnoc payment bank finalization response data.
     *
     * @param transactionID the transaction id
     * @return the adnoc payment bank finalization response data
     */
    AdnocPaymentBankFinalizationResponseData finalizeCheckoutBankPayment(String transactionID);

    /**
     * Finalize bank payment adnoc payment bank finalization response data.
     *
     * @param adnocPaymentBankFinalizationRequestData the adnoc payment bank finalization request data
     * @return the adnoc payment bank finalization response data
     */
    AdnocPaymentBankFinalizationResponseData finalizeBankPayment(AdnocPaymentBankFinalizationRequestData adnocPaymentBankFinalizationRequestData);

    /**
     * Saving credit card info more details.
     *
     * @param creditCardPaymentInfoModel the credit card payment info model
     * @param adnocPaymentResponseData   the adnoc payment response data
     */
    void savingCreditCardInfoMoreDetails(CreditCardPaymentInfoModel creditCardPaymentInfoModel, AdnocPaymentResponseData adnocPaymentResponseData);

    /**
     * Saving bank payment info more details.
     *
     * @param bankPaymentInfoModel                     the bank payment info model
     * @param adnocPaymentBankFinalizationResponseData the adnoc payment bank finalization response data
     * @param transactionId                            the transaction id
     */
    void savingBankPaymentInfoMoreDetails(BankPaymentInfoModel bankPaymentInfoModel, AdnocPaymentBankFinalizationResponseData adnocPaymentBankFinalizationResponseData, String transactionId);
}
