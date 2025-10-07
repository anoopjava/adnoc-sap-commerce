package com.adnoc.facades.downloadstatement;

import com.adnoc.facades.data.AdnocB2BInvoiceDocumentRequestData;
import de.hybris.platform.b2bacceleratorfacades.company.B2BAccountSummaryFacade;
import de.hybris.platform.b2bacceleratorfacades.document.data.AttachmentData;


/**
 * The interface Adnoc account summary facade.
 */
public interface AdnocAccountSummaryFacade extends B2BAccountSummaryFacade
{

    /**
     * Gets b 2 b document attachment data.
     *
     * @param adnocB2BInvoiceDocumentRequestData the adnoc b 2 b invoice document request data
     * @return the b 2 b document attachment data
     */
    AttachmentData getB2BDocumentAttachmentData(AdnocB2BInvoiceDocumentRequestData adnocB2BInvoiceDocumentRequestData);
}
