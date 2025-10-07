package com.adnoc.service.payment;


import com.adnoc.facades.payment.bank.data.AdnocPaymentBankRegistrationRequestData;
import com.adnoc.facades.payment.bank.data.AdnocPaymentBankTransactionRegistrationResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentCardInitiateCheckoutRequestData;
import com.adnoc.facades.payment.card.data.AdnocPaymentCardInitiateCheckoutResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentBankFinalizationRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentBankFinalizationResponseData;

/**
 * Service interface for handling payment operations in the ADNOC system.
 * This interface defines methods for initiating payment checkout requests.
 */
public interface AdnocPaymentService
{
    /**
     * Creates Payment Checkout Request
     *
     * @param adnocPaymentInitiateCheckoutRequestData the request data containing details for the payment initiation
     * @return AdnocPaymentCardInitiateCheckoutResponseData containing the response data for the payment initiation
     */
    AdnocPaymentCardInitiateCheckoutResponseData initiatePayment(AdnocPaymentCardInitiateCheckoutRequestData adnocPaymentInitiateCheckoutRequestData);

    /**
     * Register bank payment adnoc payment bank transaction registration response data.
     *
     * @param adnocPaymentBankRegistrationRequestData the adnoc payment bank registration request data
     * @return the adnoc payment bank transaction registration response data
     */
    AdnocPaymentBankTransactionRegistrationResponseData registerBankPayment(AdnocPaymentBankRegistrationRequestData adnocPaymentBankRegistrationRequestData);

    /**
     * Retrieve payment adnoc payment response data.
     *
     * @param orderId the order id
     * @return the adnoc payment response data
     */
    AdnocPaymentResponseData retrievePayment(String orderId);

    /**
     * Finalize bank transfer adnoc payment bank finalization response data.
     *
     * @param adnocPaymentBankFinalizationRequestData the adnoc payment bank finalization request data
     * @return the adnoc payment bank finalization response data
     */
    AdnocPaymentBankFinalizationResponseData finalizeBankTransfer(AdnocPaymentBankFinalizationRequestData adnocPaymentBankFinalizationRequestData);
}
