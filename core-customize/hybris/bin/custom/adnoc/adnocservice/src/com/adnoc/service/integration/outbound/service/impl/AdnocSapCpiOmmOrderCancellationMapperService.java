/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.integration.outbound.service.impl;

import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellation;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderCancellationMapperService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * SAP CPI OMM Order Cancellation Mapper Service
 */
public class AdnocSapCpiOmmOrderCancellationMapperService extends SapCpiOmmOrderCancellationMapperService
{
    private static final Logger LOG = LogManager.getLogger(AdnocSapCpiOmmOrderCancellationMapperService.class);

    @Override
    public void map(final OrderCancelRecordEntryModel orderCancelRecordEntryModel, final List<SAPCpiOutboundOrderCancellationModel> sapCpiOutboundOrderCancellationModels)
    {
        LOG.info("appEvent=AdnocSapCpiOmmOrderCancellation,entering map method with orderCancelRecordEntryModel:{}", orderCancelRecordEntryModel);
        mapSapCpiCancelOrderToSapCpiCancelOrderOutbound(getSapCpiOrderConversionService().convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntryModel), sapCpiOutboundOrderCancellationModels);
        LOG.info("appEvent=AdnocSapCpiOmmOrderCancellation, exit map method");
    }

    @Override
    protected void mapSapCpiCancelOrderToSapCpiCancelOrderOutbound(final List<SapCpiOrderCancellation> sapCpiOrderCancellations,
                                                                   final List<SAPCpiOutboundOrderCancellationModel> sapCpiOutboundOrderCancellations)
    {
        sapCpiOrderCancellations.forEach(cancellation -> {

            LOG.debug("appEvent=AdnocSapCpiOmmOrderCancellationMapper,Processing cancellation for order: {}", cancellation.getOrderId());
            final SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellation = new SAPCpiOutboundOrderCancellationModel();
            sapCpiOutboundOrderCancellation.setSapCpiConfig(mapOrderCancellationConfigInfo(cancellation.getSapCpiConfig()));
            sapCpiOutboundOrderCancellation.setOrderId(cancellation.getOrderId());
            sapCpiOutboundOrderCancellation.setSapOrderCode(cancellation.getSapOrderCode());
            sapCpiOutboundOrderCancellation.setSalesOrganization(cancellation.getSalesOrg().getSalesOrganization());
            sapCpiOutboundOrderCancellation.setDivision(cancellation.getSalesOrg().getDivision());
            sapCpiOutboundOrderCancellation.setDistributionChannel(cancellation.getSalesOrg().getDistributionChannel());
            final List<SAPCpiOutboundOrderItemModel> sapCpiOutboundOrderItems = new ArrayList<>();
            cancellation.getSapCpiOrderCancellationItems().forEach(item -> {
                LOG.debug("appEvent=AdnocSapCpiOmmOrderCancellationMapper,adding item to SAPCpiOutboundOrderItems: {}", item);
                final SAPCpiOutboundOrderItemModel sapCpiOutboundOrderItem = new SAPCpiOutboundOrderItemModel();
                sapCpiOutboundOrderItem.setProductCode(item.getProductCode());
                sapCpiOutboundOrderItem.setProductName(item.getProductName());
                sapCpiOutboundOrderItem.setRejectionReason(item.getRejectionReason());
                sapCpiOutboundOrderItem.setEntryNumber(item.getEntryNumber());
                sapCpiOutboundOrderItem.setQuantity(item.getQuantity());
                sapCpiOutboundOrderItems.add(sapCpiOutboundOrderItem);
                LOG.info("appEvent=AdnocSapCpiOmmOrderCancellationMapper,Added SAPCpiOutboundOrderCancellationModel for order: {}", cancellation.getOrderId());

            });

            sapCpiOutboundOrderCancellation.setSapCpiOutboundOrderItems(new HashSet<>(sapCpiOutboundOrderItems));
            sapCpiOutboundOrderCancellations.add(sapCpiOutboundOrderCancellation);

        });

    }
}
