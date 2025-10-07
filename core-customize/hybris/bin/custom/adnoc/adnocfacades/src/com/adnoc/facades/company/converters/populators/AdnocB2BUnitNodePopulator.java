package com.adnoc.facades.company.converters.populators;

import com.adnoc.service.category.AdnocCategoryService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BUnitNodePopulator;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;

import java.util.Objects;

public class AdnocB2BUnitNodePopulator extends B2BUnitNodePopulator
{
    private AdnocCategoryService adnocCategoryService;

    @Override
    public void populate(final B2BUnitModel source, final B2BUnitNodeData target)
    {
        super.populate(source, target);
        SAPSalesOrganizationModel salesOrg = source.getSalesOrg();
        if (Objects.nonNull(salesOrg))
        {
            String division = salesOrg.getDivision();
            CategoryModel categoryModel = getAdnocCategoryService().getCategoryForDivision(division);
            if(Objects.nonNull(categoryModel))
            {
                target.setLob(categoryModel.getName());
            }
        }
    }

    protected AdnocCategoryService getAdnocCategoryService()
    {
        return adnocCategoryService;
    }

    public void setAdnocCategoryService(AdnocCategoryService adnocCategoryService)
    {
        this.adnocCategoryService = adnocCategoryService;
    }
}
