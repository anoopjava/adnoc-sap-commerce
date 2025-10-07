package com.adnoc.service.overdueinvoices.dao.impl;

import com.adnoc.service.enums.AdnocOverDueStatus;
import com.adnoc.service.model.AdnocOverdueInvoiceDetailsModel;
import com.adnoc.service.model.AdnocOverduePaymentTransactionModel;
import com.adnoc.service.overdueinvoices.dao.AdnocOverdueInvoicesDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdnocOverdueInvoicesDaoImpl implements AdnocOverdueInvoicesDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocOverdueInvoicesDaoImpl.class);
    public static final String PAYMENTINPROGRESS = "paymentinprogress";
    private FlexibleSearchService flexibleSearchService;

    @Override
    public AdnocOverduePaymentTransactionModel findAdnocOverduePaymentTransaction(final String resultIndicator)
    {
        final String query = "SELECT {" + AdnocOverduePaymentTransactionModel.PK + "} FROM {" + AdnocOverduePaymentTransactionModel._TYPECODE + "} WHERE {" + AdnocOverduePaymentTransactionModel.CODE + "} = ?" + AdnocOverduePaymentTransactionModel.CODE;
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        searchQuery.addQueryParameter(AdnocOverduePaymentTransactionModel.CODE, resultIndicator);
        final SearchResult<AdnocOverduePaymentTransactionModel> result = getFlexibleSearchService().search(searchQuery);
        LOG.info("appEvent=AdnocOverdueInvoicesDaoImpl,payment transaction UTRN : {}", result.getResult().get(0).getCode());
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult().get(0) : null;
    }

    @Override
    public List<AdnocOverdueInvoiceDetailsModel> fetchPaymentInProgessAdnocOverdueInvoiceDetails()
    {
        final String query = "SELECT {" + AdnocOverdueInvoiceDetailsModel.PK + "} FROM {" + AdnocOverdueInvoiceDetailsModel._TYPECODE + "} WHERE {" + AdnocOverdueInvoiceDetailsModel.OVERDUESTATUS + "}=?paymentInProgress";
        final Map<String, Object> params = new HashMap<>();
        params.put(PAYMENTINPROGRESS, AdnocOverDueStatus.PAYMENT_IN_PROGRESS);
        final SearchResult<AdnocOverdueInvoiceDetailsModel> result = getFlexibleSearchService().search(query, params);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : Collections.emptyList();
    }

    protected FlexibleSearchService getFlexibleSearchService()
    {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
    {
        this.flexibleSearchService = flexibleSearchService;
    }
}
