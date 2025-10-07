package com.adnoc.facades.cart.populator;

import com.adnoc.facades.order.converter.populator.AdnocAbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

public class AdnocCartPopulator<T extends CartData> extends AdnocAbstractOrderPopulator<CartModel, CartData>
{
    @Override
    public void populate(final CartModel cartModel, final CartData cartData) throws ConversionException
    {
        if (Objects.nonNull(cartModel.getCreditLimitPaymentInfo()))
        {
            cartData.setCreditLimitUsed(true);
        }

        addTotals(cartModel, cartData);
    }
}
