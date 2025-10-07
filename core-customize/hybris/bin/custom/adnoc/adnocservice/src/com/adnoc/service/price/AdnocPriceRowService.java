package com.adnoc.service.price;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;

public interface AdnocPriceRowService
{
    /**
     * Retrieves the price row for a given product, currency, unit, and user price group.
     *
     * @param productId the ID of the product
     * @param currencyModel the currency model
     * @param unitModel the unit model
     * @param userPriceGroup the user price group
     * @return the price row model for the specified parameters
     */
    PriceRowModel getUserGroupProductPrice(String productId, CurrencyModel currencyModel, UnitModel unitModel, UserPriceGroup userPriceGroup);
}
