package com.adnoc.service.category.impl;

import com.adnoc.service.category.AdnocCategoryService;
import com.adnoc.service.category.daos.AdnocCategoryDao;
import de.hybris.platform.category.impl.DefaultCategoryService;
import de.hybris.platform.category.model.CategoryModel;

public class AdnocCategoryServiceImpl extends DefaultCategoryService implements AdnocCategoryService
{
    private AdnocCategoryDao adnocCategoryDao;

    @Override
    public CategoryModel getCategoryForDivision(String division)
    {
        return getAdnocCategoryDao().findCategoryByDivision(division);
    }

    protected AdnocCategoryDao getAdnocCategoryDao()
    {
        return adnocCategoryDao;
    }

    public void setAdnocCategoryDao(AdnocCategoryDao adnocCategoryDao)
    {
        this.adnocCategoryDao = adnocCategoryDao;
    }
}
