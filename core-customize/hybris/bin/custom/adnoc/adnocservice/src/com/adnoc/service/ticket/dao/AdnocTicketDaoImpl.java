package com.adnoc.service.ticket.dao;

import de.hybris.platform.comments.constants.GeneratedCommentsConstants;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.ticket.dao.impl.DefaultTicketDao;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.util.Collections;
import java.util.List;

public class AdnocTicketDaoImpl extends DefaultTicketDao implements AdnocTicketDao
{
    @Override
    public List<CsTicketEventModel> findTicketEventByTicket(CsTicketModel ticket)
    {
        String query = "SELECT {e:pk}, {c2i:reverseSequenceNumber} FROM {CsTicketEvent AS e JOIN " + GeneratedCommentsConstants.Relations.COMMENTITEMRELATION + " AS c2i ON {c2i:source}={e:pk} }WHERE {c2i:target}=?ticket ORDER BY {c2i:reverseSequenceNumber} DESC";
        FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
        fQuery.addQueryParameter("ticket", ticket);
        SearchResult<CsTicketEventModel> result = this.getFlexibleSearchService().search(fQuery);
        List<CsTicketEventModel> events = result.getResult();
        return events.isEmpty() ? Collections.EMPTY_LIST : Collections.singletonList(events.get(0));
    }

}
