package com.adnoc.service.sap.sapmodel.services.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.services.impl.DefaultSapCommerceUpdateCartEntryStrategy;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

public class AdnocSapCommerceUpdateCartEntryStrategy extends DefaultSapCommerceUpdateCartEntryStrategy
{
    protected static final long ADNOC_MAX_STOCK_QTY = 9999999999L;
    protected long adnocForceInStockMaxQuantity = ADNOC_MAX_STOCK_QTY;

    @Override
    public CommerceCartModification updateQuantityForCartEntry(final CommerceCartParameter parameters)
            throws CommerceCartModificationException
    {
        beforeUpdateCartEntry(parameters);
        final CartModel cartModel = parameters.getCart();
        final long newQuantity = parameters.getQuantity();
        final int entryNumber = Math.toIntExact(parameters.getEntryNumber());

        validateParameterNotNull(cartModel, "Cart model cannot be null");
        final CommerceCartModification modification;

        final AbstractOrderEntryModel entryToUpdate = getEntryForNumber(cartModel, entryNumber);
        validateEntryBeforeModification(newQuantity, entryToUpdate);
        final Integer maxOrderQuantity = entryToUpdate.getProduct().getMaxOrderQuantity();
        // Work out how many we want to add (could be negative if we are
        // removing items)
        final long quantityToAdd = newQuantity - entryToUpdate.getQuantity().longValue();

        // So now work out what the maximum allowed to be added is (note that
        // this may be negative!)
        final long actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(entryToUpdate, entryToUpdate.getProduct(),
                quantityToAdd, entryToUpdate.getDeliveryPointOfService());
        //Now do the actual cartModification
        modification = modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);
        afterUpdateCartEntry(parameters, modification);
        return modification;
    }

    private long getAllowedCartAdjustmentForProduct(final AbstractOrderEntryModel abstractOrderEntryModel, final ProductModel productModel, final long quantityToAdd, final PointOfServiceModel pointOfServiceModel)
    {
        final long existingEntryLevelQty = abstractOrderEntryModel.getQuantity().longValue();
        final long stockLevel = getAvailableStockLevel(productModel, pointOfServiceModel);

        // How many will we have in our cart if we add quantity
        final long newTotalQuantity = existingEntryLevelQty + quantityToAdd;

        // Now limit that to the total available in stock
        final long newTotalQuantityAfterStockLimit = Math.min(newTotalQuantity, stockLevel);

        // So now work out what the maximum allowed to be added is (note that
        // this may be negative!)
        final Integer maxOrderQuantity = productModel.getMaxOrderQuantity();

        if (isMaxOrderQuantitySet(maxOrderQuantity))
        {
            final long newTotalQuantityAfterProductMaxOrder = Math
                    .min(newTotalQuantityAfterStockLimit, maxOrderQuantity.longValue());
            return newTotalQuantityAfterProductMaxOrder - existingEntryLevelQty;
        }
        return newTotalQuantityAfterStockLimit - existingEntryLevelQty;
    }

    @Override
    protected long getForceInStockMaxQuantity()
    {
        return adnocForceInStockMaxQuantity;
    }
}
