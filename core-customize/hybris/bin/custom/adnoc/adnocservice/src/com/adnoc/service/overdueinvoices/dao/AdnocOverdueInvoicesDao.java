package com.adnoc.service.overdueinvoices.dao;

import com.adnoc.service.model.AdnocOverdueInvoiceDetailsModel;
import com.adnoc.service.model.AdnocOverduePaymentTransactionModel;

import java.util.List;

/**
 * The interface Adnoc overdue invoices service.
 */
public interface AdnocOverdueInvoicesDao
{
    /**
     * Gets overdue invoices response.
     *
     * @param resultIndicator the result indicator
     * @return the overdue invoices response
     */
    AdnocOverduePaymentTransactionModel findAdnocOverduePaymentTransaction(String resultIndicator);


    /**
     * Fetch payment in progess adnoc overdue invoice details list.
     *
     * @return the list
     */
    List<AdnocOverdueInvoiceDetailsModel> fetchPaymentInProgessAdnocOverdueInvoiceDetails();

}
