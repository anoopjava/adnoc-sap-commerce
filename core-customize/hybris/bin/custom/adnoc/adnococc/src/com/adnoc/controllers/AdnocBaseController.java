/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.controllers;

import com.adnoc.service.exception.AdnocS4HanaException;
import com.google.common.collect.Lists;
import de.hybris.platform.b2bocc.v2.controllers.BaseController;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.PaginationWsDTO;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.util.YSanitizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


/**
 * Base Controller. It defines the exception handler to be used by all controllers. Extending controllers can add or
 * overwrite the exception handler if needed.
 */
@RestController
public class AdnocBaseController extends BaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocBaseController.class);

    protected static final String DEFAULT_PAGE_SIZE = "20";
    protected static final String DEFAULT_CURRENT_PAGE = "0";
    protected static final String BASIC_FIELD_SET = FieldSetLevelHelper.BASIC_LEVEL;
    protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;
    protected static final String FULL_FIELD_SET = FieldSetLevelHelper.FULL_LEVEL;
    protected static final String HEADER_TOTAL_COUNT = "X-Total-Count";
    protected static final String INVALID_REQUEST_BODY_ERROR_MESSAGE = "Request body is invalid or missing";


    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    protected static String logParam(final String paramName, final Long paramValue)
    {
        return paramName + " = " + paramValue;
    }

    protected static String logParam(final String paramName, final String paramValue)
    {
        return paramName + " = " + logValue(paramValue);
    }

    protected static String logValue(final String paramValue)
    {
        return "'" + sanitize(paramValue) + "'";
    }

    protected static String sanitize(final String input)
    {
        return YSanitizer.sanitize(input);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ModelNotFoundException.class})
    public ErrorListWsDTO handleModelNotFoundException(final Exception ex)
    {
        LOG.info("Handling Exception for this request - {} - {}", ex.getClass().getSimpleName(), sanitize(ex.getMessage()));
        LOG.debug("An exception occurred!", ex);

        return handleErrorInternal(UnknownIdentifierException.class.getSimpleName(), ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({AdnocS4HanaException.class})
    public ErrorListWsDTO handleAdnocS4HanaException(final AdnocS4HanaException adnocS4HanaException)
    {
        final String rootCauseMessage = ExceptionUtils.getRootCauseMessage(adnocS4HanaException);

        LOG.error("appEvent=creditLimit, handling Exception for this request - " + adnocS4HanaException.getClass().getSimpleName() + " - " + BaseController.sanitize(adnocS4HanaException.getMessage()));
        LOG.error("appEvent=creditLimit, root Cause: " + rootCauseMessage);

        return handleErrorInternal("AdnocS4HanaError", adnocS4HanaException.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ErrorListWsDTO handleException(final Exception exception)
    {
        final String exceptionName = ExceptionUtils.getRootCause(exception).getClass().getSimpleName();
        final String rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);
        final String cleanMessage = rootCauseMessage.replaceFirst(exceptionName + ":\\s*", "");
        LOG.error("appEvent=UpdateCartEntries, Handling Exception for this request - {} - {}", exceptionName, cleanMessage);
        return handleErrorInternal(exceptionName, cleanMessage);
    }

    protected ErrorListWsDTO handleErrorInternal(final String type, final String message)
    {
        final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
        final ErrorWsDTO error = new ErrorWsDTO();
        error.setType(type.replace("Exception", "Error"));
        error.setMessage(sanitize(message));
        errorListDto.setErrors(Lists.newArrayList(error));
        return errorListDto;
    }

    protected void validate(final Object object, final String objectName, final Validator validator)
    {
        final Errors errors = new BeanPropertyBindingResult(object, objectName);
        validator.validate(object, errors);
        if (errors.hasErrors())
        {
            throw new WebserviceValidationException(errors);
        }
    }

    /**
     * Adds pagination field to the 'fields' parameter
     *
     * @param fields
     * @return fields with pagination
     */
    protected String addPaginationField(final String fields)
    {
        String fieldsWithPagination = fields;

        if (StringUtils.isNotBlank(fieldsWithPagination))
        {
            fieldsWithPagination += ",";
        }
        fieldsWithPagination += "pagination";

        return fieldsWithPagination;
    }

    protected void setTotalCountHeader(final HttpServletResponse response, final PaginationWsDTO paginationDto)
    {
        if (paginationDto != null && paginationDto.getTotalResults() != null)
        {
            response.setHeader(HEADER_TOTAL_COUNT, String.valueOf(paginationDto.getTotalResults()));
        }
    }

    protected void setTotalCountHeader(final HttpServletResponse response, final PaginationData paginationDto)
    {
        if (paginationDto != null)
        {
            response.setHeader(HEADER_TOTAL_COUNT, String.valueOf(paginationDto.getTotalNumberOfResults()));
        }
    }

    protected DataMapper getDataMapper()
    {
        return dataMapper;
    }

    protected void setDataMapper(final DataMapper dataMapper)
    {
        this.dataMapper = dataMapper;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({DuplicateUidException.class})
    public ErrorListWsDTO handleDuplicateUidException(final DuplicateUidException ex)
    {
        LOG.debug("DuplicateUidException", ex);
        return handleErrorInternal("DuplicateUidException", ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ErrorListWsDTO handleHttpMessageNotReadableException(final Exception ex)
    {
        LOG.debug(INVALID_REQUEST_BODY_ERROR_MESSAGE, ex);
        return handleErrorInternal(HttpMessageNotReadableException.class.getSimpleName(), INVALID_REQUEST_BODY_ERROR_MESSAGE);
    }
}
