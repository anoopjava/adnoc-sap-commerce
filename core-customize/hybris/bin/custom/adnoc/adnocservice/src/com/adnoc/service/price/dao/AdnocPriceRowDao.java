package com.adnoc.service.price.dao;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;

public interface AdnocPriceRowDao
{
    /**
     * Finds the price row for a given product, currency, unit, and user price group.
     *
     * @param productModel the product model
     * @param currencyModel the currency model
     * @param unitModel the unit model
     * @param userPriceGroup the user price group
     * @return the price row model for the specified parameters
     */
    PriceRowModel findUserGroupProductPrice(ProductModel productModel, CurrencyModel currencyModel, UnitModel unitModel, UserPriceGroup userPriceGroup);
}
