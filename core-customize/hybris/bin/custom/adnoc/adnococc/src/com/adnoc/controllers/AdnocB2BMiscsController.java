/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.controllers;

import com.adnoc.b2bocc.user.data.*;
import com.adnoc.facades.adnocb2bfacade.AdnocB2BUnitFacade;
import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.facades.product.data.GenderData;
import com.adnoc.facades.registration.AdnocB2BRegistrationFacade;
import com.adnoc.facades.user.AdnocB2BUserFacade;
import com.adnoc.facades.user.data.*;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Misc Controller
 */
@RestController
@RequestMapping(value = "/{baseSiteId}")
@ApiVersion("v2")
@Tag(name = "Adnoc B2B Miscs")
public class AdnocB2BMiscsController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BMiscsController.class);

    private static final String ADNOC_PAYMENT_BANK_REDIRECT_PATH = "adnoc.%s.review.order.url";

    @Resource(name = "adnocB2BRegistrationFacade")
    private AdnocB2BRegistrationFacade adnocB2BRegistrationFacade;

    @Resource(name = "b2bUserFacade")
    private AdnocB2BUserFacade adnocB2BUserFacade;

    @Resource(name = "adnocB2BUnitFacade")
    private AdnocB2BUnitFacade adnocB2BUnitFacade;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/primaryproducts")
    @Operation(operationId = "getPrimaryProducts", summary = "Retrieves the list of primary products.")
    @ApiBaseSiteIdParam
    public PrimaryProductListWsDTO getPrimaryProducts(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final PrimaryProductListWsDTO primaryProductListWsDTO = new PrimaryProductListWsDTO();
        final List<PrimaryProductData> primaryProducts = adnocB2BRegistrationFacade.getPrimaryProducts();
        primaryProductListWsDTO.setPrimaryProducts(getDataMapper().mapAsList(primaryProducts, PrimaryProductWsDTO.class, fields));

        return primaryProductListWsDTO;
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/preferredCommunicationChannels")
    @Operation(operationId = "getPreferredCommunicationChannels", summary = "Retrieves the list of preferred communication channels.")
    @ApiBaseSiteIdParam
    public PreferredCommunicationChannelListWsDTO getPreferredCommunicationChannels(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final PreferredCommunicationChannelListWsDTO preferredCommunicationChannelListWsDTO = new PreferredCommunicationChannelListWsDTO();
        final List<PreferredCommunicationChannelData> preferredCommunicationChannels = adnocB2BUserFacade.getPreferredCommunicationChannels();
        preferredCommunicationChannelListWsDTO.setPreferredCommunicationChannels(getDataMapper().mapAsList(preferredCommunicationChannels, PreferredCommunicationChannelWsDTO.class, fields));

        return preferredCommunicationChannelListWsDTO;
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/genders")
    @Operation(operationId = "getGenders", summary = "Retrieves the list of genders.")
    @ApiBaseSiteIdParam
    public GenderListWsDTO getGenders(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final GenderListWsDTO genderListWsDTO = new GenderListWsDTO();
        final List<GenderData> genders = adnocB2BUserFacade.getGenders();
        genderListWsDTO.setGenders(getDataMapper().mapAsList(genders, GenderWsDTO.class, fields));

        return genderListWsDTO;
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/nationalities")
    @Operation(operationId = "getNationalities", summary = "Retrieves the list of nationalities.")
    @ApiBaseSiteIdParam
    public NationalityListWsDTO getNationalities(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final NationalityListWsDTO nationalityListWsDTO = new NationalityListWsDTO();
        final List<NationalityData> nationalities = adnocB2BUserFacade.getNationalities();
        nationalityListWsDTO.setNationalities(getDataMapper().mapAsList(nationalities, NationalityWsDTO.class, fields));

        return nationalityListWsDTO;
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/identityTypes")
    @Operation(operationId = "getIdentityTypes", summary = "Retrieves the list of identity types.")
    @ApiBaseSiteIdParam
    public IdentityTypeListWsDTO getIdentityTypes(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final IdentityTypeListWsDTO identityTypeListWsDTO = new IdentityTypeListWsDTO();
        final List<IdentityTypeData> identityTypes = adnocB2BUserFacade.getIdentityTypes();
        identityTypeListWsDTO.setIdentityTypes(getDataMapper().mapAsList(identityTypes, IdentityTypeWsDTO.class, fields));

        return identityTypeListWsDTO;
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/getDesignationTypes")
    @Operation(operationId = "getDesignationTypes", summary = "Retrieves the list of adnoc designation types.")
    @ApiBaseSiteIdParam
    public AdnocDesignationTypeWsDTO getDesignationTypes(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final AdnocDesignationTypeWsDTO designationTypeWsDTO = new AdnocDesignationTypeWsDTO();
        final List<AdnocDesignationData> designationTypes = adnocB2BUserFacade.getDesignationTypes();
        designationTypeWsDTO.setDesignationTypes(getDataMapper().mapAsList(designationTypes, AdnocDesignationWsDTO.class, fields));

        return designationTypeWsDTO;
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/getTradeLicenseAuthorityTypes")
    @Operation(operationId = "getTradeLicenseAuthorityTypes", summary = "Retrieves the list of adnoc tradeLicenseAuthority types.")
    @ApiBaseSiteIdParam
    public AdnocTradeLicenseAuthorityTypeWsDTO getAdnocTLATypes(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final AdnocTradeLicenseAuthorityTypeWsDTO adnocTLAWsDTO = new AdnocTradeLicenseAuthorityTypeWsDTO();
        final List<AdnocTradeLicenseAuthorityData> adnocTLATypes = adnocB2BUserFacade.getTradeLicenseAuthorityTypes();
        adnocTLAWsDTO.setTradeLicenseAuthorityTypes(getDataMapper().mapAsList(adnocTLATypes, AdnocTradeLicenseAuthorityWsDTO.class, fields));

        return adnocTLAWsDTO;
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/incoterms-shipto")
    @Operation(operationId = "incoterms-shipto", summary = "Retrieves the list of IncoTerms for ShipTo Form")
    @ApiBaseSiteIdParam
    public IncoTermsListWsDTO getShipToApplicableIncoTerms()
    {
        final IncoTermsListWsDTO incoTermsListWsDTO = new IncoTermsListWsDTO();
        final List<IncoTermsData> incoTerms = adnocB2BUnitFacade.getShipToApplicableIncoTerms();
        incoTermsListWsDTO.setIncoTerms(getDataMapper().mapAsList(incoTerms, IncoTermsWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL));
        return incoTermsListWsDTO;
    }
}
