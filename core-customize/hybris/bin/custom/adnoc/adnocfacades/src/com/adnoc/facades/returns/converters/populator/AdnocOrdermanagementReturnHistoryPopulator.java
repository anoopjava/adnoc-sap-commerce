package com.adnoc.facades.returns.converters.populator;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordermanagementfacades.returns.converters.populator.OrdermanagementReturnHistoryPopulator;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;

public class AdnocOrdermanagementReturnHistoryPopulator extends OrdermanagementReturnHistoryPopulator
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrdermanagementReturnHistoryPopulator.class);
    
    private EnumerationService enumerationService;

    @Override
    public void populate(final ReturnRequestModel source, final ReturnRequestData target) throws ConversionException
    {
        if (Objects.nonNull(source))
        {
            LOG.info("appEvent=ReturnHistory,populate method called with returnCode: {}, status: {}", source.getCode(), source.getStatus());
            super.populate(source, target);
            Optional.ofNullable(getEnumerationService().getEnumerationName(source.getStatus())).ifPresent(target::setStatusDisplay);
        }
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }
}
