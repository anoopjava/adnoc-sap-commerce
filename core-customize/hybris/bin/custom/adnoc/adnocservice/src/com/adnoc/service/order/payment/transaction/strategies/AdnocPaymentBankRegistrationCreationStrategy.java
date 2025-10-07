package com.adnoc.service.order.payment.transaction.strategies;

import com.adnoc.facades.payment.bank.data.AdnocPaymentBankRegInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.service.order.payment.transaction.AdnocPaymentInitiateRequestCreationStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocPaymentBankRegistrationCreationStrategy implements AdnocPaymentInitiateRequestCreationStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocPaymentBankRegistrationCreationStrategy.class);

    public static final String RETURNURL = "mastercard.%s.payment.return.url";

    private CartService cartService;
    private ConfigurationService configurationService;

    @Override
    public AdnocPaymentInitiateRequestData createPaymentInitiateRequest(final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData, final String paymentFlow)
    {
        LOG.info("appEvent=AdnocBankTransaction,creating Transaction with payment type:{}", adnocPaymentInitiateRequestData.getPaymentType());
        final CartModel cartModel = getCartService().getSessionCart();
        final AdnocPaymentBankRegInitiateRequestData adnocPaymentBankRegInitiateRequestData = new AdnocPaymentBankRegInitiateRequestData();
        adnocPaymentBankRegInitiateRequestData.setPayerId(adnocPaymentInitiateRequestData.getPayerId());
        adnocPaymentBankRegInitiateRequestData.setPaymentType(adnocPaymentInitiateRequestData.getPaymentType());
        adnocPaymentBankRegInitiateRequestData.setPaymentAmount(adnocPaymentInitiateRequestData.getPaymentAmount());
        adnocPaymentBankRegInitiateRequestData.setCurrency(cartModel.getCurrency().getIsocode());

        final Configuration configuration = getConfigurationService().getConfiguration();
        adnocPaymentBankRegInitiateRequestData.setReturnUrl(configuration.getString(String.format(RETURNURL, paymentFlow)));
        LOG.info("appEvent=AdnocBankTransaction,Successfully Created Transaction with payment type:{}", adnocPaymentInitiateRequestData.getPaymentType());
        return adnocPaymentBankRegInitiateRequestData;
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