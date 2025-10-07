package com.adnoc.facades.customerticketingfacades.converters.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class AdnocTicketDeliveryAssociationPopulatorTest {

    @Test
    public void testPopulate() {
        // Arrange
        B2BDocumentModel source = mock(B2BDocumentModel.class);
        TicketAssociatedData target = new TicketAssociatedData();

        String expectedDocumentNumber = "DEL-987654";

        when(source.getDocumentNumber()).thenReturn(expectedDocumentNumber);

        AdnocTicketDeliveryAssociationPopulator populator = new AdnocTicketDeliveryAssociationPopulator();

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals(expectedDocumentNumber, target.getCode());
        assertEquals("Delivery", target.getType());
    }
}
