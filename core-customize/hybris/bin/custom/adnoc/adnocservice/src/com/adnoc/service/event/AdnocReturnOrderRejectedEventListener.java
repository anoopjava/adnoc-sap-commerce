package com.adnoc.service.event;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/* This file will notify the B2BCustomer when CsAdmin(CS Cockpit) rejects the Return Request */
public class AdnocReturnOrderRejectedEventListener extends AbstractSiteEventListener<AdnocReturnOrderRejectedEvent>
{
    private static final Logger LOG = LogManager.getLogger(AdnocReturnOrderRejectedEventListener.class);

    private BusinessProcessService businessProcessService;
    private ModelService modelService;

    @Override
    protected void onSiteEvent(final AdnocReturnOrderRejectedEvent adnocReturnOrderRejectedEvent)
    {
        final ReturnRequestModel returnRequest = adnocReturnOrderRejectedEvent.getReturnRequest();
        final ReturnProcessModel returnProcessModel = (ReturnProcessModel) getBusinessProcessService()
                .createProcess(
                        "ReturnOrderRejectedProcess-"
                                + returnRequest.getCode()
                                + "-" + returnRequest.getOrder().getStore().getUid()
                                + "-" + System.currentTimeMillis(),
                        "adnocReturnOrderRejectedProcess"
                );

        LOG.info("appEvent=AdnocReturnOrderRejectedEvent, Created business process for Rejected Return Order: {}", returnProcessModel.getCode());
        returnProcessModel.setReturnRequest(returnRequest);
        getModelService().save(returnProcessModel);
        getBusinessProcessService().startProcess(returnProcessModel);

    }

    @Override
    protected boolean shouldHandleEvent(final AdnocReturnOrderRejectedEvent adnocReturnOrderRejectedEvent)
    {
        return true;
    }

    protected BusinessProcessService getBusinessProcessService()
    {
        return businessProcessService;
    }

    public void setBusinessProcessService(final BusinessProcessService businessProcessService)
    {
        this.businessProcessService = businessProcessService;
    }

    public ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }
}
