package com.adnoc.service.category.impl;

import com.adnoc.service.category.daos.AdnocCategoryDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocCategoryServiceImplTest
{
    @InjectMocks
    private AdnocCategoryServiceImpl adnocCategoryService= Mockito.mock(AdnocCategoryServiceImpl.class);
    @Mock
    private AdnocCategoryDao adnocCategoryDao=Mockito.mock(AdnocCategoryDao.class);

    @Test
    void testGetCategoryForDivision()
    {
        adnocCategoryService.setAdnocCategoryDao(adnocCategoryDao);
        Mockito.when(adnocCategoryDao.findCategoryByDivision(Mockito.anyString())).thenReturn(new CategoryModel());
        CategoryModel categoryModel= adnocCategoryService.getCategoryForDivision("test01");
        Assertions.assertThat(categoryModel).isNotNull();
    }
}