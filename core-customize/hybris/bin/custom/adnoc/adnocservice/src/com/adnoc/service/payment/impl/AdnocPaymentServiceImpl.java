package com.adnoc.service.payment.impl;


import com.adnoc.facades.payment.bank.data.AdnocPaymentBankRegistrationRequestData;
import com.adnoc.facades.payment.bank.data.AdnocPaymentBankTransactionRegistrationResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentCardInitiateCheckoutRequestData;
import com.adnoc.facades.payment.card.data.AdnocPaymentCardInitiateCheckoutResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentResponseData;
import com.adnoc.facades.payment.card.data.AdnocTransactionData;
import com.adnoc.facades.payment.data.AdnocPaymentBankFinalizationRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentBankFinalizationResponseData;
import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import com.adnoc.service.payment.AdnocPaymentService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AdnocPaymentServiceImpl implements AdnocPaymentService
{
    private static final Logger LOG = LogManager.getLogger(AdnocPaymentServiceImpl.class);

    private static final String ADNOC_PAYMENT_CARD_INITIATE_GATEWAY_DESTINATION = "adnocPaymentDestination";
    public static final String ADNOC_PAYMENT_CARD_INITIATE_GATEWAY_DESTINATION_TARGET = "adnoc-payment-destination-target";

    private static final String ADNOC_PAYMENT_CARD_RETRIEVE_GATEWAY_DESTINATION = "adnocPaymentCardDestination";
    private static final String ADNOC_PAYMENT_CARD_RETRIEVE_GATEWAY_DESTINATION_TARGET = "adnoc-payment-card-destination-target";
    public static final String PAYMENT = "PAYMENT";

    private static final String ADNOC_PAYMENT_BANK_REGISTRATION_GATEWAY_DESTINATION = "adnocPaymentBankRegistrationDestination";
    private static final String ADNOC_PAYMENT_BANK_REGISTRATION_GATEWAY_DESTINATION_TARGET = "adnoc-payment-bank-registration-destination-target";

    private static final String ADNOC_PAYMENT_BANK_FINALIZATION_DESTINATION = "adnocPaymentBankFinalizationDestination";
    private static final String ADNOC_PAYMENT_BANK_FINALIZATION_DESTINATION_TARGET = "adnoc-payment-bank-finalization-destination-target";

    private AdnocRestIntegrationService adnocPaymentPostRestIntegrationService;
    private AdnocRestIntegrationService adnocPaymentGetRestIntegrationService;
    private CartService cartService;
    private ModelService modelService;

    @Override
    public AdnocPaymentCardInitiateCheckoutResponseData initiatePayment(final AdnocPaymentCardInitiateCheckoutRequestData adnocPaymentInitiateCheckoutRequestData)
    {
        final CartModel cart = getCartService().getSessionCart();
        final AdnocPaymentCardInitiateCheckoutResponseData adnocPaymentCardInitiateCheckoutResponseData =
                getAdnocPaymentPostRestIntegrationService().restIntegration(ADNOC_PAYMENT_CARD_INITIATE_GATEWAY_DESTINATION,
                        ADNOC_PAYMENT_CARD_INITIATE_GATEWAY_DESTINATION_TARGET, adnocPaymentInitiateCheckoutRequestData,
                        AdnocPaymentCardInitiateCheckoutResponseData.class);
        saveCreditCardInfoDetails(cart, adnocPaymentCardInitiateCheckoutResponseData);
        return adnocPaymentCardInitiateCheckoutResponseData;
    }

    private void saveCreditCardInfoDetails(final CartModel cart, final AdnocPaymentCardInitiateCheckoutResponseData adnocPaymentCardInitiateCheckoutResponseData)
    {
        if (Objects.nonNull(cart.getPaymentInfo()) && cart.getPaymentInfo() instanceof final CreditCardPaymentInfoModel creditCardPaymentInfoModel)
        {
            creditCardPaymentInfoModel.setSessionID(adnocPaymentCardInitiateCheckoutResponseData.getSession().getId());
            creditCardPaymentInfoModel.setSuccessIndicator(adnocPaymentCardInitiateCheckoutResponseData.getSuccessIndicator());
            getModelService().saveAll(creditCardPaymentInfoModel, cart);
        }
    }

    @Override
    public AdnocPaymentResponseData retrievePayment(final String orderId)
    {
        LOG.info("appEvent=AdnocCardPayment,Starting retrievePayment for orderId={}", orderId);
        final AdnocPaymentResponseData adnocPaymentResponseData = getAdnocPaymentGetRestIntegrationService().restIntegration(
                ADNOC_PAYMENT_CARD_RETRIEVE_GATEWAY_DESTINATION, ADNOC_PAYMENT_CARD_RETRIEVE_GATEWAY_DESTINATION_TARGET,
                orderId, AdnocPaymentResponseData.class);
        if (CollectionUtils.isNotEmpty(adnocPaymentResponseData.getTransaction()))
        {
            LOG.debug("appEvent=AdnocCardPayment,Received transactions from response for orderId={}", orderId);
            final List<AdnocTransactionData> filteredTransactions = adnocPaymentResponseData.getTransaction().stream()
                    .filter(txn -> Objects.nonNull(txn.getTransaction()))
                    .filter(txn -> StringUtils.equals(PAYMENT, txn.getTransaction().getType()))
                    .findFirst()
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
            adnocPaymentResponseData.setTransaction(filteredTransactions);
            LOG.info("appEvent=AdnocCardPayment,Filtered payment transactions for orderId={}, count={}", orderId, filteredTransactions.size());
        }
        else
        {
            LOG.debug("appEvent=AdnocCardPayment,No transactions found in REST response for orderId={}", orderId);
        }
        return adnocPaymentResponseData;
    }

    @Override
    public AdnocPaymentBankTransactionRegistrationResponseData registerBankPayment(final AdnocPaymentBankRegistrationRequestData adnocPaymentBankRegistrationRequestData)
    {
        LOG.info("appEvent=AdnocBankPayment,fetching response for customer:{}", adnocPaymentBankRegistrationRequestData.getRegistration().getCustomer());
        return getAdnocPaymentPostRestIntegrationService().restIntegration(ADNOC_PAYMENT_BANK_REGISTRATION_GATEWAY_DESTINATION,
                ADNOC_PAYMENT_BANK_REGISTRATION_GATEWAY_DESTINATION_TARGET, adnocPaymentBankRegistrationRequestData,
                AdnocPaymentBankTransactionRegistrationResponseData.class);
    }

    @Override
    public AdnocPaymentBankFinalizationResponseData finalizeBankTransfer(final AdnocPaymentBankFinalizationRequestData adnocPaymentBankFinalizationRequestData)
    {
        LOG.info("appEvent=AdnocBankPayment, Finalizing bank transfer for transactionID={}", adnocPaymentBankFinalizationRequestData.getFinalization().getTransactionID());
        return getAdnocPaymentPostRestIntegrationService().restIntegration(ADNOC_PAYMENT_BANK_FINALIZATION_DESTINATION, ADNOC_PAYMENT_BANK_FINALIZATION_DESTINATION_TARGET, adnocPaymentBankFinalizationRequestData, AdnocPaymentBankFinalizationResponseData.class);
    }

    protected AdnocRestIntegrationService getAdnocPaymentPostRestIntegrationService()
    {
        return adnocPaymentPostRestIntegrationService;
    }

    public void setAdnocPaymentPostRestIntegrationService(final AdnocRestIntegrationService adnocPaymentPostRestIntegrationService)
    {
        this.adnocPaymentPostRestIntegrationService = adnocPaymentPostRestIntegrationService;
    }

    protected AdnocRestIntegrationService getAdnocPaymentGetRestIntegrationService()
    {
        return adnocPaymentGetRestIntegrationService;
    }

    public void setAdnocPaymentGetRestIntegrationService(final AdnocRestIntegrationService adnocPaymentGetRestIntegrationService)
    {
        this.adnocPaymentGetRestIntegrationService = adnocPaymentGetRestIntegrationService;
    }

    protected CartService getCartService()
    {
        return cartService;
    }

    public void setCartService(final CartService cartService)
    {
        this.cartService = cartService;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

}
