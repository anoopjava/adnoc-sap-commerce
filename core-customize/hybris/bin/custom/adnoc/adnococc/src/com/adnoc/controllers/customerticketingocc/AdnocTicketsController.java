package com.adnoc.controllers.customerticketingocc;

import com.adnoc.b2bocc.ticket.data.AdnocCsTicketCategoryMapListWsDto;
import com.adnoc.b2bocc.ticket.data.AdnocCsTicketCategoryMapWsDto;
import com.adnoc.facades.config.AdnocConfigFacade;
import com.adnoc.facades.customerticketingfacades.AdnocTicketFacade;
import com.adnoc.facades.ticket.data.AdnocCsTicketCategoryMapData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingocc.controllers.TicketBaseController;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketAssociatedObjectListWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketAssociatedObjectWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketStarterWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketWsDTO;
import de.hybris.platform.customerticketingocc.errors.exceptions.TicketCreateException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.hybris.platform.customerticketingocc.constants.SecuredAccessConstants.ROLE_CUSTOMERGROUP;
import static de.hybris.platform.customerticketingocc.constants.SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/{baseSiteId}")
@ApiVersion("v2")
@Tag(name = "Adnoc B2B Tickets")
public class AdnocTicketsController extends TicketBaseController
{
    @Resource(name = "adnocConfigFacade")
    private AdnocConfigFacade adnocConfigFacade;

    @Resource(name = "adnocTicketFacade")
    private AdnocTicketFacade adnocTicketFacade;

    @Resource(name = "ticketStarterValidator")
    private Validator ticketStarterValidator;

    @Secured({ROLE_CUSTOMERGROUP, ROLE_CUSTOMERMANAGERGROUP})
    @GetMapping(value = "/users/{userId}/getAdnocCsTicketCategoryMap")
    @Operation(operationId = "getAdnocCsTicketCategoryMap", summary = "Retrieves the list of AdnocCsTicketCategoryMap.")
    @ApiBaseSiteIdAndUserIdParam
    public AdnocCsTicketCategoryMapListWsDto getAdnocCsTicketCategoryMapDataList()
    {
        final AdnocCsTicketCategoryMapListWsDto adnocCsTicketCategoryMapListWsDto = new AdnocCsTicketCategoryMapListWsDto();
        final List<AdnocCsTicketCategoryMapData> adnocCsTicketCategoryMapDataList = adnocConfigFacade.getAdnocCsTicketCategoryMap();
        adnocCsTicketCategoryMapListWsDto.setAdnocCsTicketCategoryMapList(getDataMapper().mapAsList(adnocCsTicketCategoryMapDataList, AdnocCsTicketCategoryMapWsDto.class, FieldSetLevelHelper.DEFAULT_LEVEL));

        return adnocCsTicketCategoryMapListWsDto;
    }

    @Secured({ROLE_CUSTOMERGROUP})
    @GetMapping(value = "/users/{userId}/getAdnocTicketAssociatedObjects")
    @Operation(operationId = "getAdnocTicketAssociatedObjects", summary = "Retrieves the object associated with a ticket with ticketCategoryMapId.",
            description = "Retrieves the order or cart that is associated with the customer ticket with ticketCategoryMapId.")
    @ApiBaseSiteIdAndUserIdParam
    public TicketAssociatedObjectListWsDTO getAdnocTicketAssociatedObjects(@ApiFieldsParam @RequestParam(required = true) final String csTicketCategoryMapId)
    {
        final Map<String, List<TicketAssociatedData>> associatedObjectDataMap = adnocTicketFacade.getAssociatedToObjects(csTicketCategoryMapId);
        final List<TicketAssociatedData> ticketAssociatedDataList = associatedObjectDataMap.values().stream().
                flatMap(associatedObjectDataMapValue -> associatedObjectDataMapValue.stream()).collect(Collectors.toList());
        final List<TicketAssociatedObjectWsDTO> ticketAssociatedObjects = getDataMapper()
                .mapAsList(ticketAssociatedDataList, TicketAssociatedObjectWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
        final TicketAssociatedObjectListWsDTO ticketAssociatedObjectListWsDTO = new TicketAssociatedObjectListWsDTO();
        ticketAssociatedObjectListWsDTO.setTicketAssociatedObjects(ticketAssociatedObjects);
        return ticketAssociatedObjectListWsDTO;
    }


    @PostMapping(value = "/users/{userId}/adnoctickets", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    @Secured({ROLE_CUSTOMERGROUP})
    @Operation(summary = "Creates a ticket.", description = "Creates a ticket associated with a customer.", operationId = "createTicket")
    @ApiBaseSiteIdAndUserIdParam
    public TicketWsDTO createAdnocTicket(
            @Parameter(description = "Basic information of the ticket.", required = true) @RequestBody final TicketStarterWsDTO ticketStarter,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = "DEFAULT") final String fields)
    {
        validate(ticketStarter, "ticketStarter", getTicketStarterValidator());
        final TicketData ticketData = getDataMapper().map(ticketStarter, TicketData.class);
        final TicketData createdTicketData;
        try
        {
            createdTicketData = getAdnocTicketFacade().createTicket(ticketData);
        }
        catch (final RuntimeException re)
        {
            throw new TicketCreateException(getErrorMessage(re).orElse("Encountered an error when creating a new ticket"), null, re);
        }
        final TicketData returnedTicketData = getAdnocTicketFacade().getTicket(createdTicketData.getId());
        return getDataMapper().map(returnedTicketData, TicketWsDTO.class, fields);
    }

    private Optional<String> getErrorMessage(final RuntimeException exception)
    {
        if (exception instanceof ModelSavingException && exception.getCause() instanceof InterceptorException)
        {
            final String causeMessage = exception.getCause().getMessage();
            final String message = causeMessage.substring(causeMessage.indexOf(':') + 1)
                    .replace("\"headline\"", "\"Subject\"")
                    .replace("\"text\"", "\"Message\"")
                    .trim();
            return Optional.of(message);
        }
        if (exception instanceof UnknownIdentifierException)
        {
            return Optional.of(exception.getMessage().trim());
        }
        if (exception.getMessage() != null && !(exception.getMessage().isBlank()))
        {
            return Optional.of(exception.getMessage().trim());
        }
        return Optional.empty();
    }

    protected Validator getTicketStarterValidator()
    {
        return ticketStarterValidator;
    }

    public void setTicketStarterValidator(final Validator ticketStarterValidator)
    {
        this.ticketStarterValidator = ticketStarterValidator;
    }


    protected AdnocTicketFacade getAdnocTicketFacade()
    {
        return adnocTicketFacade;
    }

    public void setAdnocTicketFacade(final AdnocTicketFacade adnocTicketFacade)
    {
        this.adnocTicketFacade = adnocTicketFacade;
    }


}
