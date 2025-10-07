package com.adnoc.facades.gigya.impl;

import com.adnoc.facades.gigya.AdnocGigyaLoginTokenVerificationFacade;
import com.adnoc.service.gigya.AdnocGigyaLoginTokenVerificationService;
import com.adnoc.service.gigya.login.verification.AdnocGigyaLoginVerificationRequestData;
import com.adnoc.service.gigya.login.verification.AdnocGigyaLoginVerificationResponseData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocGigyaLoginTokenVerificationFacadeImpl implements AdnocGigyaLoginTokenVerificationFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocGigyaLoginTokenVerificationFacadeImpl.class);
    private AdnocGigyaLoginTokenVerificationService adnocGigyaLoginTokenVerificationService;

    @Override
    public AdnocGigyaLoginVerificationResponseData verifyGigyaLoginToken(final AdnocGigyaLoginVerificationRequestData gigyaLoginVerificationRequestWsDTO)
    {
        LOG.info("appEvent=AdnocGigyaLoginTokenVerificationFacadeImpl, Verifying Gigya login token for UID={}", gigyaLoginVerificationRequestWsDTO.getUID());
        return getAdnocGigyaLoginTokenVerificationService().verifyGigyaLoginToken(gigyaLoginVerificationRequestWsDTO);
    }

    protected AdnocGigyaLoginTokenVerificationService getAdnocGigyaLoginTokenVerificationService()
    {
        return adnocGigyaLoginTokenVerificationService;
    }

    public void setAdnocGigyaLoginTokenVerificationService(final AdnocGigyaLoginTokenVerificationService adnocGigyaLoginTokenVerificationService)
    {
        this.adnocGigyaLoginTokenVerificationService = adnocGigyaLoginTokenVerificationService;
    }
}
