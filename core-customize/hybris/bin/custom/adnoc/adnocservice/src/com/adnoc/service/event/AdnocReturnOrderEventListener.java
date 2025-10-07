package com.adnoc.service.event;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocReturnOrderEventListener extends AbstractSiteEventListener<CreateReturnEvent>
{
    private static final Logger LOG = LogManager.getLogger(AdnocReturnOrderEventListener.class);

    private BusinessProcessService businessProcessService;
    private ModelService modelService;

    @Override
    protected void onSiteEvent(final CreateReturnEvent event)
    {
        final ReturnRequestModel returnRequest = event.getReturnRequest();
        final ReturnProcessModel returnProcessModel = (ReturnProcessModel) getBusinessProcessService()
                .createProcess(
                        "ReturnOrderSubmissionProcess-"
                                + returnRequest.getCode()
                                + "-" + returnRequest.getOrder().getStore().getUid()
                                + "-" + System.currentTimeMillis(),
                        "adnocReturnOrderSubmissionProcess"
                );

        LOG.info("appEvent=AdnocReturnOrderEvent, Created business process for Return Order: {}", returnProcessModel.getCode());
        returnProcessModel.setReturnRequest(returnRequest);
        getModelService().save(returnProcessModel);
        getBusinessProcessService().startProcess(returnProcessModel);

    }

    @Override
    protected boolean shouldHandleEvent(final CreateReturnEvent event)
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
