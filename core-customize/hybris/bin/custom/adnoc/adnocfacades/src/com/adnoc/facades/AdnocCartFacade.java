package com.adnoc.facades;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;

/**
 * Extended cart facade interface that provides additional functionality for managing Adnoc-specific cart operations.
 * This interface extends the standard CartFacade with methods for bulk updating order entries and cart modifications.
 */
public interface AdnocCartFacade extends CartFacade
{
    /**
     * Update order entry list list.
     *
     * @param cartEntriesData the cart entries data
     * @return the cart data
     * @throws CommerceCartRestorationException  the commerce cart restoration exception
     * @throws CommerceCartModificationException the commerce cart modification exception
     */
    CartData updateOrderEntryList(List<OrderEntryData> cartEntriesData) throws CommerceCartRestorationException, CommerceCartModificationException;

    /**
     * Update cart entries with the provided order entry data
     *
     * @param cartEntriesData List of order entries containing updated quantities and product data
     * @param cartModel       The cart model to be updated with the new entries
     * @return List of cart modifications containing the results of the update operations
     * @throws CommerceCartModificationException if there are issues during cart modification
     */
    List<CartModificationData> updateCartEntries(List<OrderEntryData> cartEntriesData, CartModel cartModel) throws CommerceCartModificationException;
}
