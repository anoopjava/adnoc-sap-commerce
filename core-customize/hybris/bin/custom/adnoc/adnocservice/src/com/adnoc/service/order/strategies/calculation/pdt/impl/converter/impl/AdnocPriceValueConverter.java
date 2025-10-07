package com.adnoc.service.order.strategies.calculation.pdt.impl.converter.impl;

import com.adnoc.service.price.AdnocPriceValue;
import com.adnoc.service.price.data.AdnocPriceInfoLineItemResponseData;
import de.hybris.platform.order.strategies.calculation.pdt.converter.PDTConverter;
import de.hybris.platform.order.strategies.calculation.pdt.criteria.PriceValueInfoCriteria;

public class AdnocPriceValueConverter implements PDTConverter<AdnocPriceInfoLineItemResponseData, AdnocPriceValue, PriceValueInfoCriteria>
{
    @Override
    public AdnocPriceValue convert(final AdnocPriceInfoLineItemResponseData adnocPriceInfoLineItemResponseData, final PriceValueInfoCriteria priceValueInfoCriteria)
    {
        return new AdnocPriceValue(priceValueInfoCriteria.getCurrency().getIsocode(), adnocPriceInfoLineItemResponseData);
    }
}
