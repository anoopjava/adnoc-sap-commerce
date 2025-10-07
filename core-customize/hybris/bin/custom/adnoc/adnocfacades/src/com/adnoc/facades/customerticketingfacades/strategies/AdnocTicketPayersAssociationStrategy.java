package com.adnoc.facades.customerticketingfacades.strategies;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.enums.PartnerFunction;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.strategies.TicketAssociationStrategies;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdnocTicketPayersAssociationStrategy implements TicketAssociationStrategies
{
    private static final Logger LOG = LogManager.getLogger(AdnocTicketPayersAssociationStrategy.class);

    private Converter<B2BUnitModel, TicketAssociatedData> adnocPayerTicketAssociationConverter;
    private AdnocB2BUnitService adnocB2BUnitService;

    @Override
    public Map<String, List<TicketAssociatedData>> getObjects(final UserModel currentUser)
    {
        LOG.info("appEvent=AdnocTicketPayersAssociation,Retrieving B2B units for current user with PartnerFunction.PY.");
        final Set<B2BUnitModel> payerB2BUnits = getAdnocB2BUnitService().getB2BUnits((B2BCustomerModel) currentUser, PartnerFunction.PY);
        final List<TicketAssociatedData> payerTicketAssociatedDataList = Converters.convertAll(payerB2BUnits, getAdnocPayerTicketAssociationConverter());
        return Map.of("Payer", payerTicketAssociatedDataList);
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }

    protected Converter<B2BUnitModel, TicketAssociatedData> getAdnocPayerTicketAssociationConverter()
    {
        return adnocPayerTicketAssociationConverter;
    }

    public void setAdnocPayerTicketAssociationConverter(final Converter<B2BUnitModel, TicketAssociatedData> adnocPayerTicketAssociationConverter)
    {
        this.adnocPayerTicketAssociationConverter = adnocPayerTicketAssociationConverter;
    }
}
