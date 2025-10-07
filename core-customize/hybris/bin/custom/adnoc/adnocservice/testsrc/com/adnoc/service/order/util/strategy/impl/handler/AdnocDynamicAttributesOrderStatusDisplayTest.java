package com.adnoc.service.order.util.strategy.impl.handler;


import com.adnoc.service.order.handler.AdnocDynamicAttributesOrderStatusDisplay;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdnocDynamicAttributesOrderStatusDisplayTest
{
    @InjectMocks
    private AdnocDynamicAttributesOrderStatusDisplay adnocDynamicAttributesOrderStatusDisplay;
    @Mock
    private EnumerationService enumerationService;
    @Mock
    private OrderModel orderModel;
    @Mock
    private OrderStatus orderStatus;

    @Test
    void testGetWithOrderStatus()
    {
        when(orderModel.getStatus()).thenReturn(orderStatus);
        when(enumerationService.getEnumerationName(orderStatus)).thenReturn("Paid");
        final String result = adnocDynamicAttributesOrderStatusDisplay.get(orderModel);
        assertEquals("Paid", result);
        verify(orderModel, times(1)).getStatus();
        verify(enumerationService, times(1)).getEnumerationName(orderStatus);
    }

    @Test
    void testGetWithNullOrderStatus()
    {
        when(orderModel.getStatus()).thenReturn(null);
        final String result = adnocDynamicAttributesOrderStatusDisplay.get(orderModel);
        assertEquals(AdnocDynamicAttributesOrderStatusDisplay.PROCESSING, result);
        verify(orderModel, times(1)).getStatus();
        verify(enumerationService, times(0)).getEnumerationName(any());
    }

}
