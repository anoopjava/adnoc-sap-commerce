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
public class AdnocTicketPayersAssociationStrategyTest {

    @Mock
    private AdnocB2BUnitService adnocB2BUnitService;

    @Mock
    private Converter<B2BUnitModel, TicketAssociatedData> adnocPayerTicketAssociationConverter;

    @Mock
    private B2BCustomerModel customer;

    @Mock
    private B2BUnitModel unit1;

    @Mock
    private B2BUnitModel unit2;

    @Mock
    private TicketAssociatedData ticketData1;

    @Mock
    private TicketAssociatedData ticketData2;

    @InjectMocks
    private AdnocTicketPayersAssociationStrategy strategy;

    @Test
    public void testGetObjectsReturnsData() {
        Set<B2BUnitModel> b2bUnits = new HashSet<>(Arrays.asList(unit1, unit2));
        Mockito.when(adnocB2BUnitService.getB2BUnits(customer, PartnerFunction.PY)).thenReturn(b2bUnits);
        Mockito.when(adnocPayerTicketAssociationConverter.convert(unit1)).thenReturn(ticketData1);
        Mockito.when(adnocPayerTicketAssociationConverter.convert(unit2)).thenReturn(ticketData2);

        Map<String, List<TicketAssociatedData>> result = strategy.getObjects(customer);

        assertNotNull(result);
        assertTrue(result.containsKey("Payer"));
        List<TicketAssociatedData> dataList = result.get("Payer");
        assertEquals(2, dataList.size());
        assertTrue(dataList.contains(ticketData1));
        assertTrue(dataList.contains(ticketData2));

        verify(adnocB2BUnitService).getB2BUnits(customer, PartnerFunction.PY);
    }
}
