package com.adnoc.facades.order.impl;

import com.adnoc.facades.returnreason.impl.AdnocReturnFacadeImpl;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelRequestData;
import de.hybris.platform.ordermanagementfacades.order.impl.DefaultOmsOrderFacade;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdnocOmsOrderFacadeImpl extends DefaultOmsOrderFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocOmsOrderFacadeImpl.class);

    @Override
    protected OrderCancelRequest buildOrderCancelRequest(final OrderCancelRequestData orderCancelRequestData, final OrderModel order)
    {
        LOG.info("appEvent=AdnocOmsOrderFacade, passing return reason");
        final List<OrderCancelEntry> orderCancelEntries = new ArrayList<>();
        orderCancelRequestData.getEntries().forEach(entryData -> {
            final AbstractOrderEntryModel orderEntry = getOrderService().getEntryForNumber(order, entryData.getOrderEntryNumber());
            final OrderCancelEntry cancellationEntry;
            if (Objects.nonNull(entryData.getCancelReason()))
            {
                cancellationEntry = new OrderCancelEntry(orderEntry, entryData.getCancelQuantity(),
                        entryData.getNotes(), entryData.getCancelReason());
            }
            else
            {
                cancellationEntry = new OrderCancelEntry(orderEntry, entryData.getCancelQuantity(),
                        entryData.getNotes());
            }
            if (orderEntry instanceof OrderEntryModel entryModel)
            {
                entryModel.setSapLineItemOrderStatus(SAPOrderStatus.CANCELLING);
                getModelService().save(entryModel);
            }
            orderCancelEntries.add(cancellationEntry);
        });

        return new OrderCancelRequest(order, orderCancelEntries);
    }
}
