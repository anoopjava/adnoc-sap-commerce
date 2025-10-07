package com.adnoc.service.config.dao.impl;

import com.adnoc.service.model.AdnocConfigModel;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import com.adnoc.service.model.AdnocSapIntegrationCodeMapModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
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
import java.util.List;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdnocConfigDaoImplTest
{
    @InjectMocks
    private AdnocConfigDaoImpl adnocConfigDao;

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private SearchResult<AdnocConfigModel> adnocConfigResult;

    @Mock
    private SearchResult<AdnocSapIntegrationCodeMapModel> sapIntegrationResult;

    @Mock
    private SearchResult<AdnocCsTicketCategoryMapModel> csTicketCategoryMapResult;

    @Mock
    private SearchResult<SAPSalesOrganizationModel> salesOrgResult;

    @Mock
    private AdnocConfigModel configModel;

    @Mock
    private AdnocSapIntegrationCodeMapModel sapMapModel;

    @Mock
    private AdnocCsTicketCategoryMapModel adnocCsTicketCategoryMapModel;

    @Mock
    private SAPSalesOrganizationModel sapSalesOrganizationModel;


    @Test
    public void testFindAdnocConfig()
    {
        final List<AdnocConfigModel> expectedList = Collections.singletonList(configModel);
        Mockito.when(adnocConfigResult.getResult()).thenReturn(expectedList);
        Mockito.when(flexibleSearchService.<AdnocConfigModel>search(any(FlexibleSearchQuery.class)))
                .thenReturn(adnocConfigResult);
        final List<AdnocConfigModel> result = adnocConfigDao.findAdnocConfig("key1");

        assertEquals(1, result.size());
        verify(flexibleSearchService).search(any(FlexibleSearchQuery.class));
    }

    @Test
    public void testFindAdnocSapIntegrationCodeMap()
    {
        Mockito.when(sapIntegrationResult.getResult()).thenReturn(Collections.singletonList(sapMapModel));
        Mockito.when(flexibleSearchService.<AdnocSapIntegrationCodeMapModel>search(any(FlexibleSearchQuery.class)))
                .thenReturn(sapIntegrationResult);
        final AdnocSapIntegrationCodeMapModel result = adnocConfigDao.findAdnocSapIntegrationCodeMap(
                IntegrationType.OUTBOUND, String.class, "ENUM1");
        assertNotNull(result);
        verify(flexibleSearchService).search(any(FlexibleSearchQuery.class));
    }

    @Test
    public void testFindAdnocCsTicketCategoryMap()
    {
        Mockito.when(csTicketCategoryMapResult.getResult()).thenReturn(Collections.singletonList(adnocCsTicketCategoryMapModel));
        Mockito.when(flexibleSearchService.<AdnocCsTicketCategoryMapModel>search(any(FlexibleSearchQuery.class)))
                .thenReturn(csTicketCategoryMapResult);
        final List<AdnocCsTicketCategoryMapModel> result = adnocConfigDao.findAdnocCsTicketCategoryMap();
        assertFalse(result.isEmpty());
        verify(flexibleSearchService).search(any(FlexibleSearchQuery.class));
    }

    @Test
    public void testFindAdnocCsTicketCategoryMap_byId()
    {
        Mockito.when(csTicketCategoryMapResult.getResult()).thenReturn(Collections.singletonList(adnocCsTicketCategoryMapModel));
        Mockito.when(flexibleSearchService.<AdnocCsTicketCategoryMapModel>search(any(FlexibleSearchQuery.class)))
                .thenReturn(csTicketCategoryMapResult);
        final AdnocCsTicketCategoryMapModel result = adnocConfigDao.findAdnocCsTicketCategoryMap("id123");

        assertNotNull(result);
        verify(flexibleSearchService).search(any(FlexibleSearchQuery.class));
    }

    @Test
    public void testGetSalesOrgByDivision()
    {
        Mockito.when(salesOrgResult.getResult()).thenReturn(Collections.singletonList(sapSalesOrganizationModel));
        Mockito.when(flexibleSearchService.<SAPSalesOrganizationModel>search(any(FlexibleSearchQuery.class)))
                .thenReturn(salesOrgResult);

        final SAPSalesOrganizationModel result = adnocConfigDao.getSalesOrgbyDivision("divisionX");

        assertNotNull(result);
        verify(flexibleSearchService).search(any(FlexibleSearchQuery.class));
    }

    @Test
    public void testFindAdnocSapIntegrationCodeMap_returnsNullIfEmpty()
    {
        Mockito.when(sapIntegrationResult.getResult()).thenReturn(Collections.emptyList());
        Mockito.when(flexibleSearchService.<AdnocSapIntegrationCodeMapModel>search(any(FlexibleSearchQuery.class)))
                .thenReturn(sapIntegrationResult);

        final AdnocSapIntegrationCodeMapModel result = adnocConfigDao.findAdnocSapIntegrationCodeMap(
                IntegrationType.INBOUND, String.class, "SAPCODE");

        assertNull(result);

    }
}

