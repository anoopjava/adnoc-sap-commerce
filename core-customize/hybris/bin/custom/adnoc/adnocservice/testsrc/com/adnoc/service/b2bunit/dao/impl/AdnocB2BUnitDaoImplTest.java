package com.adnoc.service.b2bunit.dao.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdnocB2BUnitDaoImplTest
{
    @InjectMocks
    private AdnocB2BUnitDaoImpl adnocB2BUnitDao=new AdnocB2BUnitDaoImpl();

    @Mock
    private FlexibleSearchService flexibleSearchService= Mockito.mock(FlexibleSearchService.class);
    @Mock
    private SearchResult searchResult=Mockito.mock(SearchResult.class);
    @Mock
    private B2BUnitModel bUnitModel=Mockito.mock(B2BUnitModel.class);

    @Test
    void testFetchB2BUnitsForChildMapping()
    {
        adnocB2BUnitDao.setFlexibleSearchService(flexibleSearchService);
        List<B2BUnitModel> b2BUnitModels=new ArrayList<>();
        b2BUnitModels.add(bUnitModel);
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);
        Mockito.when(searchResult.getResult()).thenReturn(b2BUnitModels);
        Collection<B2BUnitModel> b2BUnitModels1= adnocB2BUnitDao.fetchB2BUnitsForChildMapping();
        Assertions.assertThat(b2BUnitModels1).isNotNull();
    }
}