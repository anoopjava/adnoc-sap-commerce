package com.adnoc.facades.strategies;

import com.adnoc.facades.ordermanagement.populator.AdnocReturnReasonDataPopulator;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy;
import de.hybris.platform.core.model.order.CartModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocCartValidationStrategy extends DefaultCartValidationStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocCartValidationStrategy.class);

    @Override
    protected void validateDelivery(final CartModel cartModel)
    {
        LOG.info("Cart code is: {}", cartModel.getCode());
    }
}
