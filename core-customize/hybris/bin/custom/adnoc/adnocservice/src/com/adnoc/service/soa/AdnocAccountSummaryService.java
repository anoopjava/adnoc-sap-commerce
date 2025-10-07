package com.adnoc.service.soa;

import com.adnoc.facades.data.AdnocB2BInvoiceDocumentRequestData;
import com.adnoc.facades.data.AdnocB2BInvoiceDocumentResponseData;


/**
 * The interface Adnoc account summary service.
 */
public interface AdnocAccountSummaryService
{

     /**
      * Gets b 2 b invoice document response.
      *
      * @param adnocB2BInvoiceDocumentRequestData the adnoc b 2 b invoice document request data
      * @return the b 2 b invoice document response
      */
     AdnocB2BInvoiceDocumentResponseData getS4InvoiceDocument(AdnocB2BInvoiceDocumentRequestData adnocB2BInvoiceDocumentRequestData);
}
