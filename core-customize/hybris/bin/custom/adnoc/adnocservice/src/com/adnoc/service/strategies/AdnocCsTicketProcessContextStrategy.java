package com.adnoc.service.strategies;

import com.adnoc.service.model.AdnocCsTicketProcessModel;
import de.hybris.platform.acceleratorservices.process.strategies.impl.AbstractProcessContextStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocCsTicketProcessContextStrategy extends AbstractProcessContextStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocCsTicketProcessContextStrategy.class);

    @Override
    public BaseSiteModel getCmsSite(final BusinessProcessModel businessProcessModel)
    {
        LOG.info("appEvent=AdnocCsTicketProcess, receiving CMS site for business process:{} ", businessProcessModel.getCode());
        ServicesUtil.validateParameterNotNull(businessProcessModel, AbstractProcessContextStrategy.BUSINESS_PROCESS_MUST_NOT_BE_NULL_MSG);

        if (businessProcessModel instanceof AdnocCsTicketProcessModel)
        {
            return ((AdnocCsTicketProcessModel) businessProcessModel).getAdnocCsTicket().getBaseSite();
        }
        LOG.warn("appEvent=AdnocCsTicketProcess, current model is not instance of adnocCsTicketProcessModel:{}", businessProcessModel.getCode());
        return null;
    }

    @Override
    protected CustomerModel getCustomer(final BusinessProcessModel businessProcess)
    {
        LOG.info("appEvent=AdnocCsTicketProcess, getting current customer for process:{}", businessProcess.getCode());
        if (businessProcess instanceof AdnocCsTicketProcessModel)
        {
            return (CustomerModel) ((AdnocCsTicketProcessModel) businessProcess).getAdnocCsTicket().getCustomer();
        }
        return null;
    }
}
