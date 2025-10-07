package com.adnoc.facades.order.converter.populator;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public abstract class AdnocAbstractOrderPopulator<SOURCE extends AbstractOrderModel, TARGET extends AbstractOrderData> extends AbstractOrderPopulator<SOURCE, TARGET>
{
    @Override
    protected void addTotals(final AbstractOrderModel abstractOrderModel, final AbstractOrderData abstractOrderData)
    {
        abstractOrderData.setTotalPrice(createPrice(abstractOrderModel, abstractOrderModel.getGrossTotalPrice()));
        abstractOrderData.setTotalDiscounts(createPrice(abstractOrderModel, abstractOrderModel.getTotalDiscounts()));
        abstractOrderData.setSubTotal(createPrice(abstractOrderModel, abstractOrderModel.getNetTotalPrice()));
        abstractOrderData.setTotalTax(createPrice(abstractOrderModel, abstractOrderModel.getTotalTax()));
        abstractOrderData.setTotalPriceWithTax(createPrice(abstractOrderModel, abstractOrderModel.getTotalPrice()));
        abstractOrderData.setDeliveryCost(createPrice(abstractOrderModel, abstractOrderModel.getDeliveryCost()));
    }
}
