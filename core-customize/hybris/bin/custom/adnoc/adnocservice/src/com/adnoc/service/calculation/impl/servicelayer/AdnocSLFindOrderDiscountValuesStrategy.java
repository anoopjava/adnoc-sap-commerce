package com.adnoc.service.calculation.impl.servicelayer;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.impl.servicelayer.DefaultSLFindOrderDiscountValuesStrategy;
import de.hybris.platform.util.DiscountValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AdnocSLFindOrderDiscountValuesStrategy extends DefaultSLFindOrderDiscountValuesStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocSLFindOrderDiscountValuesStrategy.class);

    @Override
    public List<DiscountValue> findDiscountValues(final AbstractOrderEntryModel entry) throws CalculationException
    {
        if (entry.getOrder() instanceof final CartModel cartModel && Objects.nonNull(cartModel.getQuoteReference()))
        {
            LOG.info("appEvent=AdnocOrderDiscount,order has a quote reference with ID:{}", cartModel.getQuoteReference().getCode());
            return CollectionUtils.isNotEmpty(entry.getDiscountValues()) ? entry.getDiscountValues() : Collections.emptyList();
        }
        return Collections.emptyList();
    }
}