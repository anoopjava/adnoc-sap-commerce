package com.adnoc.service.ticket;

import com.adnoc.service.ticket.dao.AdnocTicketDao;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.impl.DefaultTicketService;

import java.util.List;

public class AdnocTicketServiceImpl extends DefaultTicketService implements AdnocTicketService
{
    private AdnocTicketDao adnocTicketDao;

    @Override
    public List<CsTicketEventModel> getUpdateEventsForTicket(final CsTicketModel ticket)
    {
        return getAdnocTicketDao().findTicketEventByTicket(ticket);
    }
    
    protected AdnocTicketDao getAdnocTicketDao()
    {
        return adnocTicketDao;
    }

    public void setAdnocTicketDao(final AdnocTicketDao adnocTicketDao)
    {
        this.adnocTicketDao = adnocTicketDao;
    }
}
