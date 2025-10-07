package com.adnoc.service.integration.outbound.service.impl;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdnocSapCpiOmmOrderOutboundCancellationActionTest {

    @Mock
    private OrderEntryModel orderEntryModel;

    @Test
    void testGetRawPropertyValue_andExtractSapCpiOutboundOrderItems_withValidData() {
        // Mock response map structure as per extractSapCpiOutboundOrderItems expectations
        Map<String, Object> innerMap = new HashMap<>();
        Map<String, String> itemMap = new HashMap<>();
        itemMap.put("entryNumber", "1");
        itemMap.put("lineItemOrderStatus", "EVERYTHING_REJECTED");
        itemMap.put("deliveryStatus", "NOT_DELIVERED");
        innerMap.put("results", Collections.singletonList(itemMap));
        Map<String, Object> body = new HashMap<>();
        body.put("anyKey", Collections.singletonMap("sapCpiOutboundOrderItems", innerMap));
        // But getRawPropertyValue expects a different nesting, so let's build that:
        Map<String, Object> sapCpiOutboundOrderItems = new HashMap<>();
        sapCpiOutboundOrderItems.put("results", Collections.singletonList(itemMap));
        Map<String, Object> nested = new HashMap<>();
        nested.put("sapCpiOutboundOrderItems", sapCpiOutboundOrderItems);
        Map<String, Object> top = new HashMap<>();
        top.put("someRoot", nested);
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(top);

        // getRawPropertyValue should be able to extract the nested property
        Object value = AdnocSapCpiOmmOrderOutboundCancellationAction.getRawPropertyValue(responseEntity, "sapCpiOutboundOrderItems");
        assertTrue(value instanceof Map);

        // extractSapCpiOutboundOrderItems returns list of maps
        List<Map<String, String>> list = AdnocSapCpiOmmOrderOutboundCancellationAction.extractSapCpiOutboundOrderItems(responseEntity);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("1", list.get(0).get("entryNumber"));
    }

    @Test
    void testGetRawPropertyValue_withNullAndMissing() {
        // null case
        assertNull(AdnocSapCpiOmmOrderOutboundCancellationAction.getRawPropertyValue(null, "x"));

        // missing property
        ResponseEntity<Map> response = ResponseEntity.ok(Collections.singletonMap("root", Collections.singletonMap("foo", "bar")));
        assertNull(AdnocSapCpiOmmOrderOutboundCancellationAction.getRawPropertyValue(response, "notThere"));
    }

    @Test
    void testExtractSapCpiOutboundOrderItems_withInvalidFormat() {
        // Missing results key
        Map<String, Object> sapCpiOutboundOrderItems = new HashMap<>();
        // wrong type under "results"
        sapCpiOutboundOrderItems.put("results", "notAList");
        Map<String, Object> nested = new HashMap<>();
        nested.put("sapCpiOutboundOrderItems", sapCpiOutboundOrderItems);
        Map<String, Object> top = new HashMap<>();
        top.put("root", nested);
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(top);

        List<Map<String, String>> list = AdnocSapCpiOmmOrderOutboundCancellationAction.extractSapCpiOutboundOrderItems(responseEntity);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void testGetOrderLevelStatuses_simple() throws Exception {
        // Create one concrete (not just a mock) entry so equals/hashCode is default
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);

        // ModificationEntry references the *same* entry as order.getEntries()
        OrderEntryModificationRecordEntryModel entryMod = mock(OrderEntryModificationRecordEntryModel.class);
        when(entryMod.getOrderEntry()).thenReturn(orderEntryModel);

        OrderModificationRecordEntryModel modRecordEntry = mock(OrderModificationRecordEntryModel.class);
        when(modRecordEntry.getOrderEntriesModificationEntries()).thenReturn(Collections.singletonList(entryMod));

        OrderModificationRecordModel modRecord = mock(OrderModificationRecordModel.class);
        when(modRecord.getModificationRecordEntries()).thenReturn(Collections.singletonList(modRecordEntry));

        OrderModel order = mock(OrderModel.class);
        Set<OrderModificationRecordModel> modSet = Collections.singleton(modRecord);
        when(order.getModificationRecords()).thenReturn(modSet);
        // The list contains the exact same 'entry' instance
        when(order.getEntries()).thenReturn(Collections.singletonList(entry));

        AdnocSapCpiOmmOrderOutboundCancellationAction action = new AdnocSapCpiOmmOrderOutboundCancellationAction();

        // Use reflection to invoke the private method
        java.lang.reflect.Method m = AdnocSapCpiOmmOrderOutboundCancellationAction.class
                .getDeclaredMethod("getOrderLevelStatuses", OrderModel.class);
        m.setAccessible(true);


        // Now add a *different* (unmatched) entry
        AbstractOrderEntryModel unmatchedEntry = mock(AbstractOrderEntryModel.class);
        when(order.getEntries()).thenReturn(Arrays.asList(entry, unmatchedEntry));
        OrderStatus partialStatus = (OrderStatus) m.invoke(action, order);
        assertEquals(OrderStatus.PARTIAL_REJECTED, partialStatus);
    }


    // Reflection helper for private/protected methods
    private Object invokePrivate(Object target, String methodName, Class<?>[] parameterTypes, Object... args) {
        try {
            java.lang.reflect.Method m = target.getClass().getDeclaredMethod(methodName, parameterTypes);
            m.setAccessible(true);
            return m.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
