package com.adnoc.service.registration.dao.impl;

import com.adnoc.service.model.AdnocSoldToB2BRegistrationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocB2BRegistrationDaoImplTest
{
    @InjectMocks
    private final AdnocB2BRegistrationDaoImpl adnocB2BRegistrationDao = new AdnocB2BRegistrationDaoImpl();
    @Mock
    private final FlexibleSearchService flexibleSearchService = Mockito.mock(FlexibleSearchService.class);
    @Mock
    private final SearchResult searchResult = Mockito.mock(SearchResult.class);
    @Mock
    private final AdnocSoldToB2BRegistrationModel registrationModel = Mockito.mock(AdnocSoldToB2BRegistrationModel.class);

    @Test
    void testFindAdnocB2BRegistration()
    {
        adnocB2BRegistrationDao.setFlexibleSearchService(flexibleSearchService);
        final Map<String, String> params = new HashMap<>();
        params.put("email", "test@example.com");
        final List<AdnocSoldToB2BRegistrationModel> resultList = new ArrayList<>();
        resultList.add(registrationModel);
        final String adnocRegistrationType = AdnocSoldToB2BRegistrationModel._TYPECODE;
        Mockito.when(searchResult.getResult()).thenReturn(resultList);
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);
        final AdnocSoldToB2BRegistrationModel result = (AdnocSoldToB2BRegistrationModel) adnocB2BRegistrationDao.findAdnocB2BRegistration(params, adnocRegistrationType);
        Assertions.assertThat(result).isNotNull();
    }
}