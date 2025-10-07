package com.adnoc.facades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelEntryData;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelRequestData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocOmsOrderFacadeImplTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderModel order;

    @Mock
    private OrderEntryModel orderEntry;

    @Test
    public void testBuildOrderCancelWithCancelReason() {
        AdnocOmsOrderFacadeImpl facade = new AdnocOmsOrderFacadeImpl();

        facade.setOrderService(orderService);

        Mockito.when(orderEntry.getOrder()).thenReturn(order);
        Mockito.when(orderEntry.getQuantity()).thenReturn(2L);
        Mockito.when(orderService.getEntryForNumber(order, 0)).thenReturn(orderEntry);

        OrderCancelEntryData entryData = new OrderCancelEntryData();
        entryData.setOrderEntryNumber(0);
        entryData.setCancelQuantity(2L);
        entryData.setNotes("Customer no longer require");
        entryData.setCancelReason(CancelReason.valueOf("Changed Mind"));

        OrderCancelRequestData requestData = new OrderCancelRequestData();
        requestData.setEntries(Collections.singletonList(entryData));

        OrderCancelRequest cancelRequest = facade.buildOrderCancelRequest(requestData, order);

        assertNotNull(cancelRequest);
        assertEquals(order, cancelRequest.getOrder());
    }

    @Test
    public void testBuildOrderCancelWithoutCancelReason() {

        AdnocOmsOrderFacadeImpl facade = new AdnocOmsOrderFacadeImpl();

        facade.setOrderService(orderService);
;

        Mockito.when(orderEntry.getOrder()).thenReturn(order);
        Mockito.when(orderEntry.getQuantity()).thenReturn(1L);

        OrderCancelEntryData entryData = new OrderCancelEntryData();
        entryData.setOrderEntryNumber(1);
        entryData.setCancelQuantity(1L);
        entryData.setNotes("Duplicate order");
        entryData.setCancelReason(null);

        OrderCancelRequestData requestData = new OrderCancelRequestData();
        requestData.setEntries(Collections.singletonList(entryData));

        Mockito.when(orderService.getEntryForNumber(order, 1)).thenReturn(orderEntry);


        OrderCancelRequest cancelRequest = facade.buildOrderCancelRequest(requestData, order);

        assertNotNull(cancelRequest);
        assertEquals(order, cancelRequest.getOrder());
    }

}
