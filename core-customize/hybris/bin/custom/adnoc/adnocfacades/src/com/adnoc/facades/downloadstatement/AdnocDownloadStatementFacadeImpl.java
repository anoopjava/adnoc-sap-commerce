package com.adnoc.facades.downloadstatement;

import com.adnoc.service.data.AdnocDownloadStatementRequestData;
import com.adnoc.service.data.AdnocDownloadStatementResponseData;
import com.adnoc.service.data.response.AdnocDownloadStatementOutputResponse;
import com.adnoc.service.soa.AdnocDownloadStatementService;
import de.hybris.platform.integrationservices.util.timeout.IntegrationExecutionException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;
import java.util.Objects;

public class AdnocDownloadStatementFacadeImpl implements AdnocDownloadStatementFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocDownloadStatementFacadeImpl.class);

    private AdnocDownloadStatementService adnocDownloadStatementService;

    /**
     * @param adnocDownloadStatementRequestData
     * @return
     */
    @Override
    public byte[] processDownloadStatement(final AdnocDownloadStatementRequestData adnocDownloadStatementRequestData)
    {
        LOG.info("appEvent=AdnocDownloadStatement, processDownloadStatement method being called");
        final AdnocDownloadStatementResponseData adnocDownloadStatementResponseData = adnocDownloadStatementService.getDownloadStatementResponse(adnocDownloadStatementRequestData);
        if (Objects.isNull(adnocDownloadStatementResponseData.getOutput()))
        {
            throw new IntegrationExecutionException("No Record Found for given Filter.", new RuntimeException());
        }
        final AdnocDownloadStatementOutputResponse adnocDownloadStatementOutputResponse = adnocDownloadStatementResponseData.getOutput();
        checkForErrorStatus(adnocDownloadStatementOutputResponse);
        final String statementOfAccountB64 = adnocDownloadStatementOutputResponse.getStatementOfAccountB64();
        LOG.info("appEvent=AdnocDownloadStatement,Decoding Base64 to PDF.");
        return decodeBase64ToPdf(statementOfAccountB64);
    }

    private void checkForErrorStatus(final AdnocDownloadStatementOutputResponse response)
    {
        LOG.info("appEvent=AdnocDownloadStatement, checkForErrorStatus method called");
        final String statusCode = response.getStatusCode();
        final String message = response.getMessage();
        if (StringUtils.isNotBlank(statusCode) && !StringUtils.equals(statusCode, "200"))
        {
            throw new IntegrationExecutionException(
                    String.format("Integration Failed with status code=%s and message=%s.", statusCode, message),
                    new RuntimeException()
            );
        }
        final String statementOfAccountB64 = response.getStatementOfAccountB64();
        if (StringUtils.isBlank(statementOfAccountB64))
        {
            throw new IntegrationExecutionException("No Record Found for given Filter.", new RuntimeException());
        }
    }


    private byte[] decodeBase64ToPdf(final String base64Pdf)
    {
        try
        {
            return Base64.getDecoder().decode(base64Pdf);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException("Invalid Base64 encoded PDF string", e);
        }
    }


    protected AdnocDownloadStatementService getAdnocDownloadStatementService()
    {
        return adnocDownloadStatementService;
    }

    public void setAdnocDownloadStatementService(final AdnocDownloadStatementService adnocDownloadStatementService)
    {
        this.adnocDownloadStatementService = adnocDownloadStatementService;
    }

}
