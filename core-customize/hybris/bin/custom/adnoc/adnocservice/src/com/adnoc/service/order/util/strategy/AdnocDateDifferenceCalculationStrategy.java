package com.adnoc.service.order.util.strategy;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;

public interface AdnocDateDifferenceCalculationStrategy
{
    boolean isCancellable(AbstractOrderEntryModel orderEntryModel, int adnocConfigValue);

    boolean isReturnable(OrderModel orderModel, int adnocConfigValue);
}
