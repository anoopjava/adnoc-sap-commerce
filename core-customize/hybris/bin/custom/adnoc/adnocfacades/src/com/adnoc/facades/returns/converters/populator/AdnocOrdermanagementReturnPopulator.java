package com.adnoc.facades.returns.converters.populator;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordermanagementfacades.returns.converters.populator.OrdermanagementReturnPopulator;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;

public class AdnocOrdermanagementReturnPopulator extends OrdermanagementReturnPopulator
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrdermanagementReturnPopulator.class);

    private EnumerationService enumerationService;

    @Override
    public void populate(final ReturnRequestModel source, final ReturnRequestData target) throws ConversionException
    {
        if (Objects.nonNull(source))
        {
            LOG.info("appEvent=Return,populate method called with returnCode: {}, status: {}", source.getCode(), source.getStatus());
            super.populate(source, target);
            target.setCreationTime(source.getCreationtime());
            Optional.ofNullable(getEnumerationService().getEnumerationName(source.getStatus())).ifPresent(target::setStatusDisplay);
            Optional.ofNullable(source.getReason()).ifPresent(target::setRefundReason);
            Optional.ofNullable(source.getReason()).ifPresent(code -> target.setReason(code.getCode()));
            Optional.ofNullable(getEnumerationService().getEnumerationName(source.getReason())).ifPresent(target::setReasonDisplay);
            Optional.ofNullable(source.getComment()).ifPresent(target::setComment);
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
