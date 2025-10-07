package com.adnoc.controllers;

import com.adnoc.facades.gigya.AdnocGigyaLoginTokenVerificationFacade;
import com.adnoc.service.gigya.login.verification.AdnocGigyaLoginTokenVerificationResponseWsDTO;
import com.adnoc.service.gigya.login.verification.AdnocGigyaLoginVerificationRequestData;
import com.adnoc.service.gigya.login.verification.AdnocGigyaLoginVerificationRequestWsDTO;
import com.adnoc.service.gigya.login.verification.AdnocGigyaLoginVerificationResponseData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Adnoc B2B Config Controller
 */
@RestController
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/login")
@Tag(name = "Adnoc B2B Login")
public class AdnocB2BLoginController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BLoginController.class);

    @Resource(name = "adnocGigyaLoginTokenVerificationFacade")
    private transient AdnocGigyaLoginTokenVerificationFacade adnocGigyaLoginTokenVerificationFacade;

    @SecurePortalUnauthenticatedAccess
    @Operation(operationId = "verifyGigyaLoginToken", summary = "Verify Gigya login token", description = "Verifies the validity of a Gigya login token for authentication.")
    @PostMapping(value = "/verifyGigyaLoginToken", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdParam
    public AdnocGigyaLoginTokenVerificationResponseWsDTO verifyGigyaLoginToken(@RequestBody final AdnocGigyaLoginVerificationRequestWsDTO requestWsDTO)
    {
        LOG.info("appEvent=AdnocB2BLoginController, verifyGigyaLoginToken method called with request: {}", requestWsDTO);
        final AdnocGigyaLoginVerificationRequestData adnocGigyaLoginVerificationRequestData = convertGigyaLoginVerificationRequestData(requestWsDTO);
        final AdnocGigyaLoginVerificationResponseData verifyGigyaLoginTokenResponseData = adnocGigyaLoginTokenVerificationFacade.verifyGigyaLoginToken(adnocGigyaLoginVerificationRequestData);
        return getDataMapper().map(verifyGigyaLoginTokenResponseData, AdnocGigyaLoginTokenVerificationResponseWsDTO.class);
    }

    private AdnocGigyaLoginVerificationRequestData convertGigyaLoginVerificationRequestData(final AdnocGigyaLoginVerificationRequestWsDTO requestWsDTO)
    {
        final AdnocGigyaLoginVerificationRequestData data = new AdnocGigyaLoginVerificationRequestData();
        data.setUID(requestWsDTO.getUID());
        data.setUIDSignature(requestWsDTO.getUIDSignature());
        data.setSignatureTimestamp(requestWsDTO.getSignatureTimestamp());
        LOG.debug("appEvent=AdnocGigyaLoginTokenVerificationFacadeImpl, Request data: {}", data);
        return data;
    }


}
