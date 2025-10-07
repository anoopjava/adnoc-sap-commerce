/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.event;

import de.hybris.platform.commerceservices.event.QuoteBuyerOrderPlacedEvent;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/* * This class is responsible for notify customer after placing an order from a quote. */
public class AdnocQuoteBuyerOrderPlacedEventListener extends QuoteBuyerOrderPlacedEventListener
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuoteBuyerOrderPlacedEventListener.class);

    private ModelService modelService;
    private CommerceQuoteService commerceQuoteService;
    private BusinessProcessService businessProcessService;

    @Override
    protected void onEvent(final QuoteBuyerOrderPlacedEvent quoteBuyerOrderPlacedEvent)
    {
        LOG.info("appEvent=AdnocQuoteBuyerOrderPlacedEvent, received QuoteBuyerOrderPlacedEvent for Quote: {}", quoteBuyerOrderPlacedEvent.getQuote().getCode());
        super.onEvent(quoteBuyerOrderPlacedEvent);

        final QuoteProcessModel quoteBuyerProcessModel = (QuoteProcessModel) getBusinessProcessService()
                .createProcess("QuoteBuyerOrderPlacedProcess" + "-" + quoteBuyerOrderPlacedEvent.getQuote().getCode() + "-" + quoteBuyerOrderPlacedEvent.getQuote().getStore().getUid() + "-" + System.currentTimeMillis(), "adnocQuoteBuyerOrderPlacedEmailProcess");

        LOG.info("appEvent=AdnocQuoteBuyerOrderPlacedProcess, Created business process for:{}", quoteBuyerProcessModel.getCode());

        quoteBuyerProcessModel.setQuoteCode(quoteBuyerOrderPlacedEvent.getQuote().getCode());
        getModelService().save(quoteBuyerProcessModel);

        //start the business process
        LOG.debug("appEvent=AdnocQuoteBuyerOrderPlacedProcess,starting business process for:{}", quoteBuyerProcessModel.getCode());
        getBusinessProcessService().startProcess(quoteBuyerProcessModel);

    }

    @Override
    protected ModelService getModelService()
    {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

    @Override
    protected CommerceQuoteService getCommerceQuoteService()
    {
        return commerceQuoteService;
    }

    @Override
    public void setCommerceQuoteService(final CommerceQuoteService commerceQuoteService)
    {
        this.commerceQuoteService = commerceQuoteService;
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
