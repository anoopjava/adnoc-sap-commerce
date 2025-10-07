package com.adnoc.controllers;

import com.adnoc.b2bocc.user.data.AdnocB2BUserRegistrationDataWsDTO;
import com.adnoc.facades.user.AdnocB2BUserFacade;
import com.adnoc.service.exception.AdnocRegistrationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bocc.exceptions.RegistrationRequestCreateException;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.AlreadyExistsException;
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
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;


@RestController
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@ApiVersion("v2")
@Tag(name = "Adnoc Org Customer Management")
public class AdnocCreateOrgCustomerController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocCreateOrgCustomerController.class);

    private static final String USER_ALREADY_EXISTS_ERROR_KEY = "These credentials are linked to an existing account. Try logging in or contact support for further assistance";
    private static final String OBJECT_NAME_CUSTOMER = "customer";

    @Resource(name = "orgCustomerCreationWsDTOValidator")
    protected Validator orgCustomerCreationWsDTOValidator;

    @Resource(name = "b2bUserFacade")
    private AdnocB2BUserFacade adnocB2BUserFacade;


    @Secured({SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT})
    @PostMapping(value = "/orgCustomers", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(operationId = "createOrgCustomer", summary = "Creates an organizational customer profile.")
    @ApiBaseSiteIdAndUserIdParam
    public UserWsDTO createOrgCustomer(
            @Parameter(description = "Data object that contains information necessary for user creation", required = true)
            @RequestParam("adnocB2BUserRegistrationData") final String adnocB2BUserRegistrationData,
            @Parameter(description = "File to be attached for identificationNumberDocument.", required = true)
            @RequestPart("identificationNumberDocument") final MultipartFile identificationNumberDocument,
            @ApiFieldsParam @RequestParam(defaultValue = AdnocBaseController.DEFAULT_FIELD_SET) final String fields)
    {
        final AdnocB2BUserRegistrationDataWsDTO adnocB2BUserRegistrationDataWsDTO = getAdnocB2BUserRegistrationDataWsDTO(adnocB2BUserRegistrationData);
        adnocB2BUserRegistrationDataWsDTO.setIdentificationNumberDocument(identificationNumberDocument);
        validate(adnocB2BUserRegistrationDataWsDTO, OBJECT_NAME_CUSTOMER, orgCustomerCreationWsDTOValidator);
        final CustomerData orgCustomerData = getDataMapper().map(adnocB2BUserRegistrationDataWsDTO, CustomerData.class);
        final B2BUnitData b2BUnitData = getDataMapper().map(adnocB2BUserRegistrationDataWsDTO.getOrgUnit(), B2BUnitData.class);
        orgCustomerData.setUnit(b2BUnitData);
        try
        {
            if (adnocB2BUserFacade.isUserExisting(orgCustomerData))
            {
                throw new AlreadyExistsException(USER_ALREADY_EXISTS_ERROR_KEY);
            }
        }
        catch (final AdnocRegistrationException adnocRegistrationException)
        {
            throw new AlreadyExistsException(adnocRegistrationException.getMessage());
        }
        adnocB2BUserFacade.updateCustomer(orgCustomerData);
        final CustomerData updatedCustomerData = adnocB2BUserFacade.getCustomerForUid(orgCustomerData.getEmail());
        return getDataMapper().map(updatedCustomerData, UserWsDTO.class, fields);
    }

    private AdnocB2BUserRegistrationDataWsDTO getAdnocB2BUserRegistrationDataWsDTO(final String adnocB2BUserRegistrationData)
    {
        final ObjectMapper objectMapper = new ObjectMapper();
        final AdnocB2BUserRegistrationDataWsDTO adnocB2BUserRegistrationDataWsDTO;
        try
        {
            adnocB2BUserRegistrationDataWsDTO = objectMapper.readValue(adnocB2BUserRegistrationData, AdnocB2BUserRegistrationDataWsDTO.class);
        }
        catch (final IOException ioException)
        {
            LOG.error("An error occurred {} while converting to AdnocB2BUserRegistrationDataWsDTO.", ExceptionUtils.getRootCauseMessage(ioException));
            throw new RegistrationRequestCreateException(ExceptionUtils.getRootCauseMessage(ioException), ioException);
        }
        return adnocB2BUserRegistrationDataWsDTO;
    }
}
