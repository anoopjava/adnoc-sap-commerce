package com.adnoc.service.soa;

import com.adnoc.facades.data.AdnocB2BInvoiceDocumentRequestData;
import com.adnoc.facades.data.AdnocB2BInvoiceDocumentResponseData;
import com.adnoc.service.data.AdnocDownloadStatementRequestData;
import com.adnoc.service.data.AdnocDownloadStatementResponseData;
import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;


class AdnocAccountSummaryServiceImplTest
{
    @Mock
    private AdnocRestIntegrationService adnocRestIntegrationService= Mockito.mock(AdnocRestIntegrationService.class);

    @InjectMocks
    private AdnocAccountSummaryServiceImpl adnocAccountSummaryService=new AdnocAccountSummaryServiceImpl();


    @Test
    void testGetS4InvoiceDocument()
    {
        Mockito.when(adnocRestIntegrationService.restIntegration(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new AdnocB2BInvoiceDocumentResponseData());

        adnocAccountSummaryService.setAdnocRestIntegrationService(adnocRestIntegrationService);
        AdnocB2BInvoiceDocumentResponseData adnocB2BInvoiceDocumentResponseData= adnocAccountSummaryService.getS4InvoiceDocument(new AdnocB2BInvoiceDocumentRequestData());
        Assertions.assertThat(adnocB2BInvoiceDocumentResponseData).isNotNull();
    }
}