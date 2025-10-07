package com.adnoc.facades.company.converters.populators;

import com.adnoc.facades.company.data.PartnerFunction;
import com.adnoc.facades.company.data.SAPSalesOrganizationData;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BUnitPopulator;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;

public class AdnocB2BUnitPopulator extends B2BUnitPopulator
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BUnitPopulator.class);

    private Converter<SAPSalesOrganizationModel, SAPSalesOrganizationData> salesOrganizationConverter;

    @Override
    public void populate(final B2BUnitModel source, final B2BUnitData target)
    {
        LOG.info("appEvent=AdnocB2BUnit, populate method called");
        super.populate(source, target);

        Optional.ofNullable(source.getPartnerFunction())
                .map(partnerFunction -> PartnerFunction.valueOf(partnerFunction.getCode()))
                .ifPresent(target::setPartnerFunction);

        if (Objects.nonNull(source.getSalesOrg()))
        {
            target.setSalesOrg(getSalesOrganizationConverter().convert(source.getSalesOrg()));
        }
    }

    protected Converter<SAPSalesOrganizationModel, SAPSalesOrganizationData> getSalesOrganizationConverter()
    {
        return salesOrganizationConverter;
    }

    public void setSalesOrganizationConverter(final Converter<SAPSalesOrganizationModel, SAPSalesOrganizationData> salesOrganizationConverter)
    {
        this.salesOrganizationConverter = salesOrganizationConverter;
    }
}
