package com.adnoc.service.integration.hooks;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocCSTicketPostPersistenceHook implements PostPersistHook
{
    private static final Logger LOG = LogManager.getLogger(AdnocCSTicketPostPersistenceHook.class);
    private ModelService modelService;

    @Override
    public void execute(final ItemModel item, final PersistenceContext context)
    {
        if (item instanceof final CsTicketModel csTicketModel)
        {
            getModelService().saveAll(csTicketModel.getComments());
            LOG.info("appEvent=AdnocCSTicketInbound, AdnocCSTicketPostPersistenceHook Started for ticket: {}", csTicketModel.getTicketID());
        }

    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

}
