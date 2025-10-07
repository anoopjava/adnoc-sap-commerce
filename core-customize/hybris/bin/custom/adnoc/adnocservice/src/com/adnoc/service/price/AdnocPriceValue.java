package com.adnoc.service.price;

import com.adnoc.service.price.data.AdnocPriceInfoLineItemResponseData;
import com.adnoc.service.price.data.AdnocSapPricingConditionResponseData;
import de.hybris.platform.util.PriceValue;

import java.util.List;

public class AdnocPriceValue extends PriceValue
{
    private final double grossTotalPrice;
    private final double totalDiscount;
    private final double totalICVDiscount;
    private final double totalShippingCharges;
    private final double netTotalPrice;
    private final double totalTaxAmount;
    private final double grandTotalPrice;
    private final List<AdnocSapPricingConditionResponseData> adnocSapPricingConditionResponseDataList;

    public AdnocPriceValue(String currencyIso, AdnocPriceInfoLineItemResponseData adnocPriceInfoLineItemResponseData)
    {
        super(currencyIso, adnocPriceInfoLineItemResponseData.getBasePrice(), true);
        grossTotalPrice = adnocPriceInfoLineItemResponseData.getGrossTotalPrice();
        totalDiscount = adnocPriceInfoLineItemResponseData.getTotalDiscount();
        totalICVDiscount = adnocPriceInfoLineItemResponseData.getTotalICVDiscount();
        totalShippingCharges = adnocPriceInfoLineItemResponseData.getTotalShippingCharges();
        netTotalPrice = adnocPriceInfoLineItemResponseData.getNetTotalPrice();
        totalTaxAmount = adnocPriceInfoLineItemResponseData.getTotalTaxAmount();
        grandTotalPrice = adnocPriceInfoLineItemResponseData.getGrandTotalPrice();
        adnocSapPricingConditionResponseDataList = adnocPriceInfoLineItemResponseData.getSapPricingConditions();
    }

    public double getGrossTotalPrice()
    {
        return grossTotalPrice;
    }

    public double getTotalDiscount()
    {
        return totalDiscount;
    }

    public double getTotalICVDiscount()
    {
        return totalICVDiscount;
    }

    public double getTotalShippingCharges()
    {
        return totalShippingCharges;
    }

    public double getNetTotalPrice()
    {
        return netTotalPrice;
    }

    public double getTotalTaxAmount()
    {
        return totalTaxAmount;
    }

    public double getGrandTotalPrice()
    {
        return grandTotalPrice;
    }

    public List<AdnocSapPricingConditionResponseData> getAdnocSapPricingConditionResponseDataList()
    {
        return adnocSapPricingConditionResponseDataList;
    }
}
