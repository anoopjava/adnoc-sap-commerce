package com.adnoc.service.ticket;

import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import com.adnoc.service.enums.CsTicketTargetSystem;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.impl.DefaultTicketBusinessService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
/**
 * AdnocTicketBusinessService extends the DefaultTicketBusinessService to handle ticket creation and note addition
 * with specific actions for sending SMS notifications and scheduling outbound tasks.
 */
public class AdnocTicketBusinessService extends DefaultTicketBusinessService
{
    private static final Logger LOG = LogManager.getLogger(AdnocTicketBusinessService.class);

    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;

    @Override
    protected CsTicketModel createTicketInternal(final CsTicketModel csTicketModel, final CsCustomerEventModel csCustomerEventModel)
    {
        super.createTicketInternal(csTicketModel, csCustomerEventModel);
        LOG.info("appEvent=AdnocCsTicket,sending sms notification for ticket creation.");
        if (Objects.equals(CsTicketTargetSystem.CRM, (csTicketModel.getTargetSystem())))
        {
            sendCsTicketOutbound(csTicketModel);
        }
        return csTicketModel;
    }

    @Override
    public CsCustomerEventModel addNoteToTicket(final CsTicketModel ticket, final CsInterventionType intervention, final CsEventReason reason, final String note, final Collection<MediaModel> attachments)
    {
        final CsCustomerEventModel csCustomerEventModel = super.addNoteToTicket(ticket, intervention, reason, note, attachments);
        sendCsTicketOutbound(ticket);
        return csCustomerEventModel;
    }

    private void sendCsTicketOutbound(final CsTicketModel csTicketModel) {
        getAdnocOutboundReplicationDirector().scheduleOutboundTask(csTicketModel);
    }

    protected AdnocOutboundReplicationDirector getAdnocOutboundReplicationDirector()
    {
        return adnocOutboundReplicationDirector;
    }

    public void setAdnocOutboundReplicationDirector(AdnocOutboundReplicationDirector adnocOutboundReplicationDirector)
    {
        this.adnocOutboundReplicationDirector = adnocOutboundReplicationDirector;
    }
}
