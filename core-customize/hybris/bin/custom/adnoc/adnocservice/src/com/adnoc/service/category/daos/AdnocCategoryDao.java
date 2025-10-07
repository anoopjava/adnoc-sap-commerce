package com.adnoc.service.category.daos;

import de.hybris.platform.category.daos.CategoryDao;
import de.hybris.platform.category.model.CategoryModel;

public interface AdnocCategoryDao extends CategoryDao
{
    /**
     * Find category by division category model.
     *
     * @param division the division
     * @return the category model
     */
    CategoryModel findCategoryByDivision(String division);
}
