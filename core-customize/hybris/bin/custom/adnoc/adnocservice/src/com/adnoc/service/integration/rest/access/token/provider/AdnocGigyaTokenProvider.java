package com.adnoc.service.integration.rest.access.token.provider;

import com.adnoc.service.rest.integration.GigyaTokenResponeData;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class AdnocGigyaTokenProvider
{
    private static final Logger LOG = LogManager.getLogger(AdnocGigyaTokenProvider.class);
    private static final String GRANT_TYPE_NONE = "none";
    private static final String EXPIRATION_TIME = "adnoc.gigya.session.expiration.time";

    private RestTemplate restTemplate;
    private ConfigurationService configurationService;

    public String getAccessToken(final ConsumedOAuthCredentialModel consumedOAuthCredentialModel)
    {
        LOG.info("appEvent=AdnocGigyaToken, getAccessToken method called with consumedOAuthCredentialModel");
        return refreshAccessToken(consumedOAuthCredentialModel);
    }

    private String refreshAccessToken(final ConsumedOAuthCredentialModel credentialModel)
    {
        LOG.info("appEvent=AdnocGigyaAccessToken, Refresh Access Token for clientID={}", credentialModel.getClientId());
        final String url = String.format("%s&userKey=%s&secret=%s&x_sessionExpiration=%s&grant_type=%s", credentialModel.getOAuthUrl(), credentialModel.getClientId(), credentialModel.getClientSecret(), getConfigurationService().getConfiguration().getInt(EXPIRATION_TIME), GRANT_TYPE_NONE);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final ResponseEntity<GigyaTokenResponeData> response = getRestTemplate().exchange(url, HttpMethod.GET, new HttpEntity<>(headers), GigyaTokenResponeData.class);
        final GigyaTokenResponeData gigyaTokenResponeData = response.getBody();
        if (Objects.nonNull(gigyaTokenResponeData) && StringUtils.isNotBlank(gigyaTokenResponeData.getAccess_token()))
        {
            LOG.debug("appEvent=AdnocGigyaAccessToken, Access token is received");
            return gigyaTokenResponeData.getAccess_token();
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

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }
}

