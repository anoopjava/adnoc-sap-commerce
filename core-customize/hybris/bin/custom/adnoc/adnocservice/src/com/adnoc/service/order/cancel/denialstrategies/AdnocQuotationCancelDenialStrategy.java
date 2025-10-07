package com.adnoc.service.order.cancel.denialstrategies;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.AbstractCancelDenialStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocQuotationCancelDenialStrategy extends AbstractCancelDenialStrategy implements OrderCancelDenialStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuotationCancelDenialStrategy.class);

    @Override
    public OrderCancelDenialReason getCancelDenialReason(final OrderCancelConfigModel orderCancelConfigModel, final OrderModel orderModel, final PrincipalModel principalModel, final boolean b, final boolean b1)
    {
        LOG.info("appEvent=AdnocQuotationCancel, getCancelDenialReason method called");
        ServicesUtil.validateParameterNotNull(orderCancelConfigModel, "Parameter orderCancelConfigModel must not be null");
        return Objects.nonNull(orderModel.getQuoteReference()) ? getReason() : null;
    }
}
