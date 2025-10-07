package com.adnoc.service.price.impl;

import com.adnoc.service.order.strategies.calculation.pdt.impl.converter.impl.AdnocPriceValueInfoCriteria;
import com.adnoc.service.price.AdnocPriceService;
import com.adnoc.service.price.data.AdnocPriceInfoLineItemResponseData;
import com.adnoc.service.price.data.AdnocPriceInfoResponseData;
import com.adnoc.service.price.data.AdnocSapPricingConditionResponseData;

import java.util.Arrays;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.SUCCESS;

public class AdnocMockedPriceServiceImpl implements AdnocPriceService
{
    private static final double GROSS_PRICE_PER_UNIT = 2.73;
    private static final double DISCOUNT_PER_UNIT = 0.33;
    private static final double ICV_DISCOUNT_PER_UNIT = 0.11;
    private static final double SHIPPING_CHARGES_PER_UNIT = 0.04;
    private static final double TAX_AMOUNT_PER_UNIT = 0.12;
    private static final double GRAND_TOTAL_PER_UNIT = 2.52;

    @Override
    public AdnocPriceInfoResponseData getSapPriceInformation(final AdnocPriceValueInfoCriteria adnocPriceValueInfoCriteria)
    {
        final AdnocPriceInfoLineItemResponseData adnocPriceInfoLineItemResponseData = new AdnocPriceInfoLineItemResponseData();
        adnocPriceInfoLineItemResponseData.setEntryNumber(10);
        final long quantity = adnocPriceValueInfoCriteria.getQuantity();
        adnocPriceInfoLineItemResponseData.setQuantity(quantity);
        adnocPriceInfoLineItemResponseData.setProductCode(adnocPriceValueInfoCriteria.getProduct().getCode());
        adnocPriceInfoLineItemResponseData.setUnit(adnocPriceValueInfoCriteria.getProduct().getUnit().getCode());
        final double basePrice = 2.40;
        adnocPriceInfoLineItemResponseData.setBasePrice(basePrice);
        adnocPriceInfoLineItemResponseData.setGrossTotalPrice(GROSS_PRICE_PER_UNIT * quantity);
        adnocPriceInfoLineItemResponseData.setTotalDiscount(DISCOUNT_PER_UNIT * quantity);
        adnocPriceInfoLineItemResponseData.setTotalICVDiscount(ICV_DISCOUNT_PER_UNIT * quantity);
        adnocPriceInfoLineItemResponseData.setTotalShippingCharges(SHIPPING_CHARGES_PER_UNIT * quantity);
        adnocPriceInfoLineItemResponseData.setNetTotalPrice(basePrice * quantity);
        adnocPriceInfoLineItemResponseData.setTotalTaxAmount(TAX_AMOUNT_PER_UNIT * quantity);
        adnocPriceInfoLineItemResponseData.setGrandTotalPrice(GRAND_TOTAL_PER_UNIT * quantity);

        final AdnocSapPricingConditionResponseData zpb1 = createAdnocSapPricingConditionResponseData("ZPB1", "AED", "L", 2.38, 2380.0);
        final AdnocSapPricingConditionResponseData zprm = createAdnocSapPricingConditionResponseData("ZPRM", "AED", "L", 0.35, 350.0);
        final AdnocSapPricingConditionResponseData zdsp = createAdnocSapPricingConditionResponseData("ZDSQ", "AED", "L", 0.33, 330.0);
        final AdnocSapPricingConditionResponseData mwst = createAdnocSapPricingConditionResponseData("MWST", "", "", 50.0, 120.0);

        adnocPriceInfoLineItemResponseData.setSapPricingConditions(Arrays.asList(zpb1, zprm, zdsp, mwst));

        final AdnocPriceInfoResponseData adnocPriceInfoResponseData = new AdnocPriceInfoResponseData();
        adnocPriceInfoResponseData.setStatus(SUCCESS);
        adnocPriceInfoResponseData.setB2bUnit("100000");
        adnocPriceInfoResponseData.setCurrency(adnocPriceValueInfoCriteria.getCurrency().getIsocode());
        adnocPriceInfoResponseData.setLineItem(adnocPriceInfoLineItemResponseData);

        return adnocPriceInfoResponseData;
    }

    private AdnocSapPricingConditionResponseData createAdnocSapPricingConditionResponseData(final String type, final String currency, final String unit, final Double value, final Double totalValue)
    {
        final AdnocSapPricingConditionResponseData adnocSapPricingConditionResponseData = new AdnocSapPricingConditionResponseData();
        adnocSapPricingConditionResponseData.setConditionType(type);
        adnocSapPricingConditionResponseData.setCurrency(currency);
        adnocSapPricingConditionResponseData.setUnit(unit);
        adnocSapPricingConditionResponseData.setConditionValue(value);
        adnocSapPricingConditionResponseData.setTotalConditionValue(totalValue);
        return adnocSapPricingConditionResponseData;
    }
}
