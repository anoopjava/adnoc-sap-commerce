package com.adnoc.service.order.strategies.calculation.pdt.impl.converter.impl;

import com.adnoc.service.price.AdnocPriceValue;
import com.adnoc.service.price.data.AdnocPriceInfoLineItemResponseData;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.strategies.calculation.pdt.converter.PDTConverter;
import de.hybris.platform.order.strategies.calculation.pdt.criteria.PriceValueInfoCriteria;

import java.util.HashMap;
import java.util.Map;

public class AdnocPriceInfoConverter implements PDTConverter<AdnocPriceInfoLineItemResponseData, PriceInformation, PriceValueInfoCriteria>
{
    @Override
    public PriceInformation convert(AdnocPriceInfoLineItemResponseData adnocPriceInfoLineItemResponseData, PriceValueInfoCriteria priceValueInfoCriteria)
    {
        final Map qualifiers = new HashMap();
        qualifiers.put(PriceRow.MINQTD, priceValueInfoCriteria.getQuantity());
        qualifiers.put(PriceRow.UNIT, priceValueInfoCriteria.getProduct().getUnit());
        qualifiers.put(PriceRow.PRICEROW, adnocPriceInfoLineItemResponseData);
        return new PriceInformation(qualifiers,
                new AdnocPriceValue(priceValueInfoCriteria.getCurrency().getIsocode(), adnocPriceInfoLineItemResponseData));

    }
}
