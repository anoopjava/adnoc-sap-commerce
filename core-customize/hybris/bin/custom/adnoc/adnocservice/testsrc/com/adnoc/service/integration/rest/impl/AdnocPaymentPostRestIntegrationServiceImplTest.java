package com.adnoc.service.integration.rest.impl;

import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AdnocPaymentPostRestIntegrationServiceImplTest
{

    @Test
    void testRestIntegration_successfulCall()
    {
        DestinationService destinationService = mock(DestinationService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        ConsumedOAuthCredentialModel credentialModel = mock(ConsumedOAuthCredentialModel.class);

        AdnocPaymentPostRestIntegrationServiceImpl integrationService = new AdnocPaymentPostRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setRestTemplate(restTemplate);

        String destinationId = "initiatePayment";
        String destinationTarget = "adnocTarget";
        String requestPayload = "paymentRequest";
        String expectedResponse = "paymentResponse";

        when(destinationService.getDestinationByIdAndByDestinationTargetId(destinationId, destinationTarget)).thenReturn(destinationModel);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(destinationModel.getUrl()).thenReturn("https://api.adnoc.com/payments");
        when(credentialModel.getClientId()).thenReturn("clientId");
        when(credentialModel.getClientSecret()).thenReturn("secret");

        when(restTemplate.postForObject(eq("https://api.adnoc.com/payments"), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        String result = integrationService.restIntegration(destinationId, destinationTarget, requestPayload, String.class);

        assertEquals(expectedResponse, result);
        verify(restTemplate, times(1)).postForObject(eq("https://api.adnoc.com/payments"), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void testRestIntegration_missingDestination_returnsNull()
    {
        DestinationService destinationService = mock(DestinationService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);

        AdnocPaymentPostRestIntegrationServiceImpl integrationService = new AdnocPaymentPostRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setRestTemplate(restTemplate);

        when(destinationService.getDestinationByIdAndByDestinationTargetId("any", "any")).thenReturn(null);

        String result = integrationService.restIntegration("any", "any", "request", String.class);

        assertNull(result);
    }

    @Test
    void testRestIntegration_invalidCredentialType_throwsException()
    {
        DestinationService destinationService = mock(DestinationService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        AbstractCredentialModel credentialMock = mock(AbstractCredentialModel.class);

        AdnocPaymentPostRestIntegrationServiceImpl integrationService = new AdnocPaymentPostRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setRestTemplate(restTemplate);

        when(destinationService.getDestinationByIdAndByDestinationTargetId("invalid", "target")).thenReturn(destinationModel);
        when(destinationModel.getCredential()).thenReturn(credentialMock);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                integrationService.restIntegration("invalid", "target", "request", String.class));

        assertTrue(ex.getMessage().contains("not configured with credential of type"));
    }

    @Test
    void testRestIntegration_restTemplateThrowsException()
    {
        DestinationService destinationService = mock(DestinationService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        ConsumedDestinationModel destinationModel = mock(ConsumedDestinationModel.class);
        ConsumedOAuthCredentialModel credentialModel = mock(ConsumedOAuthCredentialModel.class);

        AdnocPaymentPostRestIntegrationServiceImpl integrationService = new AdnocPaymentPostRestIntegrationServiceImpl();
        integrationService.setDestinationService(destinationService);
        integrationService.setRestTemplate(restTemplate);

        when(destinationService.getDestinationByIdAndByDestinationTargetId("fail", "target")).thenReturn(destinationModel);
        when(destinationModel.getCredential()).thenReturn(credentialModel);
        when(destinationModel.getUrl()).thenReturn("https://api.adnoc.com/payments");
        when(credentialModel.getClientId()).thenReturn("client");
        when(credentialModel.getClientSecret()).thenReturn("secret");

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Service timeout"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                integrationService.restIntegration("fail", "target", "request", String.class));

        assertTrue(ex.getMessage().contains("Service timeout"));
    }
}
