package com.adnoc.facades.customerticketingfacades.converters.populators;

import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocTicketInvoiceAssociationPopulator implements Populator<B2BDocumentModel, TicketAssociatedData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocTicketInvoiceAssociationPopulator.class);

    @Override
    public void populate(B2BDocumentModel source, TicketAssociatedData target) throws ConversionException
    {
        LOG.info("appEvent=AdnocTicketInvoiceAssociation, populating model={} to data={}", source, target);
        target.setCode(source.getDocumentNumber());
        target.setModifiedtime(source.getDate());
        target.setType("Invoice");
    }
}
