package com.adnoc.facades.order.converter.populator;

import com.adnoc.facade.product.data.UnitData;
import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.enums.IncoTerms;
import com.adnoc.service.order.util.strategy.AdnocDateDifferenceCalculationStrategy;
import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.DiscountValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdnocOrderEntryPopulator extends OrderEntryPopulator
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderEntryPopulator.class);

    public static final String MINIMUM_CANCELLABLE_DAYS = "minCancellableDays";
    public static final int DEFAULT_AUTO_CANCELLATION_DAYS = 0;
    private static final List<SAPOrderStatus> NON_CANCELLABLE_STATUSES = List.of(SAPOrderStatus.CANCELLED, SAPOrderStatus.CANCELLED_FROM_ERP, SAPOrderStatus.COMPLETED);

    private AdnocDateDifferenceCalculationStrategy adnocDateDifferenceCalculationStrategy;
    private AdnocConfigService adnocConfigService;
    private Converter<AddressModel, AddressData> addressConverter;
    private Converter<UnitModel, UnitData> adnocUnitConverter;
    private Converter<IncoTerms, IncoTermsData> adnocIncoTermsDataConverter;
    private EnumerationService enumerationService;


    @Override
    public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
    {
        LOG.info("appEvent=AdnocOrderEntry, populate method called with source:{} and target:{} ", source, target);
        super.populate(source, target);
        target.setDivision(source.getDivision());
        target.setEntryCode(source.getEntryCode());
        if (Objects.nonNull(source.getDeliveryAddress()))
        {
            target.setDeliveryAddress(getAddressConverter().convert(source.getDeliveryAddress()));
        }
        if (Objects.nonNull(source.getNamedDeliveryDate()))
        {
            LOG.info("Setting named delivery date to:{}", source.getNamedDeliveryDate());
            target.setNamedDeliveryDate(source.getNamedDeliveryDate());
        }
        if (source instanceof OrderEntryModel orderEntryModel)
        {
            final int adnocConfigValue = getAdnocConfigService().getAdnocConfigValue(MINIMUM_CANCELLABLE_DAYS, DEFAULT_AUTO_CANCELLATION_DAYS);
            final boolean isCancellable = (Objects.isNull(source.getNamedDeliveryDate())) || (getAdnocDateDifferenceCalculationStrategy().isCancellable(source, adnocConfigValue) && !NON_CANCELLABLE_STATUSES.contains(orderEntryModel.getSapLineItemOrderStatus()));
            target.setIsCancellable(isCancellable);
            target.setQuantityShipped(orderEntryModel.getQuantityShipped());
            if (Objects.nonNull(orderEntryModel.getSapLineItemOrderStatus()))
            {
                target.setSapLineItemStatus(getEnumerationService().getEnumerationName(orderEntryModel.getSapLineItemOrderStatus()));
            }
        }
        if (Objects.nonNull(source.getUnit()))
        {
            target.setUnit(getAdnocUnitConverter().convert(source.getUnit()));
        }
        if (Objects.nonNull(source.getIncoTerms()))
        {
            target.setIncoTerms(getAdnocIncoTermsDataConverter().convert(source.getIncoTerms()));
        }
        target.setDiscounts(calculateEntryDiscounts(source));
        target.setTax(calculateEntryTax(source));
    }

    private PriceData calculateEntryTax(final AbstractOrderEntryModel source)
    {
        if (CollectionUtils.isNotEmpty(source.getTaxValues()))
        {
            return source.getTaxValues().stream()
                    .findFirst()
                    .map(taxValue -> createPrice(taxValue.getCode(), taxValue.getAppliedValue(), taxValue.getCurrencyIsoCode()))
                    .orElse(null);
        }
        return null;
    }

    private List<PriceData> calculateEntryDiscounts(final AbstractOrderEntryModel abstractOrderEntryModel)
    {
        if (CollectionUtils.isNotEmpty(abstractOrderEntryModel.getDiscountValues()))
        {
            final List<DiscountValue> discountList = abstractOrderEntryModel.getDiscountValues();
            return discountList.stream().map(discountValue -> createPrice(discountValue.getCode(), discountValue.getAppliedValue(),
                    discountValue.getCurrencyIsoCode())).collect(Collectors.toList());
        }
        return List.of();
    }

    protected PriceData createPrice(final String code, final double val, final String currencyIsoCode)
    {
        final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(val), currencyIsoCode);
        priceData.setCode(code);
        return priceData;
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    protected AdnocDateDifferenceCalculationStrategy getAdnocDateDifferenceCalculationStrategy()
    {
        return adnocDateDifferenceCalculationStrategy;
    }

    public void setAdnocDateDifferenceCalculationStrategy(final AdnocDateDifferenceCalculationStrategy adnocDateDifferenceCalculationStrategy)
    {
        this.adnocDateDifferenceCalculationStrategy = adnocDateDifferenceCalculationStrategy;
    }

    protected Converter<AddressModel, AddressData> getAddressConverter()
    {
        return addressConverter;
    }

    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
    {
        this.addressConverter = addressConverter;
    }

    protected Converter<UnitModel, UnitData> getAdnocUnitConverter()
    {
        return adnocUnitConverter;
    }

    public void setAdnocUnitConverter(final Converter<UnitModel, UnitData> adnocUnitConverter)
    {
        this.adnocUnitConverter = adnocUnitConverter;
    }

    protected Converter<IncoTerms, IncoTermsData> getAdnocIncoTermsDataConverter()
    {
        return adnocIncoTermsDataConverter;
    }

    public void setAdnocIncoTermsDataConverter(final Converter<IncoTerms, IncoTermsData> adnocIncoTermsDataConverter)
    {
        this.adnocIncoTermsDataConverter = adnocIncoTermsDataConverter;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }
}
