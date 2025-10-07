package com.adnoc.service.customer.impl;

import com.adnoc.service.customer.AdnocCustomerAccountService;
import com.adnoc.service.customer.dao.AdnocCustomerAccountDao;
import com.adnoc.service.duplicatecheckhelper.AdnocDuplicateCheckHelper;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class AdnocCustomerAccountServiceImpl extends DefaultCustomerAccountService implements AdnocCustomerAccountService
{
    private static final Logger LOG = LogManager.getLogger(AdnocCustomerAccountServiceImpl.class);

    private AdnocCustomerAccountDao adnocCustomerAccountDao;
    private AdnocDuplicateCheckHelper adnocDuplicateCheckHelper;

    @Override
    public List<OrderModel> getOrderSummary(final String userId)
    {
        final List<OrderModel> orders = adnocCustomerAccountDao.getOrders(userId);
        LOG.info("appEvent=AdnocOrderSummary, found {} orders for {}", CollectionUtils.size(orders), userId);
        return orders;
    }

    @Override
    public B2BCustomerModel getB2BCustomer(final Map<String, String> duplicateCheckParams)
    {
        LOG.info("appEvent=AdnocB2BCustomerCreation, finding existing B2B customer with parameters: {}", duplicateCheckParams);
        getAdnocDuplicateCheckHelper().validateDuplicateInGigya(duplicateCheckParams);
        return adnocCustomerAccountDao.findB2BCustomer(duplicateCheckParams);
    }

    @Override
    public List<ReturnRequestModel> getReturnRequests(final String userId)
    {
        final List<ReturnRequestModel> returnRequests = adnocCustomerAccountDao.getReturnRequests(userId);
        LOG.info("appEvent=AdnocOrderSummary, found {} return orders for {}", CollectionUtils.size(returnRequests), userId);
        return returnRequests;
    }

    protected AdnocCustomerAccountDao getAdnocCustomerAccountDao()
    {
        return adnocCustomerAccountDao;
    }

    public void setAdnocCustomerAccountDao(final AdnocCustomerAccountDao adnocCustomerAccountDao)
    {
        this.adnocCustomerAccountDao = adnocCustomerAccountDao;
    }

    protected AdnocDuplicateCheckHelper getAdnocDuplicateCheckHelper()
    {
        return adnocDuplicateCheckHelper;
    }

    public void setAdnocDuplicateCheckHelper(final AdnocDuplicateCheckHelper adnocDuplicateCheckHelper)
    {
        this.adnocDuplicateCheckHelper = adnocDuplicateCheckHelper;
    }
}
