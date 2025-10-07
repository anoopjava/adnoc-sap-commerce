package com.adnoc.service.gigya;

import com.adnoc.service.gigya.login.verification.AdnocGigyaLoginVerificationRequestData;
import com.adnoc.service.gigya.login.verification.AdnocGigyaLoginVerificationResponseData;

/**
 * Service interface for verifying Gigya login tokens.
 */
public interface AdnocGigyaLoginTokenVerificationService
{
    /**
     * Verifies a Gigya login token using the provided request data.
     *
     * @param gigyaLoginVerificationRequestData the request data containing the Gigya login token
     * @return the response data containing the verification result
     */
    AdnocGigyaLoginVerificationResponseData verifyGigyaLoginToken(AdnocGigyaLoginVerificationRequestData gigyaLoginVerificationRequestData);
}
