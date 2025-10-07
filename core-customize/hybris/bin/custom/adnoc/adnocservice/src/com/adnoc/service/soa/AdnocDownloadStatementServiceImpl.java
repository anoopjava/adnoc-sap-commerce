package com.adnoc.service.soa;

import com.adnoc.service.data.AdnocDownloadStatementRequestData;
import com.adnoc.service.data.AdnocDownloadStatementResponseData;
import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocDownloadStatementServiceImpl implements AdnocDownloadStatementService
{
    private static final Logger LOG = LogManager.getLogger(AdnocDownloadStatementServiceImpl.class);
    public static final String ADNOC_DOWNLOADSTATMENT_GATEWAY_DESTINATION = "adnocDownloadStatementDestination";
    public static final String ADNOC_DOWNLOADSTATEMENT_DESTINATION_TARGET = "adnoc-donwloadstatement-destination-target";

    private AdnocRestIntegrationService adnocRestIntegrationService;

    /**
     * @param adnocDownloadStatementRequestData
     * @return
     */
    @Override
    public AdnocDownloadStatementResponseData getDownloadStatementResponse(final AdnocDownloadStatementRequestData adnocDownloadStatementRequestData)
    {
        LOG.info("appEvent=AdnocDownloadStatement, received download statement request for: {}", adnocDownloadStatementRequestData.getB2bUnitUid());

        final AdnocDownloadStatementResponseData adnocDownloadStatementResponseData = getAdnocRestIntegrationService().restIntegration(ADNOC_DOWNLOADSTATMENT_GATEWAY_DESTINATION, ADNOC_DOWNLOADSTATEMENT_DESTINATION_TARGET, adnocDownloadStatementRequestData, AdnocDownloadStatementResponseData.class);

        LOG.debug("appEven=AdnocDownloadStatement, successfully received download statement response for:{}", adnocDownloadStatementRequestData.getB2bUnitUid());
        return adnocDownloadStatementResponseData;
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
