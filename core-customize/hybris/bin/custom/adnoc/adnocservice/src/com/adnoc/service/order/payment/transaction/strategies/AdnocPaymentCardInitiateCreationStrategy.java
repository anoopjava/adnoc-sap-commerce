package com.adnoc.service.order.payment.transaction.strategies;

import com.adnoc.facades.payment.card.data.AdnocPaymentCardInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentDiscountAmountData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.service.order.payment.transaction.AdnocPaymentInitiateRequestCreationStrategy;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocPaymentCardInitiateCreationStrategy implements AdnocPaymentInitiateRequestCreationStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocPaymentCardInitiateCreationStrategy.class);

    public static final String RETURNURL = "mastercard.%s.payment.return.url";
    public static final String CANCELURL = "mastercard.%s.payment.cancel.url";
    public static final String ORDER_DESCRIPTION = "mastercard.%s.payment.order.description";

    private CartService cartService;
    private ConfigurationService configurationService;

    @Override
    public AdnocPaymentInitiateRequestData createPaymentInitiateRequest(final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData, final String paymentFlow)
    {
        LOG.info("appEvent=AdnocCardTransaction,Creating payment transaction for:{}", adnocPaymentInitiateRequestData.getPaymentType());
        final CartModel cartModel = getCartService().getSessionCart();
        final AdnocPaymentCardInitiateRequestData adnocPaymentCardInitiateRequestData = new AdnocPaymentCardInitiateRequestData();
        adnocPaymentCardInitiateRequestData.setPayerId(adnocPaymentInitiateRequestData.getPayerId());
        adnocPaymentCardInitiateRequestData.setPaymentType(adnocPaymentInitiateRequestData.getPaymentType());
        adnocPaymentCardInitiateRequestData.setPaymentAmount(adnocPaymentInitiateRequestData.getPaymentAmount());
        adnocPaymentCardInitiateRequestData.setCurrency(cartModel.getCurrency().getIsocode());

        final Configuration configuration = getConfigurationService().getConfiguration();
        adnocPaymentCardInitiateRequestData.setReturnUrl(configuration.getString(String.format(RETURNURL, paymentFlow)));
        adnocPaymentCardInitiateRequestData.setCancelUrl(configuration.getString(String.format(CANCELURL, paymentFlow)));
        adnocPaymentCardInitiateRequestData.setDescription(configuration.getString(String.format(ORDER_DESCRIPTION, paymentFlow)));
        if (paymentFlow.equalsIgnoreCase("checkout"))
        {
            setPaymentCartAmountRelatedData(cartModel, adnocPaymentCardInitiateRequestData);
        }
        LOG.info("appEvent=AdnocCardTransaction,Successfully Created payment transaction for:{}", adnocPaymentInitiateRequestData.getPaymentType());
        return adnocPaymentCardInitiateRequestData;
    }

    private void setPaymentCartAmountRelatedData(final CartModel cart, final AdnocPaymentCardInitiateRequestData adnocCardPaymentInitiateRequestData)
    {
        LOG.info("appEvent=AdnocCardTransaction,Setting cart:{} payment related data", cart.getCode());

        final boolean itemLevelTotalInclude = Objects.nonNull(cart.getCreditLimitPaymentInfo()) && Objects.equals(cart.getPaymentType(), CheckoutPaymentType.CARD);
        if (!itemLevelTotalInclude)
        {
            if (Objects.nonNull(cart.getSubtotal()))
            {
                adnocCardPaymentInitiateRequestData.setItemAmount(cart.getSubtotal());
            }
            if (Objects.nonNull(cart.getDeliveryCost()))
            {
                adnocCardPaymentInitiateRequestData.setShippingAndHandlingAmount(cart.getDeliveryCost());
            }
            if (Objects.nonNull(cart.getTotalTax()))
            {
                adnocCardPaymentInitiateRequestData.setTaxAmount(cart.getTotalTax());
            }
            if (Objects.nonNull(cart.getTotalDiscounts()))
            {
                final AdnocPaymentDiscountAmountData adnocPaymentDiscountAmountData = new AdnocPaymentDiscountAmountData();
                adnocPaymentDiscountAmountData.setAmount(cart.getTotalDiscounts());
                adnocCardPaymentInitiateRequestData.setDiscount(adnocPaymentDiscountAmountData);
            }
        }
    }

    protected CartService getCartService()
    {
        return cartService;
    }

    public void setCartService(final CartService cartService)
    {
        this.cartService = cartService;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }
}
