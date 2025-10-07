package com.adnoc.facades.pos.populator;

import de.hybris.platform.commercefacades.storelocator.converters.populator.PointOfServicePopulator;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

public class AdnocPointOfServicePopulator extends PointOfServicePopulator
{
    @Override
    public void populate(PointOfServiceModel source, PointOfServiceData target)
    {
        super.populate(source, target);
        target.setId(source.getPk().toString());
    }
}
