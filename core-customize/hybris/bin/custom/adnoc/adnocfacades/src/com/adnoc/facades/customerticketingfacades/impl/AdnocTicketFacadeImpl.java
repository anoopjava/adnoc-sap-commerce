package com.adnoc.facades.customerticketingfacades.impl;

import com.adnoc.facades.customerticketingfacades.AdnocTicketFacade;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.enums.CsTicketAssociatedTo;
import com.adnoc.service.enums.CsTicketRequestForCategory;
import com.adnoc.service.enums.CsTicketRequestForSubCategory;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import de.hybris.platform.customerticketingfacades.constants.CustomerticketingfacadesConstants;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.strategies.TicketAssociationStrategies;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ClassMismatchException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.model.CsAgentGroupModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticket.service.TicketService;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdnocTicketFacadeImpl implements AdnocTicketFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocTicketFacadeImpl.class);
    private static final String TICKET_ID_NOT_FOUND_ERROR_DESC = "Ticket not found for the given ID ";

    private AdnocConfigService adnocConfigService;
    private UserService userService;
    private TicketService ticketService;
    private TicketBusinessService ticketBusinessService;
    private Converter<CsTicketModel, TicketData> ticketConverter;
    private EnumerationService enumerationService;
    private ConfigurationService configurationService;
    private String ticketPriority;
    private String ticketReason;
    private Map<String, TicketAssociationStrategies> ticketAssociationStrategiesMap;

    @Override
    public Map<String, List<TicketAssociatedData>> getAssociatedToObjects(final String csTicketCategoryMapId)
    {
        LOG.info("appEvent=AdnocTicketAssociation, getAssociatedToObjects method called with csTicketCategoryMapId:{}", csTicketCategoryMapId);

        final AdnocCsTicketCategoryMapModel adnocCsTicketCategoryMapModel = getAdnocConfigService().getAdnocCsTicketCategoryMap(csTicketCategoryMapId);
        if (Objects.isNull(adnocCsTicketCategoryMapModel))
        {
            throw new BadRequestException(String.format("No AdnocCsTicketCategoryMap configured for %s", csTicketCategoryMapId));
        }
        final CsTicketAssociatedTo csTicketAssociatedTo = adnocCsTicketCategoryMapModel.getAssociatedTo();
        final TicketAssociationStrategies ticketAssocitedStartegy = getTicketAssociationStrategiesMap().get(csTicketAssociatedTo.getCode());
        if (Objects.isNull(ticketAssocitedStartegy))
        {
            throw new BadRequestException(String.format("No ticketAssocitedStartegy configured for %s", csTicketAssociatedTo));
        }
        LOG.info("appEvent=AdnocTicketAssociation,Retrieving associated objects for current user..");
        return ticketAssocitedStartegy.getObjects(getUserService().getCurrentUser());
    }

    @Override
    public TicketData createTicket(final TicketData ticketData)
    {
        LOG.info("appEvent=AdnocTicket,createTicket method called");
        final CsTicketModel ticket;
        final CsTicketParameter ticketParameter = createCsTicketParameter(ticketData);
        ticket = getTicketBusinessService().createTicket(ticketParameter);
        ticketData.setId(ticket.getTicketID());
        return ticketData;
    }

    @Override
    public TicketData getTicket(final String ticketId)
    {
        LOG.info("appEvent=AdnocTicket,getTicket method called with ticketID:{}", ticketId);
        final CsTicketModel ticketModel = getTicketService().getTicketForTicketId(ticketId);
        if (ticketModel == null)
        {
            throw new RuntimeException(TICKET_ID_NOT_FOUND_ERROR_DESC + ticketId);
        }
        LOG.info("appEvent=AdnocTicket, ticket found. checking customer UID");
        return ticketModel.getCustomer().getUid().equals(getUserService().getCurrentUser().getUid())
                ? getTicketConverter().convert(ticketModel, new TicketData()) : null;
    }

    protected CsTicketParameter createCsTicketParameter(final TicketData ticketData)
    {
        LOG.info("appEvent=AdnocTicket,entering createCsTicketParameter method");
        final CsTicketParameter ticketParameter = new CsTicketParameter();

        ticketParameter.setPriority(getEnumerationService().getEnumerationValue(CsTicketPriority._TYPECODE, ticketPriority));
        ticketParameter.setReason(getEnumerationService().getEnumerationValue(CsEventReason._TYPECODE, ticketReason));
        ticketParameter.setAssignedGroup(getDefaultCsAgentManagerGroup());
        if (ticketData.getTicketCategory() != null)
        {
            ticketParameter.setCategory(CsTicketCategory.valueOf(ticketData.getTicketCategory().name()));
        }
        ticketParameter.setHeadline(ticketData.getSubject());
        ticketParameter.setInterventionType(CsInterventionType.TICKETMESSAGE);
        ticketParameter.setCreationNotes(ticketData.getMessage());
        ticketParameter.setCustomer(getUserService().getCurrentUser());
        ticketParameter.setAttachments(ticketData.getAttachments());
        ticketParameter.setAssociated(ticketData.getAssociatedTo());
        ticketParameter.setCsTicketCategoryMapId(ticketData.getCsTicketCategoryMapId());
        if (Objects.nonNull(ticketData.getRequestFor()))
        {
            ticketParameter.setRequestFor(getEnumerationService().getEnumerationValue(CsTicketRequestForCategory._TYPECODE, ticketData.getRequestFor().getCode()));
        }
        if (Objects.nonNull(ticketData.getSubCategory()))
        {
            ticketParameter.setSubCategory(getEnumerationService().getEnumerationValue(CsTicketRequestForSubCategory._TYPECODE, ticketData.getSubCategory().getCode()));
        }
        LOG.info("appEvent=AdnocTicket, ticket parameter created successfully!");
        return ticketParameter;
    }

    protected CsAgentGroupModel getDefaultCsAgentManagerGroup()
    {
        LOG.info("appEvent=AdnocTicket, start getDefaultCsAgentManagerGroup method");
        final String csManagerGroup = getConfigurationService().getConfiguration()
                .getString(CustomerticketingfacadesConstants.DEFAULT_CS_AGENT_MANAGER_GROUP_UID, "");
        if (StringUtils.isNotBlank(csManagerGroup))
        {
            try
            {
                return getUserService().getUserGroupForUID(csManagerGroup, CsAgentGroupModel.class);
            }
            catch (final ClassMismatchException | UnknownIdentifierException exception)
            {
                LOG.error("Can't set AssignedGroup for the group {}, cause: {}", csManagerGroup, exception);
            }
        }
        return null;
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    protected UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    protected Map<String, TicketAssociationStrategies> getTicketAssociationStrategiesMap()
    {
        return ticketAssociationStrategiesMap;
    }

    public void setTicketAssociationStrategiesMap(Map<String, TicketAssociationStrategies> ticketAssociationStrategiesMap)
    {
        this.ticketAssociationStrategiesMap = ticketAssociationStrategiesMap;
    }

    protected TicketService getTicketService()
    {
        return ticketService;
    }

    public void setTicketService(final TicketService ticketService)
    {
        this.ticketService = ticketService;
    }

    protected TicketBusinessService getTicketBusinessService()
    {
        return ticketBusinessService;
    }

    public void setTicketBusinessService(final TicketBusinessService ticketBusinessService)
    {
        this.ticketBusinessService = ticketBusinessService;
    }

    protected Converter<CsTicketModel, TicketData> getTicketConverter()
    {
        return ticketConverter;
    }

    public void setTicketConverter(final Converter<CsTicketModel, TicketData> ticketConverter)
    {
        this.ticketConverter = ticketConverter;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    protected String getTicketPriority()
    {
        return ticketPriority;
    }

    public void setTicketPriority(final String ticketPriority)
    {
        this.ticketPriority = ticketPriority;
    }

    protected String getTicketReason()
    {
        return ticketReason;
    }

    public void setTicketReason(final String ticketReason)
    {
        this.ticketReason = ticketReason;
    }


}
