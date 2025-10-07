package com.adnoc.service.ticket;

import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketService;

import java.util.List;

/**
 * Adnoc Ticket Service interface.
 * This service extends the TicketService to provide additional functionality specific to ADNOC tickets.
 */
public interface AdnocTicketService extends TicketService
{
    List<CsTicketEventModel> getUpdateEventsForTicket(final CsTicketModel ticket);
}
