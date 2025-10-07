package com.adnoc.service.validators;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.validator.AddToCartValidator;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocDivisionGroupAddToCartValidator extends AdnocB2BCartValidator implements AddToCartValidator
{
    private static final Logger LOG = LogManager.getLogger(AdnocDivisionGroupAddToCartValidator.class);

    /**
     * @param parameter Parameters used for the add to cart process
     * @return
     */
    @Override
    public boolean supports(final CommerceCartParameter parameter)
    {
        return true;
    }

    /**
     * Validates Division Grouping
     *
     * @param parameter Parameters used for the add to cart process
     * @throws CommerceCartModificationException
     */
    @Override
    public void validate(final CommerceCartParameter parameter) throws CommerceCartModificationException
    {
        LOG.info("appEvent=AdnocAddToCartValidator, validating cart against division grouping for product:{}, and cart: {}", parameter.getProduct().getCode(), parameter.getCart().getCode());
        final ProductModel product = parameter.getProduct();
        final CartModel cartModel = parameter.getCart();
        final String productDivision = product.getDivision();
        validateCartAgainstDivisionGrouping(cartModel, productDivision);
    }
}
