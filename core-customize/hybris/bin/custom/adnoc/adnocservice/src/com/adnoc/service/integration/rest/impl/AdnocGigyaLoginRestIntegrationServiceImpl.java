package com.adnoc.service.integration.rest.impl;

import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import com.adnoc.service.integration.rest.access.token.provider.AdnocGigyaTokenProvider;
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

public class AdnocGigyaLoginRestIntegrationServiceImpl implements AdnocRestIntegrationService
{
    private static final Logger LOG = LogManager.getLogger(AdnocGigyaLoginRestIntegrationServiceImpl.class);
    private DestinationService destinationService;
    private RestTemplate restTemplate;
    private AdnocGigyaTokenProvider adnocGigyaTokenProvider;

    @Override
    public <T> T restIntegration(final String destinationId, final String destinationTarget, final Object requestObject,
                                 final Class<T> responseType)
    {
        final ConsumedDestinationModel consumedDestinationModel = (ConsumedDestinationModel) getDestinationService().getDestinationByIdAndByDestinationTargetId(destinationId, destinationTarget);
        if (Objects.isNull(consumedDestinationModel))
        {
            throw new RuntimeException("Consumed destination not defined for " + destinationId);
        }

        try
        {
            if (consumedDestinationModel.getCredential() instanceof final ConsumedOAuthCredentialModel consumedOAuthCredentialModel)
            {
                LOG.info("appEvent=AdnocGigyaRestIntegration, Calling RestIntegration with destinationId={}", destinationId);
                final String accessToken = getAdnocGigyaTokenProvider().getAccessToken(consumedOAuthCredentialModel);
                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.setBearerAuth(accessToken);
                final HttpEntity<Object> entity = new HttpEntity<>(requestObject, headers);
                final T responseObject = getRestTemplate().postForObject(consumedDestinationModel.getUrl(), entity, responseType);
                LOG.info("appEvent=AdnocGigyaRestIntegration, Successfully called RestIntegration with destinationId={}", destinationId);
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

    protected AdnocGigyaTokenProvider getAdnocGigyaTokenProvider()
    {
        return adnocGigyaTokenProvider;
    }

    public void setAdnocGigyaTokenProvider(final AdnocGigyaTokenProvider adnocGigyaTokenProvider)
    {
        this.adnocGigyaTokenProvider = adnocGigyaTokenProvider;
    }
}
