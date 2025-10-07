package com.adnoc.facades.downloadstatement;

import com.adnoc.facades.creditsimulation.data.MessageType;
import com.adnoc.facades.data.AdnocB2BInvoiceDocumentRequestData;
import com.adnoc.facades.data.AdnocB2BInvoiceDocumentResponseData;
import com.adnoc.service.exception.AdnocS4HanaException;
import com.adnoc.service.soa.AdnocAccountSummaryService;
import de.hybris.platform.b2bacceleratorfacades.document.data.AttachmentData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

import java.util.Base64;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AdnocAccountSummaryFacadeImplTest
{
    @InjectMocks
    private AdnocAccountSummaryFacadeImpl adnocAccountSummaryFacade;
    @Mock
    private AdnocAccountSummaryService adnocAccountSummaryService;
    @Mock
    private AdnocB2BInvoiceDocumentRequestData requestData;

    @Mock
    private AdnocB2BInvoiceDocumentResponseData responseData;

    @Test
    public void testGetB2BDocumentAttachmentData_Success()
    {
        final String base64Pdf = Base64.getEncoder().encodeToString("TestPDFContent".getBytes());
        Mockito.when(adnocAccountSummaryService.getS4InvoiceDocument(requestData)).thenReturn(responseData);
        Mockito.when(responseData.getPdfString()).thenReturn(base64Pdf);
        Mockito.when(responseData.getInvoiceNo()).thenReturn("testInv123");
        Mockito.when(responseData.getMessageType()).thenReturn(MessageType.S);

        final AttachmentData attachmentData = adnocAccountSummaryFacade.getB2BDocumentAttachmentData(requestData);

        assertNotNull(attachmentData);
        assertEquals("testInv123", attachmentData.getFileName());
        assertEquals(MediaType.APPLICATION_PDF_VALUE, attachmentData.getFileType());
        assertTrue(attachmentData.getFileContent() instanceof ByteArrayResource);
    }

    @Test(expected = AdnocS4HanaException.class)
    public void testGetB2BDocumentAttachmentData_Failure()
    {
        Mockito.when(adnocAccountSummaryService.getS4InvoiceDocument(requestData)).thenReturn(responseData);
        Mockito.when(responseData.getMessageType()).thenReturn(MessageType.E);

        final AttachmentData attachmentData = adnocAccountSummaryFacade.getB2BDocumentAttachmentData(requestData);
        assertNotNull(attachmentData);
    }
}

