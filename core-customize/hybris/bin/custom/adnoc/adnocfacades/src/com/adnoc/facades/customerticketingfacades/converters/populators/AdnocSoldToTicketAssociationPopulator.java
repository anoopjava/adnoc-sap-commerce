package com.adnoc.facades.customerticketingfacades.converters.populators;

import com.adnoc.service.enums.PartnerFunction;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocSoldToTicketAssociationPopulator implements Populator<B2BUnitModel, TicketAssociatedData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocSoldToTicketAssociationPopulator.class);

    @Override
    public void populate(final B2BUnitModel source, final TicketAssociatedData target) throws ConversionException
    {
        LOG.info("appEvent=AdnocSoldToTicketAssociation, populating model={} to data={}", source, target);
        target.setCode(source.getUid());
        if (Objects.equals(PartnerFunction.SP, source.getPartnerFunction()))
        {
            target.setType("Sold-To");
        }
        else
        {
            target.setType("Payer");
        }
    }
}
