package com.adnoc.facades.customerticketingfacades.converters.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocTicketInvoiceAssociationPopulatorTest {

    @Test
    public void testPopulate() {
        // Arrange
        B2BDocumentModel source = mock(B2BDocumentModel.class);
        TicketAssociatedData target = new TicketAssociatedData();

        String expectedCode = "INV123456";
        Date expectedDate = new Date();

        when(source.getDocumentNumber()).thenReturn(expectedCode);
        when(source.getDate()).thenReturn(expectedDate);

        AdnocTicketInvoiceAssociationPopulator populator = new AdnocTicketInvoiceAssociationPopulator();

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals(expectedCode, target.getCode());
        assertEquals(expectedDate, target.getModifiedtime());
        assertEquals("Invoice", target.getType());
    }
}
