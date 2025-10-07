package com.adnoc.facades.customerticketingfacades.strategies;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.enums.PartnerFunction;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocTicketSoldToPayersAssociationStrategyTest {

    @Mock
    private AdnocB2BUnitService adnocB2BUnitService;

    @Mock
    private Converter<B2BUnitModel, TicketAssociatedData> adnocTicketSoldToPayerAssociationConverter;

    @Mock
    private B2BCustomerModel customer;

    @Mock
    private B2BUnitModel payerUnit1, payerUnit2;

    @Mock
    private B2BUnitModel soldToUnit1, soldToUnit2;

    @Mock
    private TicketAssociatedData ticketData1, ticketData2, ticketData3, ticketData4;

    @InjectMocks
    private AdnocTicketSoldToPayersAssociationStrategy strategy;

    @Test
    public void testGetObjectsCombinedSoldToAndPayerData() {
        Set<B2BUnitModel> payerUnits = new HashSet<>(Arrays.asList(payerUnit1, payerUnit2));
        Set<B2BUnitModel> soldToUnits = new HashSet<>(Arrays.asList(soldToUnit1, soldToUnit2));

        Mockito.when(adnocB2BUnitService.getB2BUnits(customer, PartnerFunction.PY)).thenReturn(payerUnits);
        Mockito.when(adnocB2BUnitService.getB2BUnits(customer, PartnerFunction.SP)).thenReturn(soldToUnits);

        Mockito.when(adnocTicketSoldToPayerAssociationConverter.convert(payerUnit1)).thenReturn(ticketData1);
        Mockito.when(adnocTicketSoldToPayerAssociationConverter.convert(payerUnit2)).thenReturn(ticketData2);
        Mockito.when(adnocTicketSoldToPayerAssociationConverter.convert(soldToUnit1)).thenReturn(ticketData3);
        Mockito.when(adnocTicketSoldToPayerAssociationConverter.convert(soldToUnit2)).thenReturn(ticketData4);

        Map<String, List<TicketAssociatedData>> result = strategy.getObjects(customer);

        assertNotNull(result);
        assertTrue(result.containsKey("Sold-To/Payer"));

        List<TicketAssociatedData> combinedList = result.get("Sold-To/Payer");
        assertEquals(4, combinedList.size());
        assertTrue(combinedList.contains(ticketData1));
        assertTrue(combinedList.contains(ticketData2));
        assertTrue(combinedList.contains(ticketData3));
        assertTrue(combinedList.contains(ticketData4));

        verify(adnocB2BUnitService).getB2BUnits(customer, PartnerFunction.PY);
        verify(adnocB2BUnitService).getB2BUnits(customer, PartnerFunction.SP);
    }
}
