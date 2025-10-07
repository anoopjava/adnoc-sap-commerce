package com.adnoc.service.order.strategy;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class AdnocCommerceAddToCartStrategy extends DefaultCommerceAddToCartStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocCommerceAddToCartStrategy.class);

    private Random random;

    @Override
    protected CommerceCartModification doAddToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
    {
        final CommerceCartModification modification;

        final CartModel cartModel = parameter.getCart();
        final ProductModel productModel = parameter.getProduct();
        final long quantityToAdd = parameter.getQuantity();
        final PointOfServiceModel deliveryPointOfService = parameter.getPointOfService();

        beforeAddToCart(parameter);
        validateAddToCart(parameter);

        if (isProductForCode(parameter).booleanValue())
        {
            // So now work out what the maximum allowed to be added is (note that this may be negative!)
            final long actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd,
                    deliveryPointOfService);
            final Integer maxOrderQuantity = productModel.getMaxOrderQuantity();
            final long cartLevelAfterQuantityChange = actualAllowedQuantityChange;

            if (actualAllowedQuantityChange > 0)
            {
                // We are allowed to add items to the cart
                final CartEntryModel entryModel = addCartEntry(parameter, actualAllowedQuantityChange);
                getModelService().save(entryModel);

                final String statusCode = getStatusCodeAllowedQuantityChange(actualAllowedQuantityChange, maxOrderQuantity,
                        quantityToAdd, cartLevelAfterQuantityChange);

                modification = createAddToCartResp(parameter, statusCode, entryModel, actualAllowedQuantityChange);
            }
            else
            {
                // Not allowed to add any quantity, or maybe even asked to reduce the quantity
                // Do nothing!
                final String status = getStatusCodeForNotAllowedQuantityChange(maxOrderQuantity, maxOrderQuantity);

                modification = createAddToCartResp(parameter, status, createEmptyCartEntry(parameter), 0);

            }
        }
        else
        {
            modification = createAddToCartResp(parameter, CommerceCartModificationStatus.UNAVAILABLE,
                    createEmptyCartEntry(parameter), 0);
        }

        return modification;
    }

    @Override
    protected long getAllowedCartAdjustmentForProduct(final CartModel cartModel, final ProductModel productModel,
                                                      final long quantityToAdd, final PointOfServiceModel pointOfServiceModel)
    {
        final long stockLevel = getAvailableStockLevel(productModel, pointOfServiceModel);

        // How many will we have in our cart if we add quantity
        final long newTotalQuantity = quantityToAdd;

        // Now limit that to the total available in stock
        final long newTotalQuantityAfterStockLimit = Math.min(newTotalQuantity, stockLevel);

        // So now work out what the maximum allowed to be added is (note that
        // this may be negative!)
        final Integer maxOrderQuantity = productModel.getMaxOrderQuantity();

        if (isMaxOrderQuantitySet(maxOrderQuantity))
        {
            final long newTotalQuantityAfterProductMaxOrder = Math
                    .min(newTotalQuantityAfterStockLimit, maxOrderQuantity.longValue());
            return newTotalQuantityAfterProductMaxOrder;
        }
        return newTotalQuantityAfterStockLimit;
    }

    @Override
    protected void afterAddToCart(final CommerceCartParameter commerceCartParameter, final CommerceCartModification commerceCartModification) throws CommerceCartModificationException
    {
        LOG.info("appEvent=AdnocCommerceAddToCartStrategy,afterAddToCart method called");
        super.afterAddToCart(commerceCartParameter, commerceCartModification);
        final AbstractOrderEntryModel abstractOrderEntryModel = commerceCartModification.getEntry();
        abstractOrderEntryModel.setDivision(commerceCartParameter.getDivision());
        final int threeDigitNumber = 100 + getRandom().nextInt(900); // Generates a number between 100 and 999
        abstractOrderEntryModel.setEntryCode(threeDigitNumber);
    }

    protected Random getRandom()
    {
        return random;
    }

    public void setRandom(final Random random)
    {
        this.random = random;
    }
}
