package com.adnoc.controllers;

import com.adnoc.b2bocc.payment.bank.data.AdnocPaymentBankFinalizationResponseWsDTO;
import com.adnoc.b2bocc.payment.bank.data.AdnocPaymentBankTransactionRegistrationResponseWsDTO;
import com.adnoc.b2bocc.payment.card.data.AdnocPaymentInitiateCheckoutResponseWsDTO;
import com.adnoc.b2bocc.payment.card.data.AdnocPaymentResponseWsDTO;
import com.adnoc.facades.payment.AdnocPaymentFacade;
import com.adnoc.facades.payment.bank.data.AdnocPaymentBankTransactionRegistrationResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentCardInitiateCheckoutResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentBankFinalizationResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Objects;

import static de.hybris.platform.b2bocc.constants.B2boccConstants.OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@ApiVersion("v2")
@Tag(name = "Adnoc Payment")
public class AdnocPaymentController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocPaymentController.class);

    @Resource(name = "adnocPaymentFacade")
    private AdnocPaymentFacade adnocPaymentFacade;

    @Resource(name = "b2bCartFacade")
    private CartFacade cartFacade;

    @Resource(name = "dataMapper")
    protected DataMapper dataMapper;


    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @PostMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH + "/carts/{cartId}/adnocPaymenttype", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO setAdnocPaymentType(
            @Parameter(description = "Payment type choice. Examples:card,account", required = true) @RequestParam(required = true) final String paymentType,
            @Parameter(description = "Purchase order number to assign to the cart during the checkout process.") @RequestParam(required = true) final String purchaseOrderNumber,
            @Parameter(description = "Credit Limit Check.") @RequestParam(required = false) final boolean isCreditLimitUsed,
            @Parameter(description = "Credit Limit Value.") @RequestParam(required = false) final double creditLimitValue,
            @Parameter(description = "File to be attached for PO Document.", required = true) @RequestPart(value = "poDocument", required = true) final MultipartFile poDocument,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final B2BPaymentTypeData paymentTypeData = new B2BPaymentTypeData();
        paymentTypeData.setCode(paymentType);

        CartData cartData = new CartData();
        cartData.setPaymentType(paymentTypeData);
        if (StringUtils.isNotBlank(purchaseOrderNumber))
        {
            cartData.setPurchaseOrderNumber(purchaseOrderNumber);
        }
        if (Objects.nonNull(poDocument))
        {
            cartData.setPoDocument(poDocument);
        }
        if (isCreditLimitUsed)
        {
            cartData.setCreditLimitUsed(isCreditLimitUsed);
            cartData.setCreditLimitValue(creditLimitValue);
        }
        cartData = cartFacade.update(cartData);
        cartData.setCreditLimitUsed(isCreditLimitUsed);
        return dataMapper.map(cartData, CartWsDTO.class, fields);
    }


    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @PostMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH + "/carts/{cartId}/intitatePaymentCheckout")
    @Operation(operationId = "getIntitatePaymentCheckout", summary = "Get the session ID.", description = "Initiate Checkout for Purchase.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ResponseEntity<AdnocPaymentInitiateCheckoutResponseWsDTO> adnocCheckoutIntitatePayment(
            @Parameter(description = "Payment type choice. Examples:CARD,ACCOUNT,BANK_TRANSFER", required = true) @RequestParam(required = true) final String paymentType,
            @Parameter(description = "Payment Amount") @RequestParam(required = false) final double paymentAmount,
            @Parameter(description = "Cart Id.") @PathVariable final String cartId)
    {
        LOG.info("appEvent=AdnocPayment,Initiating checkout payment for cartId={}, paymentType={}, paymentAmount={}",
                cartId, paymentType, paymentAmount);
        final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData = new AdnocPaymentInitiateRequestData();
        adnocPaymentInitiateRequestData.setPayerId(cartId);
        adnocPaymentInitiateRequestData.setPaymentType(paymentType);
        adnocPaymentInitiateRequestData.setPaymentAmount(paymentAmount);

        final AdnocPaymentInitiateResponseData adnocPaymentInitiateResponseData = adnocPaymentFacade.initiateCheckoutPayment(adnocPaymentInitiateRequestData);
        try
        {
            if (adnocPaymentInitiateResponseData instanceof final AdnocPaymentCardInitiateCheckoutResponseData adnocPaymentCardInitiateCheckoutResponseData)
            {
                LOG.debug("appEvent=AdnocPayment,Received AdnocPaymentCardInitiateCheckoutResponseData for cartId={}", cartId);
                final AdnocPaymentInitiateCheckoutResponseWsDTO adnocPaymentInitiateCheckoutResponseWsDTO = getDataMapper().map(
                        adnocPaymentCardInitiateCheckoutResponseData, AdnocPaymentInitiateCheckoutResponseWsDTO.class);
                LOG.info("appEvent=AdnocPayment,Successfully initiated payment checkout for cartId={}", cartId);
                return ResponseEntity.status(HttpStatus.OK).body(adnocPaymentInitiateCheckoutResponseWsDTO);

            }
            else if (adnocPaymentInitiateResponseData instanceof final AdnocPaymentBankTransactionRegistrationResponseData adnocPaymentBankTransactionRegistrationResponseData)
            {
                LOG.debug("appEvent=AdnocBankPayment,Received AdnocPaymentBankTransactionRegistrationResponseData for cartId={}", cartId);
                final AdnocPaymentBankTransactionRegistrationResponseWsDTO adnocPaymentBankTransactionRegistrationResponseWsDTO = getDataMapper().map(
                        adnocPaymentBankTransactionRegistrationResponseData, AdnocPaymentBankTransactionRegistrationResponseWsDTO.class);
                LOG.info("appEvent=AdnocBankPayment,Successfully initiated payment checkout for cartId={}", cartId);
                return ResponseEntity.status(HttpStatus.OK).body(adnocPaymentBankTransactionRegistrationResponseWsDTO);
            }
            else
            {
                LOG.error("appEvent=AdnocPayment,Unexpected response type from payment initiation for cartId={}", cartId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Unexpected response type from payment initiation."));
            }
        }
        catch (final Exception exception)
        {
            LOG.error("appEvent=AdnocPayment,Exception occurred while initiating payment checkout for cartId={}: {}", cartId, exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("An unexpected error occurred. Please try again later."));
        }
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @GetMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH + "/carts/{cartId}/retrieveCheckout")
    @Operation(operationId = "retrieveCheckout", summary = "Retrieve Payment.", description = "Retrieve external payment for checkout.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ResponseEntity<AdnocPaymentResponseWsDTO> adnocCheckoutRetrieve(@Parameter(description = "Result Indicator", required = true) @RequestParam(required = true) final String resultIndicator,
                                                                           @Parameter(description = "Session Version.", required = false) @RequestParam(required = false) final String sessionVersion,
                                                                           @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        LOG.info("appEvent=AdnocPayment,Retrieving payment with resultIndicator={}", resultIndicator);
        if (StringUtils.isEmpty(resultIndicator))
        {
            LOG.error("appEvent=AdnocPayment,Result Indicator is empty.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createAdnocPaymentErrorResponse("Result Indicator cannot be empty."));
        }
        try
        {
            final AdnocPaymentResponseData responseData = adnocPaymentFacade.retrieveCheckoutPayment(resultIndicator);
            LOG.debug("appEvent=AdnocPayment,Retrieved AdnocPaymentResponseData for resultIndicator={}", resultIndicator);
            final AdnocPaymentResponseWsDTO responseWsDTO = getDataMapper().map(responseData, AdnocPaymentResponseWsDTO.class, fields);
            return ResponseEntity.status(HttpStatus.OK).body(responseWsDTO);
        }
        catch (final Exception e)
        {
            LOG.error("appEvent=AdnocPayment,Error retrieving payment for resultIndicator={}: {}", resultIndicator, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createAdnocPaymentErrorResponse(e.getMessage()));
        }
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @PostMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH + "/carts/{cartId}/adnoc-payment-bank-checkout-finalize-transaction")
    @Operation(operationId = "bankTransactionFinalization", summary = "Get the session ID.", description = "Initiate Bank Transfer Finalization.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public ResponseEntity<AdnocPaymentBankFinalizationResponseWsDTO> adnocPaymentBankTransferFinalization(
            @Parameter(description = "Transaction Id, which Generated from Bank Transfer Registration", required = true)
            @RequestParam(required = true) final String transactionID)
    {
        LOG.info("appEvent=AdnocBankPayment, Finalizing bank transfer for transactionID={}", transactionID);
        final AdnocPaymentBankFinalizationResponseData adnocPaymentBankFinalizationResponseData = adnocPaymentFacade.finalizeCheckoutBankPayment(transactionID);
        try
        {
            LOG.debug("appEvent=AdnocBankPayment, Received AdnocPaymentCardInitiateCheckoutResponseData:{}", adnocPaymentBankFinalizationResponseData);
            final AdnocPaymentBankFinalizationResponseWsDTO adnocPaymentBankFinalizationResponseWsDTO = getDataMapper().map(
                    adnocPaymentBankFinalizationResponseData, AdnocPaymentBankFinalizationResponseWsDTO.class);
            return ResponseEntity.status(HttpStatus.OK).body(adnocPaymentBankFinalizationResponseWsDTO);
        }
        catch (final Exception exception)
        {
            LOG.error("appEvent=AdnocBankFinalization, Exception occurred while initiating transaction finalization: {}", exception.getMessage());
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

    private AdnocPaymentInitiateCheckoutResponseWsDTO createErrorResponse(final String message)
    {
        final AdnocPaymentInitiateCheckoutResponseWsDTO errorResponse = new AdnocPaymentInitiateCheckoutResponseWsDTO();
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
}
