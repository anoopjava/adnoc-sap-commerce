package com.adnoc.service.validators;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.validator.AddToCartValidator;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocQuantityAddToCartValidator extends AdnocB2BCartValidator implements AddToCartValidator
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuantityAddToCartValidator.class);

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
     * Validates Payer Division Add To Cart
     *
     * @param parameter Parameters used for the add to cart process
     * @throws CommerceCartModificationException
     */
    @Override
    public void validate(final CommerceCartParameter parameter) throws CommerceCartModificationException
    {
        final ProductModel productModel = parameter.getProduct();
        final long quantity = parameter.getQuantity();
        LOG.info("appEvent=AdnocAddToCartValidator, validating add to cart request for product={} qty: {}.", productModel.getCode(), quantity);
        validateCartAgainstQuantity(productModel, quantity);
    }

    /**
     * Validate cart against quantity.
     *
     * @param productModel the product model
     * @param quantity
     */
    private void validateCartAgainstQuantity(final ProductModel productModel, final long quantity) throws CommerceCartModificationException
    {
        LOG.info("appEvent=AddToCart, validating Cart Against Quantity for productCode={}, quantity={}", productModel.getCode(), quantity);
        final Integer minOrderQuantity = productModel.getMinOrderQuantity();
        if (Objects.nonNull(minOrderQuantity) && quantity < minOrderQuantity)
        {
            LOG.info("appEvent=AddToCart, validating Cart Against Quantity, minOrderQuantity={}", minOrderQuantity);
            throw new CommerceCartModificationException(String.format("The minimum quantity to perform addToCart for this product is %s.", minOrderQuantity));
        }
        final Integer maxOrderQuantity = productModel.getMaxOrderQuantity();
        if (Objects.nonNull(maxOrderQuantity) && quantity > maxOrderQuantity)
        {
            throw new CommerceCartModificationException(String.format("The maximum quantity to perform addToCart for this product is %s.", maxOrderQuantity));
        }
    }
}
