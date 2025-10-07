package com.adnoc.service.cancellation;

import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.orderexchange.cancellation.DefaultSapEnterCancellingStrategy;

public class AdnocSapEnterCancellingStrategyImpl extends DefaultSapEnterCancellingStrategy
{
    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;

    @Override
    public void changeOrderStatusAfterCancelOperation(final OrderCancelRecordEntryModel orderCancelRecordEntry,
                                                      final boolean saveOrderModel)
    {
        getAdnocOutboundReplicationDirector().scheduleOutboundTask(orderCancelRecordEntry);
        final OrderModel order = orderCancelRecordEntry.getModificationRecord().getOrder();
        order.setStatus(OrderStatus.CANCELLING);
        getModelService().save(order);
    }

    protected AdnocOutboundReplicationDirector getAdnocOutboundReplicationDirector()
    {
        return adnocOutboundReplicationDirector;
    }

    public void setAdnocOutboundReplicationDirector(final AdnocOutboundReplicationDirector adnocOutboundReplicationDirector)
    {
        this.adnocOutboundReplicationDirector = adnocOutboundReplicationDirector;
    }
}
