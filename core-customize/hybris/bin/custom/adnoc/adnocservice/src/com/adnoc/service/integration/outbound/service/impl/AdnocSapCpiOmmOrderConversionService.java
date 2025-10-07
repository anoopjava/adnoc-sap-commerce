/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.integration.outbound.service.impl;

import com.adnoc.facades.company.data.SAPSalesOrganizationData;
import com.adnoc.facades.data.AdnocOutboundDocumentData;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.enums.IncoTerms;
import com.adnoc.service.model.BankPaymentInfoModel;
import com.adnoc.service.model.CreditLimitPaymentInfoModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PaymentCsvColumns;
import de.hybris.platform.sap.sapcpiadapter.data.*;
import de.hybris.platform.sap.sapcpiorderexchange.exceptions.SapCpiOmmOrderConversionServiceException;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderConversionService;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SAP CPI OMM Order conversion service converts OrderModel to SapCpiOrder and OrderCancelRecordEntryModel to List<SapCpiOrderCancellation>
 */
public class AdnocSapCpiOmmOrderConversionService extends SapCpiOmmOrderConversionService
{
    private static final Logger LOG = LogManager.getLogger(AdnocSapCpiOmmOrderConversionService.class);

    private AdnocConfigService adnocConfigService;
    private ConfigurationService configurationService;
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
    private MediaService mediaService;

