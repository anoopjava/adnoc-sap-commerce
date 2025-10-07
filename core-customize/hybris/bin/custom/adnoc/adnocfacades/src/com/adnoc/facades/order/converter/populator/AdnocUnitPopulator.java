package com.adnoc.facades.order.converter.populator;

import com.adnoc.facade.product.data.UnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocUnitPopulator implements Populator<UnitModel, UnitData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocUnitPopulator.class);

    @Override
    public void populate(final UnitModel source, final UnitData target) throws ConversionException
    {
        LOG.info("appEvent=AdnocUnit, populate method called with source:{} and target:{} ", source, target);
        target.setCode(source.getCode());
        target.setName(source.getName());
    }
}
