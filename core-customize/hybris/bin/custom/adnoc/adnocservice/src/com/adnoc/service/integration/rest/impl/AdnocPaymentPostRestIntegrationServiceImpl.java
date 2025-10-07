package com.adnoc.service.integration.rest.impl;

import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import com.adnoc.service.integration.rest.util.AdnocJsonPrinter;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class AdnocPaymentPostRestIntegrationServiceImpl implements AdnocRestIntegrationService
{
    private static final Logger LOG = LogManager.getLogger(AdnocPaymentPostRestIntegrationServiceImpl.class);

    private DestinationService destinationService;
    private RestTemplate restTemplate;

    @Override
    public <T> T restIntegration(final String destinationId, final String destinationTarget, final Object requestObject, final Class<T> responseType)
    {
        final ConsumedDestinationModel consumedDestinationModel = (ConsumedDestinationModel) getDestinationService().getDestinationByIdAndByDestinationTargetId(destinationId, destinationTarget);
        if (Objects.isNull(consumedDestinationModel))
        {
            return null;
        }
        try
        {
            if (consumedDestinationModel.getCredential() instanceof final ConsumedOAuthCredentialModel consumedOAuthCredentialModel)
            {
                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBasicAuth(consumedOAuthCredentialModel.getClientId(), consumedOAuthCredentialModel.getClientSecret());
                final HttpEntity<Object> entity = new HttpEntity<>(requestObject, headers);
                final T responseObject = restTemplate.postForObject(consumedDestinationModel.getUrl(), entity, responseType);
                LOG.info("appEvent=AdnocRestIntegration, request={} and response={}.", AdnocJsonPrinter.toJson(requestObject),
                        AdnocJsonPrinter.toJson(responseObject));
                return responseObject;
            }
            throw new RuntimeException(String.format("DestinationId=%s is not configured with credential of type ConsumedOAuthCredentialModel.", destinationId));
        }
        catch (final Exception exception)
        {
            LOG.error("An exception occurred while calling RestIntegration with cause" + exception);
            throw new RuntimeException(exception);
        }
    }

    protected DestinationService getDestinationService()
    {
        return destinationService;
    }

    public void setDestinationService(final DestinationService destinationService)
    {
        this.destinationService = destinationService;
    }

    protected RestTemplate getRestTemplate()
    {
        return restTemplate;
    }

    public void setRestTemplate(final RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }
}

