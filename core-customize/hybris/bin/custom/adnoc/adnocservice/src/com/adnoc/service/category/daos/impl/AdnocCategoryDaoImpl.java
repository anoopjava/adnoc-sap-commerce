package com.adnoc.service.category.daos.impl;

import com.adnoc.service.category.daos.AdnocCategoryDao;
import de.hybris.platform.category.daos.impl.DefaultCategoryDao;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;

public class AdnocCategoryDaoImpl extends DefaultCategoryDao implements AdnocCategoryDao
{
    @Override
    public CategoryModel findCategoryByDivision(final String division)
    {
        final String FIND_CATEGORY_BY_DIVISION = "SELECT " + CategoryModel.PK + " FROM {" + CategoryModel._TYPECODE
                + "} WHERE {" + CategoryModel.DIVISION + "}=?" + CategoryModel.DIVISION;
        final SearchResult<CategoryModel> searchResult = search(FIND_CATEGORY_BY_DIVISION, Collections.singletonMap(CategoryModel.DIVISION, division));

        return CollectionUtils.isNotEmpty(searchResult.getResult()) ? searchResult.getResult().get(0) : null;
    }
}
