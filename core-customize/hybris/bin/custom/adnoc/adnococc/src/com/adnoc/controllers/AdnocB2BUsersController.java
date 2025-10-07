/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.controllers;

import com.adnoc.b2bocc.company.creditsimulation.data.AdnocCreditSimulationWsDTO;
import com.adnoc.b2bocc.company.response.data.AdnocCreditLimitResponseWsDTO;
import com.adnoc.b2bocc.config.data.B2BUnitListWsDto;
import com.adnoc.b2bocc.data.AdnocOrderSummaryWsDTO;
import com.adnoc.b2bocc.unit.data.AdnocB2BRegistrationDataWsDTO;
import com.adnoc.b2bocc.unit.data.AdnocB2BRegistrationRequestWsDTO;
import com.adnoc.b2bocc.user.data.IncoTermsListWsDTO;
import com.adnoc.b2bocc.user.data.IncoTermsWsDTO;
import com.adnoc.facades.adnocb2bfacade.AdnocB2BUnitFacade;
import com.adnoc.facades.b2b.data.AdnocB2BRegistrationData;
import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.facades.company.request.data.AdnocCreditLimitRequestData;
import com.adnoc.facades.company.response.data.AdnocCreditLimitResponseData;
import com.adnoc.facades.data.AdnocOrderSummaryData;
import com.adnoc.facades.registration.AdnocB2BRegistrationFacade;
import com.adnoc.facades.user.AdnocB2BUserFacade;
import com.adnoc.facades.user.creditlimit.AdnocCreditLimitFacade;
import com.adnoc.service.enums.PartnerFunction;
import com.adnoc.service.exception.AdnocRegistrationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bacceleratorfacades.exception.RegistrationNotEnabledException;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bocc.exceptions.B2BRegistrationException;
import de.hybris.platform.b2bocc.exceptions.RegistrationRequestCreateException;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitWsDTO;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservices.core.user.data.AddressDataList;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.commercewebservicescommons.annotation.SiteChannelRestriction;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressListWsDTO;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.errors.exceptions.AlreadyExistsException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static de.hybris.platform.b2bocc.constants.B2boccConstants.OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * Main Controller for Users
 */
