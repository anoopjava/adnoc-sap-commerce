package com.adnoc.service.integration.rest.impl;

import com.adnoc.service.integration.rest.access.token.provider.AdnocSmsAccessTokenProvider;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdnocSmsRestIntegrationServiceImplTest {

    @Test
    public void testRestIntegration_successfulRequest() {
        MockitoAnnotations.openMocks(this);

        AdnocSmsRestIntegrationServiceImpl restIntegrationService = new AdnocSmsRestIntegrationServiceImpl();

        DestinationService destinationService = mock(DestinationService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        AdnocSmsAccessTokenProvider accessTokenProvider = mock(AdnocSmsAccessTokenProvider.class);
        ConsumedOAuthCredentialModel credentialModel = mock(ConsumedOAuthCredentialModel.class);
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);

        restIntegrationService.setDestinationService(destinationService);
        restIntegrationService.setRestTemplate(restTemplate);
        restIntegrationService.setAdnocSmsAccessTokenProvider(accessTokenProvider);

        String token = "test-access-token";
        String destinationId = "smsDest";
        String destinationTarget = "smsTarget";
        String response = "Message Sent";
        String url = "http://api.sms.com/send";

        when(destinationService.getDestinationByIdAndByDestinationTargetId(destinationId, destinationTarget))
                .thenReturn(destinationModel);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(destinationModel.getUrl()).thenReturn(url);
        when(accessTokenProvider.getAccessToken(credentialModel)).thenReturn(token);
        when(restTemplate.postForObject(eq(url), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        String result = restIntegrationService.restIntegration(destinationId, destinationTarget, "somePayload", String.class);

        assertEquals(response, result);

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForObject(eq(url), entityCaptor.capture(), eq(String.class));
        HttpHeaders headers = entityCaptor.getValue().getHeaders();

        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals("Bearer " + token, headers.getFirst("Authorization"));
    }

    @Test
    public void testRestIntegration_missingDestination_throwsException() {
        MockitoAnnotations.openMocks(this);

        AdnocSmsRestIntegrationServiceImpl restIntegrationService = new AdnocSmsRestIntegrationServiceImpl();

        DestinationService destinationService = mock(DestinationService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        AdnocSmsAccessTokenProvider accessTokenProvider = mock(AdnocSmsAccessTokenProvider.class);

        restIntegrationService.setDestinationService(destinationService);
        restIntegrationService.setRestTemplate(restTemplate);
        restIntegrationService.setAdnocSmsAccessTokenProvider(accessTokenProvider);

        when(destinationService.getDestinationByIdAndByDestinationTargetId(any(), any()))
                .thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                restIntegrationService.restIntegration("badId", "badTarget", "payload", String.class));

        assertTrue(ex.getMessage().contains("Consumed destination not defined"));
    }

    @Test
    public void testRestIntegration_throwsForNonOAuthCredential() {
        MockitoAnnotations.openMocks(this);

        AdnocSmsRestIntegrationServiceImpl restIntegrationService = new AdnocSmsRestIntegrationServiceImpl();

        DestinationService destinationService = mock(DestinationService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        AdnocSmsAccessTokenProvider accessTokenProvider = mock(AdnocSmsAccessTokenProvider.class);
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);

        restIntegrationService.setDestinationService(destinationService);
        restIntegrationService.setRestTemplate(restTemplate);
        restIntegrationService.setAdnocSmsAccessTokenProvider(accessTokenProvider);

        when(destinationService.getDestinationByIdAndByDestinationTargetId(any(), any()))
                .thenReturn(destinationModel);
        when(destinationModel.getCredential()).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                restIntegrationService.restIntegration("noCred", "target", "payload", String.class));

        assertTrue(ex.getMessage().contains("DestinationId="));
    }

    @Test
    public void testRestIntegration_restCallFails_throwsException() {
        MockitoAnnotations.openMocks(this);

        AdnocSmsRestIntegrationServiceImpl restIntegrationService = new AdnocSmsRestIntegrationServiceImpl();

        DestinationService destinationService = mock(DestinationService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        AdnocSmsAccessTokenProvider accessTokenProvider = mock(AdnocSmsAccessTokenProvider.class);
        ConsumedOAuthCredentialModel credentialModel = mock(ConsumedOAuthCredentialModel.class);
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);

        restIntegrationService.setDestinationService(destinationService);
        restIntegrationService.setRestTemplate(restTemplate);
        restIntegrationService.setAdnocSmsAccessTokenProvider(accessTokenProvider);

        when(destinationService.getDestinationByIdAndByDestinationTargetId(any(), any())).thenReturn(destinationModel);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(destinationModel.getUrl()).thenReturn("http://mock.url");
        when(accessTokenProvider.getAccessToken(credentialModel)).thenReturn("abc-token");
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Connection refused"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                restIntegrationService.restIntegration("id", "target", "payload", String.class));

        assertTrue(ex.getCause() instanceof RestClientException);
    }
}
