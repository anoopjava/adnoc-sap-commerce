/*
 *  * Copyright (c) SCL. All rights reserved.
 */

package com.adnoc.service.integration.hooks;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocQuotePostPersistHook implements PostPersistHook
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuotePostPersistHook.class);

    private CalculationService calculationService;

    @Override
    public void execute(final ItemModel item, final PersistenceContext context)
    {
        if (item instanceof final QuoteModel quoteModel)
        {
            LOG.info("appEvent=AdnocQuoteInbound, AdnocQuotePostPersistHook Started for Quote: {}", quoteModel.getCode());

            try
            {
                getCalculationService().calculateTotals(quoteModel, true);
            }
            catch (final CalculationException calculationException)
            {
                LOG.error(String.format("appEvent=AdnocQuoteInbound exception occurred while calculating totals %s.", ExceptionUtils.getRootCause(calculationException)), calculationException);
            }
        }
    }

    protected CalculationService getCalculationService()
    {
        return calculationService;
    }

    public void setCalculationService(final CalculationService calculationService)
    {
        this.calculationService = calculationService;
    }

}