    @Override
    public SapCpiOrder convertOrderToSapCpiOrder(final OrderModel orderModel)
    {
        LOG.info("appEvent=AdnocSapCpiOmmOrder,entering convertOrderToSapCpiOrder method..");

        final SapCpiOrder sapCpiOrder = new SapCpiOrder();

        getSapOrderContributor().createRows(orderModel).stream().findFirst().ifPresent(row -> {
            sapCpiOrder.setSapCpiConfig(mapOrderConfigInfo(orderModel));
            setAdnocSapCpiOrderFromRawItem(sapCpiOrder, row);

            final SAPSalesOrganizationModel sapSalesOrganizationModel = orderModel.getSapSalesOrganization();
            sapCpiOrder.setSalesOrganization(sapSalesOrganizationModel.getSalesOrganization());
            sapCpiOrder.setDistributionChannel(sapSalesOrganizationModel.getDistributionChannel());
            sapCpiOrder.setDivision(sapSalesOrganizationModel.getDivision());
            // Get the initial transaction type
            final String transactionTypeConfigIdentifier = isPickupOrder(orderModel) ?
                    sapSalesOrganizationModel.getDivision() + "_" + IncoTerms.PICKUP : sapSalesOrganizationModel.getDivision();
            final String transactionType = getAdnocConfigService().getAdnocSapIntegrationCodeMap(IntegrationType.OUTBOUND, String.class, transactionTypeConfigIdentifier);
            sapCpiOrder.setTransactionType(transactionType);
            sapCpiOrder.setPaymentMode(orderModel.getPaymentType().getCode());
            sapCpiOrder.setDeliveryMode(orderModel.getDeliveryMode().getCode());
            sapCpiOrder.setPurchaseOrderNumber(orderModel.getPurchaseOrderNumber());
            sapCpiOrder.setPaymentInfo(orderModel.getPaymentInfo().getCode());
            sapCpiOrder.setPlantCode(orderModel.getSapPlantCode());
            if (Objects.nonNull(orderModel.getPaymentInfo()))
            {
                final PaymentInfoModel paymentInfoModel = orderModel.getPaymentInfo();
                sapCpiOrder.setMop(paymentInfoModel instanceof CreditCardPaymentInfoModel ? "9" : "W");
                sapCpiOrder.setUtrnNumber(paymentInfoModel.getUtrnNumber());
            }
            if (Objects.nonNull(orderModel.getQuoteReference()))
            {
                sapCpiOrder.setQuotation(orderModel.getQuoteReference().getSapOrderCode());
            }

            if (CollectionUtils.isNotEmpty(orderModel.getPaymentTransactions()))
            {
                final List<PaymentTransactionModel> paymentTransactions = orderModel.getPaymentTransactions();
                PaymentTransactionModel creditLimitTransaction = null;
                PaymentTransactionModel creditCardTransaction = null;
                PaymentTransactionModel bankPaymentTransaction = null;

                for (final PaymentTransactionModel paymentTransactionModel : paymentTransactions)
                {
                    final PaymentInfoModel paymentInfoModel = paymentTransactionModel.getInfo();
                    if (paymentInfoModel instanceof CreditLimitPaymentInfoModel)
                    {
                        creditLimitTransaction = paymentTransactionModel;
                    }
                    else if (paymentInfoModel instanceof final CreditCardPaymentInfoModel creditCardPaymentInfoModel)
                    {
                        creditCardTransaction = paymentTransactionModel;
                        sapCpiOrder.setCardNumber(creditCardPaymentInfoModel.getNumber());
                        sapCpiOrder.setCardType(creditCardPaymentInfoModel.getType().getCode());
                    }
                    else if (paymentInfoModel instanceof BankPaymentInfoModel)
                    {
                        bankPaymentTransaction = paymentTransactionModel;
                    }
                }
                if (Objects.nonNull(creditLimitTransaction) && Objects.nonNull(bankPaymentTransaction))
                {
                    setPaymentFields1(sapCpiOrder, bankPaymentTransaction, "ZBTR");
                    setPaymentFields2(sapCpiOrder, creditLimitTransaction, "ZCRE");
                }
                else if (Objects.nonNull(creditLimitTransaction) && Objects.nonNull(creditCardTransaction))
                {
                    setPaymentFields1(sapCpiOrder, creditCardTransaction, "ZBAN");
                    setPaymentFields2(sapCpiOrder, creditLimitTransaction, "ZCRE");
                }
                else if (Objects.nonNull(creditLimitTransaction))
                {
                    setPaymentFields1(sapCpiOrder, creditLimitTransaction, "ZCRE");
                }
                else if (Objects.nonNull(bankPaymentTransaction))
                {
                    setPaymentFields1(sapCpiOrder, bankPaymentTransaction, "ZBTR");
                }
                else if (Objects.nonNull(creditCardTransaction))
                {
                    setPaymentFields1(sapCpiOrder, creditCardTransaction, "ZBAN");
                }
            }
            sapCpiOrder.setTotalAmount(orderModel.getTotalPrice());

            B2BUnitModel b2BUnitModel = null;
            if (StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getSapCustomerID()))
            {
                b2BUnitModel = getB2BUnitService().getUnitForUid(orderModel.getDeliveryAddress().getSapCustomerID());
            }
            if (Objects.nonNull(b2BUnitModel) && CollectionUtils.isNotEmpty(b2BUnitModel.getDeliveryTypes()))
            {
                sapCpiOrder.setDeliveryType(b2BUnitModel.getDeliveryTypes().stream().findFirst().get().getCode());
            }
            if (Objects.nonNull(orderModel.getPoDocument()))
            {
                final AdnocOutboundDocumentData adnocOutboundDocumentData = convertToOutboundDocumentData(orderModel.getPoDocument(), "PO_Document");
                sapCpiOrder.setPoDocument(adnocOutboundDocumentData);
            }
            sapCpiOrder.setSapCpiOrderItems(mapOrderItems(orderModel));
            sapCpiOrder.setSapCpiPartnerRoles(mapOrderPartners(orderModel));
            sapCpiOrder.setSapCpiOrderAddresses(mapOrderAddresses(orderModel));
            sapCpiOrder.setSapCpiOrderPriceComponents(mapOrderPrices(orderModel));
            sapCpiOrder.setSapCpiCreditCardPayments(mapCreditCards(orderModel));
            LOG.debug("appEvent=AdnocSapCpiOmmOrder,Completed setting SAP CPI order details for order:{}", orderModel.getCode());

        });

        if (LOG.isDebugEnabled())
        {
            LOG.debug(String.format("SCPI OMM order object: %n %s",
                    ReflectionToStringBuilder.toString(sapCpiOrder, ToStringStyle.MULTI_LINE_STYLE)));
        }
        LOG.info("appEvent=AdnocSapCpiOmmOrder,finished conversion of orderModel to SapCpiOrder");

