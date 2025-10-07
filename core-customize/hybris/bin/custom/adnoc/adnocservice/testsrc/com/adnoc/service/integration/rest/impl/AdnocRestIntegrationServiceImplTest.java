package com.adnoc.service.integration.rest.impl;

import com.adnoc.service.integration.rest.impl.AdnocRestIntegrationServiceImpl;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.utils.DefaultRestTemplateFactory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdnocRestIntegrationServiceImplTest {

    @Test
    void testRestIntegration_successfulResponse() throws CredentialException {
        // Arrange
        DestinationService destinationService = mock(DestinationService.class);
        DefaultRestTemplateFactory restTemplateFactory = mock(DefaultRestTemplateFactory.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);

        AdnocRestIntegrationServiceImpl integrationService = new AdnocRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setDefaultRestTemplateFactory(restTemplateFactory);

        String destinationId = "testDest";
        String destinationTarget = "target";
        String requestBody = "{\"key\":\"value\"}";
        String url = "http://mock.url";
        String expectedResponse = "SuccessResponse";

        when(destinationService.getDestinationByIdAndByDestinationTargetId(destinationId, destinationTarget))
                .thenReturn(destinationModel);
        when(destinationModel.getUrl()).thenReturn(url);
        when(destinationModel.getId()).thenReturn(destinationId);
        when(restTemplateFactory.getRestTemplate(destinationModel)).thenReturn(restTemplate);
        when(restTemplate.postForObject(eq(url), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        // Act
        String actual = integrationService.restIntegration(destinationId, destinationTarget, requestBody, String.class);

        // Assert
        assertEquals(expectedResponse, actual);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForObject(eq(url), captor.capture(), eq(String.class));
        HttpHeaders headers = captor.getValue().getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void testRestIntegration_missingDestination_throwsException() {
        DestinationService destinationService = mock(DestinationService.class);
        DefaultRestTemplateFactory restTemplateFactory = mock(DefaultRestTemplateFactory.class);

        AdnocRestIntegrationServiceImpl integrationService = new AdnocRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setDefaultRestTemplateFactory(restTemplateFactory);

        when(destinationService.getDestinationByIdAndByDestinationTargetId(any(), any()))
                .thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                integrationService.restIntegration("noId", "noTarget", "body", String.class));

        assertTrue(ex.getMessage().contains("Consumed destination not defined"));
    }

    @Test
    void testRestIntegration_restTemplateThrowsException() throws CredentialException {
        DestinationService destinationService = mock(DestinationService.class);
        DefaultRestTemplateFactory restTemplateFactory = mock(DefaultRestTemplateFactory.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);

        AdnocRestIntegrationServiceImpl integrationService = new AdnocRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setDefaultRestTemplateFactory(restTemplateFactory);

        when(destinationService.getDestinationByIdAndByDestinationTargetId(any(), any()))
                .thenReturn(destinationModel);
        when(destinationModel.getUrl()).thenReturn("http://mock.url");
        when(destinationModel.getId()).thenReturn("mockId");
        when(restTemplateFactory.getRestTemplate(destinationModel)).thenReturn(restTemplate);

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Timeout"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                integrationService.restIntegration("id", "target", "body", String.class));
        assertTrue(ex.getMessage().contains("Timeout"));

        assertTrue(ex.getMessage().contains("Timeout"));
    }

    @Test
    void testRestIntegration_throwsCredentialException() throws CredentialException {
        DestinationService destinationService = mock(DestinationService.class);
        DefaultRestTemplateFactory restTemplateFactory = mock(DefaultRestTemplateFactory.class);
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);

        AdnocRestIntegrationServiceImpl integrationService = new AdnocRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setDefaultRestTemplateFactory(restTemplateFactory);

        when(destinationService.getDestinationByIdAndByDestinationTargetId(any(), any()))
                .thenReturn(destinationModel);
        when(restTemplateFactory.getRestTemplate(destinationModel)).thenThrow(new CredentialException("Bad credentials"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                integrationService.restIntegration("id", "target", "body", String.class));

        assertTrue(ex.getCause() instanceof CredentialException);
    }
}
