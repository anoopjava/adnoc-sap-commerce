package com.adnoc.service.category;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;

/**
 * Adnoc Category Service interface.
 * This service extends the CategoryService to provide additional functionality specific to ADNOC categories.
 */
public interface AdnocCategoryService extends CategoryService
{

    /**
     * Gets category for division.
     *
     * @param division the division
     * @return the category for division
     */
    CategoryModel getCategoryForDivision(String division);
}
