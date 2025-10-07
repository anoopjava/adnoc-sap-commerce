package com.adnoc.service.customer.impl;

import com.adnoc.service.customer.dao.AdnocCustomerAccountDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static de.hybris.platform.testframework.Assert.assertEquals;

public class AdnocCustomerAccountServiceImplTest
{
    @InjectMocks
    private AdnocCustomerAccountServiceImpl adnocCustomerAccountService = new AdnocCustomerAccountServiceImpl();
    @Mock
    private AdnocCustomerAccountDao adnocCustomerAccountDao = Mockito.mock(AdnocCustomerAccountDao.class);
    @Mock
    private OrderModel orderModel;
    @Mock
    private ReturnRequestModel returnRequestModel;

    @Test
    void testGetOrderSummary()
    {
        final List<OrderModel> listOfOrders = Arrays.asList(orderModel, orderModel);
        adnocCustomerAccountService.setAdnocCustomerAccountDao(adnocCustomerAccountDao);
        Mockito.when(adnocCustomerAccountDao.getOrders("testuserid@gmail.com")).thenReturn(listOfOrders);
        final List<OrderModel> orders = adnocCustomerAccountService.getOrderSummary("testuserid@gmail.com");
        Assertions.assertThat(orders).isNotNull();
        Mockito.verify(adnocCustomerAccountDao).getOrders("testuserid@gmail.com");
    }

    @Test
    public void testGetReturnRequests()
    {
        final String userId = "testreturnorders@gmail.com";
        final List<ReturnRequestModel> mockReturnRequests = Arrays.asList(returnRequestModel, returnRequestModel, returnRequestModel);
        adnocCustomerAccountService.setAdnocCustomerAccountDao(adnocCustomerAccountDao);
        Mockito.when(adnocCustomerAccountDao.getReturnRequests(userId)).thenReturn(mockReturnRequests);

        final List<ReturnRequestModel> listOfReturnOrders = adnocCustomerAccountService.getReturnRequests(userId);

        Assertions.assertThat(listOfReturnOrders).isNotNull();
        assertEquals(3, CollectionUtils.size(listOfReturnOrders));
        Mockito.verify(adnocCustomerAccountDao).getReturnRequests(userId);
    }
}
