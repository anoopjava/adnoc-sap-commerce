package com.adnoc.service.validators;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.validator.AddToCartValidator;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocPayerDivisionAddToCartValidator extends AdnocB2BCartValidator implements AddToCartValidator
{
    private static final Logger LOG = LogManager.getLogger(AdnocPayerDivisionAddToCartValidator.class);

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
        LOG.info("appEvent=AdnocAddToCartValidator, validating cart against product:{}, and division: {}", parameter.getProduct().getCode(), parameter.getDivision());
        final ProductModel product = parameter.getProduct();
        final String productDivision = product.getDivision();
        validateCartAgainstPayerDivision(productDivision);
    }
}
