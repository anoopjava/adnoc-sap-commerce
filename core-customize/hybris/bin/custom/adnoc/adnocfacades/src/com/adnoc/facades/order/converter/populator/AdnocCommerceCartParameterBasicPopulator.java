package com.adnoc.facades.order.converter.populator;

import de.hybris.platform.commercefacades.order.converters.populator.CommerceCartParameterBasicPopulator;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocCommerceCartParameterBasicPopulator extends CommerceCartParameterBasicPopulator
{
    private static final Logger LOG = LogManager.getLogger(AdnocCommerceCartParameterBasicPopulator.class);

    @Override
    public void populate(final AddToCartParams addToCartParams, final CommerceCartParameter commerceCartParameter) throws ConversionException
    {
        super.populate(addToCartParams, commerceCartParameter);
        if (Objects.nonNull(commerceCartParameter.getProduct()))
        {
            LOG.info("appEvent=AdnocCommerceCartParameter,Product found in commerceCartParameter and getting division");
            final String division = commerceCartParameter.getProduct().getDivision();
            commerceCartParameter.setDivision(division);
        }
    }
}
