package com.adnoc.facades.customerticketingfacades.converters.populators;

import com.adnoc.service.enums.PartnerFunction;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class AdnocSoldToTicketAssociationPopulatorTest {

    @Test
    public void testPopulateWithPartnerFunctionSP() {
        // Arrange
        B2BUnitModel source = mock(B2BUnitModel.class);
        TicketAssociatedData target = new TicketAssociatedData();

        when(source.getUid()).thenReturn("B2BUnit123");
        when(source.getPartnerFunction()).thenReturn(PartnerFunction.SP);

        AdnocSoldToTicketAssociationPopulator populator = new AdnocSoldToTicketAssociationPopulator();

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals("B2BUnit123", target.getCode());
        assertEquals("Sold-To", target.getType());
    }

    @Test
    public void testPopulateWithOtherPartnerFunction() {
        // Arrange
        B2BUnitModel source = mock(B2BUnitModel.class);
        TicketAssociatedData target = new TicketAssociatedData();

        when(source.getUid()).thenReturn("B2BUnit456");
        when(source.getPartnerFunction()).thenReturn(PartnerFunction.PY); // Assume PY means "Payer"

        AdnocSoldToTicketAssociationPopulator populator = new AdnocSoldToTicketAssociationPopulator();

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals("B2BUnit456", target.getCode());
        assertEquals("Payer", target.getType());
    }
}
