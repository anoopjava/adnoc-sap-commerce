package com.adnoc.controllers;

import com.adnoc.b2bocc.order.data.OrderListWsDTO;
import com.adnoc.b2bocc.ordermanagement.data.CancelReasonListWsDTO;
import com.adnoc.b2bocc.ordermanagement.data.CancelReasonWsDTO;
import com.adnoc.facades.checkout.impl.AdnocB2BCheckoutFacadeImpl;
import com.adnoc.facades.order.AdnocCancelReasonFacade;
import com.adnoc.facades.ordermanagement.data.CancelReasonData;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.b2bocc.v2.requestfrom.RequestFromValueSetter;
import de.hybris.platform.b2bocc.v2.skipfield.SkipOrderFieldValueSetter;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.annotation.SiteChannelRestriction;
import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.PaymentAuthorizationException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelEntryData;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelRequestData;
import de.hybris.platform.ordermanagementfacades.order.OmsOrderFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static de.hybris.platform.b2bocc.constants.B2boccConstants.OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH;
import static de.hybris.platform.b2bocc.security.SecuredAccessConstants.ROLE_CUSTOMERGROUP;
import static de.hybris.platform.b2bocc.security.SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP;
import static de.hybris.platform.commercefacades.order.constants.OrderOccControllerRequestFromConstants.B2B_ORDERS_CONTROLLER;
import static de.hybris.platform.util.localization.Localization.getLocalizedString;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH)
@ApiVersion("v2")
@Tag(name = "Adnoc B2B Orders")
public class AdnocB2BOrdersController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BOrdersController.class);

    @Resource(name = "userFacade")
    protected UserFacade userFacade;

    @Resource(name = "orderFieldValueSetter")
    private SkipOrderFieldValueSetter skipOrderFieldValueSetter;

    @Resource(name = "b2bRequestFromValueSetter")
    private RequestFromValueSetter requestFromValueSetter;

    @Resource(name = "cartLoaderStrategy")
    private CartLoaderStrategy cartLoaderStrategy;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource(name = "b2bCartFacade")
    private CartFacade cartFacade;

    @Resource(name = "adnocB2BPlaceOrderCartValidator")
    private Validator adnocB2BPlaceOrderCartValidator;

    @Resource(name = "adnocB2BCheckoutFacade")
    private AdnocB2BCheckoutFacadeImpl adnocB2BCheckoutFacade;

    @Resource(name = "adnocCancelReasonFacade")
    private AdnocCancelReasonFacade adnocCancelReasonFacade;

    @Resource(name = "omsOrderFacade")
    private OmsOrderFacade omsOrderFacade;

    @Resource(name = "adnocCancellationRequestEntryInputListDTOValidator")
    private Validator adnocCancellationRequestEntryInputListDTOValidator;

    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;

    @Resource(name = "wsCustomerFacade")
    private CustomerFacade customerFacade;

    @Resource(name = "enumerationService")
    private EnumerationService enumerationService;

    protected static final String API_COMPATIBILITY_B2B_CHANNELS = "api.compatibility.b2b.channels";
    private static final String CART_CHECKOUT_TERM_UNCHECKED = "cart.term.unchecked";

    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
            SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT})
    @PostMapping(value = "/adnocOrders")
    @RequestMappingOverride(priorityProperty = "b2bocc.B2BOrdersController.placeOrder.priority")
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdAndUserIdParam
    @Operation(operationId = "placeAdnocOrgOrder", summary = "Creates a B2B order.", description = "Creates a B2B order. By default the payment type is ACCOUNT. Set payment type to CARD if placing an order using credit card.")
    public OrderListWsDTO placeAdnocOrgOrder(
            @Parameter(description = "Cart identifier: cart code for logged-in user, cart GUID for anonymous user, or 'current' for the last modified cart.", example = "00000110", required = true) @RequestParam(required = true) final String cartId,
            @Parameter(description = "Whether terms were accepted or not.", required = true) @RequestParam(required = true) final boolean termsChecked,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
            throws InvalidCartException, PaymentAuthorizationException
    {

        validateTerms(termsChecked);
        validateUser();
        skipOrderFieldValueSetter.setValue(fields);
        requestFromValueSetter.setRequestFrom(B2B_ORDERS_CONTROLLER);
        cartLoaderStrategy.loadCart(cartId);
        final CartData cartData = cartFacade.getCurrentCart();
        validateCart(cartData);
        validateAndAuthorizePayment(cartData);
        final OrderListWsDTO orderListWsDTO = new OrderListWsDTO();
        final List<OrderData> orderDataList = adnocB2BCheckoutFacade.placeAdnocOrder(new PlaceOrderData());
        orderListWsDTO.setOrders(getDataMapper().mapAsList(orderDataList, OrderWsDTO.class, fields));
        return orderListWsDTO;
    }

    @Secured({ROLE_CUSTOMERGROUP, ROLE_CUSTOMERMANAGERGROUP})
    @GetMapping(value = "/cancel-reasons")
    @Operation(operationId = "getCancelReason", summary = "Finds a list of all cancellation reasons", description = "Returns a list of all cancellation reasons.")
    @ApiBaseSiteIdAndUserIdParam
    public CancelReasonListWsDTO getCancelReason()
    {
        final CancelReasonListWsDTO cancelReasonListWsDTO = new CancelReasonListWsDTO();
        final List<CancelReasonData> cancelReasonData = adnocCancelReasonFacade.getCanceReasons();
        cancelReasonListWsDTO.setCacelReasons(getDataMapper().mapAsList(cancelReasonData, CancelReasonWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL));
        return cancelReasonListWsDTO;
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP"})
    @PostMapping(value = "/users/{userId}/orders/{code}/adnocCancellation", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "doAdnocCancelOrder", summary = "Adnoc Cancels an order.", description = "Adnoc Cancels an order, completely or partially. For a complete cancellation, add all the order entry numbers and quantities in the request body."
            + " For partial cancellation, only add the order entry numbers and quantities to be cancelled.")
    @ApiBaseSiteIdAndUserIdParam
    public void doAdnocCancelOrder(@Parameter(description = "Order code", required = true) @PathVariable final String code,
                                   @Parameter(description = "Cancellation request input list for the current order.", required = true) @RequestBody final CancellationRequestEntryInputListWsDTO cancellationRequestEntryInputList)
    {
        validate(cancellationRequestEntryInputList, "cancellationRequestEntryInputList", adnocCancellationRequestEntryInputListDTOValidator);
        validateUserForOrder(code);
        omsOrderFacade.createRequestOrderCancel(prepareCancellationRequestData(code, cancellationRequestEntryInputList));
    }


    protected void validateUserForOrder(final String code)
    {
        try
        {
            orderFacade.getOrderDetailsForCode(code);
        }
        catch (final UnknownIdentifierException ex)
        {
            LOG.warn("Order not found for the current user in current BaseStore", ex);
            throw new NotFoundException("Resource not found");
        }
    }

    protected OrderCancelRequestData prepareCancellationRequestData(final String code,
                                                                    final CancellationRequestEntryInputListWsDTO cancellationRequestEntryInputList)
    {
        final OrderCancelRequestData cancellationRequest = new OrderCancelRequestData();

        final List<OrderCancelEntryData> cancellationEntries = cancellationRequestEntryInputList.getCancellationRequestEntryInputs()
                .stream().map(this::mapToOrderCancelEntryData).collect(toList());

        cancellationRequest.setUserId(customerFacade.getCurrentCustomerUid());
        cancellationRequest.setOrderCode(code);
        cancellationRequest.setEntries(cancellationEntries);

        return cancellationRequest;
    }

    protected OrderCancelEntryData mapToOrderCancelEntryData(final CancellationRequestEntryInputWsDTO entryInput)
    {
        final OrderCancelEntryData cancelEntry = new OrderCancelEntryData();

        cancelEntry.setOrderEntryNumber(entryInput.getOrderEntryNumber());
        cancelEntry.setCancelQuantity(entryInput.getQuantity());
        Optional.ofNullable(mapToReturnReasonData(entryInput.getCancelReason()))
                .ifPresent(cancelEntry::setCancelReason);
        return cancelEntry;
    }

    private CancelReason mapToReturnReasonData(final CancelReasonWsDTO cancelReasonListWsDTO)
    {
        return Objects.nonNull(cancelReasonListWsDTO) ? enumerationService.getEnumerationValue(CancelReason.class, cancelReasonListWsDTO.getCode()) : null;
    }

    protected void validateAndAuthorizePayment(final CartData cartData)
            throws PaymentAuthorizationException
    {
        if (CheckoutPaymentType.CARD.getCode().equals(cartData.getPaymentType().getCode()) && !adnocB2BCheckoutFacade.authorizePayment(null))
        {
            throw new PaymentAuthorizationException();
        }
    }

    protected void validateTerms(final boolean termsChecked)
    {
        if (!termsChecked)
        {
            throw new RequestParameterException(getLocalizedString(CART_CHECKOUT_TERM_UNCHECKED));
        }
    }

    protected void validateCart(final CartData cartData) throws InvalidCartException
    {
        final Errors errors = new BeanPropertyBindingResult(cartData, "sessionCart");
        adnocB2BPlaceOrderCartValidator.validate(cartData, errors);
        if (errors.hasErrors())
        {
            throw new WebserviceValidationException(errors);
        }

        try
        {
            final List<CartModificationData> modificationList = cartFacade.validateCurrentCartData();
            if (CollectionUtils.isNotEmpty(modificationList))
            {
                final CartModificationDataList cartModificationDataList = new CartModificationDataList();
                cartModificationDataList.setCartModificationList(modificationList);
                throw new WebserviceValidationException(cartModificationDataList);
            }
        }
        catch (final CommerceCartModificationException e)
        {
            throw new InvalidCartException(e);
        }
    }

    protected void validateUser()
    {
        if (userFacade.isAnonymousUser())
        {
            throw new AccessDeniedException("Access is denied");
        }
    }


}
