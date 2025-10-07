package com.adnoc.service.gigya.impl;

import com.adnoc.service.gigya.AdnocGigyaLoginTokenVerificationService;
import com.adnoc.service.gigya.login.verification.AdnocGigyaLoginVerificationRequestData;
import com.adnoc.service.gigya.login.verification.AdnocGigyaLoginVerificationResponseData;
import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AdnocGigyaLoginTokenVerificationServiceImpl implements AdnocGigyaLoginTokenVerificationService
{
    private static final Logger LOG = LogManager.getLogger(AdnocGigyaLoginTokenVerificationServiceImpl.class);

    public static final String GIGYA_LOGIN_TOKEN_VERIFICATION_DESTINATION = "gigyaLoginTokenVerificationDestination";
    public static final String GIGYA_LOGIN_TOKEN_VERIFICATION_DESTINATION_TARGET = "adnoc-gigya-login-verification-destination-target";

    private AdnocRestIntegrationService adnocGigyaLoginRestIntegrationService;

    @Override
    public AdnocGigyaLoginVerificationResponseData verifyGigyaLoginToken(final AdnocGigyaLoginVerificationRequestData gigyaLoginVerificationRequestData)
    {
        LOG.info("appEvent=AdnocGigyaLoginTokenVerificationServiceImpl, Verifying Gigya login token for UID={}", gigyaLoginVerificationRequestData.getUID());
        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("UID", gigyaLoginVerificationRequestData.getUID());
        formData.add("UIDSignature", gigyaLoginVerificationRequestData.getUIDSignature());
        formData.add("signatureTimestamp", gigyaLoginVerificationRequestData.getSignatureTimestamp());
        return getAdnocGigyaLoginRestIntegrationService().restIntegration(GIGYA_LOGIN_TOKEN_VERIFICATION_DESTINATION, GIGYA_LOGIN_TOKEN_VERIFICATION_DESTINATION_TARGET,
                formData, AdnocGigyaLoginVerificationResponseData.class);
    }

    protected AdnocRestIntegrationService getAdnocGigyaLoginRestIntegrationService()
    {
        return adnocGigyaLoginRestIntegrationService;
    }

    public void setAdnocGigyaLoginRestIntegrationService(final AdnocRestIntegrationService adnocGigyaLoginRestIntegrationService)
    {
        this.adnocGigyaLoginRestIntegrationService = adnocGigyaLoginRestIntegrationService;
    }
}
