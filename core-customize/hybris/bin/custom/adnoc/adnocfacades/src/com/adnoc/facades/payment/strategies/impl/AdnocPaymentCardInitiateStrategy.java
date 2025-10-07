package com.adnoc.facades.payment.strategies.impl;

import com.adnoc.facades.payment.card.data.*;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;
import com.adnoc.facades.payment.strategies.AdnocInitiatePaymentStrategy;
import com.adnoc.service.constants.AdnocserviceConstants;
import com.adnoc.service.payment.AdnocPaymentService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AdnocPaymentCardInitiateStrategy implements AdnocInitiatePaymentStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocPaymentCardInitiateStrategy.class);

    public static final String ADNOC_HQSITE_CODE = "adnoc.payment.hqsite.code";
    public static final String ADNOC_SHIFT_CODE = "adnoc.payment.shift.code";
    public static final String ADNOC_LOB_CODE = "adnoc.payment.lob.code";
    private final String pattern = "yyyyMMdd";
    private static final int TRANSACTION_NUMBER_LENGTH = 10;

    private AdnocPaymentService adnocPaymentService;
    private ConfigurationService configurationService;

    @Override
    public AdnocPaymentInitiateResponseData initiatePayment(final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData,final String paymentFlow)
    {
        LOG.info("appEvent=AdnocCardPayment, initiated card payment for payer:{}",adnocPaymentInitiateRequestData.getPayerId());
        final AdnocPaymentCardInitiateCheckoutRequestData adnocPaymentCardInitiateCheckoutRequestData = createAdnocPaymentCardInitiateCheckoutRequestData(adnocPaymentInitiateRequestData);
        final AdnocPaymentInitiateResponseData adnocPaymentInitiateResponseData = getAdnocPaymentService().initiatePayment(adnocPaymentCardInitiateCheckoutRequestData);
        adnocPaymentInitiateResponseData.setUtrn(adnocPaymentCardInitiateCheckoutRequestData.getOrder().getId());
        return adnocPaymentInitiateResponseData;
    }

    private AdnocPaymentCardInitiateCheckoutRequestData createAdnocPaymentCardInitiateCheckoutRequestData(final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData)
    {
        LOG.info("appEvent=AdnocCardPayment, initiated card payment transaction data");
        final AdnocPaymentCardInitiateCheckoutRequestData adnocPaymentCardInitiateCheckoutRequestData = new AdnocPaymentCardInitiateCheckoutRequestData();
        final Configuration configuration = getConfigurationService().getConfiguration();
        adnocPaymentCardInitiateCheckoutRequestData.setApiOperation(configuration.getString(AdnocserviceConstants.INITIATECHECKOUT));

        final AdnocPaymentInteractionData adnocPaymentInteractionData = new AdnocPaymentInteractionData();
        adnocPaymentInteractionData.setOperation(configuration.getString(AdnocserviceConstants.OPERATION));
        adnocPaymentInteractionData.setReturnUrl(adnocPaymentInitiateRequestData.getReturnUrl());
        adnocPaymentInteractionData.setCancelUrl(adnocPaymentInitiateRequestData.getCancelUrl());

        final AdnocPaymentMerchantData adnocPaymentMerchantData = new AdnocPaymentMerchantData();
        adnocPaymentMerchantData.setName(configuration.getString(AdnocserviceConstants.MERCHANT_NAME));
        adnocPaymentMerchantData.setUrl(configuration.getString(AdnocserviceConstants.MERCHANT_URL));
        adnocPaymentInteractionData.setMerchant(adnocPaymentMerchantData);

        final AdnocPaymentDisplayControlData adnocPaymentDisplayControlData = new AdnocPaymentDisplayControlData();
        adnocPaymentDisplayControlData.setBillingAddress(configuration.getString(AdnocserviceConstants.BILLING_ADDRESS));
        adnocPaymentInteractionData.setDisplayControl(adnocPaymentDisplayControlData);

        adnocPaymentCardInitiateCheckoutRequestData.setInteraction(adnocPaymentInteractionData);
        final AdnocPaymentOrderData adnocPaymentOrderData = createAdnocPaymentOrderData(adnocPaymentInitiateRequestData);
        adnocPaymentCardInitiateCheckoutRequestData.setOrder(adnocPaymentOrderData);
        return adnocPaymentCardInitiateCheckoutRequestData;
    }

    private AdnocPaymentOrderData createAdnocPaymentOrderData(final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData)
    {
        final AdnocPaymentOrderData adnocPaymentOrderData = new AdnocPaymentOrderData();
        adnocPaymentOrderData.setCurrency(adnocPaymentInitiateRequestData.getCurrency());
        final BigDecimal totalPaymentAmount = BigDecimal.valueOf(adnocPaymentInitiateRequestData.getPaymentAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
        adnocPaymentOrderData.setAmount(totalPaymentAmount);
        adnocPaymentOrderData.setId(generateUtrnCode(adnocPaymentInitiateRequestData.getPayerId()));
        adnocPaymentOrderData.setDescription(adnocPaymentInitiateRequestData.getDescription());
        if (adnocPaymentInitiateRequestData instanceof AdnocPaymentCardInitiateRequestData adnocCardPaymentInitiateRequestData)
        {
            if (Objects.nonNull(adnocCardPaymentInitiateRequestData.getItemAmount()))
            {
                adnocPaymentOrderData.setItemAmount(adnocCardPaymentInitiateRequestData.getItemAmount());
            }
            if (Objects.nonNull(adnocCardPaymentInitiateRequestData.getShippingAndHandlingAmount()))
            {
                adnocPaymentOrderData.setShippingAndHandlingAmount(adnocCardPaymentInitiateRequestData.getShippingAndHandlingAmount());
            }
            if (Objects.nonNull(adnocCardPaymentInitiateRequestData.getTaxAmount()))
            {
                adnocPaymentOrderData.setTaxAmount(adnocCardPaymentInitiateRequestData.getTaxAmount());
            }
            if (Objects.nonNull(adnocCardPaymentInitiateRequestData.getDiscount()) && Objects.nonNull(adnocCardPaymentInitiateRequestData.getDiscount().getAmount()))
            {
                adnocCardPaymentInitiateRequestData.getDiscount().setAmount(Math.abs(adnocCardPaymentInitiateRequestData.getDiscount().getAmount()));
                adnocPaymentOrderData.setDiscount(adnocCardPaymentInitiateRequestData.getDiscount());
            }
        }
        return adnocPaymentOrderData;
    }

    private String generateUtrnCode(final String idValue)
    {
        final Configuration configuration = getConfigurationService().getConfiguration();
        final LocalDateTime localDateTime = LocalDateTime.now();
        final String datePart = localDateTime.toLocalDate().format(DateTimeFormatter.ofPattern(pattern));
        final String timePart = localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HHmm"));
        return datePart +
                configuration.getString(ADNOC_HQSITE_CODE) +
                configuration.getString(ADNOC_SHIFT_CODE) +
                configuration.getString(ADNOC_LOB_CODE) +
                timePart + formatTrxNumber(idValue);
    }

    private String formatTrxNumber(final String idValue)
    {
        return idValue.length() < TRANSACTION_NUMBER_LENGTH
                ? StringUtils.leftPad(idValue, TRANSACTION_NUMBER_LENGTH, '0')
                : idValue.length() > TRANSACTION_NUMBER_LENGTH
                ? idValue.substring(idValue.length() - TRANSACTION_NUMBER_LENGTH)
                : idValue;
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
