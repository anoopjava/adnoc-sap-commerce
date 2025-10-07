package com.adnoc.service.address;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocAddressDaoImplTest
{
    @InjectMocks
    private AdnocAddressDaoImpl adnocAddressDao=new AdnocAddressDaoImpl();

    @Mock
    private FlexibleSearchService flexibleSearchService= Mockito.mock(FlexibleSearchService.class);

    @Mock
    private AddressModel addressModel=Mockito.mock(AddressModel.class);

    @Test
    void testGetAddress()
    {
        SearchResult searchResult= Mockito.mock(SearchResult.class);
        List<AddressModel> addressModels= new ArrayList<>();
        addressModels.add(addressModel);
        Mockito.when(searchResult.getResult()).thenReturn(addressModels);
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);
        adnocAddressDao.setFlexibleSearchService(flexibleSearchService);
        AddressModel addressModel1= adnocAddressDao.getAddress("testPK");
        Assertions.assertThat(addressModel1).isNotNull();
    }
}