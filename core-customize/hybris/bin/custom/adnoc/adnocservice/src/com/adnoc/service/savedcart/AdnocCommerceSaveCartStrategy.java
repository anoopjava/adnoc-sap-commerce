package com.adnoc.service.savedcart;

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceSaveCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.model.order.CartModel;

import java.util.Objects;

public class AdnocCommerceSaveCartStrategy extends DefaultCommerceSaveCartStrategy
{

    @Override
    public CommerceSaveCartResult saveCart(final CommerceSaveCartParameter parameters) throws CommerceSaveCartException
    {
        final CommerceSaveCartResult saveCartResult = super.saveCart(parameters);
        final CartModel cartModel = saveCartResult.getSavedCart();
        if (Objects.nonNull(cartModel))
        {
            cartModel.getEntries().forEach(entry -> {
                entry.setNamedDeliveryDate(null);
                entry.setIncoTerms(null);
                entry.setDeliveryAddress(null);
            });
            getModelService().saveAll(cartModel.getEntries());
        }
        return saveCartResult;
    }
}