        return sapCpiOrder;

    }

    private boolean isPickupOrder(final OrderModel order)
    {
        return order.getEntries().stream()
                .anyMatch(abstractOrderEntryModel -> Objects.equals(IncoTerms.PICKUP, abstractOrderEntryModel.getIncoTerms()));
    }

    private void setPaymentFields1(SapCpiOrder sapCpiOrder, PaymentTransactionModel txn, String mopType)
    {
        sapCpiOrder.setMopType1(mopType);
        sapCpiOrder.setRrnCode1(txn.getRrnNumber());
        sapCpiOrder.setAuthCode1(txn.getRequestToken());
        if (Objects.nonNull(txn.getPlannedAmount()))
        {
            BigDecimal scaled = txn.getPlannedAmount().setScale(2, BigDecimal.ROUND_DOWN);
            sapCpiOrder.setAmount1(scaled.doubleValue());
        }
    }


    private void setPaymentFields2(SapCpiOrder sapCpiOrder, PaymentTransactionModel txn, String mopType)
    {
        sapCpiOrder.setMopType2(mopType);
        if (Objects.nonNull(txn.getPlannedAmount()))
        {
            BigDecimal scaled = txn.getPlannedAmount().setScale(2, BigDecimal.ROUND_DOWN);
            sapCpiOrder.setAmount2(scaled.doubleValue());
        }
    }


    /**
     * @param documentMediaModel
     * @return
     */
    private AdnocOutboundDocumentData convertToOutboundDocumentData(final DocumentMediaModel documentMediaModel, final String supportedDocumentType)
    {
        final AdnocOutboundDocumentData adnocOutboundDocumentData = new AdnocOutboundDocumentData();
        adnocOutboundDocumentData.setSupportedDocumentType(supportedDocumentType);
        adnocOutboundDocumentData.setDocumentBase64(getMediaContentBase64Encoded(documentMediaModel));
        adnocOutboundDocumentData.setDocumentName(getFileName(documentMediaModel));
        adnocOutboundDocumentData.setDocumentType(getFileType(documentMediaModel));
        return adnocOutboundDocumentData;
    }

    private String getMediaContentBase64Encoded(final MediaModel mediaModel)
    {
        LOG.info("appEvent=AdnocOutboundPODocument,encoding media content to Base64 for media with code: {}", mediaModel.getCode());
        final byte[] mediaByteArray = getMediaService().getDataFromMedia(mediaModel);
        return Base64.getEncoder().encodeToString(mediaByteArray);
    }

    private String getFileName(final MediaModel mediaModel)
    {
        return mediaModel.getRealFileName();
    }

    private String getFileType(final MediaModel mediaModel)
    {
        return mediaModel.getMime();
    }

    private void setAdnocSapCpiOrderFromRawItem(final SapCpiOrder sapCpiOrder, final Map<String, Object> rawITem)
    {
        LOG.info("appEvent=AdnocSapCpiOmmOrder,setSapCpiOrderFromRawItem method being called");
        sapCpiOrder.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, rawITem));
        sapCpiOrder.setBaseStoreUid(mapAttribute(OrderCsvColumns.BASE_STORE, rawITem));
        sapCpiOrder.setCreationDate(mapDateAttribute(OrderCsvColumns.DATE, rawITem));
        sapCpiOrder.setCurrencyIsoCode(mapAttribute(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, rawITem));
        sapCpiOrder.setPaymentMode(mapAttribute(OrderCsvColumns.PAYMENT_MODE, rawITem));
        sapCpiOrder.setDeliveryMode(mapAttribute(OrderCsvColumns.DELIVERY_MODE, rawITem));
        sapCpiOrder.setChannel(mapAttribute(OrderCsvColumns.CHANNEL, rawITem));
        sapCpiOrder.setPurchaseOrderNumber(mapAttribute(OrderCsvColumns.PURCHASE_ORDER_NUMBER, rawITem));
    }


    @Override
    protected List<SapCpiOrderItem> mapOrderItems(final OrderModel orderModel)
    {
        final List<SapCpiOrderItem> sapCpiOrderItems = new ArrayList<>();
        LOG.info("appEvent=AdnocSapCpiOmmOrder, mapping order items for order:{}", orderModel.getCode());

        orderModel.getEntries().forEach(orderEntryModel -> {

            final SapCpiOrderItem sapCpiOrderItem = new SapCpiOrderItem();

            sapCpiOrderItem.setOrderId(orderModel.getCode());
            sapCpiOrderItem.setEntryNumber(String.valueOf((orderEntryModel.getEntryNumber() + 1) * 10));
            sapCpiOrderItem.setQuantity(orderEntryModel.getQuantity().toString());
            sapCpiOrderItem.setProductCode(orderEntryModel.getProduct().getCode());
            sapCpiOrderItem.setUnit(orderEntryModel.getUnit().getCode());
            sapCpiOrderItem.setProductName(orderEntryModel.getProduct().getName());
            if (Objects.equals(IncoTerms.PICKUP, orderEntryModel.getIncoTerms()))
            {
                sapCpiOrderItem.setIncoTerms(getAdnocConfigService().getAdnocSapIntegrationCodeMap(IntegrationType.OUTBOUND, IncoTerms.class, IncoTerms.PICKUP.getCode()));
            }
            else
            {
                sapCpiOrderItem.setIncoTerms(orderEntryModel.getIncoTerms().getCode());
            }
            if (Objects.nonNull(orderEntryModel.getNamedDeliveryDate()))
            {
                final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                final LocalDate namedDeliveryLocalDate = orderEntryModel.getNamedDeliveryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                sapCpiOrderItem.setNamedDeliveryDate(namedDeliveryLocalDate.format(dateTimeFormatter));
            }
            sapCpiOrderItems.add(sapCpiOrderItem);
            LOG.debug("appEvent=AdnocSapCpiOmmOrder, added order item:{}", orderEntryModel.getEntryNumber());

        });

        return sapCpiOrderItems;

    }

    @Override
    public List<SapCpiOrderCancellation> convertCancelOrderToSapCpiCancelOrder(
            final OrderCancelRecordEntryModel orderCancelRecordEntryModel)
    {
        LOG.info("appEvent=AdnocSapCpiOmmOrder,converting cancel order to sap cpi cancel order:{}", orderCancelRecordEntryModel.getCode());

        final SapCpiOrderCancellation sapCpiOrderCancellation = new SapCpiOrderCancellation();
        sapCpiOrderCancellation
                .setSapCpiConfig(mapOrderConfigInfo(orderCancelRecordEntryModel.getModificationRecord().getOrder()));
        sapCpiOrderCancellation.getSapCpiConfig().getSapCpiTargetSystem().setUrl(getConfigurationService().getConfiguration().getString("adnocoutbound.omm.cancelorder.s4url"));
        sapCpiOrderCancellation.setOrderId(orderCancelRecordEntryModel.getModificationRecord().getOrder().getCode());
        sapCpiOrderCancellation.setSapOrderCode(orderCancelRecordEntryModel.getModificationRecord().getOrder().getSapOrderCode());
        if (Objects.nonNull(orderCancelRecordEntryModel.getModificationRecord()) &&
                Objects.nonNull(orderCancelRecordEntryModel.getModificationRecord().getOrder()) &&
                Objects.nonNull(orderCancelRecordEntryModel.getModificationRecord().getOrder().getSapSalesOrganization()))
        {
            final SAPSalesOrganizationModel organization = orderCancelRecordEntryModel.getModificationRecord().getOrder().getSapSalesOrganization();
            final SAPSalesOrganizationData organizationData = new SAPSalesOrganizationData();
            organizationData.setSalesOrganization(organization.getSalesOrganization());
            organizationData.setDivision(organization.getDivision());
            organizationData.setDistributionChannel(organization.getDistributionChannel());
            sapCpiOrderCancellation.setSalesOrg(organizationData);
        }
        final List<SapCpiOrderCancellationItem> sapCpiOrderCancellationItems = new ArrayList<>();

        orderCancelRecordEntryModel.getOrderEntriesModificationEntries().forEach(cancellationItem -> {
            final SapCpiOrderCancellationItem sapCpiOrderCancellationItem = new SapCpiOrderCancellationItem();
            sapCpiOrderCancellationItem.setEntryNumber(cancellationItem.getOrderEntry().getEntryNumber().toString());
            sapCpiOrderCancellationItem.setProductCode(cancellationItem.getOrderEntry().getProduct().getCode());
            sapCpiOrderCancellationItem.setProductName(cancellationItem.getOrderEntry().getProduct().getName());
            Optional.ofNullable(((OrderEntryCancelRecordEntryModel) cancellationItem).getCancelReason())
                    .ifPresent(reason -> sapCpiOrderCancellationItem.setRejectionReason(reason.getCode()));
            sapCpiOrderCancellationItem.setQuantity(((OrderEntryCancelRecordEntryModel) cancellationItem).getCancelRequestQuantity().toString());
            sapCpiOrderCancellationItems.add(sapCpiOrderCancellationItem);
        });


        sapCpiOrderCancellation.setSapCpiOrderCancellationItems(sapCpiOrderCancellationItems);


        if (LOG.isDebugEnabled())
        {
            LOG.debug(String.format("SCPI cancel order object: %n %s",
                    ReflectionToStringBuilder.toString(sapCpiOrderCancellation, ToStringStyle.MULTI_LINE_STYLE)));
        }

        return Arrays.asList(sapCpiOrderCancellation);
    }

    @Override
    protected List<SapCpiCreditCardPayment> mapCreditCards(final OrderModel orderModel)
    {
        final List<SapCpiCreditCardPayment> sapCpiCreditCardPayments = new ArrayList<>();
        try
        {
            getSapPaymentContributor().createRows(orderModel).forEach(row -> {
                final SapCpiCreditCardPayment sapCpiCreditCardPayment = new SapCpiCreditCardPayment();
                sapCpiCreditCardPayment.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
                sapCpiCreditCardPayment.setCcOwner(mapAttribute(PaymentCsvColumns.CC_OWNER, row));
                sapCpiCreditCardPayment.setPaymentProvider(mapAttribute(PaymentCsvColumns.PAYMENT_PROVIDER, row));
                sapCpiCreditCardPayment.setSubscriptionId(mapAttribute(PaymentCsvColumns.SUBSCRIPTION_ID, row));
                final String requestId = mapAttribute(PaymentCsvColumns.REQUEST_ID, row);
                sapCpiCreditCardPayment.setRequestId(requestId);
                setAdnocSapCpiCreditCardPaymentValidityDate(sapCpiCreditCardPayment, row);
                sapCpiCreditCardPayments.add(sapCpiCreditCardPayment);
            });
        }
        catch (final RuntimeException ex)
        {
            throw new SapCpiOmmOrderConversionServiceException(String.format("Error occurs while setting the payment information for the order [%s]!", orderModel.getCode()), ex);
        }

        return sapCpiCreditCardPayments;

    }

    private void setAdnocSapCpiCreditCardPaymentValidityDate(final SapCpiCreditCardPayment sapCpiCreditCardPayment,
                                                             final Map<String, Object> rawItem)
    {
        final String month = mapAttribute(PaymentCsvColumns.VALID_TO_MONTH, rawItem);
        sapCpiCreditCardPayment.setValidToMonth(month);
        final String year = mapAttribute(PaymentCsvColumns.VALID_TO_YEAR, rawItem);
        if (year != null && month != null)
        {
            final YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
            sapCpiCreditCardPayment.setValidToYear(yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        else
        {
            sapCpiCreditCardPayment.setValidToYear(null);
        }
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
    {
        return b2BUnitService;
    }

    public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService)
    {
        this.b2BUnitService = b2BUnitService;
    }

    protected MediaService getMediaService()
    {
        return mediaService;
    }

    public void setMediaService(final MediaService mediaService)
    {
        this.mediaService = mediaService;
    }
}
