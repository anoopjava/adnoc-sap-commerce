package com.adnoc.service.overdueinvoices;


import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceRequestData;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceResponseData;
import com.adnoc.service.model.AdnocOverduePaymentTransactionModel;

/**
 * The interface Adnoc overdue invoices service.
 */
public interface AdnocOverdueInvoicesService
{

    /**
     * Gets overdue invoices response.
     *
     * @param adnocOverdueInvoiceRequestData the adnoc overdue invoice request data
     * @return the overdue invoices response
     */
    AdnocOverdueInvoiceResponseData getOverdueInvoicesResponse(AdnocOverdueInvoiceRequestData adnocOverdueInvoiceRequestData);

    /**
     * Gets overdue invoices response.
     *
     * @param resultIndicator the result indicator
     * @return the overdue invoices response
     */
    AdnocOverduePaymentTransactionModel getAdnocOverduePaymentTransaction(String resultIndicator);

}
