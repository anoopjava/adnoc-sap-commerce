package com.adnoc.service.soa;

import com.adnoc.facades.data.AdnocB2BInvoiceDocumentRequestData;
import com.adnoc.facades.data.AdnocB2BInvoiceDocumentResponseData;
import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import de.hybris.platform.b2bacceleratorservices.company.impl.DefaultB2BDocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocAccountSummaryServiceImpl extends DefaultB2BDocumentService implements AdnocAccountSummaryService
{
    private static final Logger LOG = LogManager.getLogger(AdnocAccountSummaryServiceImpl.class);
    public static final String ADNOC_B2BInvoice_Document_GATEWAY_DESTINATION = "adnocB2BInvoiceDocumentStatementDestination";
    public static final String ADNOC_B2BInvoice_Document_DESTINATION_TARGET = "adnoc-B2BInvoice-Document-Destination-target";

    private AdnocRestIntegrationService adnocRestIntegrationService;

    @Override
    public AdnocB2BInvoiceDocumentResponseData getS4InvoiceDocument(final AdnocB2BInvoiceDocumentRequestData adnocB2BInvoiceDocumentRequestData)
    {
        LOG.info("appEvent=AdnocS4InvoiceDocument, received document request for: {}", adnocB2BInvoiceDocumentRequestData);
        final AdnocB2BInvoiceDocumentResponseData adnocB2BInvoiceDocumentResponseData = getAdnocRestIntegrationService().restIntegration(ADNOC_B2BInvoice_Document_GATEWAY_DESTINATION, ADNOC_B2BInvoice_Document_DESTINATION_TARGET, adnocB2BInvoiceDocumentRequestData, AdnocB2BInvoiceDocumentResponseData.class);

        LOG.debug("appEven=AdnocS4InvoiceDocument, successfully received document response for:{}", adnocB2BInvoiceDocumentRequestData);
        return adnocB2BInvoiceDocumentResponseData;
    }

    protected AdnocRestIntegrationService getAdnocRestIntegrationService()
    {
        return adnocRestIntegrationService;
    }

    public void setAdnocRestIntegrationService(final AdnocRestIntegrationService adnocRestIntegrationService)
    {
        this.adnocRestIntegrationService = adnocRestIntegrationService;
    }

}
