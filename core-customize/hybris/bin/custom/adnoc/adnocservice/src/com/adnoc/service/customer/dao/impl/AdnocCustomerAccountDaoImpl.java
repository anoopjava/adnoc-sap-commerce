package com.adnoc.service.customer.dao.impl;

import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.customer.dao.AdnocCustomerAccountDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.dao.impl.DefaultCustomerAccountDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdnocCustomerAccountDaoImpl extends DefaultCustomerAccountDao implements AdnocCustomerAccountDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocCustomerAccountDaoImpl.class);
    public static final String ORDER_PROCESSING_DAYS = "orderProcessingDays";

    private AdnocConfigService adnocConfigService;

    @Override
    public List<OrderModel> getOrders(final String userID)
    {
        LOG.info("appEvent=AdnocOrderSummary,fetching orders for the current user:{}", userID);
        final int orderProcessingDays = getAdnocConfigService().getAdnocConfigValue(ORDER_PROCESSING_DAYS, 30);

        final LocalDateTime localDateToFetchOrders = LocalDateTime.now().minusDays(orderProcessingDays);
        final Date dateToFetchOrders = Date.from(localDateToFetchOrders.atZone(ZoneId.systemDefault()).toInstant());
        LOG.info("appEvent=AdnocOrderSummary,getting Orders: {}", dateToFetchOrders);

        final String query = "SELECT {o.pk} FROM {Order AS o JOIN User AS u ON {o.user} = {u.pk}} WHERE {u.uid} = ?userID AND {o.creationtime} >= ?orderCreationTime";

        final Map<String, Object> params = new HashMap<>();
        params.put("userId", userID);
        params.put("orderCreationTime", dateToFetchOrders);

        final SearchResult<OrderModel> result = getFlexibleSearchService().search(query, params);
        LOG.info("appEvent=AdnocOrderSummary,total orders {}", result.getTotalCount());
        return result.getResult();
    }

    @Override
    public List<ReturnRequestModel> getReturnRequests(final String userID)
    {
        LOG.info("appEvent=AdnocOrderSummary,fetching return orders for the current user:{}", userID);
        final int orderProcessingDays = getAdnocConfigService().getAdnocConfigValue(ORDER_PROCESSING_DAYS, 30);
        final LocalDateTime localDateToFetchReturnOrders = LocalDateTime.now().minusDays(orderProcessingDays);
        final Date dateToFetchReturnOrders = Date.from(localDateToFetchReturnOrders.atZone(ZoneId.systemDefault()).toInstant());

        final String query = "SELECT {returnRequest.pk} FROM {ReturnRequest AS returnRequest JOIN Order AS o ON {returnRequest.order} = {o.pk} JOIN User AS u ON {o.user} = {u.pk}} WHERE {u.uid} = ?userId AND {o.creationtime} >= ?returnOrderCreationTime";

        final Map<String, Object> params = new HashMap<>();
        params.put("userId", userID);
        params.put("returnOrderCreationTime", dateToFetchReturnOrders);

        final SearchResult<ReturnRequestModel> result = getFlexibleSearchService().search(query, params);
        LOG.info("appEvent=AdnocOrderSummary,total return orders: {}", result.getTotalCount());
        return result.getResult();
    }

    @Override
    public B2BCustomerModel findB2BCustomer(final Map<String, String> duplicateCheckParams)
    {
        String query = "SELECT {" + B2BCustomerModel.PK + "} FROM {" + B2BCustomerModel._TYPECODE + "}";
        if (MapUtils.isNotEmpty(duplicateCheckParams))
        {
            duplicateCheckParams.put(B2BCustomerModel.UID, duplicateCheckParams.get(B2BCustomerModel.EMAIL));
            final List<String> entries = duplicateCheckParams.entrySet().stream()
                    .map(duplicacyCheckParamEntry -> "{" + duplicacyCheckParamEntry.getKey() + "}=?" + duplicacyCheckParamEntry.getKey())
                    .toList();
            query = query + " WHERE (" + String.join(" OR ", entries) + ")";
        }
        LOG.info("appEvent=AdnocB2BCustomerCreation, Executing query: {}", query);
        final Map<String, Object> parameterMap = new HashMap<>(duplicateCheckParams);
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, parameterMap);

        final SearchResult<B2BCustomerModel> result = getFlexibleSearchService().search(searchQuery);
        if (CollectionUtils.isNotEmpty(result.getResult()))
        {
            LOG.info("appEvent=AdnocB2BCustomerCreation, found {} existing records.", CollectionUtils.size(result.getResult()));
            return result.getResult().get(0);
        }
        LOG.info("appEvent=AdnocB2BCustomerCreation, No existing record found.");
        return null;
    }


    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }
}
