package com.adnoc.service.b2bcart;

import de.hybris.platform.b2b.services.impl.DefaultB2BCartService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AdnocB2BCartService extends the default B2BCartService to customize the cart creation process.
 * It overrides the createCartFromAbstractOrder method to set specific fields to null.
 */
public class AdnocB2BCartService extends DefaultB2BCartService
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BCartService.class);

    @Override
    public CartModel createCartFromAbstractOrder(final AbstractOrderModel order)
    {
        LOG.info("appEvent=AdnocCreateCart, creating cart from abstract order with code: {}", order.getCode());

        final CartModel cart = super.createCartFromAbstractOrder(order);
        cart.setPurchaseOrderNumber(null);
        cart.getEntries().
                forEach(entry -> {
                    entry.setNamedDeliveryDate(null);
                    entry.setIncoTerms(null);
                });
        return cart;
    }
}
