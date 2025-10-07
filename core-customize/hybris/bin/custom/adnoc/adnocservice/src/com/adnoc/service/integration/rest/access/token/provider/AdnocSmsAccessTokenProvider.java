package com.adnoc.service.integration.rest.access.token.provider;

import com.adnoc.service.rest.integration.SmsAccessTokenRequestData;
import com.adnoc.service.rest.integration.SmsAccessTokenResponseData;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Objects;

public class AdnocSmsAccessTokenProvider
{
    private static final Logger LOG = LogManager.getLogger(AdnocSmsAccessTokenProvider.class);

    private RestTemplate restTemplate;
    private String accessToken;
    private Instant tokenExpirationTime;

    public String getAccessToken(final ConsumedOAuthCredentialModel consumedOAuthCredentialModel)
    {
        LOG.info("appEvent=AdnocSmsAccessToken, getAccessToken method called with consumedOAuthCredentialModel");

        if (isTokenExpired())
        {
            LOG.debug("appEvent=AdnocSmsAccessToken,token is expired. Refreshing access token");
            refreshAccessToken(consumedOAuthCredentialModel);
        }
        return accessToken;
    }

    private boolean isTokenExpired()
    {
        return Objects.isNull(accessToken) || Instant.now().isAfter(tokenExpirationTime);
    }

    private void refreshAccessToken(final ConsumedOAuthCredentialModel consumedOAuthCredentialModel)
    {
        LOG.info("appEvent=AdnocSmsAccessToken,refresh Access Token for clientID={}", consumedOAuthCredentialModel.getClientId());
        final SmsAccessTokenRequestData smsAccessTokenRequestData = new SmsAccessTokenRequestData();
        smsAccessTokenRequestData.setClient_id(consumedOAuthCredentialModel.getClientId());
        smsAccessTokenRequestData.setClient_secret(consumedOAuthCredentialModel.getClientSecret());
        smsAccessTokenRequestData.setGrant_type("client_credentials");

        // Make the POST request to get the new token
        final SmsAccessTokenResponseData smsAccessTokenResponseData = getRestTemplate().postForObject(consumedOAuthCredentialModel.getOAuthUrl(), smsAccessTokenRequestData, SmsAccessTokenResponseData.class);

        if (Objects.nonNull(smsAccessTokenResponseData))
        {
            LOG.debug("appEvent=AdnocSmsAccessToken, Access token is received");
            accessToken = smsAccessTokenResponseData.getAccess_token();
            tokenExpirationTime = Instant.now().plusSeconds(smsAccessTokenResponseData.getExpires_in());
        }
        else
        {
            throw new RuntimeException("Failed to obtain access token.");
        }
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

