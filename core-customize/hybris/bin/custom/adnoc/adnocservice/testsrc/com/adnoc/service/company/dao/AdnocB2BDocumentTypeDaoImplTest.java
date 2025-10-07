package com.adnoc.service.company.dao;

import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdnocB2BDocumentTypeDaoImplTest
{

    @InjectMocks
    private AdnocB2BDocumentTypeDaoImpl adnocB2BDocumentTypeDao=new AdnocB2BDocumentTypeDaoImpl();

    @Mock
    private FlexibleSearchService flexibleSearchService= Mockito.mock(FlexibleSearchService.class);
    @Mock
    private SearchResult searchResult=Mockito.mock(SearchResult.class);
    @Mock
    private B2BDocumentTypeModel b2BDocumentTypeModel=Mockito.mock(B2BDocumentTypeModel.class);

    @Test
    void testFindB2BDocumentType()
    {
        adnocB2BDocumentTypeDao.setFlexibleSearchService(flexibleSearchService);
        List<B2BDocumentTypeModel> b2BDocumentTypeModels=new ArrayList<>();
        b2BDocumentTypeModels.add(b2BDocumentTypeModel);
        Mockito.when(searchResult.getResult()).thenReturn(b2BDocumentTypeModels);
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);
        B2BDocumentTypeModel b2BDocumentTypeModel1=adnocB2BDocumentTypeDao.findB2BDocumentType("test");
        Assertions.assertThat(b2BDocumentTypeModel1).isNotNull();
    }
}