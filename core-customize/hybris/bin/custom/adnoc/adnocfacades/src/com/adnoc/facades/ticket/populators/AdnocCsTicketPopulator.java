package com.adnoc.facades.ticket.populators;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.ticket.converters.populator.CsTicketPopulator;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocCsTicketPopulator extends CsTicketPopulator<CsTicketParameter, CsTicketModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocCsTicketPopulator.class);

    private AdnocConfigService adnocConfigService;
    private AdnocB2BUnitService adnocB2BUnitService;

    @Override
    public void populate(final CsTicketParameter source, final CsTicketModel target) throws ConversionException
    {
        LOG.info("appEvent=AdnocCsTicket,populate method called with source: {}, target: {}", source, target);
        super.populate(source, target);
        target.setRequestFor(source.getRequestFor());
        target.setSubCategory(source.getSubCategory());
        target.setAssociatedTo(source.getAssociated());
        final AdnocCsTicketCategoryMapModel adnocCsTicketCategoryMapModel = getAdnocConfigService().getAdnocCsTicketCategoryMap(source.getCsTicketCategoryMapId());
        target.setTargetSystem(Objects.nonNull(adnocCsTicketCategoryMapModel) ? adnocCsTicketCategoryMapModel.getTargetSystem() : null);
        final B2BUnitModel currentB2BUnit = getAdnocB2BUnitService().getCurrentB2BUnit();
        if (Objects.nonNull(currentB2BUnit))
        {
            target.setB2bUnit(currentB2BUnit);
            LOG.info("appEvent=AdnocCsTicket,Set b2bUnit field in CsTicketModel.");
        }
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }
}
