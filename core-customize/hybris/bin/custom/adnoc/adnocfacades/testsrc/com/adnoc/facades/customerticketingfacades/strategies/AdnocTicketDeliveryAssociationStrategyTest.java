package com.adnoc.facades.customerticketingfacades.strategies;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.company.service.AdnocB2BDocumentService;
import com.adnoc.service.company.service.AdnocB2BDocumentTypeService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.core.model.user.UserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocTicketDeliveryAssociationStrategyTest {

   @Mock
    Converter<B2BDocumentModel, TicketAssociatedData> converter;

    @Mock
    private AdnocB2BUnitService b2BUnitService;

    @Mock
    private AdnocB2BDocumentService documentService;

    @Mock
    private AdnocB2BDocumentTypeService docTypeService;


    @Mock
    private B2BCustomerModel customer;

    @Mock
    private B2BUnitModel unit;

    @Mock
    private B2BDocumentTypeModel documentType;

    @Mock
    private B2BDocumentModel doc1;


    @Test
    public void testGetObjectsWithValidB2BCustomer() {


        TicketAssociatedData ticketData = new TicketAssociatedData();

        Mockito.when(b2BUnitService.getParent(customer)).thenReturn(unit);
        Mockito.when(docTypeService.getB2BDocumentType("DELIVERY")).thenReturn(documentType);
        Mockito.when(documentService.getB2BDocuments(eq(unit), anyList(), anyList())).thenReturn(List.of(doc1));
        Mockito.when(converter.convert(doc1)).thenReturn(ticketData);

        // Class under test
        AdnocTicketDeliveryAssociationStrategy strategy = new AdnocTicketDeliveryAssociationStrategy();
        strategy.setAdnocB2BUnitService(b2BUnitService);
        strategy.setAdnocB2BDocumentService(documentService);
        strategy.setAdnocB2BDocumentTypeService(docTypeService);
        strategy.setAdnocTicketDeliveryAssociationConverter(converter);

        // Execute
        Map<String, List<TicketAssociatedData>> result = strategy.getObjects(customer);

        // Verify
        assertNotNull(result);
        assertTrue(result.containsKey("Delivery"));
        assertEquals(1, result.get("Delivery").size());
        assertSame(ticketData, result.get("Delivery").get(0));

        // Optional verifications
        verify(b2BUnitService).getParent(customer);
        verify(docTypeService).getB2BDocumentType("DELIVERY");
        verify(documentService).getB2BDocuments(eq(unit), anyList(), anyList());
        verify(converter).convert(doc1);
    }

    @Test
    public void testGetObjectsWithNonB2BCustomer() {
        // Arrange
        UserModel nonB2BCustomer = mock(UserModel.class);

        AdnocTicketDeliveryAssociationStrategy strategy = new AdnocTicketDeliveryAssociationStrategy();
        strategy.setAdnocB2BUnitService(b2BUnitService);
        strategy.setAdnocB2BDocumentService(documentService);
        strategy.setAdnocB2BDocumentTypeService(docTypeService);
        strategy.setAdnocTicketDeliveryAssociationConverter(converter);

        // Act
        Map<String, List<TicketAssociatedData>> result = strategy.getObjects(nonB2BCustomer);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
