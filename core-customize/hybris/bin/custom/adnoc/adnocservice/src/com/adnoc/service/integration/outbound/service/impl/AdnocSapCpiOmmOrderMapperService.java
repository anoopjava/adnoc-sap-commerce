/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.integration.outbound.service.impl;

import com.adnoc.facades.data.AdnocOutboundDocumentData;
import com.adnoc.service.enums.IncoTerms;
import com.adnoc.service.model.AdnocOutboundDocumentModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderItem;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderMapperService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * SAP CPI OMM Order Mapper Service
 */
public class AdnocSapCpiOmmOrderMapperService extends SapCpiOmmOrderMapperService
{
    private static final Logger LOG = LogManager.getLogger(AdnocSapCpiOmmOrderMapperService.class);

    @Override
    public void map(final OrderModel orderModel, final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel)
    {
        LOG.info("appEvent=AdnocSapCpiOmmOrderMapper,mapping orderModel to SAPCpiOutboundOrder, order ID: {}", orderModel.getCode());

        mapSapCpiOrderToSAPCpiOrderOutbound(getSapCpiOrderConversionService().convertOrderToSapCpiOrder(orderModel), sapCpiOutboundOrderModel);
        LOG.debug("appEvent=AdnocSapCpiOmmOrderMapper,mapping completed for orderId: {}", sapCpiOutboundOrderModel.getOrderId());

    }

    @Override
    protected void mapSapCpiOrderToSAPCpiOrderOutbound(final SapCpiOrder sapCpiOrder, final SAPCpiOutboundOrderModel sapCpiOutboundOrder)
    {
        LOG.debug("appEvent=AdnocSapCpiOmmOrderMapper: mapping sapCpiOrder to SAPCpiOutboundOrder,order Id:{} ", sapCpiOrder.getOrderId());

        super.mapSapCpiOrderToSAPCpiOrderOutbound(sapCpiOrder, sapCpiOutboundOrder);

        sapCpiOutboundOrder.setPlantCode(sapCpiOrder.getPlantCode());
        sapCpiOutboundOrder.setDeliveryType(sapCpiOrder.getDeliveryType());
        sapCpiOutboundOrder.setPaymentInfo(sapCpiOrder.getPaymentInfo());
        sapCpiOutboundOrder.setCardNumber(sapCpiOrder.getCardNumber());
        sapCpiOutboundOrder.setCardType(sapCpiOrder.getCardType());
        //Set Utrn Number
        sapCpiOutboundOrder.setUtrnNo(sapCpiOrder.getUtrnNumber());
        //Set Quote code
        if (StringUtils.isNotBlank(sapCpiOrder.getQuotation()))
        {
            sapCpiOutboundOrder.setQuotation(sapCpiOrder.getQuotation());
        }
        sapCpiOutboundOrder.setMop(sapCpiOrder.getMop());
        sapCpiOutboundOrder.setMopType1(sapCpiOrder.getMopType1());
        sapCpiOutboundOrder.setRrnCode1(sapCpiOrder.getRrnCode1());
        sapCpiOutboundOrder.setAuthCode1(sapCpiOrder.getAuthCode1());
        sapCpiOutboundOrder.setAmount1(sapCpiOrder.getAmount1());
        sapCpiOutboundOrder.setMopType2(sapCpiOrder.getMopType2());
        sapCpiOutboundOrder.setAmount2(sapCpiOrder.getAmount2());
        sapCpiOutboundOrder.setTotalAmount(sapCpiOrder.getTotalAmount());
        // Set PO Document
        if (Objects.nonNull(sapCpiOrder.getPoDocument()))
        {
            final AdnocOutboundDocumentModel adnocOutboundDocument = convertToOutboundDocumentModel(sapCpiOrder.getPoDocument());
            sapCpiOutboundOrder.setPoDocument(adnocOutboundDocument);
        }

        sapCpiOutboundOrder.setSapCpiConfig(mapOrderConfigInfo(sapCpiOrder.getSapCpiConfig()));
        sapCpiOutboundOrder.setSapCpiOutboundOrderItems(mapOrderItems(sapCpiOrder.getSapCpiOrderItems()));
        sapCpiOutboundOrder.setSapCpiOutboundPartnerRoles(mapOrderPartners(sapCpiOrder.getSapCpiPartnerRoles()));
        sapCpiOutboundOrder.setSapCpiOutboundAddresses(mapOrderAddresses(sapCpiOrder.getSapCpiOrderAddresses()));
        sapCpiOutboundOrder.setSapCpiOutboundPriceComponents(mapOrderPrices(sapCpiOrder.getSapCpiOrderPriceComponents()));
        sapCpiOutboundOrder.setSapCpiOutboundCardPayments(mapCreditCards(sapCpiOrder.getSapCpiCreditCardPayments()));

        LOG.info("appEvent=AdnocSapCpiOmmOrderMapper: mapping completed for order Id:{} ", sapCpiOrder.getOrderId());

    }

    /**
     * @param adnocOutboundDocumentData
     * @return
     */
    private AdnocOutboundDocumentModel convertToOutboundDocumentModel(final AdnocOutboundDocumentData adnocOutboundDocumentData)
    {
        final AdnocOutboundDocumentModel adnocOutboundDocument = new AdnocOutboundDocumentModel();
        adnocOutboundDocument.setDocumentBase64(adnocOutboundDocumentData.getDocumentBase64());
        adnocOutboundDocument.setDocumentName(adnocOutboundDocumentData.getDocumentName());
        adnocOutboundDocument.setDocumentType(adnocOutboundDocumentData.getDocumentType());
        adnocOutboundDocument.setSupportedDocumentType(adnocOutboundDocumentData.getSupportedDocumentType());
        return adnocOutboundDocument;
    }

    @Override
    protected Set<SAPCpiOutboundOrderItemModel> mapOrderItems(final List<SapCpiOrderItem> sapCpiOrderItems)
    {

        final List<SAPCpiOutboundOrderItemModel> sapCpiOutboundOrderItems = new ArrayList<>();

        sapCpiOrderItems.forEach(item -> {

            final SAPCpiOutboundOrderItemModel sapCpiOutboundOrderItem = new SAPCpiOutboundOrderItemModel();
            sapCpiOutboundOrderItem.setOrderId(item.getOrderId());
            sapCpiOutboundOrderItem.setEntryNumber(item.getEntryNumber());
            sapCpiOutboundOrderItem.setQuantity(item.getQuantity());
            sapCpiOutboundOrderItem.setCurrencyIsoCode(item.getCurrencyIsoCode());
            sapCpiOutboundOrderItem.setUnit(item.getUnit());
            sapCpiOutboundOrderItem.setProductCode(item.getProductCode());
            sapCpiOutboundOrderItem.setProductName(item.getProductName());
            sapCpiOutboundOrderItem.setPlant(item.getPlant());
            sapCpiOutboundOrderItem.setNamedDeliveryDate(item.getNamedDeliveryDate());
            sapCpiOutboundOrderItem.setItemCategory(item.getItemCategory());
            sapCpiOutboundOrderItem.setIncoTerms(IncoTerms.valueOf(item.getIncoTerms()));

            sapCpiOutboundOrderItems.add(sapCpiOutboundOrderItem);

        });

        return new HashSet<>(sapCpiOutboundOrderItems);

    }


}
