package com.adnoc.service.order.returnorder.check;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.strategy.ReturnableCheck;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocQuoteRefBasedReturnableCheck implements ReturnableCheck
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuoteRefBasedReturnableCheck.class);

    @Override
    public boolean perform(final OrderModel orderModel, final AbstractOrderEntryModel abstractOrderEntryModel, final long l)
    {
        LOG.info("appEvent=AdnocQuote, perform method start!");
        ServicesUtil.validateParameterNotNull(orderModel, "Parameter orderCancelConfigModel must not be null");
        return Objects.isNull(orderModel.getQuoteReference());
    }
}
