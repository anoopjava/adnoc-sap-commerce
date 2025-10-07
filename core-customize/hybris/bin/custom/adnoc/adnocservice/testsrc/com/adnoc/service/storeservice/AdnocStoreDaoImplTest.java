package com.adnoc.service.storeservice;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocStoreDaoImplTest
{
    @InjectMocks
    private AdnocStoreDaoImpl adnocStoreDao = new AdnocStoreDaoImpl();
    @Mock
    private FlexibleSearchService flexibleSearchService= Mockito.mock(FlexibleSearchService.class);
    @Mock
    private SearchResult searchResult=Mockito.mock(SearchResult.class);
    @Mock
    private PointOfServiceModel pointOfServiceModel = Mockito.mock(PointOfServiceModel.class);

    @Test
    void testFindPoSByPk() {
        adnocStoreDao.setFlexibleSearchService(flexibleSearchService);
        List<PointOfServiceModel> posList = new ArrayList<>();
        posList.add(pointOfServiceModel);
        Mockito.when(searchResult.getResult()).thenReturn(posList);
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);
        PointOfServiceModel result = adnocStoreDao.findPointOfServiceByPk("testPk");
        Assertions.assertThat(result).isNotNull();
    }
}
