package com.adnoc.facades.order.impl;

import com.adnoc.facades.order.AdnocCancelReasonFacade;
import com.adnoc.facades.ordermanagement.data.CancelReasonData;
import com.adnoc.facades.returnreason.impl.AdnocReturnFacadeImpl;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AdnocCancelReasonFacadeImpl implements AdnocCancelReasonFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocCancelReasonFacadeImpl.class);
    private Converter<CancelReason, CancelReasonData> cancelReasonDataConverter;
    private EnumerationService enumerationService;

    @Override
    public List<CancelReasonData> getCanceReasons()
    {
        LOG.info("appEvent=AdnocReturnReasons, getReturnReasons method start");
        final List<CancelReason> cancelReasons = getEnumerationService().getEnumerationValues(CancelReason._TYPECODE);
        return Converters.convertAll(cancelReasons, getCancelReasonDataConverter());
    }

    protected Converter<CancelReason, CancelReasonData> getCancelReasonDataConverter()
    {
        return cancelReasonDataConverter;
    }

    public void setCancelReasonDataConverter(final Converter<CancelReason, CancelReasonData> cancelReasonDataConverter)
    {
        this.cancelReasonDataConverter = cancelReasonDataConverter;
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
