package com.adnoc.facades.payment;

import com.adnoc.facades.constants.AdnocFacadesConstants;
import com.adnoc.facades.payment.bank.data.AdnocPaymentBankTransactionRegistrationResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentResponseData;
import com.adnoc.facades.payment.card.data.AdnocTransactionData;
import com.adnoc.facades.payment.card.data.AdnocTransactionDetailsData;
import com.adnoc.facades.payment.data.*;
import com.adnoc.facades.payment.strategies.AdnocInitiatePaymentStrategy;
import com.adnoc.service.model.BankPaymentInfoModel;
import com.adnoc.service.order.payment.transaction.AdnocPaymentInitiateRequestCreationStrategy;
import com.adnoc.service.payment.AdnocPaymentService;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdnocPaymentFacadeImpl implements AdnocPaymentFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocPaymentFacadeImpl.class);

    private static final String SUCCESS_RESPONSE_CODE = "0";
    private static final String CHECKOUT = "checkout";
    private static final String ADNOC_BANK_FINALIZATION_CUSTOMER = "adnoc.banktransfer.finalization.customer";
    private static final List<String> BANK_PAYMENT_FINALIZE_RETRY_RESPONSE_CODES = List.of("6000", "6002");

    private Map<CheckoutPaymentType, AdnocInitiatePaymentStrategy> initiatePaymentStrategiesMap;
    private AdnocPaymentService adnocPaymentService;
    private CartService cartService;
    private ModelService modelService;
    private ConfigurationService configurationService;
    private Map<CheckoutPaymentType, AdnocPaymentInitiateRequestCreationStrategy> adnocPaymentInitiateRequestCreationStrategyMap;
    private int maxAttempts;

    @Override
    public AdnocPaymentInitiateResponseData initiateCheckoutPayment(final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData)
    {
        final CartModel cartModel = getCartService().getSessionCart();

        final CheckoutPaymentType checkoutPaymentType = CheckoutPaymentType.valueOf(adnocPaymentInitiateRequestData.getPaymentType());
        final AdnocPaymentInitiateRequestCreationStrategy adnocPaymentInitiateRequestCreationStrategy = getAdnocPaymentInitiateRequestCreationStrategyMap().get(checkoutPaymentType);
        final AdnocPaymentInitiateRequestData paymentInitiateRequestData = adnocPaymentInitiateRequestCreationStrategy.createPaymentInitiateRequest(adnocPaymentInitiateRequestData, CHECKOUT);
        final AdnocPaymentInitiateResponseData adnocPaymentInitiateResponseData = initiatePayment(paymentInitiateRequestData, CHECKOUT);

        final PaymentInfoModel paymentInfo = cartModel.getPaymentInfo();
        if (Objects.nonNull(paymentInfo))
        {
            LOG.debug("appEvent=AdnocPayment, Setting UTRN number={} in paymentInfo for cartId={}",
                    adnocPaymentInitiateResponseData.getUtrn(), cartModel.getCode());
            paymentInfo.setUtrnNumber(adnocPaymentInitiateResponseData.getUtrn());
            getModelService().save(paymentInfo);

            if (paymentInfo instanceof final BankPaymentInfoModel bankPaymentInfoModel
                    && adnocPaymentInitiateResponseData instanceof final AdnocPaymentBankTransactionRegistrationResponseData bankResponseData)
            {
                LOG.debug("appEvent=AdnocBankPayment, Saving BankPaymentInfoModel with TransactionId={}",
                        bankResponseData.getTransaction().getTransactionID());
                bankPaymentInfoModel.setTransactionID(bankResponseData.getTransaction().getTransactionID());
                getModelService().save(bankPaymentInfoModel);
            }
        }

        LOG.info("appEvent=AdnocPayment,Checkout payment initiation complete for cartId={}", cartModel.getCode());
        return adnocPaymentInitiateResponseData;
    }

    @Override
    public AdnocPaymentInitiateResponseData initiatePayment(final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData, final String paymentFlow)
    {
        LOG.info("appEvent=AdnocPayment,Initiating payment with Payment flow={} and paymentType={}", paymentFlow, adnocPaymentInitiateRequestData.getPaymentType());
        final CheckoutPaymentType checkoutPaymentType = CheckoutPaymentType.valueOf(adnocPaymentInitiateRequestData.getPaymentType());
        final AdnocInitiatePaymentStrategy initiatePaymentStrategy = getInitiatePaymentStrategiesMap().get(checkoutPaymentType);
        if (Objects.isNull(initiatePaymentStrategy))
        {
            LOG.error("appEvent=AdnocPayment,No strategy found for paymentType={}", adnocPaymentInitiateRequestData.getPaymentType());
            throw new IllegalStateException("Unsupported payment type: " + adnocPaymentInitiateRequestData.getPaymentType());
        }

        final AdnocPaymentInitiateResponseData adnocPaymentInitiateResponseData = initiatePaymentStrategy.initiatePayment(adnocPaymentInitiateRequestData, paymentFlow);
        LOG.info("appEvent=AdnocPayment,Payment initiated response generated for UTRN={}", adnocPaymentInitiateResponseData.getUtrn());
        return adnocPaymentInitiateResponseData;
    }

    @Override
    public AdnocPaymentResponseData retrieveCheckoutPayment(final String resultIndicator)
    {
        LOG.info("appEvent=AdnocPayment,Starting checkout payment retrieval with resultIndicator={}", resultIndicator);
        if (getCartService().hasSessionCart() && (getCartService().getSessionCart().getPaymentInfo() instanceof final CreditCardPaymentInfoModel creditCardPaymentInfoModel)
                && StringUtils.equals(resultIndicator, creditCardPaymentInfoModel.getSuccessIndicator()))
        {
            LOG.debug("appEvent=AdnocPayment,Checking successIndicator={} against resultIndicator={}", creditCardPaymentInfoModel.getSuccessIndicator(), resultIndicator);
            final AdnocPaymentResponseData adnocPaymentResponseData = retrievePayment(creditCardPaymentInfoModel.getUtrnNumber());
            if (CollectionUtils.isNotEmpty(adnocPaymentResponseData.getTransaction())
                    && StringUtils.equals(AdnocFacadesConstants.SUCCESS, adnocPaymentResponseData.getTransaction().get(0).getResult()))
            {
                LOG.info("appEvent=AdnocPayment,Transaction is successful, saving card details..");
                savingCreditCardInfoMoreDetails(creditCardPaymentInfoModel, adnocPaymentResponseData);
            }
            else
            {
                LOG.warn("appEvent=AdnocPayment,Transaction result was not SUCCESS for UTRN={}", creditCardPaymentInfoModel.getUtrnNumber());
            }
            return adnocPaymentResponseData;
        }
        return null;
    }

    @Override
    public AdnocPaymentResponseData retrievePayment(final String resultIndicator)
    {
        final AdnocPaymentResponseData adnocPaymentResponseData = getAdnocPaymentService().retrievePayment(resultIndicator);
        LOG.info("appEvent=AdnocCardPayment, retrieved payment response for UTRN={}", resultIndicator);
        return adnocPaymentResponseData;
    }

    @Override
    public AdnocPaymentBankFinalizationResponseData finalizeCheckoutBankPayment(final String transactionID)
    {
        LOG.info("appEvent=AdnocBankPayment, Preparing to finalize bank transfer for transactionID={}", transactionID);
        if (getCartService().hasSessionCart() && (getCartService().getSessionCart().getPaymentInfo() instanceof final BankPaymentInfoModel bankPaymentInfoModel)
                && StringUtils.equals(transactionID, bankPaymentInfoModel.getTransactionID()))
        {
            final AdnocPaymentBankFinalizationRequestData adnocPaymentBankFinalizationRequestData = createAdnocPaymentBankFinalizationRequestData(transactionID);
            final AdnocPaymentBankFinalizationResponseData adnocPaymentBankFinalizationResponseData = finalizeBankPayment(adnocPaymentBankFinalizationRequestData);
            if (Objects.nonNull(adnocPaymentBankFinalizationResponseData.getTransaction())
                    && StringUtils.equals(SUCCESS_RESPONSE_CODE, adnocPaymentBankFinalizationResponseData.getTransaction().getResponseCode()))
            {
                LOG.info("appEvent=AdnocBankPayment,Transaction is successful, saving card details..");
                savingBankPaymentInfoMoreDetails(bankPaymentInfoModel, adnocPaymentBankFinalizationResponseData, transactionID);
            }
            else
            {
                LOG.warn("appEvent=AdnocBankPayment,Transaction result was not SUCCESS for UTRN={}", bankPaymentInfoModel.getUtrnNumber());
                throw new RuntimeException("Bank finalization failed after maxAttempts attempts with ResponseCode=" + adnocPaymentBankFinalizationResponseData.getTransaction().getResponseCode());
            }
            return adnocPaymentBankFinalizationResponseData;
        }
        LOG.error("appEvent=AdnocBankPayment, Cart not found or invalid transactionId={}", transactionID);
        return null;
    }

    @Override
    public AdnocPaymentBankFinalizationResponseData finalizeBankPayment(final AdnocPaymentBankFinalizationRequestData adnocPaymentBankFinalizationRequestData)
    {
        int currentAttempt = 0;
        String responseCode = null;
        AdnocPaymentBankFinalizationResponseData adnocPaymentBankFinalizationResponseData;
        final String transactionID = adnocPaymentBankFinalizationRequestData.getFinalization().getTransactionID();
        do
        {
            currentAttempt++;
            LOG.info("appEvent=AdnocBankPayment, currentAttempt={} for transactionID={}.", currentAttempt, transactionID);
            adnocPaymentBankFinalizationResponseData = getAdnocPaymentService().finalizeBankTransfer(adnocPaymentBankFinalizationRequestData);
            if (Objects.nonNull(adnocPaymentBankFinalizationResponseData))
            {
                AdnocPaymentBankTransactionFinalizationResponseData adnocPaymentBankFinalizationResponseDataTransaction = adnocPaymentBankFinalizationResponseData.getTransaction();
                if (Objects.nonNull(adnocPaymentBankFinalizationResponseDataTransaction))
                {
                    responseCode = adnocPaymentBankFinalizationResponseDataTransaction.getResponseCode();
                    LOG.info("appEvent=AdnocBankPayment, responseCode={} for transactionID={}.", responseCode, transactionID);
                    if (StringUtils.equals(SUCCESS_RESPONSE_CODE, responseCode))
                    {
                        adnocPaymentBankFinalizationResponseDataTransaction.setTransactionId(transactionID);
                        return adnocPaymentBankFinalizationResponseData;
                    }
                }
            }
        } while (currentAttempt < getMaxAttempts() && BANK_PAYMENT_FINALIZE_RETRY_RESPONSE_CODES.contains(responseCode));

        return adnocPaymentBankFinalizationResponseData;
    }

    private AdnocPaymentBankFinalizationRequestData createAdnocPaymentBankFinalizationRequestData(final String transactionID)
    {
        LOG.info("appEvent=AdnocBankPayment, Creating AdnocPaymentBankFinalizationRequestData for transactionID={}", transactionID);
        final AdnocPaymentBankTransactionFinalizationRequestData adnocPaymentBankTransactionFinalizationRequestData = new AdnocPaymentBankTransactionFinalizationRequestData();
        final AdnocPaymentBankFinalizationRequestData paymentBankFinalizationRequestData = new AdnocPaymentBankFinalizationRequestData();
        adnocPaymentBankTransactionFinalizationRequestData.setTransactionID(transactionID);
        final Configuration configuration = getConfigurationService().getConfiguration();
        adnocPaymentBankTransactionFinalizationRequestData.setCustomer(configuration.getString(ADNOC_BANK_FINALIZATION_CUSTOMER));
        paymentBankFinalizationRequestData.setFinalization(adnocPaymentBankTransactionFinalizationRequestData);
        return paymentBankFinalizationRequestData;
    }

    @Override
    public void savingCreditCardInfoMoreDetails(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final AdnocPaymentResponseData adnocPaymentResponseData)
    {
        final List<AdnocTransactionData> adnocTransactionData = adnocPaymentResponseData.getTransaction();
        final AdnocTransactionDetailsData adnocTransactionDetailsData = adnocTransactionData.get(0).getTransaction();
        LOG.info("appEvent=AdnocPayment,Saving credit card info RRN={}, AuthCode={}, Brand={}",
                adnocTransactionDetailsData.getReceipt(),
                adnocTransactionDetailsData.getAuthorizationCode(),
                adnocPaymentResponseData.getSourceOfFunds().getProvided().getCard().getBrand());
        creditCardPaymentInfoModel.setRrnNumber(adnocTransactionDetailsData.getReceipt());
        creditCardPaymentInfoModel.setAuthorizationCode(adnocTransactionDetailsData.getAuthorizationCode());
        creditCardPaymentInfoModel.setType(CreditCardType.valueOf(adnocPaymentResponseData.getSourceOfFunds().getProvided().getCard().getBrand()));
        creditCardPaymentInfoModel.setNumber(adnocTransactionData.get(0).getSourceOfFunds().getProvided().getCard().getNumber());
        getModelService().save(creditCardPaymentInfoModel);
    }

    @Override
    public void savingBankPaymentInfoMoreDetails(final BankPaymentInfoModel bankPaymentInfoModel, final AdnocPaymentBankFinalizationResponseData adnocPaymentBankFinalizationResponseData, final String transactionID)
    {
        final AdnocPaymentBankTransactionFinalizationResponseData transaction = adnocPaymentBankFinalizationResponseData.getTransaction();
        bankPaymentInfoModel.setAmount(Double.valueOf(transaction.getAmount().getValue()));
        bankPaymentInfoModel.setAuthorizationCode(transaction.getApprovalCode());
        bankPaymentInfoModel.setRrnNumber(transactionID);
        bankPaymentInfoModel.setCardType(transaction.getCardBrand());
        bankPaymentInfoModel.setUtrnNumber(transaction.getOrderID());
        bankPaymentInfoModel.setAccount(transaction.getAccount());
        getModelService().save(bankPaymentInfoModel);
    }

    protected Map<CheckoutPaymentType, AdnocInitiatePaymentStrategy> getInitiatePaymentStrategiesMap()
    {
        return initiatePaymentStrategiesMap;
    }

    public void setInitiatePaymentStrategiesMap(final Map<CheckoutPaymentType, AdnocInitiatePaymentStrategy> initiatePaymentStrategiesMap)
    {
        this.initiatePaymentStrategiesMap = initiatePaymentStrategiesMap;
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

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    protected Map<CheckoutPaymentType, AdnocPaymentInitiateRequestCreationStrategy> getAdnocPaymentInitiateRequestCreationStrategyMap()
    {
        return adnocPaymentInitiateRequestCreationStrategyMap;
    }

    public void setAdnocPaymentInitiateRequestCreationStrategyMap(final Map<CheckoutPaymentType, AdnocPaymentInitiateRequestCreationStrategy> adnocPaymentInitiateRequestCreationStrategyMap)
    {
        this.adnocPaymentInitiateRequestCreationStrategyMap = adnocPaymentInitiateRequestCreationStrategyMap;
    }

    protected AdnocPaymentService getAdnocPaymentService()
    {
        return adnocPaymentService;
    }

    public void setAdnocPaymentService(final AdnocPaymentService adnocPaymentService)
    {
        this.adnocPaymentService = adnocPaymentService;
    }

    protected int getMaxAttempts()
    {
        return maxAttempts;
    }

    public void setMaxAttempts(final int maxAttempts)
    {
        this.maxAttempts = maxAttempts;
    }
}
