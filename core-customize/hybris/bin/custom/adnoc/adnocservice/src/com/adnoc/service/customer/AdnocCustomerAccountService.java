package com.adnoc.service.customer;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.List;
import java.util.Map;

/**
 * Adnoc Customer Account Service interface.
 * This service extends the CustomerAccountService to provide additional functionality specific to ADNOC customer accounts.
 */
public interface AdnocCustomerAccountService extends CustomerAccountService
{

    /**
     * Gets order summary.
     *
     * @param userId the current user
     * @return the order summary
     */
    List<OrderModel> getOrderSummary(String userId);

    /**
     * Gets return requests.
     *
     * @param userId the user id
     * @return the return requests
     */
    List<ReturnRequestModel> getReturnRequests(String userId);

    /**
     * Gets b 2 b customer.
     *
     * @param duplicateCheckParams the duplicate check params
     * @return the b 2 b customer
     */
    B2BCustomerModel getB2BCustomer(final Map<String, String> duplicateCheckParams);
}
