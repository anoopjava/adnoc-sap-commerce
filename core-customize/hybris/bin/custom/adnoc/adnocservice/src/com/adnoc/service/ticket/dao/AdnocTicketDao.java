package com.adnoc.service.ticket.dao;

import de.hybris.platform.ticket.dao.TicketDao;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.util.List;

public interface AdnocTicketDao extends TicketDao
{
    List<CsTicketEventModel> findTicketEventByTicket(CsTicketModel ticket);

}
