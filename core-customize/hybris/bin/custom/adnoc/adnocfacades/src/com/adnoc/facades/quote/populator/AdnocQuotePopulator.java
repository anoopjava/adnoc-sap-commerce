package com.adnoc.facades.quote.populator;

import de.hybris.platform.commercefacades.order.converters.populator.QuotePopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.util.DiscountValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocQuotePopulator extends QuotePopulator
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuotePopulator.class);

    @Override
    public void populate(final QuoteModel source, final QuoteData target)
    {
        super.populate(source, target);
        target.setSellerComment(source.getSellerComment());
    }

    @Override
    protected void addTotals(final AbstractOrderModel source, final AbstractOrderData prototype)
    {
        LOG.info("appEvent=AdnocQuote, addTotals method called with model={} and data={} ", source, prototype);
        super.addTotals(source, prototype);

        final double productsDiscountsAmount = getProductsDiscountsAmount(source);
        LOG.info("Products discounts amount calculated={} ", productsDiscountsAmount);

        final double orderDiscountsAmount = getOrderLevelDiscountsAmount(source);
        LOG.info("Order discounts amount calculated={} ", orderDiscountsAmount);
        final CurrencyModel currencyModel = source.getCurrency();
        final int digits = currencyModel.getDigits().intValue();
        final double total = orderDiscountsAmount + source.getTotalPrice();
        final double rounded = getCommonI18NService().roundCurrency(total, digits);
        prototype.setSubTotalWithoutQuoteDiscounts(createPrice(source, rounded));
    }

    private double getOrderLevelDiscountsAmount(final AbstractOrderModel source)
    {
        if (CollectionUtils.isNotEmpty(source.getGlobalDiscountValues()))
        {
            return source.getGlobalDiscountValues()
                    .stream()
                    .mapToDouble(DiscountValue::getAppliedValue)
                    .sum();
        }
        return 0.0d;
    }


}
