package com.adnoc.service.order.calculate;

import com.adnoc.service.price.AdnocPriceValue;
import com.adnoc.service.price.data.AdnocSapPricingConditionResponseData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Custom calculation service for ADNOC orders, extending the default calculation service.
 * This service handles specific pricing and discount logic for ADNOC orders.
 */
public class AdnocCalculationService extends DefaultCalculationService
{
    private static final Logger LOG = LogManager.getLogger(AdnocCalculationService.class);

    private static final String CONTRACT_DISCOUNT = "CONTRACT_DISCOUNT";
    private static final String ICV_DISCOUNT = "ICV_DISCOUNT";
    private static final String VAT = "VAT";
    private static final List<String> B2B_DISCOUNT_CODES = List.of(CONTRACT_DISCOUNT, ICV_DISCOUNT);

    private OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;
    private CommonI18NService commonI18NService;

    @Override
    protected void resetAllValues(final AbstractOrderEntryModel entry) throws CalculationException
    {
        if (entry.getOrder() instanceof final CartModel cartModel)
        {
            final QuoteModel quote = cartModel.getQuoteReference();
            if (Objects.nonNull(quote) && Objects.equals(quote.getState(), QuoteState.BUYER_OFFER))
            {
                LOG.info("appEvent=AdnocOrderCalculation, skipping resetAllValues for EntryNumber={} as it is part of a Quote in BUYER_OFFER state.", entry.getEntryNumber());
                return;
            }
        }
        LOG.info("appEvent=AdnocOrderCalculation, resetting pricing for EntryNumber={}.", entry.getEntryNumber());
        final PriceValue priceValue = findBasePrice(entry);
        if (priceValue instanceof final AdnocPriceValue adnocPriceValue)
        {
            //BasePrice
            LOG.debug("appEvent=AdnocOrderCalculation, resetting BasePrice={} for EntryNumber={}.",
                    adnocPriceValue.getValue(), entry.getEntryNumber());
            entry.setBasePrice(adnocPriceValue.getValue());

            //Tax
            LOG.debug("appEvent=AdnocOrderCalculation, resetting TotalTaxAmount={} for EntryNumber={}.",
                    adnocPriceValue.getTotalTaxAmount(), entry.getEntryNumber());
            final Collection<TaxValue> entryTaxes = List.of(new TaxValue(VAT, adnocPriceValue.getTotalTaxAmount(), true,
                    adnocPriceValue.getTotalTaxAmount(), adnocPriceValue.getCurrencyIso()));
            entry.setTaxValues(entryTaxes);

            //Discount
            LOG.debug("appEvent=AdnocOrderCalculation, resetting TotalDiscount={}, TotalICVDiscount={} for EntryNumber={}.",
                    adnocPriceValue.getTotalDiscount(), adnocPriceValue.getTotalICVDiscount(), entry.getEntryNumber());
            final List<DiscountValue> discountValues = entry.getDiscountValues();
            final List<DiscountValue> updatedDiscountValues = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(discountValues))
            {
                final List<DiscountValue> discountValuesWithoutB2B = discountValues.stream()
                        .filter(discountValue -> !B2B_DISCOUNT_CODES.contains(discountValue.getCode()))
                        .collect(Collectors.toList());
                LOG.debug("appEvent=AdnocOrderCalculation, found non-contract DiscountValues={} for EntryNumber={}.",
                        CollectionUtils.isNotEmpty(discountValuesWithoutB2B), entry.getEntryNumber());
                updatedDiscountValues.addAll(discountValuesWithoutB2B);
            }
            updatedDiscountValues.add(new DiscountValue(CONTRACT_DISCOUNT, adnocPriceValue.getTotalDiscount(), true, adnocPriceValue.getTotalDiscount(), adnocPriceValue.getCurrencyIso()));
            updatedDiscountValues.add(new DiscountValue(ICV_DISCOUNT, adnocPriceValue.getTotalICVDiscount(), true, adnocPriceValue.getTotalICVDiscount(), adnocPriceValue.getCurrencyIso()));
            entry.setDiscountValues(updatedDiscountValues);

            //Other Values
            LOG.debug("appEvent=AdnocOrderCalculation, resetting TotalShippingCharges={} for EntryNumber={}.",
                    adnocPriceValue.getTotalShippingCharges(), entry.getEntryNumber());
            entry.setTotalShippingCharges(adnocPriceValue.getTotalShippingCharges());

            LOG.debug("appEvent=AdnocOrderCalculation, resetting GrossTotalPrice={} for EntryNumber={}.",
                    adnocPriceValue.getGrossTotalPrice(), entry.getEntryNumber());
            entry.setGrossTotalPrice(adnocPriceValue.getGrossTotalPrice());

            LOG.debug("appEvent=AdnocOrderCalculation, resetting NetTotalPrice={} for EntryNumber={}.",
                    adnocPriceValue.getNetTotalPrice(), entry.getEntryNumber());
            entry.setNetTotalPrice(adnocPriceValue.getNetTotalPrice());

            LOG.debug("appEvent=AdnocOrderCalculation, resetting GrandTotalPrice={} for EntryNumber={}.",
                    adnocPriceValue.getGrandTotalPrice(), entry.getEntryNumber());
            entry.setTotalPrice(adnocPriceValue.getGrandTotalPrice());

            LOG.debug("appEvent=AdnocOrderCalculation, resetting SapPricingConditions for EntryNumber={}.",
                    entry.getEntryNumber());
            entry.setSapPricingConditions(convertToSapPricingConditions(entry, adnocPriceValue.getAdnocSapPricingConditionResponseDataList()));
        }
        else
        {
            super.resetAllValues(entry);
            final int digits = 2;
            final double grossTotalPrice = getCommonI18NService().roundCurrency(entry.getBasePrice().doubleValue() * entry.getQuantity().longValue(), digits);
            entry.setTotalShippingCharges(0.00);
            entry.setGrossTotalPrice(grossTotalPrice);
            entry.setNetTotalPrice(entry.getTotalPrice());
            entry.setTotalPrice(grossTotalPrice);
        }
    }

    private Set<SAPPricingConditionModel> convertToSapPricingConditions(final AbstractOrderEntryModel abstractOrderEntryModel, final List<AdnocSapPricingConditionResponseData> adnocSapPricingConditionResponseDataList)
    {
        if (CollectionUtils.isEmpty(adnocSapPricingConditionResponseDataList))
        {
            LOG.debug("appEvent=AdnocOrderCalculation, no SapPricingConditions for EntryNumber={}.",
                    abstractOrderEntryModel.getEntryNumber());
            return Collections.emptySet();
        }
        //Removing existing SapPricingConditions
        if (CollectionUtils.isNotEmpty(abstractOrderEntryModel.getSapPricingConditions()))
        {
            LOG.debug("appEvent=AdnocOrderCalculation, removing existing SapPricingConditions for EntryNumber={}.",
                    abstractOrderEntryModel.getEntryNumber());
            getModelService().removeAll(abstractOrderEntryModel.getSapPricingConditions());
        }

        final AtomicInteger counter = new AtomicInteger(1);
        return adnocSapPricingConditionResponseDataList.stream().map(adnocSapPricingConditionResponseData -> {
            LOG.debug("appEvent=AdnocOrderCalculation, creating SapPricingCondition with ConditionType={}, ConditionValue={} for EntryNumber={}.",
                    adnocSapPricingConditionResponseData.getConditionType(), adnocSapPricingConditionResponseData.getConditionValue(),
                    abstractOrderEntryModel.getEntryNumber());
            final SAPPricingConditionModel sapPricingConditionModel = getModelService().create(SAPPricingConditionModel.class);
            sapPricingConditionModel.setConditionType(adnocSapPricingConditionResponseData.getConditionType());
            sapPricingConditionModel.setConditionRate(String.valueOf(adnocSapPricingConditionResponseData.getConditionValue()));
            sapPricingConditionModel.setCurrencyKey(adnocSapPricingConditionResponseData.getCurrency());
            sapPricingConditionModel.setConditionUnit(adnocSapPricingConditionResponseData.getUnit());
            sapPricingConditionModel.setConditionValue(String.valueOf(adnocSapPricingConditionResponseData.getTotalConditionValue()));

            sapPricingConditionModel.setOrderEntry(abstractOrderEntryModel);
            sapPricingConditionModel.setConditionCounter(String.valueOf(abstractOrderEntryModel.getEntryNumber()) + String.valueOf(counter.getAndIncrement()));
            sapPricingConditionModel.setOrder(abstractOrderEntryModel.getOrder().getCode());
            return sapPricingConditionModel;
        }).collect(Collectors.toSet());
    }

    @Override
    public void calculateTotals(final AbstractOrderEntryModel abstractOrderEntryModel, final boolean recalculate)
    {
        if (recalculate || getOrderRequiresCalculationStrategy().requiresCalculation(abstractOrderEntryModel))
        {
            LOG.info("appEvent=AdnocOrderCalculation, Calculate Totals for EntryNumber={}.",
                    abstractOrderEntryModel.getEntryNumber());

            final AbstractOrderModel order = abstractOrderEntryModel.getOrder();
            final CurrencyModel curr = order.getCurrency();
            final int digits = curr.getDigits().intValue();
            final double totalPriceWithoutDiscount = Objects.isNull(abstractOrderEntryModel.getGrossTotalPrice()) ? getCommonI18NService()
                    .roundCurrency(abstractOrderEntryModel.getBasePrice().doubleValue() * abstractOrderEntryModel.getQuantity().longValue(), digits)
                    : abstractOrderEntryModel.getGrossTotalPrice();
            final double quantity = abstractOrderEntryModel.getQuantity().doubleValue();

            final List<DiscountValue> discountValues = abstractOrderEntryModel.getDiscountValues();
            final List<DiscountValue> updatedDiscountValues = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(discountValues))
            {
                final List<DiscountValue> discountValuesWithB2B = discountValues.stream()
                        .filter(discountValue -> B2B_DISCOUNT_CODES.contains(discountValue.getCode()))
                        .collect(Collectors.toList());

                final List<DiscountValue> discountValuesWithoutB2B = discountValues.stream()
                        .filter(discountValue -> !B2B_DISCOUNT_CODES.contains(discountValue.getCode()))
                        .collect(Collectors.toList());

                final List<DiscountValue> appliedDiscounts = DiscountValue.apply(quantity, totalPriceWithoutDiscount, digits,
                        convertDiscountValues(order, discountValuesWithoutB2B), curr.getIsocode());

                updatedDiscountValues.addAll(appliedDiscounts);
                updatedDiscountValues.addAll(discountValuesWithB2B);
                abstractOrderEntryModel.setDiscountValues(updatedDiscountValues);
            }
            setCalculatedStatus(abstractOrderEntryModel);
        }
    }

    @Override
    protected Map<TaxValue, Map<Set<TaxValue>, Double>> calculateSubtotal(final AbstractOrderModel order,
                                                                          final boolean recalculate)
    {
        if (recalculate || getOrderRequiresCalculationStrategy().requiresCalculation(order))
        {
            LOG.info("appEvent=AdnocOrderCalculation, Calculate SubTotal for abstractOrder's code={}.",
                    order.getCode());

            double subtotal = 0.0;
            final List<AbstractOrderEntryModel> entries = order.getEntries();
            final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = new LinkedHashMap<>(
                    entries.size() * 2);

            for (final AbstractOrderEntryModel entry : entries)
            {
                calculateTotals(entry, recalculate);
                final double entryTotal = Objects.nonNull(entry.getGrossTotalPrice()) ? entry.getGrossTotalPrice() : entry.getTotalPrice();
                subtotal += entryTotal;
            }
            // store subtotal
            subtotal = commonI18NService.roundCurrency(subtotal, order.getCurrency().getDigits().intValue());
            order.setSubtotal(Double.valueOf(subtotal));
            return taxValueMap;
        }
        return Collections.emptyMap();
    }

    @Override
    protected void calculateTotals(final AbstractOrderModel abstractOrderModel, final boolean recalculate,
                                   final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) throws CalculationException
    {
        if (recalculate || getOrderRequiresCalculationStrategy().requiresCalculation(abstractOrderModel))
        {
            LOG.info("appEvent=AdnocOrderCalculation, Calculate Totals for abstractOrder's code={}.",
                    abstractOrderModel.getCode());

            final CurrencyModel currencyModel = abstractOrderModel.getCurrency();
            final int digits = currencyModel.getDigits().intValue();

            // discount
            final double totalDiscounts = calculateDiscountValues(abstractOrderModel, recalculate);// discounts
            final double totalDiscountsRounded = getCommonI18NService().roundCurrency(totalDiscounts, digits);
            LOG.debug("appEvent=AdnocOrderCalculation, calculated TotalDiscounts={} for abstractOrder's code={}.",
                    totalDiscountsRounded, abstractOrderModel.getCode());
            abstractOrderModel.setTotalDiscounts(Double.valueOf(totalDiscountsRounded));

            // taxes
            final double totalTaxes = abstractOrderModel.getEntries().stream().flatMap(abstractOrderEntryModel ->
                    abstractOrderEntryModel.getTaxValues().stream()).mapToDouble(TaxValue::getAppliedValue).sum();
            final double totalTaxesRounded = getCommonI18NService().roundCurrency(totalTaxes, digits);
            LOG.debug("appEvent=AdnocOrderCalculation, calculated TotalTaxes={} for abstractOrder's code={}.",
                    totalTaxesRounded, abstractOrderModel.getCode());
            abstractOrderModel.setTotalTax(Double.valueOf(totalTaxesRounded));

            // totalShippingCharges
            final double totalShippingCharges = abstractOrderModel.getEntries().stream().mapToDouble(abstractOrderEntryModel ->
                    abstractOrderEntryModel.getTotalShippingCharges()).sum();
            final double totalShippingChargesRounded = getCommonI18NService().roundCurrency(totalShippingCharges, digits);
            LOG.debug("appEvent=AdnocOrderCalculation, calculated TotalShippingCharges={} for abstractOrder's code={}.",
                    totalShippingChargesRounded, abstractOrderModel.getCode());
            abstractOrderModel.setDeliveryCost(Double.valueOf(totalShippingChargesRounded));

            // grossTotalPrice
            final double grossTotalPrice = abstractOrderModel.getEntries().stream().mapToDouble(abstractOrderEntryModel ->
                    abstractOrderEntryModel.getGrossTotalPrice()).sum();
            final double grossTotalPriceRounded = getCommonI18NService().roundCurrency(grossTotalPrice, digits);
            LOG.debug("appEvent=AdnocOrderCalculation, calculated GrossTotalPrice={} for abstractOrder's code={}.",
                    grossTotalPriceRounded, abstractOrderModel.getCode());
            abstractOrderModel.setGrossTotalPrice(Double.valueOf(grossTotalPriceRounded));

            // netTotalPrice
            final double netTotalPrice = abstractOrderModel.getEntries().stream().mapToDouble(abstractOrderEntryModel ->
                    abstractOrderEntryModel.getNetTotalPrice()).sum();
            final double netTotalPriceRounded = getCommonI18NService().roundCurrency(netTotalPrice, digits);
            LOG.debug("appEvent=AdnocOrderCalculation, calculated NetTotalPriceRounded={} for abstractOrder's code={}.",
                    netTotalPriceRounded, abstractOrderModel.getCode());
            abstractOrderModel.setNetTotalPrice(Double.valueOf(netTotalPriceRounded));

            // total
            final double totalPrice = abstractOrderModel.getEntries().stream().mapToDouble(abstractOrderEntryModel ->
                    abstractOrderEntryModel.getTotalPrice()).sum();
            final double totalGlobalDiscount = DiscountValue.sumAppliedValues(abstractOrderModel.getGlobalDiscountValues());
            final double totalPriceAfterDiscount = totalPrice - totalGlobalDiscount;
            final double totalPriceRounded = getCommonI18NService().roundCurrency(totalPriceAfterDiscount, digits);

            LOG.debug("appEvent=AdnocOrderCalculation, calculated TotalPriceRounded={} for abstractOrder's code={}.",
                    totalPriceRounded, abstractOrderModel.getCode());
            abstractOrderModel.setTotalPrice(Double.valueOf(totalPriceRounded));

            setCalculatedStatus(abstractOrderModel);
            saveOrder(abstractOrderModel);
        }
    }

    @Override
    protected double calculateDiscountValues(final AbstractOrderModel abstractOrderModel, final boolean recalculate)
    {
        final double totalEntryDiscount = abstractOrderModel.getEntries().stream().flatMap(abstractOrderEntryModel ->
                abstractOrderEntryModel.getDiscountValues().stream()).mapToDouble(discount -> Math.abs(discount.getAppliedValue())).sum();
        LOG.debug("appEvent=AdnocOrderCalculation, calculated totalEntryDiscount={} for abstractOrder's code={}.",
                totalEntryDiscount, abstractOrderModel.getCode());

        final CurrencyModel currencyModel = abstractOrderModel.getCurrency();
        final String currencyIsoCode = currencyModel.getIsocode();
        final int digits = currencyModel.getDigits().intValue();
        final List<DiscountValue> appliedDiscounts = DiscountValue.apply(1.0, abstractOrderModel.getTotalPrice(), digits,
                convertDiscountValues(abstractOrderModel, abstractOrderModel.getGlobalDiscountValues()), currencyIsoCode);
        abstractOrderModel.setGlobalDiscountValues(appliedDiscounts);
        final double totalGlobalDiscount = DiscountValue.sumAppliedValues(abstractOrderModel.getGlobalDiscountValues());
        LOG.debug("appEvent=AdnocOrderCalculation, calculated totalGlobalDiscount={} for abstractOrder's code={}.",
                totalGlobalDiscount, abstractOrderModel.getCode());

        final double totalDiscountValue = totalEntryDiscount + totalGlobalDiscount;
        return totalDiscountValue;
    }

    @Override
    protected void resetAdditionalCosts(final AbstractOrderModel order, final Collection<TaxValue> relativeTaxValues)
    {
    }

    protected OrderRequiresCalculationStrategy getOrderRequiresCalculationStrategy()
    {
        return orderRequiresCalculationStrategy;
    }

    @Override
    public void setOrderRequiresCalculationStrategy(final OrderRequiresCalculationStrategy orderRequiresCalculationStrategy)
    {
        this.orderRequiresCalculationStrategy = orderRequiresCalculationStrategy;
        super.setOrderRequiresCalculationStrategy(orderRequiresCalculationStrategy);
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    @Override
    public void setCommonI18NService(final CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
        super.setCommonI18NService(commonI18NService);
    }
}