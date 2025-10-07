package com.adnoc.facades.customerticketingfacades.strategies;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.company.service.AdnocB2BDocumentService;
import com.adnoc.service.company.service.AdnocB2BDocumentTypeService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdnocTicketInvoiceAssociationStrategyTest {

    @Mock
    private AdnocB2BUnitService adnocB2BUnitService;

    @Mock
    private AdnocB2BDocumentService adnocB2BDocumentService;

    @Mock
    private AdnocB2BDocumentTypeService adnocB2BDocumentTypeService;

    @Mock
    private Converter<B2BDocumentModel, TicketAssociatedData> adnocTicketInvoiceAssociationConverter;

    @Mock
    private B2BCustomerModel b2BCustomerModel;

    @Mock
    private B2BUnitModel b2BUnitModel;

    @Mock
    private B2BDocumentTypeModel b2BDocumentTypeModel;

    @Mock
    private B2BDocumentModel b2BDocumentModel;

    @Mock
    private TicketAssociatedData ticketAssociatedData;

    @Mock
    private UserModel nonB2BCustomerModel;

    @Test
    public void testGetObjects_WithB2BCustomerModel() {
        Mockito.when(adnocB2BUnitService.getParent(b2BCustomerModel)).thenReturn(b2BUnitModel);
        Mockito.when(adnocB2BDocumentTypeService.getB2BDocumentType("INVOICE")).thenReturn(b2BDocumentTypeModel);
        Mockito.when(adnocB2BDocumentService.getB2BDocuments(eq(b2BUnitModel), anyList(), anyList()))
                .thenReturn(List.of(b2BDocumentModel));
        Mockito.when(adnocTicketInvoiceAssociationConverter.convert(b2BDocumentModel)).thenReturn(ticketAssociatedData);

        AdnocTicketInvoiceAssociationStrategy strategy = new AdnocTicketInvoiceAssociationStrategy();
        strategy.setAdnocB2BUnitService(adnocB2BUnitService);
        strategy.setAdnocB2BDocumentService(adnocB2BDocumentService);
        strategy.setAdnocB2BDocumentTypeService(adnocB2BDocumentTypeService);
        strategy.setAdnocTicketInvoiceAssociationConverter(adnocTicketInvoiceAssociationConverter);

        Map<String, List<TicketAssociatedData>> result = strategy.getObjects(b2BCustomerModel);

        assertNotNull(result);
        assertTrue(result.containsKey("Invoice"));
        assertEquals(1, result.get("Invoice").size());
        assertSame(ticketAssociatedData, result.get("Invoice").get(0));
    }

    @Test
    public void testGetObjects_WithNonB2BCustomerModel_ReturnsEmptyMap() {
        AdnocTicketInvoiceAssociationStrategy strategy = new AdnocTicketInvoiceAssociationStrategy();
        strategy.setAdnocB2BUnitService(adnocB2BUnitService);
        strategy.setAdnocB2BDocumentService(adnocB2BDocumentService);
        strategy.setAdnocB2BDocumentTypeService(adnocB2BDocumentTypeService);
        strategy.setAdnocTicketInvoiceAssociationConverter(adnocTicketInvoiceAssociationConverter);

        Map<String, List<TicketAssociatedData>> result = strategy.getObjects(nonB2BCustomerModel);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
