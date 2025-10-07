package com.adnoc.service.price.impl;

import com.adnoc.service.price.AdnocPriceRowService;
import com.adnoc.service.price.dao.AdnocPriceRowDao;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.product.ProductService;

public class AdnocPriceRowServiceImpl implements AdnocPriceRowService
{
    private AdnocPriceRowDao adnocPriceRowDao;
    private ProductService productService;

    @Override
    public PriceRowModel getUserGroupProductPrice(String productId, CurrencyModel currencyModel, UnitModel unitModel, UserPriceGroup userPriceGroup)
    {
        final ProductModel productModel = getProductService().getProductForCode(productId);
        return getAdnocPriceRowDao().findUserGroupProductPrice(productModel, currencyModel, unitModel, userPriceGroup);
    }

    protected AdnocPriceRowDao getAdnocPriceRowDao()
    {
        return adnocPriceRowDao;
    }

    public void setAdnocPriceRowDao(AdnocPriceRowDao adnocPriceRowDao)
    {
        this.adnocPriceRowDao = adnocPriceRowDao;
    }

    protected ProductService getProductService()
    {
        return productService;
    }

    public void setProductService(ProductService productService)
    {
        this.productService = productService;
    }
}
