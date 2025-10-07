package com.adnoc.facades.ticket.populators;

import com.adnoc.facades.ticket.data.CsTicketRequestForCategoryData;
import com.adnoc.facades.ticket.data.CsTicketRequestForSubCategoryData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import com.adnoc.service.enums.CsTicketRequestForCategory;
import com.adnoc.service.enums.CsTicketRequestForSubCategory;
import de.hybris.platform.customerticketingfacades.data.TicketEventData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.service.TicketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocTicketPopulatorTest {

    @Spy
    private AdnocTicketPopulator populator;

    @Mock
    private CsTicketModel csTicketModel;

    @Mock
    private CsTicketRequestForCategory requestFor;

    @Mock
    private CsTicketRequestForSubCategory subCategory;

    @Mock
    private CsTicketState csTicketState;

    @Mock
    private Map<String, StatusData> statusMapping;

    @Mock
    private UserModel customer;

    @Mock
    private Map<StatusData, List<StatusData>> validTransitions;

    @Mock
    private TicketService ticketService;

    @Mock
    private Converter<CsTicketEventModel, TicketEventData> ticketEventConverter;

    @Test
    public void testSetsRequestForAndSubCategory() {
        TicketData ticketData = new TicketData();

        Mockito.when(csTicketModel.getState()).thenReturn(csTicketState);
        Mockito.when(csTicketState.getCode()).thenReturn("OPEN");

        Mockito.when(csTicketModel.getRequestFor()).thenReturn(requestFor);
        Mockito.when(csTicketModel.getSubCategory()).thenReturn(subCategory);

        Mockito.when(requestFor.getCode()).thenReturn("customer");
        Mockito.when(subCategory.getCode()).thenReturn("billing");

        Mockito.when(csTicketModel.getCustomer()).thenReturn(customer);
        Mockito.when(customer.getUid()).thenReturn("testuser@example.com");

        Mockito.when(csTicketModel.getAssociatedTo()).thenReturn("key=value");
        Mockito.when(csTicketModel.getModifiedtime()).thenReturn(new Date());

        // Act
        populator.setStatusMapping(statusMapping);
        populator.setTicketService(ticketService);
        populator.setTicketEventConverter(ticketEventConverter);
        populator.setValidTransitions(validTransitions);
        populator.populate(csTicketModel, ticketData);

        // Assert
        assertNotNull(ticketData.getRequestFor());
        assertEquals("customer", ticketData.getRequestFor().getCode());

        assertNotNull(ticketData.getSubCategory());
        assertEquals("billing", ticketData.getSubCategory().getCode());

        verify(populator).populate(csTicketModel, ticketData);
    }


    @Test
    public void testAssociatedToDataformats() {
        TicketData ticketData = new TicketData();

        Date date = new Date();
        Mockito.when(csTicketModel.getAssociatedTo()).thenReturn("Key=Value");
        Mockito.when(csTicketModel.getModifiedtime()).thenReturn(date);

        populator.setStatusMapping(statusMapping);
        populator.setTicketService(ticketService);
        populator.setTicketEventConverter(ticketEventConverter);
        populator.setValidTransitions(validTransitions);

        populator.populateAssociatedTodata(csTicketModel, ticketData);

        String expectedPrefix = "Key: Value; Updated: ";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
        String expected = expectedPrefix + sdf.format(date);

        assertEquals(expected, ticketData.getAssociatedTo());
    }
}
