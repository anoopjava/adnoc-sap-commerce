package com.adnoc.facades.order.converter.populator;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public class AdnocOrderSummaryPopulator extends AdnocAbstractOrderPopulator<AbstractOrderModel, AbstractOrderData>
{
    @Override
    public void populate(AbstractOrderModel source, AbstractOrderData target)
    {
        addTotals(source, target);
    }
}