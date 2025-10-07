//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.adnoc.service.integration.outbound;

import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.impl.DefaultReturnActionAdapter;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocReturnActionAdapter extends DefaultReturnActionAdapter
{
    private static final Logger LOG = LogManager.getLogger(AdnocReturnActionAdapter.class);

    private BusinessProcessService businessProcessService;
    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;
    private ModelService modelService;

    public AdnocReturnActionAdapter()
    {
        // Default constructor
    }

    @Override
    public void requestReturnApproval(final ReturnRequestModel returnRequest)
    {
        getAdnocOutboundReplicationDirector().scheduleOutboundTask(returnRequest);
        LOG.info("appEvent=AdnocReturnAction,requestReturnApproval being called");
        final ReturnProcessModel returnOrderProcessModel = createsAndSaveProcess("adnocReturnOrderOutbound-process", returnRequest);
        LOG.debug("appEvent=AdnocReturnAction,starting business process for " + returnOrderProcessModel.getCode());
        getBusinessProcessService().startProcess(returnOrderProcessModel);
    }

    private ReturnProcessModel createsAndSaveProcess(final String processDefName, final ReturnRequestModel returnRequest)
    {
        final ReturnProcessModel processModel = (ReturnProcessModel) businessProcessService.createProcess(processDefName + "-" + System.currentTimeMillis(), processDefName);
        processModel.setReturnRequest(returnRequest);
        getModelService().save(processModel);
        return processModel;
    }

    protected BusinessProcessService getBusinessProcessService()
    {
        return businessProcessService;
    }

    public void setBusinessProcessService(final BusinessProcessService businessProcessService)
    {
        this.businessProcessService = businessProcessService;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected AdnocOutboundReplicationDirector getAdnocOutboundReplicationDirector()
    {
        return adnocOutboundReplicationDirector;
    }

    public void setAdnocOutboundReplicationDirector(final AdnocOutboundReplicationDirector adnocOutboundReplicationDirector)
    {
        this.adnocOutboundReplicationDirector = adnocOutboundReplicationDirector;
    }
}
