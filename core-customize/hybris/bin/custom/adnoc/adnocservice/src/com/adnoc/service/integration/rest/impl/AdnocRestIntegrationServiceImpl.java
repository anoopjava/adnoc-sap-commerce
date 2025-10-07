package com.adnoc.service.integration.rest.impl;

import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import com.adnoc.service.integration.rest.util.AdnocJsonPrinter;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.utils.DefaultRestTemplateFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AdnocRestIntegrationServiceImpl implements AdnocRestIntegrationService
{
    private static final Logger LOG = LogManager.getLogger(AdnocRestIntegrationServiceImpl.class);

    private DestinationService destinationService;
    private DefaultRestTemplateFactory defaultRestTemplateFactory;
    private final Map<String, RestTemplate> destinationIdRestTemplateMap = new ConcurrentHashMap<>();
    private int connectTimeout;
    private int readTimeout;

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
            final RestTemplate restTemplate = getRestTemplate(consumedDestinationModel);
            final T responseObject = postAndGetResponse(requestObject, responseType, restTemplate, consumedDestinationModel);
            LOG.info("appEvent=AdnocRestIntegration, request={} and response={}.", AdnocJsonPrinter.toJson(requestObject),
                    AdnocJsonPrinter.toJson(responseObject));
            return responseObject;
        }
        catch (final Exception exception)
        {
            LOG.error("appEvent=AdnocRestIntegration, An exception occurred while calling RestIntegration with cause={}", ExceptionUtils.getRootCauseMessage(exception), exception);
            throw new RuntimeException(exception);
        }
    }

    private RestTemplate getRestTemplate(final ConsumedDestinationModel consumedDestinationModel)
    {
        return getDestinationIdRestTemplateMap().computeIfAbsent(consumedDestinationModel.getId(), consumedDestinationId -> {
            try
            {
                LOG.info("appEvent=AdnocRestIntegration, Creating new RestTemplate for destinationId={}", consumedDestinationId);
                final RestTemplate restTemplate = getDefaultRestTemplateFactory().getRestTemplate(consumedDestinationModel);
                final HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
                httpComponentsClientHttpRequestFactory.setConnectTimeout(connectTimeout);
                httpComponentsClientHttpRequestFactory.setReadTimeout(readTimeout);
                restTemplate.setRequestFactory(httpComponentsClientHttpRequestFactory);
                return restTemplate;
            }
            catch (final CredentialException credentialException)
            {
                LOG.error("appEvent=AdnocRestIntegration, An exception occurred while creating new RestTemplate for destinationId={} with cause={}", consumedDestinationId, ExceptionUtils.getRootCauseMessage(credentialException), credentialException);
                throw new RuntimeException("Failed to create RestTemplate for destinationId=" + consumedDestinationId, credentialException);
            }
        });
    }

    private <T> T postAndGetResponse(final Object requestBody, final Class<T> responseType,
                                     final RestTemplate restTemplate,
                                     final ConsumedDestinationModel consumedDestinationModel)
    {
        try
        {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            final HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);
            LOG.info("Rest integration with id={}, url={}.", consumedDestinationModel.getId(),
                    consumedDestinationModel.getUrl());
            return restTemplate.postForObject(consumedDestinationModel.getUrl(), request, responseType);
        }
        catch (final RestClientException restClientException)
        {
            LOG.error("appEvent={}, Rest integration failed with id={}, url={}.", consumedDestinationModel.getId(),
                    consumedDestinationModel.getUrl(), ExceptionUtils.getRootCauseMessage(restClientException));
            throw restClientException;
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

    protected DefaultRestTemplateFactory getDefaultRestTemplateFactory()
    {
        return defaultRestTemplateFactory;
    }

    public void setDefaultRestTemplateFactory(final DefaultRestTemplateFactory defaultRestTemplateFactory)
    {
        this.defaultRestTemplateFactory = defaultRestTemplateFactory;
    }

    protected Map<String, RestTemplate> getDestinationIdRestTemplateMap()
    {
        return destinationIdRestTemplateMap;
    }

    protected int getConnectTimeout()
    {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    protected int getReadTimeout()
    {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout)
    {
        this.readTimeout = readTimeout;
    }
}
