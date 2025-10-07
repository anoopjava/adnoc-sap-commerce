package com.adnoc.facades.ordermanagement.populator;

import com.adnoc.facades.ordermanagement.data.CancelReasonData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.enumeration.EnumerationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest

@RunWith(MockitoJUnitRunner.class)
public class AdnocCancelReasonDataPopulatorTest {

    @Mock
    private EnumerationService enumerationService;

    @InjectMocks
    private AdnocCancelReasonDataPopulator populator;

    @Test
    public void testPopulate() {
        // Arrange
        CancelReason source = CancelReason.CUSTOMERREQUEST;
        CancelReasonData target = new CancelReasonData();

        String expectedName = "CUSTOMER REQUEST";
        when(enumerationService.getEnumerationName(source)).thenReturn(expectedName);

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals("CustomerRequest", target.getCode());
        assertEquals(expectedName, target.getName());
    }
}
