package com.adnoc.facades.downloadstatement;

import com.adnoc.service.data.AdnocDownloadStatementRequestData;

/**
 * Facade interface for handling download statement operations in the ADNOC system.
 * This interface defines methods for processing download statements based on request data.
 */
public interface AdnocDownloadStatementFacade
{
    /**
     * Processes a download statement request and returns the corresponding byte array.
     *
     * @param requestData the request data containing details for the download statement
     * @return a byte array representing the processed download statement
     */
    byte[] processDownloadStatement(AdnocDownloadStatementRequestData requestData);
}
