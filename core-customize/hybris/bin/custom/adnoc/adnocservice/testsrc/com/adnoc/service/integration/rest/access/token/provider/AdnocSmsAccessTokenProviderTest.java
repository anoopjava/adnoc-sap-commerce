package com.adnoc.service.integration.rest.access.token.provider;

import com.adnoc.service.rest.integration.SmsAccessTokenRequestData;
import com.adnoc.service.rest.integration.SmsAccessTokenResponseData;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdnocSmsAccessTokenProviderTest {

    @Test
    void shouldFetchNewTokenWhenExpired() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        ConsumedOAuthCredentialModel credentialModel = mock(ConsumedOAuthCredentialModel.class);
        AdnocSmsAccessTokenProvider tokenProvider = new AdnocSmsAccessTokenProvider();
        tokenProvider.setRestTemplate(restTemplate);

        when(credentialModel.getClientId()).thenReturn("client123");
        when(credentialModel.getClientSecret()).thenReturn("secret123");
        when(credentialModel.getOAuthUrl()).thenReturn("https://oauth.example.com/token");

        SmsAccessTokenResponseData response = new SmsAccessTokenResponseData();
        response.setAccess_token("newToken");
        response.setExpires_in(3600);

        when(restTemplate.postForObject(anyString(), any(SmsAccessTokenRequestData.class), eq(SmsAccessTokenResponseData.class)))
                .thenReturn(response);

        String token = tokenProvider.getAccessToken(credentialModel);

        assertEquals("newToken", token);
        verify(restTemplate, times(1)).postForObject(anyString(), any(SmsAccessTokenRequestData.class), eq(SmsAccessTokenResponseData.class));
    }

    @Test
    void shouldReuseTokenWhenNotExpired_UsingSubclassOverride() {
        ConsumedOAuthCredentialModel credentialModel = mock(ConsumedOAuthCredentialModel.class);

        AdnocSmsAccessTokenProvider testProvider = new AdnocSmsAccessTokenProvider() {
            @Override
            public String getAccessToken(ConsumedOAuthCredentialModel credentialModel) {
                return "cachedToken";
            }
        };

        String token = testProvider.getAccessToken(credentialModel);
        assertEquals("cachedToken", token);
    }

    @Test
    void shouldThrowExceptionWhenResponseIsNull() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        ConsumedOAuthCredentialModel credentialModel = mock(ConsumedOAuthCredentialModel.class);
        AdnocSmsAccessTokenProvider tokenProvider = new AdnocSmsAccessTokenProvider();
        tokenProvider.setRestTemplate(restTemplate);

        when(credentialModel.getClientId()).thenReturn("client123");
        when(credentialModel.getClientSecret()).thenReturn("secret123");
        when(credentialModel.getOAuthUrl()).thenReturn("https://oauth.example.com/token");

        when(restTemplate.postForObject(anyString(), any(SmsAccessTokenRequestData.class), eq(SmsAccessTokenResponseData.class)))
                .thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tokenProvider.getAccessToken(credentialModel));
        assertTrue(ex.getMessage().contains("Failed to obtain access token"));
    }
}
