package com.adnoc.service.soa;

import com.adnoc.service.data.AdnocDownloadStatementRequestData;
import com.adnoc.service.data.AdnocDownloadStatementResponseData;

/**
 * Service interface for handling ADNOC statement download operations.
 * * @param adnocDownloadStatementRequestData The request data containing parameters for statement download
 * * @return AdnocDownloadStatementResponseData containing the download response information
 */
public interface AdnocDownloadStatementService
{
    /**
     * Retrieves the download statement response based on the provided request data.
     *
     * @param adnocDownloadStatementRequestData The request data containing parameters for statement download
     * @return AdnocDownloadStatementResponseData containing the download response information
     */
    AdnocDownloadStatementResponseData getDownloadStatementResponse(AdnocDownloadStatementRequestData adnocDownloadStatementRequestData);
}
