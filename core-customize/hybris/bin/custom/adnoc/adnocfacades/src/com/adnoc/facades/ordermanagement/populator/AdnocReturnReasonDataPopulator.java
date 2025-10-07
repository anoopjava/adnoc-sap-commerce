package com.adnoc.facades.ordermanagement.populator;

import com.adnoc.facades.ordermanagement.data.ReturnReasonData;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocReturnReasonDataPopulator implements Populator<RefundReason, ReturnReasonData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocReturnReasonDataPopulator.class);

    private TypeService typeService;

    @Override
    public void populate(final RefundReason source, final ReturnReasonData target) throws ConversionException
    {
        LOG.info("appEvent=AdnocReturnReason, populate method called with source:{} and target:{}", source, target);
        final RefundReason refundReason = source;
        target.setCode(refundReason.getCode());
        target.setName(getTypeService().getEnumerationValue(refundReason).getName());
    }

    protected TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(final TypeService typeService)
    {
        this.typeService = typeService;
    }
}


