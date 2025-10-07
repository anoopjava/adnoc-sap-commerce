package com.adnoc.facades.customerticketingfacades.strategies;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.strategies.TicketAssociationStrategies;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class AdnocTicketNAAssociationStrategy implements TicketAssociationStrategies
{
    private static final Logger LOG = LogManager.getLogger(AdnocTicketNAAssociationStrategy.class);

    @Override
    public Map<String, List<TicketAssociatedData>> getObjects(final UserModel currentUser)
    {
        LOG.info("appEvent=AdnocTicketNAAssociation, getObjects method for={}", currentUser.getUid());
        final TicketAssociatedData ticketAssociatedData = new TicketAssociatedData();
        ticketAssociatedData.setCode("NA");
        ticketAssociatedData.setType("NA");
        return Map.of("NA", List.of(ticketAssociatedData));
    }
}
