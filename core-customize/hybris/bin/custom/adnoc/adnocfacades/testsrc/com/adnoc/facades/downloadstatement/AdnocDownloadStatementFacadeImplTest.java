package com.adnoc.facades.downloadstatement;

import com.adnoc.service.data.AdnocDownloadStatementRequestData;
import com.adnoc.service.data.AdnocDownloadStatementResponseData;
import com.adnoc.service.data.response.AdnocDownloadStatementOutputResponse;
import com.adnoc.service.soa.AdnocDownloadStatementService;
import de.hybris.platform.integrationservices.util.timeout.IntegrationExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Base64;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AdnocDownloadStatementFacadeImplTest
{
    @InjectMocks
    private AdnocDownloadStatementFacadeImpl adnocDownloadStatementFacade;
    @Mock
    private AdnocDownloadStatementService adnocDownloadStatementService;
    @Mock
    private AdnocDownloadStatementRequestData requestData;
    @Mock
    private AdnocDownloadStatementResponseData responseData;
    @Mock
    private AdnocDownloadStatementOutputResponse outputResponse;

    @Test
    public void testProcessDownloadStatementSuccess()
    {
        final String base64Pdf = Base64.getEncoder().encodeToString("TestPDFContent".getBytes());
        outputResponse.setStatusCode("200");
        outputResponse.setMessage("Success");
        Mockito.when(outputResponse.getStatementOfAccountB64()).thenReturn(base64Pdf);
        Mockito.when(responseData.getOutput()).thenReturn(outputResponse);
        Mockito.when(adnocDownloadStatementService.getDownloadStatementResponse(requestData)).thenReturn(responseData);

        final byte[] result = adnocDownloadStatementFacade.processDownloadStatement(requestData);

        assertNotNull(result);
        assertTrue(result.length > 0);

    }

    @Test(expected = IntegrationExecutionException.class)
    public void testCheckForErrorStatus_statementOfAccountB64_null()
    {

        outputResponse.setStatusCode("200");
        outputResponse.setMessage("Success");

        Mockito.when(responseData.getOutput()).thenReturn(outputResponse);
        Mockito.when(adnocDownloadStatementService.getDownloadStatementResponse(requestData)).thenReturn(responseData);
        final byte[] result = adnocDownloadStatementFacade.processDownloadStatement(requestData);
        assertNull(result);
    }

    @Test(expected = IntegrationExecutionException.class)
    public void testprocessDownloadStatementRecordEmpty()
    {
        Mockito.when(adnocDownloadStatementService.getDownloadStatementResponse(requestData)).thenReturn(responseData);
        responseData.setOutput(null);

        Mockito.when(adnocDownloadStatementService.getDownloadStatementResponse(requestData)).thenReturn(responseData);
        final byte[] result = adnocDownloadStatementFacade.processDownloadStatement(requestData);
        assertNotNull(result);
    }

    @Test(expected = IntegrationExecutionException.class)
    public void testCheckForErrorStatusStatusCode()
    {
        Mockito.when(outputResponse.getStatusCode()).thenReturn("500");
        outputResponse.setMessage("Internal server error");

        Mockito.when(responseData.getOutput()).thenReturn(outputResponse);
        Mockito.when(adnocDownloadStatementService.getDownloadStatementResponse(requestData)).thenReturn(responseData);

        final byte[] result = adnocDownloadStatementFacade.processDownloadStatement(requestData);

        assertNotNull(result);
    }
}
