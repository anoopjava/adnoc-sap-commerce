package com.adnoc.facades.customerticketingfacades.converters.populators;

import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocTicketDeliveryAssociationPopulator implements Populator<B2BDocumentModel, TicketAssociatedData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocTicketDeliveryAssociationPopulator.class);

    @Override
    public void populate(final B2BDocumentModel source, final TicketAssociatedData target) throws ConversionException
    {
        LOG.info("appEvent=AdnocTicketDeliveryAssociation, populating model={} to data={}", source, target);

        target.setCode(source.getDocumentNumber());
        target.setType("Delivery");
    }
}
