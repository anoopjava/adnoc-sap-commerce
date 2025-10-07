package com.adnoc.facades.overdueinvoices.impl;

import com.adnoc.facades.company.overdue.invoice.data.AdnocOverduePaymentRequestData;
import com.adnoc.facades.constants.AdnocFacadesConstants;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceRequestData;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceResponseData;
import com.adnoc.facades.overdueinvoices.AdnocOverdueInvoiceFacade;
import com.adnoc.facades.payment.AdnocPaymentFacade;
import com.adnoc.facades.payment.bank.data.AdnocPaymentBankTransactionRegistrationResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentCardInitiateCheckoutResponseData;
import com.adnoc.facades.payment.card.data.AdnocPaymentResponseData;
import com.adnoc.facades.payment.data.*;
import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import com.adnoc.service.exception.AdnocPaymentException;
import com.adnoc.service.model.AdnocOverduePaymentTransactionModel;
import com.adnoc.service.model.BankPaymentInfoModel;
import com.adnoc.service.order.payment.transaction.AdnocPaymentInitiateRequestCreationStrategy;
import com.adnoc.service.order.payment.transaction.strategies.AdnocPaymentTransactionStrategy;
import com.adnoc.service.overdueinvoices.AdnocOverdueInvoicesService;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdnocOverdueInvoiceFacadeImpl implements AdnocOverdueInvoiceFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocOverdueInvoiceFacadeImpl.class);

    public static final String ADNOC_OVERDUE_INVOICES_PAYMENT_TYPES = "adnoc.overdue.invoices.payment.types";
    public static final String OVERDUEINVOICE = "overdueinvoice";
    public static final String SUCCESS_RESPONSE_CODE = "0";
    public static final String BANK_PAYMENT_CUSTOMER_NAME = "adnoc.payment.banktransfer.customer.name";

    private AdnocOverdueInvoicesService adnocOverdueInvoicesService;
    private EnumerationService enumerationService;
    private Converter<CheckoutPaymentType, B2BPaymentTypeData> b2bPaymentTypeDataConverter;
    private ConfigurationService configurationService;
    private AdnocPaymentFacade adnocPaymentFacade;
    private AdnocPaymentTransactionStrategy adnocPaymentTransactionStrategy;
    private ModelService modelService;
    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;
    private Map<CheckoutPaymentType, AdnocPaymentInitiateRequestCreationStrategy> adnocPaymentInitiateRequestCreationStrategyMap;

    @Override
    public AdnocOverdueInvoiceResponseData getOverdueInvoices(final AdnocOverdueInvoiceRequestData adnocOverdueInvoiceRequestData)
    {
        return getAdnocOverdueInvoicesService().getOverdueInvoicesResponse(adnocOverdueInvoiceRequestData);
    }

    @Override
    public List<B2BPaymentTypeData> getPaymentTypes()
    {
        final List<CheckoutPaymentType> checkoutPaymentTypes = getEnumerationService()
                .getEnumerationValues(CheckoutPaymentType._TYPECODE);
        final String configuredPaymentTypes = getConfigurationService().getConfiguration().getString(ADNOC_OVERDUE_INVOICES_PAYMENT_TYPES, "CARD");
        final List<CheckoutPaymentType> overdueInvoicePaymentTypes = checkoutPaymentTypes.stream().filter(pt -> configuredPaymentTypes.contains(pt.getCode())).collect(Collectors.toList());
        return Converters.convertAll(overdueInvoicePaymentTypes, getB2bPaymentTypeDataConverter());
    }

    @Override
    public AdnocPaymentInitiateResponseData initiatePayment(final AdnocOverduePaymentRequestData adnocOverduePaymentRequestData)
    {
        AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData = createAdnocPaymentInitiateRequestData(adnocOverduePaymentRequestData);
        LOG.info("appEvent=Overdue, created payment request data for type:{}", adnocPaymentInitiateRequestData.getPaymentType());

        final CheckoutPaymentType checkoutPaymentType = CheckoutPaymentType.valueOf(adnocPaymentInitiateRequestData.getPaymentType());
        final AdnocPaymentInitiateRequestCreationStrategy adnocPaymentInitiateRequestCreationStrategy = getAdnocPaymentInitiateRequestCreationStrategyMap().get(checkoutPaymentType);
        if (Objects.isNull(adnocPaymentInitiateRequestCreationStrategy))
        {
            LOG.error("appEvent=AdnocPayment,No strategy found for paymentType={}", adnocPaymentInitiateRequestData.getPaymentType());
            throw new IllegalStateException("Unsupported payment type: " + adnocPaymentInitiateRequestData.getPaymentType());
        }
        adnocPaymentInitiateRequestData = adnocPaymentInitiateRequestCreationStrategy.createPaymentInitiateRequest(adnocPaymentInitiateRequestData, OVERDUEINVOICE);

        final AdnocPaymentInitiateResponseData adnocPaymentInitiateResponseData = getAdnocPaymentFacade().initiatePayment(adnocPaymentInitiateRequestData, OVERDUEINVOICE);
        if (adnocPaymentInitiateResponseData instanceof final AdnocPaymentCardInitiateCheckoutResponseData adnocPaymentCardInitiateCheckoutResponseData)
        {
            LOG.debug("appEvent=AdnocOverduePayment,Creating payment transaction for UTRN={}, successIndicator={}",
                    adnocPaymentCardInitiateCheckoutResponseData.getUtrn(),
                    adnocPaymentCardInitiateCheckoutResponseData.getSuccessIndicator());
            getAdnocPaymentTransactionStrategy().createPaymentTransaction(adnocPaymentCardInitiateCheckoutResponseData.getUtrn(), adnocPaymentCardInitiateCheckoutResponseData.getSuccessIndicator(), adnocOverduePaymentRequestData);
        }
        else if (adnocPaymentInitiateResponseData instanceof final AdnocPaymentBankTransactionRegistrationResponseData adnocPaymentBankTransactionRegistrationResponseData)
        {
            LOG.debug("appEvent=AdnocBankPayment,Creating payment transaction for UTRN={}, successIndicator={}",
                    adnocPaymentBankTransactionRegistrationResponseData.getUtrn(),
                    adnocPaymentBankTransactionRegistrationResponseData.getTransaction().getTransactionID());
            getAdnocPaymentTransactionStrategy().createPaymentTransaction(adnocPaymentBankTransactionRegistrationResponseData.getUtrn(), adnocPaymentBankTransactionRegistrationResponseData.getTransaction().getTransactionID(), adnocOverduePaymentRequestData);
        }
        LOG.info("appEvent=AdnocOverduePayment,Completed overdue payment initiation for payerId={}", adnocOverduePaymentRequestData.getPayerId());
        return adnocPaymentInitiateResponseData;
    }

    private AdnocPaymentInitiateRequestData createAdnocPaymentInitiateRequestData(final AdnocOverduePaymentRequestData adnocOverduePaymentRequestData)
    {
        LOG.info("appEvent=Overdue, createPaymentRequest method Started creating payment request");
        final AdnocPaymentInitiateRequestData adnocPaymentInitiateRequestData = new AdnocPaymentInitiateRequestData();
        adnocPaymentInitiateRequestData.setPayerId(adnocOverduePaymentRequestData.getPayerId());
        adnocPaymentInitiateRequestData.setPaymentType(adnocOverduePaymentRequestData.getPaymentType());
        adnocPaymentInitiateRequestData.setPaymentAmount(adnocOverduePaymentRequestData.getTotalAmount());
        adnocPaymentInitiateRequestData.setCurrency(adnocOverduePaymentRequestData.getCurrency());
        LOG.info("appEvent=Overdue, createPaymentRequest()- Finished building payment request for payerId={}", adnocPaymentInitiateRequestData.getPayerId());
        return adnocPaymentInitiateRequestData;
    }

    @Override
    public AdnocPaymentResponseData retrievePayment(final String resultIndicator)
    {
        LOG.info("appEvent=AdnocOverduePayment,Retrieving payment for resultIndicator={}", resultIndicator);
        final AdnocOverduePaymentTransactionModel adnocOverduePaymentTransactionModel = getAdnocOverdueInvoicesService().getAdnocOverduePaymentTransaction(resultIndicator);
        if (Objects.nonNull(adnocOverduePaymentTransactionModel) && StringUtils.equals(resultIndicator, adnocOverduePaymentTransactionModel.getCode()))
        {
            LOG.debug("appEvent=AdnocOverduePayment,Found AdnocOverduePaymentTransactionModel for resultIndicator={}, RequestId={}",
                    resultIndicator, adnocOverduePaymentTransactionModel.getRequestId());
            final AdnocPaymentResponseData adnocPaymentResponseData = getAdnocPaymentFacade().retrievePayment(adnocOverduePaymentTransactionModel.getRequestId());
            if (CollectionUtils.isNotEmpty(adnocPaymentResponseData.getTransaction())
                    && StringUtils.equals(AdnocFacadesConstants.SUCCESS, adnocPaymentResponseData.getTransaction().get(0).getResult()))
            {
                LOG.info("appEvent=AdnocOverduePayment,Payment successful for resultIndicator={}. Processing payment details.", resultIndicator);
                if (adnocOverduePaymentTransactionModel.getInfo() instanceof final CreditCardPaymentInfoModel creditCardPaymentInfoModel)
                {
                    getAdnocPaymentFacade().savingCreditCardInfoMoreDetails(creditCardPaymentInfoModel, adnocPaymentResponseData);
                    getAdnocOutboundReplicationDirector().scheduleOutboundTask(adnocOverduePaymentTransactionModel);
                    LOG.info("appEvent=AdnocOverduePayment,Payment details saved successfully for resultIndicator={}. Outbound task scheduled.", resultIndicator);
                }
            }
            else
            {
                LOG.error("appEvent=AdnocOverduePayment,Payment failed or no transaction found for resultIndicator={}", resultIndicator);
            }
            return adnocPaymentResponseData;
        }
        LOG.warn("appEvent=AdnocOverduePayment,No matching AdnocOverduePaymentTransactionModel found for resultIndicator={}", resultIndicator);
        throw new AdnocPaymentException("No matching AdnocOverduePaymentTransactionModel found for resultIndicator: " + resultIndicator);
    }

    @Override
    public AdnocPaymentBankFinalizationResponseData finalizeBankTransfer(final String transactionID)
    {
        LOG.info("appEvent=AdnocBankPayment, finalize bank payment for transactionID:{}", transactionID);
        final AdnocOverduePaymentTransactionModel adnocOverduePaymentTransactionModel = getAdnocOverdueInvoicesService().getAdnocOverduePaymentTransaction(transactionID);
        if (Objects.nonNull(adnocOverduePaymentTransactionModel) && StringUtils.equals(transactionID, adnocOverduePaymentTransactionModel.getCode()))
        {
            LOG.debug("appEvent=AdnocOverduePayment,Found AdnocOverduePaymentTransactionModel for transactionID={}, RequestId={}",
                    transactionID, adnocOverduePaymentTransactionModel.getRequestId());

            final AdnocPaymentBankTransactionFinalizationRequestData adnocPaymentBankTransactionFinalizationRequestData = new AdnocPaymentBankTransactionFinalizationRequestData();
            adnocPaymentBankTransactionFinalizationRequestData.setTransactionID(transactionID);
            adnocPaymentBankTransactionFinalizationRequestData.setCustomer(getConfigurationService().getConfiguration().getString(BANK_PAYMENT_CUSTOMER_NAME));
            final AdnocPaymentBankFinalizationRequestData adnocPaymentBankFinalizationRequestData = new AdnocPaymentBankFinalizationRequestData();
            adnocPaymentBankFinalizationRequestData.setFinalization(adnocPaymentBankTransactionFinalizationRequestData);
            final AdnocPaymentBankFinalizationResponseData adnocPaymentBankFinalizationResponseData = adnocPaymentFacade.finalizeBankPayment(adnocPaymentBankFinalizationRequestData);
            if (Objects.nonNull(adnocPaymentBankFinalizationResponseData.getTransaction()) && StringUtils.equals(SUCCESS_RESPONSE_CODE, adnocPaymentBankFinalizationResponseData.getTransaction().getResponseCode()))
            {
                if (adnocOverduePaymentTransactionModel.getInfo() instanceof final BankPaymentInfoModel bankPaymentInfoModel)
                {
                    LOG.info("appEvent=AdnocBankPayment,Transaction is successful, saving bank payment details..");
                    getAdnocPaymentFacade().savingBankPaymentInfoMoreDetails(bankPaymentInfoModel, adnocPaymentBankFinalizationResponseData, transactionID);
                    getAdnocOutboundReplicationDirector().scheduleOutboundTask(adnocOverduePaymentTransactionModel);
                }
            }
            else
            {
                LOG.warn("appEvent=AdnocBankPayment,Transaction result was not SUCCESS for Payer:{}", adnocPaymentBankFinalizationResponseData.getTransaction().getOrderID());
                throw new RuntimeException("Bank finalization failed after maxAttempts attempts with ResponseCode=" + adnocPaymentBankFinalizationResponseData.getTransaction().getResponseCode());
            }
            return adnocPaymentBankFinalizationResponseData;
        }
        LOG.warn("appEvent=AdnocOverduePayment,No matching AdnocOverduePaymentTransactionModel found for transactionID={}", transactionID);
        throw new AdnocPaymentException("No matching AdnocOverduePaymentTransactionModel found for transactionID: " + transactionID);
    }

    protected AdnocOverdueInvoicesService getAdnocOverdueInvoicesService()
    {
        return adnocOverdueInvoicesService;
    }

    public void setAdnocOverdueInvoicesService(final AdnocOverdueInvoicesService adnocOverdueInvoicesService)
    {
        this.adnocOverdueInvoicesService = adnocOverdueInvoicesService;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    protected Converter<CheckoutPaymentType, B2BPaymentTypeData> getB2bPaymentTypeDataConverter()
    {
        return b2bPaymentTypeDataConverter;
    }

    public void setB2bPaymentTypeDataConverter(final Converter<CheckoutPaymentType, B2BPaymentTypeData> b2bPaymentTypeDataConverter)
    {
        this.b2bPaymentTypeDataConverter = b2bPaymentTypeDataConverter;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    protected AdnocPaymentFacade getAdnocPaymentFacade()
    {
        return adnocPaymentFacade;
    }

    public void setAdnocPaymentFacade(final AdnocPaymentFacade adnocPaymentFacade)
    {
        this.adnocPaymentFacade = adnocPaymentFacade;
    }

    protected AdnocPaymentTransactionStrategy getAdnocPaymentTransactionStrategy()
    {
        return adnocPaymentTransactionStrategy;
    }

    public void setAdnocPaymentTransactionStrategy(final AdnocPaymentTransactionStrategy adnocPaymentTransactionStrategy)
    {
        this.adnocPaymentTransactionStrategy = adnocPaymentTransactionStrategy;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected AdnocOutboundReplicationDirector getAdnocOutboundReplicationDirector()
    {
        return adnocOutboundReplicationDirector;
    }

    public void setAdnocOutboundReplicationDirector(final AdnocOutboundReplicationDirector adnocOutboundReplicationDirector)
    {
        this.adnocOutboundReplicationDirector = adnocOutboundReplicationDirector;
    }

    protected Map<CheckoutPaymentType, AdnocPaymentInitiateRequestCreationStrategy> getAdnocPaymentInitiateRequestCreationStrategyMap()
    {
        return adnocPaymentInitiateRequestCreationStrategyMap;
    }

    public void setAdnocPaymentInitiateRequestCreationStrategyMap(final Map<CheckoutPaymentType, AdnocPaymentInitiateRequestCreationStrategy> adnocPaymentInitiateRequestCreationStrategyMap)
    {
        this.adnocPaymentInitiateRequestCreationStrategyMap = adnocPaymentInitiateRequestCreationStrategyMap;
    }
}
