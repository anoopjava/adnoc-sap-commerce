package com.adnoc.service.category.daos.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.category.model.CategoryModel;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocCategoryDaoImplTest
{
    @InjectMocks
    private AdnocCategoryDaoImpl adnocCategoryDao=new AdnocCategoryDaoImpl();
    @Mock
    private FlexibleSearchService flexibleSearchService= Mockito.mock(FlexibleSearchService.class);
    @Mock
    private SearchResult searchResult=Mockito.mock(SearchResult.class);
    @Mock
    private CategoryModel categoryModel=Mockito.mock(CategoryModel.class);

    @Test
    void testFindCategoryByDivision()
    {
        adnocCategoryDao.setFlexibleSearchService(flexibleSearchService);
        Mockito.when(flexibleSearchService.search(Mockito.anyString(),Mockito.anyMap())).thenReturn(searchResult);
        List<CategoryModel> categoryModelList=new ArrayList<>();
        categoryModelList.add(categoryModel);
        Mockito.when(searchResult.getResult()).thenReturn(categoryModelList);
        CategoryModel categoryModel1= adnocCategoryDao.findCategoryByDivision("test01");
        Assertions.assertThat(categoryModel1).isNotNull();
    }
}