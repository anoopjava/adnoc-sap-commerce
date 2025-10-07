package com.adnoc.service.integration.rest;


/**
 * Interface that defines REST integration operations for ADNOC services.
 * Handles communication with external REST endpoints using specified destination configurations.
 */
public interface AdnocRestIntegrationService
{
    /**
     * Rest integration t.
     *
     * @param <T>               the type parameter
     * @param destinationId     the destination id
     * @param destinationTarget the destination target
     * @param requestBody       the request body
     * @param responseType      the response type
     * @return the t
     */
    <T extends Object> T restIntegration(String destinationId, String destinationTarget, Object requestBody, Class<T> responseType);
}
