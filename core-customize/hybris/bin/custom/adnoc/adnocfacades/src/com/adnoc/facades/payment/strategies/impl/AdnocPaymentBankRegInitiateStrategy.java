package com.adnoc.facades.payment.strategies.impl;

import com.adnoc.facades.payment.bank.data.AdnocPaymentBankRegistrationRequestData;
import com.adnoc.facades.payment.bank.data.AdnocPaymentBankTransactionRegistrationResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentBankTransactionRegistrationRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;
import com.adnoc.facades.payment.strategies.AdnocInitiatePaymentStrategy;
import com.adnoc.service.payment.AdnocPaymentService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdnocPaymentBankRegInitiateStrategy implements AdnocInitiatePaymentStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocPaymentBankRegInitiateStrategy.class);

    public static final String ADNOC_BANK_TRANSFER_CHECKOUT_RETURN_PATH = "adnoc.banktransfer.registration.checkout.return.path";
    private static final String ADNOC_BANK_TRANSFER_OVERDUE_RETURN_PATH = "adnoc.banktransfer.registration.overdue.return.path";
    public static final String ADNOC_TRANSACTION_HINT = "adnoc.payment.banktransfer.transaction.hint";
    public static final String ADNOC_ORDER_NAME = "adnoc.payment.banktransfer.order.name";
    public static final String ADNOC_CHANNEL_NAME = "adnoc.payment.banktransfer.channel.name";
    public static final String ADNOC_CUSTOMER_NAME = "adnoc.payment.banktransfer.customer.name";
    public static final String ADNOC_STORE_NAME = "adnoc.payment.banktransfer.store.name";
    public static final String ADNOC_TERMINAL_NAME = "adnoc.payment.banktransfer.terminal.name";
    public static final String ADNOC_HQSITE_CODE = "adnoc.payment.hqsite.code";
    public static final String ADNOC_SHIFT_CODE = "adnoc.payment.shift.code";
    public static final String ADNOC_LOB_CODE = "adnoc.payment.lob.code";
    public static final String TIME_PATTERN = "HHmm";
    private final String DATE_PATTERN = "yyyyMMdd";
    private static final int TRANSACTION_NUMBER_LENGTH = 10;

    private AdnocPaymentService adnocPaymentService;
    private ConfigurationService configurationService;

    @Override
    public AdnocPaymentInitiateResponseData initiatePayment(final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData, final String paymentFlow)
    {
        LOG.info("appEvent=AdnocBankPayment,initiatePayment method start");
        final AdnocPaymentBankRegistrationRequestData adnocPaymentBankRegistrationRequestData = createAdnocPaymentBankRegistrationRequestData(adnocPaymentInitiateRequestData, paymentFlow);
        final AdnocPaymentBankTransactionRegistrationResponseData adnocPaymentBankTransactionRegistrationResponseData = getAdnocPaymentService().registerBankPayment(adnocPaymentBankRegistrationRequestData);
        adnocPaymentBankTransactionRegistrationResponseData.setUtrn(adnocPaymentBankRegistrationRequestData.getRegistration().getOrderID());
        return adnocPaymentBankTransactionRegistrationResponseData;
    }

    private AdnocPaymentBankRegistrationRequestData createAdnocPaymentBankRegistrationRequestData(final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData, final String paymentFlow)
    {
        LOG.info("appEvent=AdnocBankPayment,Setting Bank Registration Transaction information for Payer:{}", adnocPaymentInitiateRequestData.getPayerId());
        final AdnocPaymentBankRegistrationRequestData adnocPaymentBankRegistrationRequestData = new AdnocPaymentBankRegistrationRequestData();
        final AdnocPaymentBankTransactionRegistrationRequestData adnocPaymentBankTransactionRegistrationRequestData = new AdnocPaymentBankTransactionRegistrationRequestData();
        final Configuration configuration = getConfigurationService().getConfiguration();
        adnocPaymentBankTransactionRegistrationRequestData.setTransactionHint(configuration.getString(String.format(ADNOC_TRANSACTION_HINT)));
        adnocPaymentBankTransactionRegistrationRequestData.setOrderID(generateUtrnCode(adnocPaymentInitiateRequestData.getPayerId()));
        adnocPaymentBankTransactionRegistrationRequestData.setOrderName(configuration.getString(String.format(ADNOC_ORDER_NAME)));
        adnocPaymentBankTransactionRegistrationRequestData.setCurrency(adnocPaymentInitiateRequestData.getCurrency());
        adnocPaymentBankTransactionRegistrationRequestData.setChannel(configuration.getString(String.format(ADNOC_CHANNEL_NAME)));
        adnocPaymentBankTransactionRegistrationRequestData.setAmount(adnocPaymentInitiateRequestData.getPaymentAmount());
        adnocPaymentBankTransactionRegistrationRequestData.setCustomer(configuration.getString(String.format(ADNOC_CUSTOMER_NAME)));
        adnocPaymentBankTransactionRegistrationRequestData.setStore(configuration.getString(String.format(ADNOC_STORE_NAME)));
        adnocPaymentBankTransactionRegistrationRequestData.setTerminal(configuration.getString(String.format(ADNOC_TERMINAL_NAME)));

        final String returnPath = StringUtils.equalsIgnoreCase(paymentFlow, "CHECKOUT")
                ? configuration.getString(ADNOC_BANK_TRANSFER_CHECKOUT_RETURN_PATH)
                : configuration.getString(ADNOC_BANK_TRANSFER_OVERDUE_RETURN_PATH);

        adnocPaymentBankTransactionRegistrationRequestData.setReturnPath(returnPath);
        adnocPaymentBankRegistrationRequestData.setRegistration(adnocPaymentBankTransactionRegistrationRequestData);
        return adnocPaymentBankRegistrationRequestData;
    }

    private String generateUtrnCode(final String idValue)
    {
        final Configuration configuration = getConfigurationService().getConfiguration();
        final LocalDateTime localDateTime = LocalDateTime.now();
        final String datePart = localDateTime.toLocalDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        final String timePart = localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern(TIME_PATTERN));
        return datePart + timePart;
    }

    protected AdnocPaymentService getAdnocPaymentService()
    {
        return adnocPaymentService;
    }

    public void setAdnocPaymentService(final AdnocPaymentService adnocPaymentService)
    {
        this.adnocPaymentService = adnocPaymentService;
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
