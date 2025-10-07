package com.adnoc.facades.ticket.populators;

import com.adnoc.facades.ticket.data.CsTicketRequestForCategoryData;
import com.adnoc.facades.ticket.data.CsTicketRequestForSubCategoryData;
import de.hybris.platform.customerticketingfacades.converters.populators.DefaultTicketPopulator;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class AdnocTicketPopulator extends DefaultTicketPopulator<CsTicketModel, TicketData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocTicketPopulator.class);

    @Override
    public void populate(final CsTicketModel source, final TicketData target) throws ConversionException
    {
        LOG.info("appEvent=AdnocTicket,populate method called with source: {}, target: {}", source, target);

        super.populate(source, target);
        if (Objects.nonNull(source.getRequestFor()))
        {
            final CsTicketRequestForCategoryData csTicketRequestForCategoryData = new CsTicketRequestForCategoryData();
            csTicketRequestForCategoryData.setCode(source.getRequestFor().getCode());
            target.setRequestFor(csTicketRequestForCategoryData);
        }
        if (Objects.nonNull(source.getSubCategory()))
        {
            final CsTicketRequestForSubCategoryData csTicketRequestForSubCategoryData = new CsTicketRequestForSubCategoryData();
            csTicketRequestForSubCategoryData.setCode(source.getSubCategory().getCode());
            target.setSubCategory(csTicketRequestForSubCategoryData);
            LOG.info("appEvent=AdnocTicket,set SubCategory in TicketData:{}", csTicketRequestForSubCategoryData);
        }
        if (Objects.nonNull(source.getCrmCaseId()))
        {
            target.setCrmCaseId(source.getCrmCaseId());
        }
    }

    @Override
    protected void populateAssociatedTodata(final CsTicketModel source, final TicketData target)
    {
        LOG.info("appEvent=AdnocTicket, populateAssociatedTodata method start!");
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
        target.setAssociatedTo(source.getAssociatedTo().replace("=", ": ") + "; "
                + DefaultTicketPopulator.LAST_UPDATED + ": " + sdf.format(source.getModifiedtime()));
    }

}
