package com.adnoc.facades.customerticketingfacades;

import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.data.TicketData;

import java.util.List;
import java.util.Map;

/**
 * Facade interface for handling customer ticketing operations in the ADNOC system.
 * This interface provides methods to create and retrieve tickets, as well as to get associated objects.
 */
public interface AdnocTicketFacade
{
    /**
     * Gets associated to objects.
     *
     * @param csTicketCategoryMapId the cs ticket category map id
     * @return the associated to objects
     */
    Map<String, List<TicketAssociatedData>> getAssociatedToObjects(String csTicketCategoryMapId);

    /**
     * Creates a new ticket.
     *
     * @param ticketData the ticket data containing details for the new ticket
     * @return TicketData containing the created ticket information
     */
    TicketData createTicket(TicketData ticketData);

    /**
     * Retrieves a ticket by its ID.
     *
     * @param ticketId the ID of the ticket to retrieve
     * @return TicketData containing the details of the requested ticket
     */
    TicketData getTicket(String ticketId);
}
