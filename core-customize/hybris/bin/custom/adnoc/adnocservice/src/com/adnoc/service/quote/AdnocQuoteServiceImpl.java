package com.adnoc.service.quote;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.impl.DefaultQuoteService;

import java.util.List;
import java.util.Objects;

public class AdnocQuoteServiceImpl extends DefaultQuoteService implements AdnocQuoteService
{
    private CartService cartService;

    @Override
    public void validateQuoteAgainstCrossDivision() throws CommerceCartModificationException
    {
        final CartModel cartModel = getCartService().getSessionCart();
        final List<AbstractOrderEntryModel> entries = cartModel.getEntries();
        final boolean allSameDivision = entries.stream().map(entryModel -> entryModel.getDivision())
                .filter(Objects::nonNull)
                .distinct()
                .count() <= 1;
        if (!allSameDivision)
        {
            throw new CommerceCartModificationException("There are cross division products in your cart, quote can be created for single division only.");
        }
    }

    protected CartService getCartService()
    {
        return cartService;
    }

    public void setCartService(final CartService cartService)
    {
        this.cartService = cartService;
    }
}
