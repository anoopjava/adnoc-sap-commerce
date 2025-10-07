package com.adnoc.facades.process.email.context;

import com.adnoc.service.model.AdnocCsTicketProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocCsTicketClosedEmailContext extends AbstractEmailContext<AdnocCsTicketProcessModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocCsTicketClosedEmailContext.class);

    private CsTicketModel ticket;

    @Override
    public void init(final AdnocCsTicketProcessModel adnocCsTicketProcessModel, final EmailPageModel emailPageModel)
    {
        LOG.info("appEvent=AdnocCsTicketClosed, inti method start... ");
        super.init(adnocCsTicketProcessModel, emailPageModel);

        final CsTicketModel csTicketModel = adnocCsTicketProcessModel.getAdnocCsTicket();
        LOG.info("appEvent=AdnocCsTicketClosed, CsTicketModel retrieved");
        ticket = csTicketModel;
    }

    @Override
    protected BaseSiteModel getSite(final AdnocCsTicketProcessModel adnocCsTicketProcessModel)
    {
        return adnocCsTicketProcessModel.getAdnocCsTicket().getBaseSite();
    }

    @Override
    protected CustomerModel getCustomer(final AdnocCsTicketProcessModel adnocCsTicketProcessModel)
    {
        return (CustomerModel) adnocCsTicketProcessModel.getAdnocCsTicket().getCustomer();
    }

    @Override
    protected LanguageModel getEmailLanguage(final AdnocCsTicketProcessModel adnocCsTicketProcessModel)
    {
        return adnocCsTicketProcessModel.getAdnocCsTicket().getBaseSite().getDefaultLanguage();
    }

}
