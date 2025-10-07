package com.adnoc.facades.customerticketingfacades.converters.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AdnocPayerTicketAssociationPopulatorTest {

    @Test
    public void testPopulate() {
        // Arrange
        B2BUnitModel source = mock(B2BUnitModel.class);
        when(source.getUid()).thenReturn("Unit123");

        TicketAssociatedData target = new TicketAssociatedData();

        AdnocPayerTicketAssociationPopulator populator = new AdnocPayerTicketAssociationPopulator();

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals("Unit123", target.getCode());
        assertEquals("Payer", target.getType());
    }
}
