package com.adnoc.service.overdueinvoices.dao.impl;


import com.adnoc.service.model.AdnocOverduePaymentTransactionModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocOverdueInvoicesDaoImplTest
{
    @InjectMocks
    private AdnocOverdueInvoicesDaoImpl adnocOverdueInvoicesDao = new AdnocOverdueInvoicesDaoImpl();

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private SearchResult<AdnocOverduePaymentTransactionModel> searchResult;

    @Mock
    private AdnocOverduePaymentTransactionModel overduePaymentTransactionModel;

    @Test
    public void testFindAdnocOverduePaymentTransaction_Found()
    {
        Mockito.when(searchResult.getResult()).thenReturn(Collections.singletonList(overduePaymentTransactionModel));
        Mockito.when(flexibleSearchService.<AdnocOverduePaymentTransactionModel>search(any(FlexibleSearchQuery.class)))
                .thenReturn(searchResult);
        final AdnocOverduePaymentTransactionModel result = adnocOverdueInvoicesDao.findAdnocOverduePaymentTransaction("testPk");
        assertNotNull(result);
        verify(flexibleSearchService).search(any(FlexibleSearchQuery.class));
    }
}
