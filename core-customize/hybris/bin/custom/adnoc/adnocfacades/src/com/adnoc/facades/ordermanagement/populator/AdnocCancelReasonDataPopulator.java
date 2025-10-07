package com.adnoc.facades.ordermanagement.populator;

import com.adnoc.facades.ordermanagement.data.CancelReasonData;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocCancelReasonDataPopulator implements Populator<CancelReason, CancelReasonData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocCancelReasonDataPopulator.class);

    private EnumerationService enumerationService;

    @Override
    public void populate(final CancelReason source, final CancelReasonData target) throws ConversionException
    {
        LOG.info("appEvent=AdnocCancelReason, populate method called with source:{} and target:{}", source, target);
        target.setCode(source.getCode());
        target.setName(getEnumerationService().getEnumerationName(source));
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }
}


