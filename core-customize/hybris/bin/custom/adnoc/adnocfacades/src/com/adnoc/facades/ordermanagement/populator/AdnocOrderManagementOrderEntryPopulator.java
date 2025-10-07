package com.adnoc.facades.ordermanagement.populator;

import com.adnoc.facade.product.data.UnitData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.warehousingfacades.order.converters.populator.WarehousingOrderEntryPopulator;

import java.util.Objects;
import java.util.Optional;

public class AdnocOrderManagementOrderEntryPopulator extends WarehousingOrderEntryPopulator
{
    private Converter<UnitModel, UnitData> adnocUnitConverter;

    @Override
    public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
    {
        super.populate(source, target);
        if (Objects.nonNull(source.getUnit()))
        {
            target.setUnit(getAdnocUnitConverter().convert(source.getUnit()));
        }
        Optional.ofNullable(source.getNamedDeliveryDate()).ifPresent(target::setNamedDeliveryDate);
    }

    protected Converter<UnitModel, UnitData> getAdnocUnitConverter()
    {
        return adnocUnitConverter;
    }

    public void setAdnocUnitConverter(final Converter<UnitModel, UnitData> adnocUnitConverter)
    {
        this.adnocUnitConverter = adnocUnitConverter;
    }
}