@RestController
@ApiVersion("v2")
@Tag(name = "Adnoc B2B Users")
public class AdnocB2BUsersController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BUsersController.class);

    private static final String API_COMPATIBILITY_B2B_CHANNELS = "api.compatibility.b2b.channels";
    private static final String REGISTRATION_NOT_ENABLED_ERROR_KEY = "Registration is not enabled";

    @Resource(name = "adnocB2BRegistrationFacade")
    private AdnocB2BRegistrationFacade adnocB2BRegistrationFacade;

    @Resource(name = "adnocOrgUserRegistrationDataValidator")
    private Validator adnocOrgUserRegistrationDataValidator;

    @Resource(name = "adnocB2BUnitFacade")
    private AdnocB2BUnitFacade adnocB2BUnitFacade;

    @Resource(name = "adnocCreditLimitFacade")
    private AdnocCreditLimitFacade adnocCreditLimitFacade;

    @Resource(name = "b2bCustomerFacade")
    private CustomerFacade customerFacade;

    @Resource(name = "b2bUserFacade")
    private AdnocB2BUserFacade adnocB2BUserFacade;

    @Resource(name = "userService")
    private UserService userService;

    @SecurePortalUnauthenticatedAccess
    @PostMapping(value = "/{baseSiteId}/adnocOrgUsers", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(CREATED)
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @Operation(operationId = "createAdnocRegistrationRequest", summary = "Creates a registration request for Adnoc B2B customer.")
    @ApiBaseSiteIdParam
    public AdnocB2BRegistrationRequestWsDTO createAdnocRegistrationRequest(
            @Parameter(description = "Data object that contains information necessary to apply user registration", required = true)
            @RequestParam("orgUserRegistrationData") final String orgUserRegistrationData,
            @Parameter(description = "File to be attached for identificationNumberDocument.", required = true)
            @RequestPart("identificationNumberDocument") final MultipartFile identificationNumberDocument,
            @Parameter(description = "File to be attached for tlnDocument.", required = true)
            @RequestPart("tlnDocument") final MultipartFile tlnDocument,
            @Parameter(description = "File to be attached for vatIdDocument.", required = true)
            @RequestPart("vatIdDocument") final MultipartFile vatIdDocument,
            @Parameter(description = "File to be attached for otherDocument.", required = false)
            @RequestPart(value = "otherDocument", required = false) final MultipartFile otherDocument)
    {
        LOG.info("appEvent=B2BRegistration, Starting processing for orgUserRegistrationData...");
        final AdnocB2BRegistrationDataWsDTO adnocB2BRegistrationDataWsDTO = getAdnocB2BRegistrationDataWsDTO(orgUserRegistrationData);

        LOG.info("appEvent=B2BRegistration, Setting identification and document fields...");
        adnocB2BRegistrationDataWsDTO.setIdentificationNumberDocument(identificationNumberDocument);
        adnocB2BRegistrationDataWsDTO.setTlnDocument(tlnDocument);
        adnocB2BRegistrationDataWsDTO.setVatIdDocument(vatIdDocument);
        adnocB2BRegistrationDataWsDTO.setOtherDocument(otherDocument);

        LOG.info("appEvent=B2BRegistration, Validating AdnocB2BRegistrationDataWsDTO...");
        validate(adnocB2BRegistrationDataWsDTO, "orgUserRegistrationData", adnocOrgUserRegistrationDataValidator);

        final AdnocB2BRegistrationData adnocb2BRegistrationData = getDataMapper().map(adnocB2BRegistrationDataWsDTO, AdnocB2BRegistrationData.class);
        final String registrationRequestId = register(adnocb2BRegistrationData);

        final AdnocB2BRegistrationRequestWsDTO adnocB2BRegistrationRequestWsDTO = new AdnocB2BRegistrationRequestWsDTO();
        adnocB2BRegistrationRequestWsDTO.setRegistrationRequestId(registrationRequestId);
        LOG.info("appEvent=B2BRegistration, Registration process completed successfully.");
        return adnocB2BRegistrationRequestWsDTO;
    }

    @Secured({"ROLE_CLIENT", "ROLE_CUSTOMERGROUP"})
    @GetMapping(value = "/{baseSiteId}/users/{userId}/getB2BUnits")
    @Operation(operationId = "getB2BUnits", summary = "Get List of B2B units")
    @ApiBaseSiteIdAndUserIdParam
    public B2BUnitListWsDto getB2BUnits(@ApiFieldsParam(examples = {"SP", "PY", "SH"}) @RequestParam final PartnerFunction partnerFunction,
                                        @ApiFieldsParam @RequestParam(defaultValue = AdnocBaseController.DEFAULT_FIELD_SET) final String fields)
    {
        final B2BUnitListWsDto b2BUnitListWsDto = new B2BUnitListWsDto();
        final List<B2BUnitData> b2bUnitDataList = adnocB2BUnitFacade.getB2BUnits(partnerFunction);
        b2BUnitListWsDto.setB2bUnitListData(getDataMapper().mapAsList(b2bUnitDataList, B2BUnitWsDTO.class, fields));

        return b2BUnitListWsDto;
    }

    @Secured({"ROLE_CLIENT", "ROLE_CUSTOMERGROUP"})
    @GetMapping(value = "/{baseSiteId}/users/{userId}/getCurrentB2BUnits")
    @Operation(operationId = "getCurrentB2BUnits", summary = "Get List of Current B2B units")
    @ApiBaseSiteIdAndUserIdParam
    public B2BUnitListWsDto getCurrentB2BUnits(@ApiFieldsParam @RequestParam(defaultValue = AdnocBaseController.BASIC_FIELD_SET) final String fields)
    {
        final B2BUnitListWsDto b2BUnitListWsDto = new B2BUnitListWsDto();
        final List<B2BUnitData> b2bUnitDataList = adnocB2BUnitFacade.getCurrentB2BUnits();
        b2BUnitListWsDto.setB2bUnitListData(getDataMapper().mapAsList(b2bUnitDataList, B2BUnitWsDTO.class, fields));
        return b2BUnitListWsDto;
    }

    @Secured({"ROLE_CLIENT", "ROLE_CUSTOMERGROUP"})
    @PostMapping(value = "/{baseSiteId}/users/{userId}/setCurrentB2BUnit")
    @Operation(operationId = "setCurrentB2BUnit", summary = "Setting Current B2BUnit")
    @ApiBaseSiteIdAndUserIdParam
    public void setCurrentB2BUnit(@ApiFieldsParam @RequestParam final String b2BUnitUid)
    {
        adnocB2BUnitFacade.setCurrentB2BUnit(b2BUnitUid);
    }

    @Secured({"ROLE_CLIENT", "ROLE_CUSTOMERGROUP"})
    @GetMapping(value = "/{baseSiteId}/users/{userId}/getShippingAddresses")
    @Operation(operationId = "getAddressList", summary = "Get Address List")
    @ApiBaseSiteIdAndUserIdParam
    public AddressListWsDTO getShippingAddresses(@ApiFieldsParam @RequestParam final String division,
                                                 @ApiFieldsParam @RequestParam final String incoTerms)
    {
        final AddressDataList addressDataList = adnocB2BUnitFacade.getShippingAddressList(division, incoTerms);
        return getDataMapper().map(addressDataList, AddressListWsDTO.class, AdnocBaseController.FULL_FIELD_SET);
    }

    @Secured({"ROLE_CLIENT", "ROLE_CUSTOMERGROUP"})
    @GetMapping(value = "/{baseSiteId}/users/{userId}/getIncoTerms")
    @Operation(operationId = "getIncoTerms", summary = "Retrieves the list of IncoTerms.")
    @ApiBaseSiteIdAndUserIdParam
    public IncoTermsListWsDTO getIncoTerms(@ApiFieldsParam @RequestParam final String division,
                                           @Parameter(description = "Pickup Flag.") @RequestParam(required = false) final boolean pickup)
    {
        final IncoTermsListWsDTO incoTermsListWsDTO = new IncoTermsListWsDTO();
        final List<IncoTermsData> incoTerms = adnocB2BUnitFacade.getIncoTerms(division, pickup);
        incoTermsListWsDTO.setIncoTerms(getDataMapper().mapAsList(incoTerms, IncoTermsWsDTO.class, AdnocBaseController.FULL_FIELD_SET));
        return incoTermsListWsDTO;
    }

    private AdnocB2BRegistrationDataWsDTO getAdnocB2BRegistrationDataWsDTO(final String orgUserRegistrationData)
    {
        LOG.info("appEvent=B2BRegistration, getAdnocB2BRegistrationDataWsDTO method called...");
        final ObjectMapper objectMapper = new ObjectMapper();
        final AdnocB2BRegistrationDataWsDTO adnocB2BRegistrationDataWsDTO;
        try
        {
            LOG.info("appEvent=B2BRegistration, Attempting to map orgUserRegistrationData to AdnocB2BRegistrationDataWsDTO...");
            adnocB2BRegistrationDataWsDTO = objectMapper.readValue(orgUserRegistrationData, AdnocB2BRegistrationDataWsDTO.class);
            LOG.info("appEvent=B2BRegistration, Successfully mapped orgUserRegistrationData to AdnocB2BRegistrationDataWsDTO.");
        }
        catch (final IOException ioException)
        {
            LOG.error("An error occurred {} while converting to AdnocB2BRegistrationDataWsDTO.", ExceptionUtils.getRootCauseMessage(ioException));
            throw new RegistrationRequestCreateException(ExceptionUtils.getRootCauseMessage(ioException), ioException);
        }
        return adnocB2BRegistrationDataWsDTO;
    }

    private String register(final AdnocB2BRegistrationData adnocb2BRegistrationData)
    {
        String registrationRequestId = null;
        LOG.info("appEvent=B2BRegistration, register method called with AdnocB2BRegistrationData.");
        try
        {
            LOG.info("appEvent=B2BRegistration, Invoking adnocB2BRegistrationFacade.register...");
            registrationRequestId = adnocB2BRegistrationFacade.registerSoldTo(adnocb2BRegistrationData);
            LOG.info("appEvent=B2BRegistration, Registration successful with registrationRequestId={}.", registrationRequestId);
        }
        catch (final AdnocRegistrationException adnocRegistrationException)
        {
            throw new AlreadyExistsException(adnocRegistrationException.getMessage());
        }
        catch (final CustomerAlreadyExistsException customerAlreadyExistsException)
        {
            LOG.warn("appEvent=B2BRegistration, Customer already exists: {}", customerAlreadyExistsException.getMessage());
            throw new AlreadyExistsException("These credentials are linked to an existing account. Try signing in with your credentials or contact on 800-300.");
        }
        catch (final RegistrationNotEnabledException registrationNotEnabledException)
        {
            LOG.error("appEvent=B2BRegistration, Registration not enabled: {}", registrationNotEnabledException.getMessage());
            throw new RegistrationRequestCreateException(REGISTRATION_NOT_ENABLED_ERROR_KEY, registrationNotEnabledException);
        }
        catch (final UnknownIdentifierException unknownIdentifierException)
        {
            LOG.error("appEvent=B2BRegistration, Unknown identifier: {}", unknownIdentifierException.getMessage());
            throw new RegistrationRequestCreateException(unknownIdentifierException.getMessage(), unknownIdentifierException);
        }
        catch (final RuntimeException runtimeException)
        {
            LOG.error("appEvent=B2BRegistration, Runtime exception encountered: {}", runtimeException.getMessage(), runtimeException);
            throw new B2BRegistrationException("Encountered an error when creating a registration request", runtimeException);
        }
        return registrationRequestId;
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT"})
    @Operation(operationId = "getCreditLimit", summary = "Get the Credit Limit.", description = "Credit Limit.")
    @PostMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH + "/getCreditLimit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdAndUserIdParam
    public AdnocCreditLimitResponseWsDTO getAdnocCreditLimit(@RequestBody final AdnocCreditLimitRequestData adnocCreditLimitRequestData)
    {
        LOG.info("appEvent=creditLimit, requesting credit limit data:{}", adnocCreditLimitRequestData.getB2bUnitUid());
        final AdnocCreditLimitResponseData adnocCreditLimitResponseData = adnocCreditLimitFacade.getCreditLimitDetails(adnocCreditLimitRequestData);

        final AdnocCreditLimitResponseWsDTO adnocCreditLimitResponseWsDTO = getDataMapper().map(adnocCreditLimitResponseData, AdnocCreditLimitResponseWsDTO.class);
        final Date updatedOnDate = new Date();
        adnocCreditLimitResponseWsDTO.setUpdatedOn(updatedOnDate);
        LOG.info("appEvent=creditLimit, fetch creditLimit response: {}", adnocCreditLimitResponseWsDTO.getUpdatedOn());
        return adnocCreditLimitResponseWsDTO;
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT"})
    @Operation(operationId = "credit-simulation-check", summary = "Credit simulation check", description = "Credit simulation check")
    @GetMapping(value = "/{baseSiteId}/credit-simulation-check", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiBaseSiteIdAndUserIdParam
    public AdnocCreditSimulationWsDTO creditSimulation(@Parameter(description = "PayerId", required = true)
                                                       @RequestParam(required = true) final String payerId)
    {
        final boolean creditSimulationCheck = adnocCreditLimitFacade.creditSimulationCheck(payerId);
        LOG.info("appEvent=AdnoccreditSimulation, creditSimulationCheck={} for payer={}", creditSimulationCheck, payerId);
        final AdnocCreditSimulationWsDTO adnocCreditSimulationWsDTO = new AdnocCreditSimulationWsDTO();
        adnocCreditSimulationWsDTO.setCreditSimulation(creditSimulationCheck);
        return adnocCreditSimulationWsDTO;
    }

    @Secured({"ROLE_CLIENT", "ROLE_CUSTOMERGROUP"})
    @GetMapping(value = "/{baseSiteId}/users/{userId}/getOrderSummary")
    @Operation(operationId = "getAdnocOrderSummary", summary = "Get Adnoc Order Summary Details")
    @ApiBaseSiteIdAndUserIdParam
    public AdnocOrderSummaryWsDTO getOrderSummary(@PathVariable final String userId)
    {
        String userUid = userId;
        if ("current".equalsIgnoreCase(userId))
        {
            userUid = userService.getCurrentUser().getUid();
        }
        final AdnocOrderSummaryData adnocOrderSummaryData = adnocB2BUserFacade.getOrderSummary(userUid);
        LOG.info("appEvent=OrderSummary, total orders :{}", adnocOrderSummaryData.getOrdersPlacedCount());

        return getDataMapper().map(adnocOrderSummaryData, AdnocOrderSummaryWsDTO.class);
    }

}