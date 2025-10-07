package com.adnoc.service.order.handler;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocDynamicAttributesOrderStatusDisplay implements DynamicAttributeHandler<String, OrderModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocDynamicAttributesOrderStatusDisplay.class);

    public static final String PROCESSING = "Processing";
    private EnumerationService enumerationService;


    @Override
    public String get(final OrderModel orderModel)
    {
        final OrderStatus orderStatus = orderModel.getStatus();
        if (Objects.nonNull(orderStatus))
        {
            LOG.debug("appEvent=AdnocDynamicAttributesOrderStatusDisplay, Order Status: {}", orderStatus);
            return getEnumerationService().getEnumerationName(orderStatus);
        }
        return PROCESSING;
    }

    @Override
    public void set(final OrderModel model, final String s)
    {
        throw new UnsupportedOperationException();
    }

    public EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }
}

