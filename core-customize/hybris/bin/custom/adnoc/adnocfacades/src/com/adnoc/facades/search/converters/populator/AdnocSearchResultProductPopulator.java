package com.adnoc.facades.search.converters.populator;

import com.adnoc.facade.product.data.UnitData;
import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.price.AdnocPriceRowService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class AdnocSearchResultProductPopulator implements Populator<SearchResultValueData, ProductData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocSearchResultProductPopulator.class);

    private AdnocPriceRowService adnocPriceRowService;
    private PriceDataFactory priceDataFactory;
    private AdnocB2BUnitService adnocB2BUnitService;
    private CommonI18NService commonI18NService;
    private UnitService unitService;

    @Override
    public void populate(final SearchResultValueData source, final ProductData target)
    {
        LOG.info("appEvent=product, populate method called");
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        final Map<String, Object> valuesMap = source.getValues();

        final String unitCode = (String) valuesMap.get("unit");
        final UnitData unitData = new UnitData();
        unitData.setCode(unitCode);
        unitData.setName(unitCode);
        target.setUnit(unitData);
        target.setMinOrderQuantity((Integer) valuesMap.get("minOrderQuantity"));
        target.setMaxOrderQuantity((Integer) valuesMap.get("maxOrderQuantity"));
        resolvePrice(target);
    }

    private void resolvePrice(final ProductData productData)
    {
        LOG.debug("appEvent=product, resolvePrice method called");
        final B2BUnitModel currentB2BUnit = getAdnocB2BUnitService().getCurrentSoldTo();
        final UserPriceGroup userPriceGroup = currentB2BUnit.getUserPriceGroup();
        if (Objects.nonNull(userPriceGroup))
        {
            LOG.debug("appEvent=product, userPriceGroup exists!");
            final String currencyIso = productData.getPrice().getCurrencyIso();
            final CurrencyModel currencyModel = getCommonI18NService().getCurrency(currencyIso);
            final String unitCode = productData.getUnit().getCode();
            final UnitModel unitModel = getUnitService().getUnitForCode(unitCode);
            final PriceRowModel priceRowModel = getAdnocPriceRowService().getUserGroupProductPrice(productData.getCode(),
                    currencyModel, unitModel, userPriceGroup);
            if (Objects.nonNull(priceRowModel))
            {
                LOG.debug("appEvent=product, priceRow exists!");
                final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(priceRowModel.getPrice()),
                        currencyIso);
                productData.setPrice(priceData);
            }
        }
    }

    protected AdnocPriceRowService getAdnocPriceRowService()
    {
        return adnocPriceRowService;
    }

    public void setAdnocPriceRowService(final AdnocPriceRowService adnocPriceRowService)
    {
        this.adnocPriceRowService = adnocPriceRowService;
    }

    protected PriceDataFactory getPriceDataFactory()
    {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
    {
        this.priceDataFactory = priceDataFactory;
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }

    protected UnitService getUnitService()
    {
        return unitService;
    }

    public void setUnitService(final UnitService unitService)
    {
        this.unitService = unitService;
    }
}
