package com.adnoc.facades.checkout.impl;

import com.adnoc.facades.strategies.AdnocCreateCartFromCartStrategy;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.enums.DeliveryType;
import com.adnoc.service.model.CreditLimitPaymentInfoModel;
import com.adnoc.service.order.payment.strategies.AdnocPaymentInfoCreateStrategy;
import com.adnoc.service.order.payment.transaction.strategies.AdnocPaymentTransactionStrategy;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCheckoutFacade;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.ticket.service.UnsupportedAttachmentException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static de.hybris.platform.util.localization.Localization.getLocalizedString;

public class AdnocB2BCheckoutFacadeImpl extends DefaultB2BCheckoutFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BCheckoutFacadeImpl.class);

    private static final String CART_CHECKOUT_NO_QUOTE_DESCRIPTION = "cart.no.quote.description";
    private static final String CART_CHECKOUT_QUOTE_REQUIREMENTS_NOT_SATISFIED = "cart.quote.requirements.not.satisfied";
    private static final String CART_CHECKOUT_NOT_CALCULATED = "cart.not.calculated";
    private static final String MY_STATION = "02";
    private static final String CART_CHECKOUT_PAYMENTTYPE_INVALID = "cart.paymenttype.invalid";


    private AdnocCreateCartFromCartStrategy adnocCreateCartFromCartStrategy;
    private AdnocConfigService adnocConfigService;
    private EnumerationService enumerationService;
    private String allowedUploadedFormats;
    private MediaService mediaService;
    private Map<String, AdnocPaymentInfoCreateStrategy> adnocPaymentInfoCreateStrategyMap;
    private AdnocPaymentTransactionStrategy adnocPaymentTransactionStrategy;

    public List<OrderData> placeAdnocOrder(final PlaceOrderData placeOrderData) throws InvalidCartException
    {
        LOG.info("appEvent=AdnocB2BCheckout, placeAdnocOrder method called..");

        final List<OrderData> orderListData = new ArrayList<>();

        if (!isValidCheckoutCart(placeOrderData))
        {
            return orderListData;
        }

        LOG.info("checkout cart is valid, proceeding with order placement");

        if (placeOrderData.getNegotiateQuote() != null && placeOrderData.getNegotiateQuote().equals(Boolean.TRUE))
        {
            handleQuoteNegotiation(placeOrderData);
        }

        return placeAdnocOrder();
    }

    @Override
    public boolean authorizePayment(final String securityCode)
    {
        final CartModel cart = getCart();
        if (cart == null)
        {
            return false;
        }
        if (cart.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
        {
            return true;
        }
        return true;
    }

    private void handleQuoteNegotiation(final PlaceOrderData placeOrderData) throws EntityValidationException
    {
        if (StringUtils.isBlank(placeOrderData.getQuoteRequestDescription()))
        {
            throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_NO_QUOTE_DESCRIPTION));
        }
        else
        {
            final B2BCommentData b2BComment = new B2BCommentData();
            b2BComment.setComment(placeOrderData.getQuoteRequestDescription());

            final CartData cartData = new CartData();
            cartData.setB2BComment(b2BComment);

            updateCheckoutCart(cartData);
        }
    }

    private List<OrderData> placeAdnocOrder() throws InvalidCartException
    {
        LOG.info("appEvent=AdnocB2BCheckout, placeAdnocOrder internal method called..");
        final CartModel cartModel = getCart();
        final List<OrderData> orderListData = new ArrayList<>();
        if (cartModel != null && (cartModel.getUser().equals(getCurrentUserForCheckout()) || getCheckoutCustomerStrategy().isAnonymousCheckout()))
        {
            final List<CartModel> clonedCarts = getAdnocCreateCartFromCartStrategy().createCartFromCart(cartModel);
            LOG.info("appEvent=AdnocB2BCheckout,cloning the cart for checkout");
            for (final CartModel clonedCart : clonedCarts)
            {
                LOG.info("appEvent=AdnocB2BCheckout,placing order for cloned carts");
                beforePlaceOrder(clonedCart);
                final OrderModel orderModel = placeOrder(clonedCart);
                afterPlaceOrder(cartModel, clonedCart, orderModel);
                if (Objects.nonNull(orderModel))
                {
                    final OrderData orderData = new OrderData();
                    orderData.setCode(orderModel.getCode());
                    orderListData.add(orderData);
                }
            }
            getCartService().removeSessionCart();
        }
        return orderListData;
    }

    @Override
    protected boolean isValidCheckoutCart(final PlaceOrderData placeOrderData)
    {
        LOG.info("appEvent=AdnocB2BCheckout,isValidCheckoutCart method called");
        final CartData cartData = getCheckoutCart();
        final boolean valid = true;
        if (!cartData.isCalculated())
        {
            LOG.error("cart is not calculated, throwing EntityValidationException:{}", CART_CHECKOUT_NOT_CALCULATED);
            throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_NOT_CALCULATED));
        }
        if (Boolean.TRUE.equals(placeOrderData.getNegotiateQuote()) && !cartData.getQuoteAllowed())
        {
            throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_QUOTE_REQUIREMENTS_NOT_SATISFIED));
        }
        return valid;
    }

    @Override
    public CartData updateCheckoutCart(final CartData cartData)
    {
        LOG.info("appEvent=AdnocB2BCheckout, updateCheckoutCart method called..");
        final CartModel cartModel = getCart();
        if (cartModel == null)
        {
            LOG.error("Cart model is null. Returning null.");
            return null;
        }
        // set payment type
        if (Objects.nonNull(cartData.getPaymentType()))
        {
            final String newPaymentTypeCode = cartData.getPaymentType().getCode();
            LOG.info("Updating payment type to:{} ", newPaymentTypeCode);

            // clear delivery address, delivery mode and payment details when changing payment type
            if (Objects.isNull(cartModel.getPaymentType()) || !StringUtils.equalsIgnoreCase(newPaymentTypeCode, cartModel.getPaymentType().getCode()))
            {
                cartModel.setPaymentInfo(null);
            }
            setPaymentTypeForCart(newPaymentTypeCode, cartData, cartModel);
        }
        // set purchase order number
        if (Objects.nonNull(cartData.getPurchaseOrderNumber()))
        {
            LOG.info("appEvent=AdnocCheckout,Updating purchase order number to:{} ", cartData.getPurchaseOrderNumber());
            cartModel.setPurchaseOrderNumber(cartData.getPurchaseOrderNumber());
        }
        // set po document
        if (Objects.nonNull(cartData.getPoDocument()))
        {
            final MultipartFile poDocument = cartData.getPoDocument();
            try
            {
                final DocumentMediaModel idnDocumentMediaModel = createAttachment(poDocument.getOriginalFilename(), poDocument.getContentType(),
                        poDocument.getBytes());
                cartModel.setPoDocument(idnDocumentMediaModel);
            }
            catch (final IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        // set delivery address
        if (cartData.getDeliveryAddress() != null)
        {
            LOG.info("Updating delivery address.");
            setDeliveryAddress(cartData.getDeliveryAddress());
        }
        // set quote request description
        if (cartData.getB2BComment() != null)
        {
            final B2BCommentModel b2bComment = getModelService().create(B2BCommentModel.class);
            b2bComment.setComment(cartData.getB2BComment().getComment());
            getB2bCommentService().addComment(cartModel, b2bComment);
        }
        getModelService().save(cartModel);
        return getCheckoutCart();
    }

    protected void setPaymentTypeForCart(final String paymentType, final CartData cartData, final CartModel cartModel)
    {
        final List<CheckoutPaymentType> checkoutPaymentTypes = getEnumerationService()
                .getEnumerationValues(CheckoutPaymentType._TYPECODE);
        if (!checkoutPaymentTypes.contains(CheckoutPaymentType.valueOf(paymentType)))
        {
            throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_PAYMENTTYPE_INVALID));
        }
        cartModel.setPaymentType(CheckoutPaymentType.valueOf(paymentType));

        double totalPrice = cartModel.getTotalPrice();
        if (cartData.isCreditLimitUsed())
        {
            final AdnocPaymentInfoCreateStrategy adnocPaymentInfoCreateStrategy = getAdnocPaymentInfoCreateStrategyMap().get(CheckoutPaymentType.CREDIT_LIMIT.getCode());
            if(Objects.isNull(adnocPaymentInfoCreateStrategy))
            {
                throw new EntityValidationException(String.format("No AdnocPaymentInfoCreateStrategy mapped for %s", CheckoutPaymentType.CREDIT_LIMIT.getCode()));
            }
            final CreditLimitPaymentInfoModel creditLimitPaymentInfoModel = (CreditLimitPaymentInfoModel) adnocPaymentInfoCreateStrategy.createPaymentInfo(cartData.getCreditLimitValue(), cartModel.getUser());
            LOG.info("appEvent=UpdateCheckoutCart, setting CreditLimitPaymentInfo with value={}.", cartData.getCreditLimitValue());
            cartModel.setCreditLimitPaymentInfo(creditLimitPaymentInfoModel);
            totalPrice = totalPrice - cartData.getCreditLimitValue();
        }
        else
        {
            cartModel.setCreditLimitPaymentInfo(null);
        }
        final AdnocPaymentInfoCreateStrategy adnocPaymentInfoCreateStrategy = getAdnocPaymentInfoCreateStrategyMap().get(paymentType);
        if(Objects.isNull(adnocPaymentInfoCreateStrategy))
        {
            throw new EntityValidationException(String.format("No AdnocPaymentInfoCreateStrategy mapped for %s", CheckoutPaymentType.CREDIT_LIMIT.getCode()));
        }
        final PaymentInfoModel paymentInfoModel = adnocPaymentInfoCreateStrategy.createPaymentInfo(totalPrice, cartModel.getUser());
        LOG.info("appEvent=UpdateCheckoutCart, setting PaymentInfo with value={}.", totalPrice);
        cartModel.setPaymentInfo(paymentInfoModel);

        getModelService().save(cartModel);
    }

    private DocumentMediaModel createAttachment(final String fileName, final String contentType, final byte[] data)
    {
        LOG.info("appEvent=CartPoDocument, createAttachment method start");
        checkFileExtension(fileName);
        final DocumentMediaModel documentMediaModel = getModelService().create(DocumentMediaModel.class);
        documentMediaModel.setCode(UUID.randomUUID().toString());
        documentMediaModel.setMime(contentType);
        documentMediaModel.setRealFileName(fileName);
        getModelService().save(documentMediaModel);
        getMediaService().setDataForMedia(documentMediaModel, data);
        return documentMediaModel;
    }

    private void checkFileExtension(final String name)
    {
        if (!FilenameUtils.isExtension(name.toLowerCase(), getAllowedUploadedFormats().replaceAll("\\s", "").toLowerCase().split(",")))
        {
            throw new UnsupportedAttachmentException(String.format("File %s has unsupported extension. Only [%s] allowed.", name, getAllowedUploadedFormats()));
        }
    }


    protected void afterPlaceOrder(final CartModel originalCart, final CartModel clonedCart, final OrderModel orderModel)
    {
        LOG.info("appEvent=AdnocB2BCheckout, afterPlaceOrder method called");
        orderModel.setSapSalesOrganization(getAdnocConfigService().getSalesOrgbyDivision(orderModel.getEntries().get(0).getProduct().getDivision()));
        if (clonedCart.getDeliveryAddress().getOwner() instanceof final B2BUnitModel b2BUnitModel)
        {
            final Set<DeliveryType> deliveryTypes = b2BUnitModel.getDeliveryTypes();
            final DeliveryType myStationDeliveryType = getEnumerationService().getEnumerationValue(DeliveryType.class, MY_STATION);
            if (CollectionUtils.isNotEmpty(deliveryTypes) && deliveryTypes.contains(myStationDeliveryType))
            {
                orderModel.setSapPlantCode(myStationDeliveryType.getCode());
            }
        }
        orderModel.setPaymentTransactions(getAdnocPaymentTransactionStrategy().createPaymentTransactions(orderModel));
        orderModel.setCartIdReference(originalCart.getCode());
        orderModel.getEntries().forEach(abstractOrderEntryModel -> {
            OrderEntryModel orderEntryModel = (OrderEntryModel) abstractOrderEntryModel;
            orderEntryModel.setSapLineItemOrderStatus(SAPOrderStatus.NOT_YET_PROCESSED);
        });
        getModelService().saveAll(orderModel.getEntries());
        afterPlaceOrder(clonedCart, orderModel);
        getModelService().save(orderModel);
    }

    @Override
    protected void afterPlaceOrder(final CartModel clonedCart, final OrderModel orderModel)
    {
        getModelService().remove(clonedCart);
    }

    @Override
    public List<B2BPaymentTypeData> getPaymentTypes()
    {
        final List<CheckoutPaymentType> checkoutPaymentTypes = getEnumerationService()
                .getEnumerationValues(CheckoutPaymentType._TYPECODE);
        checkoutPaymentTypes.remove(CheckoutPaymentType.ACCOUNT);
        checkoutPaymentTypes.removeAll(getB2bCommerceUnitService().getRootUnit().getB2bExcludedPaymentTypes());
        return Converters.convertAll(checkoutPaymentTypes, getB2bPaymentTypeDataConverter());
    }

    protected AdnocCreateCartFromCartStrategy getAdnocCreateCartFromCartStrategy()
    {
        return adnocCreateCartFromCartStrategy;
    }

    public void setAdnocCreateCartFromCartStrategy(final AdnocCreateCartFromCartStrategy adnocCreateCartFromCartStrategy)
    {
        this.adnocCreateCartFromCartStrategy = adnocCreateCartFromCartStrategy;
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    @Override
    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    @Override
    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    protected String getAllowedUploadedFormats()
    {
        return allowedUploadedFormats;
    }

    public void setAllowedUploadedFormats(final String allowedUploadedFormats)
    {
        this.allowedUploadedFormats = allowedUploadedFormats;
    }

    protected MediaService getMediaService()
    {
        return mediaService;
    }

    public void setMediaService(final MediaService mediaService)
    {
        this.mediaService = mediaService;
    }

    protected Map<String, AdnocPaymentInfoCreateStrategy> getAdnocPaymentInfoCreateStrategyMap()
    {
        return adnocPaymentInfoCreateStrategyMap;
    }

    public void setAdnocPaymentInfoCreateStrategyMap(final Map<String, AdnocPaymentInfoCreateStrategy> adnocPaymentInfoCreateStrategyMap)
    {
        this.adnocPaymentInfoCreateStrategyMap = adnocPaymentInfoCreateStrategyMap;
    }

    protected AdnocPaymentTransactionStrategy getAdnocPaymentTransactionStrategy()
    {
        return adnocPaymentTransactionStrategy;
    }

    public void setAdnocPaymentTransactionStrategy(final AdnocPaymentTransactionStrategy adnocPaymentTransactionStrategy)
    {
        this.adnocPaymentTransactionStrategy = adnocPaymentTransactionStrategy;
    }
}

