package com.adnoc.service.customer.dao;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.List;
import java.util.Map;

public interface AdnocCustomerAccountDao extends CustomerAccountDao
{
    /**
     * Gets orders.
     *
     * @param userID the user id
     * @return the orders
     */
    List<OrderModel> getOrders(final String userID);

    /**
     * Gets return requests.
     *
     * @param userID the user id
     * @return the return requests
     */
    List<ReturnRequestModel> getReturnRequests(final String userID);

    /**
     * Find b 2 b customer b 2 b customer model.
     *
     * @param duplicateCheckParams the duplicate check params
     * @return the b 2 b customer model
     */
    B2BCustomerModel findB2BCustomer(final Map<String, String> duplicateCheckParams);
}
