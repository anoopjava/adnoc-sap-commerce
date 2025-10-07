package com.adnoc.service.customer.dao.impl;

import com.adnoc.service.config.AdnocConfigService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AdnocCustomerAccountDaoImplTest
{
    @InjectMocks
    private AdnocCustomerAccountDaoImpl adnocCustomerAccountDao = new AdnocCustomerAccountDaoImpl();
    @Mock
    private AdnocConfigService adnocConfigService = Mockito.mock(AdnocConfigService.class);
    @Mock
    private FlexibleSearchService flexibleSearchService = Mockito.mock(FlexibleSearchService.class);
    @Mock
    private SearchResult searchResult = Mockito.mock(SearchResult.class);
    @Mock
    private OrderModel orderModel;
    @Mock
    private ReturnRequestModel returnRequestModel;

    @Test
    public void testGetOrders()
    {
        final int orderProcessingDays = 30;
        adnocCustomerAccountDao.setAdnocConfigService(adnocConfigService);
        Mockito.when(adnocConfigService.getAdnocConfigValue(AdnocCustomerAccountDaoImpl.ORDER_PROCESSING_DAYS, 30)).thenReturn(orderProcessingDays);

        adnocCustomerAccountDao.setFlexibleSearchService(flexibleSearchService);
        final List<OrderModel> orderModels = new ArrayList<>();
        orderModels.add(orderModel);

        Mockito.when(searchResult.getResult()).thenReturn(orderModels);
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);

        Mockito.when(searchResult.getTotalCount()).thenReturn(2);
        Mockito.when(searchResult.getResult()).thenReturn(Arrays.asList(orderModel, orderModel));

        Mockito.doReturn(searchResult)
                .when(flexibleSearchService)
                .search(Mockito.anyString(), Mockito.anyMap());

        final List<OrderModel> orders = adnocCustomerAccountDao.getOrders("testorders@gmail.com");

        assertNotNull(orders);
        assertEquals(2, orders.size());
        Mockito.verify(flexibleSearchService).search(Mockito.anyString(), Mockito.anyMap());
    }

    @Test
    public void testGetReturnRequests()
    {
        final int orderProcessingDays = 30;
        adnocCustomerAccountDao.setAdnocConfigService(adnocConfigService);
        Mockito.when(adnocConfigService.getAdnocConfigValue(AdnocCustomerAccountDaoImpl.ORDER_PROCESSING_DAYS, 30)).thenReturn(orderProcessingDays);

        adnocCustomerAccountDao.setFlexibleSearchService(flexibleSearchService);
        final List<ReturnRequestModel> returnRequestModels = new ArrayList<>();
        returnRequestModels.add(returnRequestModel);

        Mockito.when(searchResult.getResult()).thenReturn(returnRequestModels);
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);

        Mockito.when(searchResult.getTotalCount()).thenReturn(2);
        Mockito.when(searchResult.getResult()).thenReturn(Arrays.asList(returnRequestModel, returnRequestModel));

        Mockito.doReturn(searchResult)
                .when(flexibleSearchService)
                .search(Mockito.anyString(), Mockito.anyMap());

        final List<ReturnRequestModel> returnRequests = adnocCustomerAccountDao.getReturnRequests("testreturnorders@gmail.com");

        assertNotNull(returnRequests);
        assertEquals(2, returnRequests.size());
        Mockito.verify(flexibleSearchService).search(Mockito.anyString(), Mockito.anyMap());
    }
}
