package com.adnoc.facades.company.converters.populators;

import com.adnoc.facades.company.data.SAPSalesOrganizationData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class AdnocSAPSalesOrganizationPopulator implements Populator<SAPSalesOrganizationModel, SAPSalesOrganizationData>
{

    @Override
    public void populate(SAPSalesOrganizationModel sapSalesOrganizationModel, SAPSalesOrganizationData sapSalesOrganizationData) throws ConversionException
    {
        sapSalesOrganizationData.setSalesOrgId(sapSalesOrganizationModel.getSalesOrgId());
        sapSalesOrganizationData.setSalesOrganization(sapSalesOrganizationModel.getSalesOrganization());
        sapSalesOrganizationData.setDistributionChannel(sapSalesOrganizationModel.getDistributionChannel());
        sapSalesOrganizationData.setDivision(sapSalesOrganizationModel.getDivision());
    }
}
