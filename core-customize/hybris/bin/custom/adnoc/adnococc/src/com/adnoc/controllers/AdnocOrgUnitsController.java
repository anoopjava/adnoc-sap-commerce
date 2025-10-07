package com.adnoc.controllers;

import com.adnoc.b2bocc.unit.data.AdnocB2BRegistrationRequestWsDTO;
import com.adnoc.b2bocc.unit.data.AdnocB2BUnitRegistrationDataWsDTO;
import com.adnoc.facades.adnocb2bfacade.AdnocB2BUnitFacade;
import com.adnoc.facades.b2b.unit.data.AdnocB2BUnitRegistrationData;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.b2bocc.exceptions.B2BRegistrationException;
import de.hybris.platform.b2bocc.exceptions.RegistrationRequestCreateException;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitNodeListWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitNodeWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.errors.exceptions.AlreadyExistsException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@ApiVersion("v2")
@Tag(name = "Adnoc B2B Units")
public class AdnocOrgUnitsController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrgUnitsController.class);

    private static final String REGISTRATION_NOT_ENABLED_ERROR_KEY = "Registration is not enabled";

    @Resource(name = "b2BUnitWsDTOValidator")
    protected Validator b2BUnitWsDTOValidator;

    @Resource(name = "adnocB2BUnitFacade")
    private AdnocB2BUnitFacade adnocB2BUnitFacade;

    @Resource(name = "adnocB2BUnitRegistrationWsDTOValidator")
    private Validator adnocB2BUnitRegistrationWsDTOValidator;

    @Secured({SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT})
    @PostMapping(value = "/adnocOrgB2BUnits", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(operationId = "createOrgUnit", summary = "Creates a new Adnoc B2B organizational unit.", description = "Creates a new Adnoc B2B unit. For example, the Rustic Organization used the word 'Rustic' to refer to their organizational unit.")
    @ApiBaseSiteIdAndUserIdParam
    public AdnocB2BRegistrationRequestWsDTO createAdnocB2BUnitRegistration(@Parameter(description = "Organizational unit object.", required = true)
                                                                           @RequestParam("adnocB2BUnitRegistrationData") final String adnocB2BUnitRegistrationData,
                                                                           @Parameter(description = "File to be attached for identificationNumberDocument.", required = true)
                                                                           @RequestPart("identificationNumberDocument") final MultipartFile identificationNumberDocument,
                                                                           @Parameter(description = "File to be attached for otherDocument.", required = false)
                                                                           @RequestPart(value = "otherDocument", required = false) final MultipartFile otherDocument,
                                                                           @Parameter(description = "File to be attached for vatIdDocument.", required = false)
                                                                           @RequestPart(value = "vatIdDocument", required = false) final MultipartFile vatIdDocument)
    {
        final AdnocB2BUnitRegistrationDataWsDTO adnocB2BUnitRegistrationDataWsDTO = getAdnocB2BUnitRegistrationDataWsDTO(adnocB2BUnitRegistrationData);
        adnocB2BUnitRegistrationDataWsDTO.setIdentificationNumberDocument(identificationNumberDocument);
        adnocB2BUnitRegistrationDataWsDTO.setOtherDocument(otherDocument);
        adnocB2BUnitRegistrationDataWsDTO.setVatIdDocument(vatIdDocument);
        validate(adnocB2BUnitRegistrationDataWsDTO, "orgUnitRegistrationData", adnocB2BUnitRegistrationWsDTOValidator);
        final AdnocB2BUnitRegistrationData adnocb2BRegistrationData = getDataMapper().map(adnocB2BUnitRegistrationDataWsDTO, AdnocB2BUnitRegistrationData.class);
        final AdnocB2BRegistrationRequestWsDTO adnocB2BRegistrationRequestWsDTO = new AdnocB2BRegistrationRequestWsDTO();
        final String pk = registerNewB2BUnit(adnocb2BRegistrationData);
        adnocB2BRegistrationRequestWsDTO.setRegistrationRequestId(pk);
        return adnocB2BRegistrationRequestWsDTO;
    }

    private AdnocB2BUnitRegistrationDataWsDTO getAdnocB2BUnitRegistrationDataWsDTO(final String adnocB2BUnitRegistrationData)
    {
        final ObjectMapper objectMapper = new ObjectMapper();
        final AdnocB2BUnitRegistrationDataWsDTO adnocB2BUnitRegistrationDataWsDTO;
        try
        {
            LOG.info("appEvent=B2BRegistration, Attempting to map adnocB2BUnitRegistrationData to AdnocB2BUnitRegistrationDataWsDTO...");
            adnocB2BUnitRegistrationDataWsDTO = objectMapper.readValue(adnocB2BUnitRegistrationData, AdnocB2BUnitRegistrationDataWsDTO.class);
            LOG.info("appEvent=B2BRegistration, Successfully mapped adnocB2BUnitRegistrationData to AdnocB2BUnitRegistrationDataWsDTO.");
        }
        catch (final IOException ioException)
        {
            LOG.error("An error occurred {} while converting to AdnocB2BUnitRegistrationDataWsDTO.", ExceptionUtils.getRootCauseMessage(ioException));
            throw new RegistrationRequestCreateException(ExceptionUtils.getRootCauseMessage(ioException), ioException);
        }
        return adnocB2BUnitRegistrationDataWsDTO;
    }

    private String registerNewB2BUnit(final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData)
    {
        final String b2BUnitRegistrationModelPk;
        try
        {
            LOG.info("appEvent=B2BRegistration, Invoking adnocB2BUnitFacade.registerNewB2BUnit...");
            b2BUnitRegistrationModelPk = adnocB2BUnitFacade.registerNewB2BUnit(adnocB2BUnitRegistrationData);
            LOG.info("appEvent=B2BRegistration, New B2BUnit registration successful.");
        }
        catch (final CustomerAlreadyExistsException customerAlreadyExistsException)
        {

            LOG.error("appEvent=B2BRegistration, Secure registration not enabled. Throwing AlreadyExistsException.");
            throw new AlreadyExistsException("These credentials are linked to an existing account. Try logging in or contact support for further assistance.");
        }
        catch (final UnknownIdentifierException unknownIdentifierException)
        {
            throw new RegistrationRequestCreateException(unknownIdentifierException.getMessage(), unknownIdentifierException);
        }
        catch (final RuntimeException runtimeException)
        {
            throw new B2BRegistrationException("Encountered an error when creating new B2BUnit registration request", runtimeException);
        }
        return b2BUnitRegistrationModelPk;
    }

    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP})
    @Operation(operationId = "getAdnocOrgUnitsRootNodeTree", summary = "Retrieves the root Account Summary unit node.", description = "Retrieves the root account summary unit node and the child nodes associated with it.")
    @GetMapping(value = "/adnocOrgUnitsRootNodeTree", produces = MediaType.APPLICATION_JSON)
    @ApiBaseSiteIdAndUserIdParam
    public B2BUnitNodeWsDTO getAdnocOrgUnitsRootNodeTree(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final B2BUnitNodeData unitNodeData = adnocB2BUnitFacade.getParentUnitNode();
        return getDataMapper().map(unitNodeData, B2BUnitNodeWsDTO.class, fields);
    }

    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP})
    @Operation(operationId = "getAdnocOrgUnitsHierarchy", summary = "Retrieves the org units hierarchy.", description = "Retrieves the org units hierarchy and the child nodes associated with them.")
    @GetMapping(value = "/adnoc-orgunits-hierarchy", produces = MediaType.APPLICATION_JSON)
    @ApiBaseSiteIdAndUserIdParam
    public B2BUnitNodeListWsDTO getAdnocOrgUnitsHierarchy(
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final B2BUnitNodeListWsDTO b2BUnitNodeListWsDTO = new B2BUnitNodeListWsDTO();
        final List<B2BUnitNodeData> b2BUnitHierarchy = adnocB2BUnitFacade.getB2BUnitHierarchy();
        b2BUnitNodeListWsDTO.setUnitNodes(getDataMapper().mapAsList(b2BUnitHierarchy, B2BUnitNodeWsDTO.class, fields));
        return b2BUnitNodeListWsDTO;
    }
}


