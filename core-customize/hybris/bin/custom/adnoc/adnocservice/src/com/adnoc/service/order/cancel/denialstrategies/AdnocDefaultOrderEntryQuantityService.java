package com.adnoc.service.order.cancel.denialstrategies;

import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.warehousing.orderentry.service.impl.DefaultOrderEntryQuantityService;

public class AdnocDefaultOrderEntryQuantityService extends DefaultOrderEntryQuantityService
{
    @Override
    public Long getQuantityPending(final OrderEntryModel orderEntryModel)
    {
        return orderEntryModel.getQuantity() - getQuantityShipped(orderEntryModel) - orderEntryModel.getQuantityCancelled();
    }
}
