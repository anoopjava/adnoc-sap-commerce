package com.adnoc.facades.customerticketingfacades.strategies;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.company.service.AdnocB2BDocumentService;
import com.adnoc.service.company.service.AdnocB2BDocumentTypeService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.strategies.TicketAssociationStrategies;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdnocTicketDeliveryAssociationStrategy implements TicketAssociationStrategies
{
    private static final Logger LOG = LogManager.getLogger(AdnocTicketDeliveryAssociationStrategy.class);

    private Converter<B2BDocumentModel, TicketAssociatedData> adnocTicketDeliveryAssociationConverter;
    private AdnocB2BDocumentTypeService adnocB2BDocumentTypeService;
    private AdnocB2BDocumentService adnocB2BDocumentService;
    private AdnocB2BUnitService adnocB2BUnitService;

    @Override
    public Map<String, List<TicketAssociatedData>> getObjects(final UserModel currentUser)
    {
        LOG.info("appEvent=AdnocTicketDeliveryAssociation,getObjects method for user={}", currentUser.getUid());
        final List<DocumentStatus> documentStatuses = new ArrayList<>();
        if (currentUser instanceof B2BCustomerModel b2BCustomerModel)
        {
            final B2BUnitModel parent = getAdnocB2BUnitService().getParent(b2BCustomerModel);
            final B2BDocumentTypeModel b2BDocumentTypeModel = getAdnocB2BDocumentTypeService().getB2BDocumentType("DELIVERY");
            final List<B2BDocumentModel> b2BDocumentModels = getAdnocB2BDocumentService().getB2BDocuments(parent, List.of(b2BDocumentTypeModel), documentStatuses);

            final List<TicketAssociatedData> payerTicketAssociatedDataList = Converters.convertAll(b2BDocumentModels, getAdnocTicketDeliveryAssociationConverter());

            LOG.debug("appEvent=AdnocTicketDeliveryAssociation, converted b2BDocumentModels to TicketAssociatedData");
            return Map.of("Delivery", payerTicketAssociatedDataList);
        }
        return Collections.emptyMap();
    }

    protected AdnocB2BDocumentTypeService getAdnocB2BDocumentTypeService()
    {
        return adnocB2BDocumentTypeService;
    }

    public void setAdnocB2BDocumentTypeService(final AdnocB2BDocumentTypeService adnocB2BDocumentTypeService)
    {
        this.adnocB2BDocumentTypeService = adnocB2BDocumentTypeService;
    }

    protected AdnocB2BDocumentService getAdnocB2BDocumentService()
    {
        return adnocB2BDocumentService;
    }

    public void setAdnocB2BDocumentService(final AdnocB2BDocumentService adnocB2BDocumentService)
    {
        this.adnocB2BDocumentService = adnocB2BDocumentService;
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }

    protected Converter<B2BDocumentModel, TicketAssociatedData> getAdnocTicketDeliveryAssociationConverter()
    {
        return adnocTicketDeliveryAssociationConverter;
    }

    public void setAdnocTicketDeliveryAssociationConverter(final Converter<B2BDocumentModel, TicketAssociatedData> adnocTicketDeliveryAssociationConverter)
    {
        this.adnocTicketDeliveryAssociationConverter = adnocTicketDeliveryAssociationConverter;
    }
}
