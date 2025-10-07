package com.adnoc.service.integration.hooks;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class AdnocCSTicketPrePersistenceHook implements PrePersistHook
{
    private static final Logger LOG = LogManager.getLogger(AdnocCSTicketPrePersistenceHook.class);

    private ModelService modelService;
    private FlexibleSearchService flexibleSearchService;
    private EnumerationService enumerationService;

    @Override
    public Optional<ItemModel> execute(final ItemModel item, final PersistenceContext context)
    {
        if (item instanceof final CsTicketModel csTicketModel)
        {
            final IntegrationItem integrationsItem = context.getIntegrationItem();
            final String externalTicketId = csTicketModel.getExternalTicketId();
            if (StringUtils.isEmpty(externalTicketId) || !externalTicketExists(externalTicketId))
            {
                LOG.warn("appEvent=AdnocInboundCSTicket, ExternalTicketId is either empty or No CSTicket with ExternalTicketId = {} exist.", externalTicketId);
                return Optional.empty();
            }
            if (Objects.equals(csTicketModel.getState(), CsTicketState.NEW))
            {
                csTicketModel.setState(CsTicketState.OPEN);
            }
            final Object eventsObj = integrationsItem.getAttribute("events");
            if (eventsObj instanceof final Collection<?> eventCollection)
            {
                final Set<CommentModel> commentModels = Objects.nonNull(csTicketModel.getComments())
                        ? new HashSet<>(csTicketModel.getComments()) : new HashSet<>();

                for (final Object event : eventCollection)
                {
                    if (event instanceof final IntegrationItem integrationItem)
                    {
                        LOG.info("appEvent=AdnocInboundCSTicket,iterating integrationItem");
                        final String commentCode = (String) integrationItem.getAttribute("code");
                        final CsCustomerEventModel csCustomerEventModel = getOrCreateCsCustomerEvent(commentCode, integrationItem, commentModels);

                        csCustomerEventModel.setText((String) integrationItem.getAttribute("text"));
                        csCustomerEventModel.setSubject((String) integrationItem.getAttribute("subject"));

                        LOG.info("appEvent=AdnocInboundCSTicket,Adding csCustomerEventModel into commentModels");
                        commentModels.add(csCustomerEventModel);
                    }
                }
                LOG.info("appEvent=AdnocInboundCSTicket,Setting final CommentModel into CsTicketModel");
                csTicketModel.setComments(new ArrayList<>(commentModels));
            }
        }
        return Optional.of(item);
    }

    private CsCustomerEventModel getOrCreateCsCustomerEvent(final String commentCode, final IntegrationItem integrationItem, final Set<CommentModel> commentModels)
    {
        LOG.info("appEvent=AdnocInboundCSTicket, get or create CsCustomerEvent for comment code:{}", commentCode);
        final CommentModel existingComment = commentModels.stream().filter(commentModel ->
                StringUtils.equals(commentCode, commentModel.getCode())).findFirst().orElse(null);
        if (Objects.nonNull(existingComment) && existingComment instanceof final CsCustomerEventModel csCustomerEventModel)
        {
            LOG.info("appEvent=AdnocInboundCSTicket,Found Existing csCustomerEventModel and returning it.");
            return csCustomerEventModel;
        }
        LOG.info("appEvent=AdnocInboundCSTicket,No existing csCustomerEventModel found and creating new csCustomerEventModel.");
        final CsCustomerEventModel csCustomerEventModel = getModelService().create(CsCustomerEventModel.class);
        csCustomerEventModel.setCode(commentCode);

        final ComponentModel componentModel = getComponentModelById((IntegrationItem) integrationItem.getAttribute("component"));
        csCustomerEventModel.setComponent(componentModel);

        final UserModel userModel = getAuthorModelByUid((IntegrationItem) integrationItem.getAttribute("author"));
        csCustomerEventModel.setAuthor(userModel);

        final CommentTypeModel commentTypeModel = getComponentTypeModel((IntegrationItem) integrationItem.getAttribute("commentType"));
        csCustomerEventModel.setCommentType(commentTypeModel);

        final CsInterventionType csInterventionType = getInterventionType((IntegrationItem) integrationItem.getAttribute("interventionType"));
        csCustomerEventModel.setInterventionType(csInterventionType);

        LOG.info("appEvent=AdnocInboundCSTicket,Returning model from getOrCreateCsCustomerEvent() method");
        return csCustomerEventModel;
    }

    private CsInterventionType getInterventionType(final IntegrationItem integrationItem)
    {
        LOG.info("appEvent=AdnocInboundCSTicket,Inside getInterventionType()");
        final String interventionTypeCode = (String) integrationItem.getAttribute("code");
        final CsInterventionType csInterventionType = getEnumerationService().getEnumerationValue(CsInterventionType._TYPECODE, interventionTypeCode);
        return csInterventionType;
    }

    private UserModel getAuthorModelByUid(final IntegrationItem integrationItem)
    {
        LOG.info("appEvent=AdnocInboundCSTicket,Inside getAuthorModelByUid() method");
        final UserModel userModel = new UserModel();
        final String uid = (String) integrationItem.getAttribute("uid");
        userModel.setUid(uid);
        final UserModel usersModel = getFlexibleSearchService().getModelByExample(userModel);
        return usersModel;
    }

    private CommentTypeModel getComponentTypeModel(final IntegrationItem integrationItem)
    {
        LOG.info("appEvent=AdnocInboundCSTicket,started getComponentTypeModel() method");
        final String componentTypeCode = (String) integrationItem.getAttribute("code");
        final IntegrationItem componentTypeDomain = (IntegrationItem) integrationItem.getAttribute("domain");
        final String componentDomainCode = (String) componentTypeDomain.getAttribute("code");
        final DomainModel domainModel = getComponentDomainByCode(componentDomainCode);
        final CommentTypeModel CommentTypeModel = new CommentTypeModel();
        CommentTypeModel.setCode(componentTypeCode);
        CommentTypeModel.setDomain(domainModel);
        final CommentTypeModel commentTypesModel = getFlexibleSearchService().getModelByExample(CommentTypeModel);
        return commentTypesModel;
    }

    private DomainModel getComponentDomainByCode(final String componentDomainCode)
    {
        LOG.info("appEvent=AdnocInboundCSTicket,Inside getComponentDomainByCode() with code:{}", componentDomainCode);
        final DomainModel domainModel = new DomainModel();
        domainModel.setCode(componentDomainCode);
        final DomainModel domainsModel = getFlexibleSearchService().getModelByExample(domainModel);
        return domainsModel;
    }

    private ComponentModel getComponentModelById(final IntegrationItem integrationItem)
    {
        LOG.info("appEvent=AdnocInboundCSTicket,Inside getComponentModelById() method");
        final IntegrationItem componentDomain = (IntegrationItem) integrationItem.getAttribute("domain");
        final String doaminCode = (String) componentDomain.getAttribute("code");
        final ComponentModel componentModel = new ComponentModel();
        final DomainModel domainModel = getDomainByCode(doaminCode);
        componentModel.setDomain(domainModel);
        final ComponentModel ComponentsModel = getFlexibleSearchService().getModelByExample(componentModel);
        return ComponentsModel;
    }

    private DomainModel getDomainByCode(final String doaminCode)
    {
        LOG.info("appEvent=AdnocInboundCSTicket,Fetching getDomain by domainCode:{}", doaminCode);
        final DomainModel domainModel = new DomainModel();
        domainModel.setCode(doaminCode);
        final DomainModel domainsModel = getFlexibleSearchService().getModelByExample(domainModel);
        return domainsModel;
    }

    private boolean externalTicketExists(final String externalTicketId)
    {
        LOG.info("appEvent=AdnocInboundCSTicket,Fetching csticket by externalTicketId:{}", externalTicketId);
        final CsTicketModel csTicketModel = new CsTicketModel();
        csTicketModel.setExternalTicketId(externalTicketId);
        CsTicketModel csTicketsModel = null;
        try
        {
            csTicketsModel = getFlexibleSearchService().getModelByExample(csTicketModel);
        }
        catch (final ModelNotFoundException modelNotFoundException)
        {
            LOG.error("appEvent=AdnocInboundCSTicket, No CSTicket with ExternalTicketId = {} exist.", externalTicketId);
        }
        return Objects.nonNull(csTicketsModel);
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected FlexibleSearchService getFlexibleSearchService()
    {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
    {
        this.flexibleSearchService = flexibleSearchService;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

}
