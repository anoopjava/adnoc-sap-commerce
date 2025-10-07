/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.event;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/* *  If quote was approved, a new quote snapshot will be created with status as BUYER_OFFER, and the customer will be notified that the quote is being resubmitted. */
public class AdnocReQuoteBuyerSubmissionEventListener extends AbstractEventListener<AdnocReQuoteBuyerSubmissionEvent>
{
    private static final Logger LOG = LogManager.getLogger(AdnocReQuoteBuyerSubmissionEventListener.class);

    private ModelService modelService;
    private BusinessProcessService businessProcessService;

    @Override
    protected void onEvent(final AdnocReQuoteBuyerSubmissionEvent adnocReQuoteBuyerSubmissionEvent)
    {
        LOG.info("appEvent=AdnocReQuoteBuyerSubmissionEvent, received adnocReQuoteBuyerSubmissionEvent for Quote: {}", adnocReQuoteBuyerSubmissionEvent.getQuote().getCode());

        final QuoteProcessModel quoteBuyerProcessModel = (QuoteProcessModel) getBusinessProcessService().createProcess(
                "quoteBuyerProcess" + "-" + System.currentTimeMillis(), "adnocRequoteBuyerSubmissionProcess");

        LOG.info("appEvent=AdnocReQuoteBuyerSubmissionProcess, Created business process for:{}", quoteBuyerProcessModel.getCode());

        final QuoteModel quoteModel = adnocReQuoteBuyerSubmissionEvent.getQuote();
        quoteBuyerProcessModel.setQuoteCode(quoteModel.getCode());
        getModelService().save(quoteBuyerProcessModel);

        //start the business process
        LOG.debug("appEvent=AdnocReQuoteBuyerSubmissionProcess,started business process for:{}", quoteBuyerProcessModel.getCode());
        getBusinessProcessService().startProcess(quoteBuyerProcessModel);

    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected BusinessProcessService getBusinessProcessService()
    {
        return businessProcessService;
    }

    public void setBusinessProcessService(final BusinessProcessService businessProcessService)
    {
        this.businessProcessService = businessProcessService;
    }
}
