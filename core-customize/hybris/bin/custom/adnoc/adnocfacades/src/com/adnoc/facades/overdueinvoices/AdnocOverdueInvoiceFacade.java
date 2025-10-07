package com.adnoc.facades.overdueinvoices;

import com.adnoc.facades.company.overdue.invoice.data.AdnocOverduePaymentRequestData;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceRequestData;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentBankFinalizationResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;

import java.util.List;

/**
 * The interface Adnoc overdue invoice facade.
 */
public interface AdnocOverdueInvoiceFacade
{

    /**
     * Gets overdue invoices.
     *
     * @param adnocOverdueInvoiceRequestData the adnoc overdue invoice request data
     * @return the overdue invoices
     */
    AdnocOverdueInvoiceResponseData getOverdueInvoices(AdnocOverdueInvoiceRequestData adnocOverdueInvoiceRequestData);

    /**
     * Gets payment types.
     *
     * @return the payment types
     */
    List<B2BPaymentTypeData> getPaymentTypes();


    /**
     * Initiate payment adnoc payment initiate response data.
     *
     * @param adnocOverduePaymentRequestData the adnoc overdue payment request data
     * @return the adnoc payment initiate response data
     */
    AdnocPaymentInitiateResponseData initiatePayment(AdnocOverduePaymentRequestData adnocOverduePaymentRequestData);

    /**
     * Retrieve payment adnoc payment response data.
     *
     * @param resultIndicator the result indicator
     * @return the adnoc payment response data
     */
    AdnocPaymentResponseData retrievePayment(String resultIndicator);

    /**
     * Finalize bank transfer adnoc payment bank finalization response data.
     *
     * @param transactionID the transaction id
     * @return the adnoc payment bank finalization response data
     */
    AdnocPaymentBankFinalizationResponseData finalizeBankTransfer(String transactionID);

}
