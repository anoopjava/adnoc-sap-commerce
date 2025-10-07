
/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.integration.outbound.service.impl;

import com.adnoc.service.event.AdnocOrderReplicationFailedEvent;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.events.OrderPlacedEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.sap.sapcpiorderexchange.actions.SapCpiOmmOrderOutboundAction;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.task.RetryLaterException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;

public class AdnocSapCpiOmmOrderOutboundAction extends SapCpiOmmOrderOutboundAction
{

    private static final Logger LOG = LogManager.getLogger(AdnocSapCpiOmmOrderOutboundAction.class);

    private final String ORDER_CODE = "orderId";
    private EventService eventService;

    @Override
    public void executeAction(final OrderProcessModel process) throws RetryLaterException
    {

        final OrderModel order = process.getOrder();
        if (StringUtils.isNotEmpty(order.getSapOrderCode()))
        {
            LOG.info(String.format("The OMM order [%s] already sent to ERP", order.getCode()));
            return;
        }
        getSapCpiOutboundService().sendOrder(getSapCpiOrderOutboundConversionService().convertOrderToSapCpiOrder(order)).subscribe(
                // onNext
                responseEntityMap -> {
                    if (isSentSuccessfully(responseEntityMap))
                    {
                        order.setSapOrderCode(getPropertyValue(responseEntityMap, ORDER_CODE));
                        order.setSapOrderStatus(SAPOrderStatus.CONFIRMED_FROM_ERP);
                        order.setStatus(OrderStatus.CONFIRMED);
                        order.getEntries().stream()
                                .filter(OrderEntryModel.class::isInstance)
                                .map(OrderEntryModel.class::cast)
                                .forEach(entry -> entry.setSapLineItemOrderStatus(SAPOrderStatus.CONFIRMED));
                        getModelService().saveAll(order.getEntries());
                        setOrderStatus(order, ExportStatus.EXPORTED);
                        resetEndMessage(process);
                        LOG.info(String.format("The OMM order [%s] has been successfully sent to the SAP backend through SCPI, SapOrderCode: [%s]!",
                                order.getCode(), order.getSapOrderCode()));
                        final OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(process);
                        getEventService().publishEvent(orderPlacedEvent);
                        LOG.info("appEvent=Order,successfully sent order confirmation email to customer");
                    }
                    else
                    {
                        order.setSapErrorMessage(getPropertyValue(responseEntityMap, RESPONSE_MESSAGE));
                        order.setSapOrderStatus(SAPOrderStatus.NOT_SENT_TO_ERP);
                        setOrderStatus(order, ExportStatus.NOTEXPORTED);
                        LOG.error(String.format("The OMM order [%s] has not been sent to the SAP backend! %n%s",
                                order.getCode(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));
                        final AdnocOrderReplicationFailedEvent adnocOrderReplicationFailedEvent = new AdnocOrderReplicationFailedEvent(order);
                        getEventService().publishEvent(adnocOrderReplicationFailedEvent);
                    }
                }
                // onError
                , error -> {
                    order.setSapErrorMessage(error.getMessage());
                    order.setSapOrderStatus(SAPOrderStatus.NOT_SENT_TO_ERP);
                    setOrderStatus(order, ExportStatus.NOTEXPORTED);
                    LOG.error(String.format("The OMM order [%s] has not been sent to the SAP backend through SCPI! %n%s", order.getCode(), error.getMessage()), error);
                    final AdnocOrderReplicationFailedEvent adnocOrderReplicationFailedEvent = new AdnocOrderReplicationFailedEvent(order);
                    getEventService().publishEvent(adnocOrderReplicationFailedEvent);
                }
        );

    }

    protected EventService getEventService()
    {
        return eventService;
    }

    public void setEventService(final EventService eventService)
    {
        this.eventService = eventService;
    }

}
