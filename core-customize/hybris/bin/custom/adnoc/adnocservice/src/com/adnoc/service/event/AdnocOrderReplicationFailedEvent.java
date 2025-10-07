package com.adnoc.service.event;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class AdnocOrderReplicationFailedEvent extends AbstractEvent
{
    private OrderModel order;

    public AdnocOrderReplicationFailedEvent(final OrderModel order)
    {
        this.order = order;
    }

    public OrderModel getOrder()
    {
        return order;
    }

    public void setOrder(final OrderModel order)
    {
        this.order = order;
    }
}
