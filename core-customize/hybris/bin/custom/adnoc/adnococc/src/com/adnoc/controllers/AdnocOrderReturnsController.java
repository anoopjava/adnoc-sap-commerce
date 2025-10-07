package com.adnoc.controllers;

import com.adnoc.b2bocc.ordermanagement.data.ReturnReasonListWsDTO;
import com.adnoc.b2bocc.ordermanagement.data.ReturnReasonWsDTO;
import com.adnoc.facades.ordermanagement.data.ReturnReasonData;
import com.adnoc.facades.returnreason.AdnocReturnFacade;
import com.adnoc.validators.helper.AdnocOrderReturnsHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservices.core.requestfrom.RequestFromValueSetter;
import de.hybris.platform.commercewebservices.core.skipfield.SkipReturnRequestFieldValueSetter;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestWsDTO;
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

import static de.hybris.platform.b2bocc.security.SecuredAccessConstants.ROLE_CUSTOMERGROUP;
import static de.hybris.platform.b2bocc.security.SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP;
import static de.hybris.platform.commercefacades.order.constants.OrderOccControllerRequestFromConstants.ORDER_RETURN_CONTROLLER;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/returns")
@Tag(name = "Adnoc Order Return")
public class AdnocOrderReturnsController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderReturnsController.class);

    @Resource(name = "omsReturnFacade")
    private AdnocReturnFacade adnocReturnFacade;
    @Resource(name = "skipReturnRequestFieldValueSetter")
    private SkipReturnRequestFieldValueSetter skipReturnRequestFieldValueSetter;
    @Resource(name = "requestFromValueSetter")
    private RequestFromValueSetter requestFromValueSetter;
    @Resource(name = "returnRequestEntryInputListDTOValidator")
    private Validator returnRequestEntryInputListDTOValidator;
    @Resource(name = "adnocOrderReturnsHelper")
    private AdnocOrderReturnsHelper adnocOrderReturnsHelper;

    @Secured({ROLE_CUSTOMERGROUP, ROLE_CUSTOMERMANAGERGROUP})
    @GetMapping(value = "/return-reasons")
    @Operation(operationId = "getReturnReasons", summary = "Finds a list of all possible return reasons", description = "Finds a list of all possible return reasons")
    @ApiBaseSiteIdAndUserIdParam
    public ReturnReasonListWsDTO getReturnReasons()
    {
        final ReturnReasonListWsDTO returnReasonListWsDTO = new ReturnReasonListWsDTO();
        final List<ReturnReasonData> returnReason = adnocReturnFacade.getReturnReasons();
        returnReasonListWsDTO.setReturnReasons(getDataMapper().mapAsList(returnReason, ReturnReasonWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL));
        return returnReasonListWsDTO;
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @PostMapping(produces = MediaType.APPLICATION_JSON, consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(operationId = "createReturnsRequest", summary = "Creates an order return request.",
            description = "Creates an order return request. An order can be completely or partially returned."
                    + " For a complete return, add all order entry numbers and quantities in the request body."
                    + " For a partial return, only add the order entry numbers and quantities of the selected products.")
    @ApiBaseSiteIdAndUserIdParam
    public ReturnRequestWsDTO createReturnsRequest(
            @Parameter(description = "Return request input list for the current order.", required = true)
            @RequestParam("returnRequestData") final String returnRequestData,
            @Parameter(description = "File to be attached for returnRequestDocument.", required = false)
            @RequestPart(value = "returnRequestDocument", required = false) final MultipartFile returnRequestDocument,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        skipReturnRequestFieldValueSetter.setValue(fields);
        requestFromValueSetter.setRequestFrom(ORDER_RETURN_CONTROLLER);
        final ReturnRequestEntryInputListWsDTO returnRequestEntryInputListWsDTO = getReturnRequestEntryInputListWsDTO(returnRequestData);
        returnRequestEntryInputListWsDTO.setReturnRequestDocument(returnRequestDocument);
        validate(returnRequestEntryInputListWsDTO, "returnRequestEntryInputList", returnRequestEntryInputListDTOValidator);
        return adnocOrderReturnsHelper.createOrderReturnRequest(returnRequestEntryInputListWsDTO, fields);
    }

    private ReturnRequestEntryInputListWsDTO getReturnRequestEntryInputListWsDTO(final String returnRequestData)
    {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ReturnRequestEntryInputListWsDTO returnRequestEntryInputListWsDTO;
        try
        {
            returnRequestEntryInputListWsDTO = objectMapper.readValue(returnRequestData, ReturnRequestEntryInputListWsDTO.class);
        }
        catch (final IOException ioException)
        {
            LOG.error("An error occurred {} while converting to ReturnRequestEntryInputListWsDTO.", ExceptionUtils.getRootCauseMessage(ioException));
            throw new RuntimeException(ExceptionUtils.getRootCauseMessage(ioException), ioException);
        }
        return returnRequestEntryInputListWsDTO;
    }


}




