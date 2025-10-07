package com.adnoc.facades.ticket.populators;

import com.adnoc.service.email.impl.AdnocDkimEmailService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AdnocTicketListPopulator implements Populator<CsTicketModel, TicketData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocTicketListPopulator.class);

    @Override
    public void populate(final CsTicketModel source, final TicketData target)
    {
        LOG.info("appEvent=AdnocTicketList,populate method called with source: {}, target: {}", source, target);
        target.setCrmCaseId(source.getCrmCaseId());
    }

}
