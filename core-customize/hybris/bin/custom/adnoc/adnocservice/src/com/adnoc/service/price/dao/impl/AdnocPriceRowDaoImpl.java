package com.adnoc.service.price.dao.impl;

import com.adnoc.service.price.dao.AdnocPriceRowDao;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

public class AdnocPriceRowDaoImpl implements AdnocPriceRowDao
{
    public static final String QUERY_PRICE_ROW_CUSTOMER_GROUP = "SELECT {" + PriceRowModel.PK + "} FROM {" + PriceRowModel._TYPECODE
            + "} WHERE ({" + PriceRowModel.PRODUCTID + "}=?productId OR {" + PriceRowModel.PRODUCT + "}=?product) AND {" + PriceRowModel.CURRENCY + "}=?currency AND {"
            + PriceRowModel.UNIT + "}=?unit AND {" + PriceRowModel.UG + "}=?userPriceGroup";

    private FlexibleSearchService flexibleSearchService;

    @Override
    public PriceRowModel findUserGroupProductPrice(final ProductModel productModel, final CurrencyModel currencyModel, final UnitModel unitModel, final UserPriceGroup userPriceGroup)
    {
        final Map<String, Object> searchParams = Map.of("productId", productModel.getCode(),
                "product", productModel,
                "currency", currencyModel,
                "unit", unitModel,
                "userPriceGroup", userPriceGroup);
        final SearchResult<PriceRowModel> searchResult = getFlexibleSearchService().search(QUERY_PRICE_ROW_CUSTOMER_GROUP, searchParams);
        return CollectionUtils.isEmpty(searchResult.getResult()) ? null : searchResult.getResult().get(0);
    }

    protected FlexibleSearchService getFlexibleSearchService()
    {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
    {
        this.flexibleSearchService = flexibleSearchService;
    }

}
