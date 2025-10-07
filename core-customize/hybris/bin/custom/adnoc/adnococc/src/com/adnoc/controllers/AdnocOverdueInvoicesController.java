package com.adnoc.controllers;

import com.adnoc.b2bocc.company.overdue.invoice.data.AdnocOverdueInvoiceListWsDTO;
import com.adnoc.b2bocc.company.overdue.invoice.data.AdnocOverduePaymentRequestWsDTO;
import com.adnoc.b2bocc.payment.bank.data.AdnocPaymentBankFinalizationResponseWsDTO;
import com.adnoc.b2bocc.payment.bank.data.AdnocPaymentBankTransactionRegistrationResponseWsDTO;
import com.adnoc.b2bocc.payment.card.data.AdnocPaymentInitiateCheckoutResponseWsDTO;
import com.adnoc.b2bocc.payment.card.data.AdnocPaymentResponseWsDTO;
import com.adnoc.facades.company.overdue.invoice.data.AdnocOverduePaymentRequestData;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceRequestData;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceResponseData;
import com.adnoc.facades.overdueinvoices.AdnocOverdueInvoiceFacade;
import com.adnoc.facades.payment.bank.data.AdnocPaymentBankTransactionRegistrationResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentCardInitiateCheckoutResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentBankFinalizationResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;
import com.adnoc.service.exception.AdnocPaymentException;
import com.adnoc.service.exception.AdnocS4HanaException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.b2bwebservicescommons.dto.order.B2BPaymentTypeListWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.order.B2BPaymentTypeWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/overdue")
@Tag(name = "Adnoc Overdue Invoices")
public class AdnocOverdueInvoicesController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocOverdueInvoicesController.class);

    @Resource(name = "adnocOverdueInvoiceFacade")
    private AdnocOverdueInvoiceFacade adnocOverdueInvoiceFacade;

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT"})
    @Operation(operationId = "invoices", summary = "Get Overdue Invoices", description = "Get Overdue Invoices")
    @PostMapping(value = "/invoices", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiBaseSiteIdAndUserIdParam
    public AdnocOverdueInvoiceListWsDTO getOverdueInvoices(@RequestBody final AdnocOverdueInvoiceRequestData adnocOverdueInvoiceRequestData)
    {
        final AdnocOverdueInvoiceResponseData adnocOverdueInvoiceResponseData = adnocOverdueInvoiceFacade.getOverdueInvoices(adnocOverdueInvoiceRequestData);
        LOG.info("appEvent=AdnocOverdueInvoices, processing Overdue Invoices for payer={}", adnocOverdueInvoiceRequestData.getPayer());
        final AdnocOverdueInvoiceListWsDTO adnocOverdueInvoiceListWsDTO = getDataMapper().map(adnocOverdueInvoiceResponseData, AdnocOverdueInvoiceListWsDTO.class, AdnocBaseController.DEFAULT_FIELD_SET);
        return adnocOverdueInvoiceListWsDTO;
    }

    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT})
    @GetMapping(value = "/payment-types")
    @Operation(operationId = "payment-types", summary = "Retrieves the available overdue invoices payment types.",
            description = "Retrieves the payment types that are available during the Overdue Invoices Payment.")
    @ApiBaseSiteIdAndUserIdParam
    public B2BPaymentTypeListWsDTO getOverdueInvoicesPaymentTypes()
    {
        final List<? extends B2BPaymentTypeData> paymentTypes = adnocOverdueInvoiceFacade.getPaymentTypes();
        final B2BPaymentTypeListWsDTO b2BPaymentTypeListWsDTO = new B2BPaymentTypeListWsDTO();
        b2BPaymentTypeListWsDTO.setPaymentTypes(getDataMapper().mapAsList(paymentTypes, B2BPaymentTypeWsDTO.class, AdnocBaseController.DEFAULT_FIELD_SET));
        return b2BPaymentTypeListWsDTO;
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/initiate-payment", consumes = APPLICATION_JSON_VALUE)
    @Operation(operationId = "initiate-payment", summary = "Initiate OverdueInvoice Payment Session.",
            description = "Initiate OverdueInvoice Payment Session.")
    @ApiBaseSiteIdAndUserIdParam
    public ResponseEntity<AdnocPaymentInitiateCheckoutResponseWsDTO> initiatePaymentOverdueInvoices(@RequestBody final AdnocOverduePaymentRequestWsDTO adnocOverduePaymentRequestWsDTO)
    {
        LOG.info("appEvent=AdnocOverdue,Initiating payment for overdue invoices. Request received: {}", adnocOverduePaymentRequestWsDTO);
        final AdnocOverduePaymentRequestData adnocOverduePaymentRequestData = getDataMapper().map(adnocOverduePaymentRequestWsDTO, AdnocOverduePaymentRequestData.class, AdnocBaseController.DEFAULT_FIELD_SET);
        LOG.debug("appEvent=AdnocOverdue,Mapped request data for overdue payment: {}", adnocOverduePaymentRequestData);
        final AdnocPaymentInitiateResponseData adnocPaymentInitiateResponseData = adnocOverdueInvoiceFacade.initiatePayment(adnocOverduePaymentRequestData);
        try
        {
            if (adnocPaymentInitiateResponseData instanceof final AdnocPaymentCardInitiateCheckoutResponseData adnocPaymentCardInitiateCheckoutResponseData)
            {
                LOG.info("appEvent=AdnocOverdue,Payment initiation successful. UTRN: {}", adnocPaymentCardInitiateCheckoutResponseData.getUtrn());
                final AdnocPaymentInitiateCheckoutResponseWsDTO adnocPaymentInitiateCheckoutResponseWsDTO = getDataMapper().map(
                        adnocPaymentCardInitiateCheckoutResponseData, AdnocPaymentInitiateCheckoutResponseWsDTO.class);
                return ResponseEntity.status(HttpStatus.OK).body(adnocPaymentInitiateCheckoutResponseWsDTO);
            }
            else if (adnocPaymentInitiateResponseData instanceof final AdnocPaymentBankTransactionRegistrationResponseData adnocBankTransferRegistrationResponseData)
            {
                LOG.info("appEvent=AdnocBankPayment,Payment Registration successful. UTRN: {}", adnocBankTransferRegistrationResponseData.getUtrn());
                final AdnocPaymentBankTransactionRegistrationResponseWsDTO adnocBankTransferRegistrationResponseWsDTO = getDataMapper().map(
                        adnocBankTransferRegistrationResponseData, AdnocPaymentBankTransactionRegistrationResponseWsDTO.class);
                return ResponseEntity.status(HttpStatus.OK).body(adnocBankTransferRegistrationResponseWsDTO);
            }
            else
            {
                LOG.error("appEvent=AdnocOverdue, Unexpected response type from payment initiation. Returning error response.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Unexpected response type from payment initiation."));
            }
        }
        catch (final Exception e)
        {
            LOG.error("appEvent=AdnocOverdue, Error occurred during payment initiation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An unexpected error occurred. Please try again later."));
        }
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @GetMapping(value = "/retrieve-payment")
    @Operation(operationId = "retrieve-payment", summary = "Retrieve Overdue Invoice Payment.", description = "Retrieve Overdue Invoice Payment.")
    @ApiBaseSiteIdAndUserIdParam
    public ResponseEntity<AdnocPaymentResponseWsDTO> retrieveOverdueInvoicePayment(@Parameter(description = "Result Indicator", required = true)
                                                                                   @RequestParam(required = true) final String resultIndicator)
    {
        LOG.info("appEvent=AdnocOverdue,Received request to retrieve overdue invoice payment with resultIndicator={}", resultIndicator);
        if (StringUtils.isEmpty(resultIndicator))
        {
            LOG.error("Result Indicator is empty or null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createAdnocPaymentErrorResponse("Result Indicator cannot be empty."));
        }
        try
        {
            final AdnocPaymentResponseData adnocPaymentResponseData = adnocOverdueInvoiceFacade.retrievePayment(resultIndicator);
            final AdnocPaymentResponseWsDTO adnocPaymentResponseWsDTO = getDataMapper().map(
                    adnocPaymentResponseData, AdnocPaymentResponseWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
            LOG.info("appEvent=AdnocOverdue,Returning payment response DTO for resultIndicator={}", resultIndicator);
            return ResponseEntity.status(HttpStatus.OK).body(adnocPaymentResponseWsDTO);
        }
        catch (final AdnocPaymentException exception)
        {
            LOG.error("No matching payment transaction found for resultIndicator: {}", resultIndicator);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createAdnocPaymentErrorResponse(exception.getMessage()));
        }
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @PostMapping(value = "/adnoc-payment-bank-overdue-finalize-transaction")
    @Operation(operationId = "getBankTransactionFinalization", summary = "Get the session ID.", description = "Initiate Bank Transfer Finalization.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ResponseEntity<AdnocPaymentBankFinalizationResponseWsDTO> finalizeBankTransaction(
            @Parameter(description = "Transaction Id, which Generated from Bank Transfer Registration", required = true)
            @RequestParam(required = true) final String transactionID)
    {
        final AdnocPaymentBankFinalizationResponseData adnocPaymentBankFinalizationResponseData = adnocOverdueInvoiceFacade.finalizeBankTransfer(transactionID);
        try
        {
            LOG.debug("appEvent=AdnocBankPayment,Received payment Response for account={}", adnocPaymentBankFinalizationResponseData.getTransaction().getAccount());
            final AdnocPaymentBankFinalizationResponseWsDTO adnocPaymentBankFinalizationResponseWsDTO = getDataMapper().map(
                    adnocPaymentBankFinalizationResponseData, AdnocPaymentBankFinalizationResponseWsDTO.class);
            LOG.info("appEvent=AdnocBankPayment,Successfully initiated payment");
            return ResponseEntity.status(HttpStatus.OK).body(adnocPaymentBankFinalizationResponseWsDTO);
        }
        catch (final Exception exception)
        {
            LOG.error("appEvent=AdnocBankPayment,Error finalizing bank transaction for transactionID={}", transactionID, exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorDetailResponse("An unexpected error occurred. Please try again later."));
        }
    }

    private AdnocPaymentBankFinalizationResponseWsDTO createErrorDetailResponse(final String message)
    {
        final AdnocPaymentBankFinalizationResponseWsDTO errorResponse = new AdnocPaymentBankFinalizationResponseWsDTO();
        final ErrorWsDTO errorWsDTO = new ErrorWsDTO();
        errorWsDTO.setMessage(message);
        errorResponse.setError(errorWsDTO);
        return errorResponse;
    }

    private AdnocPaymentResponseWsDTO createAdnocPaymentErrorResponse(final String message)
    {
        final AdnocPaymentResponseWsDTO errorResponse = new AdnocPaymentResponseWsDTO();
        final ErrorWsDTO errorWsDTO = new ErrorWsDTO();
        errorWsDTO.setMessage(message);
        errorResponse.setError(errorWsDTO);
        return errorResponse;
    }

    private AdnocPaymentInitiateCheckoutResponseWsDTO createErrorResponse(final String message)
    {
        final AdnocPaymentInitiateCheckoutResponseWsDTO errorResponse = new AdnocPaymentInitiateCheckoutResponseWsDTO();
        final ErrorWsDTO errorWsDTO = new ErrorWsDTO();
        errorWsDTO.setMessage(message);
        errorResponse.setError(errorWsDTO);
        return errorResponse;
    }

    @Override
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({AdnocS4HanaException.class})
    public ErrorListWsDTO handleAdnocS4HanaException(final AdnocS4HanaException adnocS4HanaException)
    {
        final String rootCauseMessage = ExceptionUtils.getRootCauseMessage(adnocS4HanaException);
        LOG.error("appEvent=AdnocOverdueInvoices, handling Exception for this request - {} - {}", adnocS4HanaException.getClass().getSimpleName(), AdnocBaseController.sanitize(adnocS4HanaException.getMessage()));
        LOG.error("appEvent=AdnocOverdueInvoices, root Cause: {}", rootCauseMessage);
        return handleErrorInternal("AdnocS4HanaError", adnocS4HanaException.getMessage());
    }
}