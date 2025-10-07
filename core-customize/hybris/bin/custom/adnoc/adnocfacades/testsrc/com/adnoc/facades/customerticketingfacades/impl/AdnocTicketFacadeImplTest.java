package com.adnoc.facades.customerticketingfacades.impl;

import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.enums.CsTicketAssociatedTo;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.strategies.TicketAssociationStrategies;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocTicketFacadeImplTest {


    @Mock
    private AdnocConfigService adnocConfigService;

    @Mock
    private UserService userService;

    @Mock
    private TicketService ticketService;

    @Mock
    private Converter<CsTicketModel, TicketData> ticketConverter;

    @Mock
    private AdnocCsTicketCategoryMapModel csTicketCategoryMapModel;

    @Mock
    private TicketAssociationStrategies ticketAssociationStrategy;

    @Mock
    private CsTicketAssociatedTo associatedToEnum;

    @Mock
    private UserModel currentUser;

    @Mock
    private CsTicketModel ticketModel;

    @InjectMocks
    private AdnocTicketFacadeImpl adnocTicketFacade;


    @Test
    public void testGetAssociatedToObjects() {
        String categoryId = "TEST-CAT";
        Mockito.when(adnocConfigService.getAdnocCsTicketCategoryMap(categoryId)).thenReturn(csTicketCategoryMapModel);
        Mockito.when(csTicketCategoryMapModel.getAssociatedTo()).thenReturn(associatedToEnum);
        Mockito.when(associatedToEnum.getCode()).thenReturn("SOLD-TO");
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(ticketAssociationStrategy.getObjects(currentUser)).thenReturn(Map.of("SOLD-TO", List.of(new TicketAssociatedData())));

        adnocTicketFacade.setTicketAssociationStrategiesMap(Map.of("SOLD-TO", ticketAssociationStrategy));
        Map<String, List<TicketAssociatedData>> result = adnocTicketFacade.getAssociatedToObjects(categoryId);

        assertNotNull(result);
        assertTrue(result.containsKey("SOLD-TO"));
        verify(ticketAssociationStrategy).getObjects(currentUser);
    }

    @Test
    public void testWhenNoMapFound() {
        Mockito.when(adnocConfigService.getAdnocCsTicketCategoryMap("INVALID")).thenReturn(null);
        adnocTicketFacade.getAssociatedToObjects("INVALID");
    }

    @Test
    public void testWhenStrategyNotFound() {
        Mockito.when(adnocConfigService.getAdnocCsTicketCategoryMap("TEST")).thenReturn(csTicketCategoryMapModel);
        Mockito.when(csTicketCategoryMapModel.getAssociatedTo()).thenReturn(associatedToEnum);
        Mockito.when(associatedToEnum.getCode()).thenReturn("UNKNOWN");

        adnocTicketFacade.setTicketAssociationStrategiesMap(Map.of()); // empty map
        adnocTicketFacade.getAssociatedToObjects("TEST");
    }

    @Test
    public void testGetTicketReturnsTicketData() {
        String ticketId = "TCKT123";
        TicketData ticketData = new TicketData();
        ticketData.setId(ticketId);

        Mockito.when(ticketService.getTicketForTicketId(ticketId)).thenReturn(ticketModel);
        Mockito.when(ticketModel.getCustomer()).thenReturn(currentUser);
        Mockito.when(currentUser.getUid()).thenReturn("user123");
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(ticketConverter.convert(eq(ticketModel), any(TicketData.class))).thenReturn(ticketData);

        TicketData result = adnocTicketFacade.getTicket(ticketId);

        assertNotNull(result);
        assertEquals(ticketId, result.getId());
    }

    @Test
    public void testTicketNotFound() {
        Mockito.when(ticketService.getTicketForTicketId("INVALID")).thenReturn(null);
        adnocTicketFacade.getTicket("INVALID");
    }

    @Test
    public void testReturnsNullIfUserMismatch() {
        Mockito.when(ticketService.getTicketForTicketId("TCKT123")).thenReturn(ticketModel);
        UserModel otherUser = mock(UserModel.class);

        Mockito.when(ticketModel.getCustomer()).thenReturn(otherUser);
        Mockito.when(otherUser.getUid()).thenReturn("user999");
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(currentUser.getUid()).thenReturn("user123");

        TicketData result = adnocTicketFacade.getTicket("TCKT123");
        assertNull(result);
    }
}
