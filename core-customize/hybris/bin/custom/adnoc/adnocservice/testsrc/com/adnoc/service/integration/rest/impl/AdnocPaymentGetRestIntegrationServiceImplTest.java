package com.adnoc.service.integration.rest.impl;

import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AdnocPaymentGetRestIntegrationServiceImplTest
{

    private final String destinationId = "cardService";
    private final String destinationTarget = "adnocTarget";

    @Test
    void testRestIntegration_successfulCall()
    {
        final DestinationService destinationService = mock(DestinationService.class);
        final RestTemplate restTemplate = mock(RestTemplate.class);
        final ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        final ConsumedOAuthCredentialModel credentialModel = mock(ConsumedOAuthCredentialModel.class);

        final AdnocPaymentGetRestIntegrationServiceImpl integrationService = new AdnocPaymentGetRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setRestTemplate(restTemplate);

        final String request = "paymentId123";
        final String baseUrl = "https://api.adnoc.com/payment";
        final String fullUrl = baseUrl + "/" + request;
        final String expectedResponse = "payment-details";

        when(destinationService.getDestinationByIdAndByDestinationTargetId(destinationId, destinationTarget))
                .thenReturn(destinationModel);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(destinationModel.getUrl()).thenReturn(baseUrl);
        when(credentialModel.getClientId()).thenReturn("client");
        when(credentialModel.getClientSecret()).thenReturn("secret");

        final ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(eq(fullUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        final String actualResponse = integrationService.restIntegration(destinationId, destinationTarget, request, String.class);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate, times(1)).exchange(eq(fullUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void testRestIntegration_missingDestination()
    {
        final DestinationService destinationService = mock(DestinationService.class);
        final RestTemplate restTemplate = mock(RestTemplate.class);

        final AdnocPaymentGetRestIntegrationServiceImpl integrationService = new AdnocPaymentGetRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setRestTemplate(restTemplate);

        when(destinationService.getDestinationByIdAndByDestinationTargetId(destinationId, destinationTarget))
                .thenReturn(null);

        final RuntimeException exception = assertThrows(RuntimeException.class, () ->
                integrationService.restIntegration(destinationId, destinationTarget, "requestObj", String.class));

        assertTrue(exception.getMessage().contains("Consumed destination not defined"));
    }

    @Test
    void testRestIntegration_invalidCredentialType()
    {
        final DestinationService destinationService = mock(DestinationService.class);
        final RestTemplate restTemplate = mock(RestTemplate.class);
        final ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        final AbstractCredentialModel mockCredential = mock(AbstractCredentialModel.class);

        final AdnocPaymentGetRestIntegrationServiceImpl integrationService = new AdnocPaymentGetRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setRestTemplate(restTemplate);

        when(destinationService.getDestinationByIdAndByDestinationTargetId(destinationId, destinationTarget))
                .thenReturn(destinationModel);
        when(destinationModel.getCredential()).thenReturn(mockCredential);

        final RuntimeException exception = assertThrows(RuntimeException.class, () ->
                integrationService.restIntegration(destinationId, destinationTarget, "requestObj", String.class));

        assertTrue(exception.getMessage().contains("is not configured with credential of type"));
    }

    @Test
    void testRestIntegration_exceptionDuringCall()
    {
        final DestinationService destinationService = mock(DestinationService.class);
        final RestTemplate restTemplate = mock(RestTemplate.class);
        final ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        final ConsumedOAuthCredentialModel credentialModel = mock(ConsumedOAuthCredentialModel.class);

        final AdnocPaymentGetRestIntegrationServiceImpl integrationService = new AdnocPaymentGetRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setRestTemplate(restTemplate);

        when(destinationService.getDestinationByIdAndByDestinationTargetId(destinationId, destinationTarget))
                .thenReturn(destinationModel);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(destinationModel.getUrl()).thenReturn("https://api.adnoc.com/payment");
        when(credentialModel.getClientId()).thenReturn("client");
        when(credentialModel.getClientSecret()).thenReturn("secret");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        final RuntimeException exception = assertThrows(RuntimeException.class, () ->
                integrationService.restIntegration(destinationId, destinationTarget, "paymentId123", String.class));

        assertTrue(exception.getMessage().contains("Service unavailable"));
    }
}
