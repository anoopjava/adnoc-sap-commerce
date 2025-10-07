package com.adnoc.facades.strategies;

import de.hybris.platform.core.model.order.CartModel;

import java.util.List;

public interface AdnocCreateCartFromCartStrategy
{
    /**
     * Create cart from cart list.
     *
     * @param cartModel the cart model
     * @return the list
     */
    List<CartModel> createCartFromCart(CartModel cartModel);
}
