package com.adnoc.facades.ordermanagement.populator;
import com.adnoc.facades.ordermanagement.data.ReturnReasonData;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdnocReturnReasonDataPopulatorTest {

    @Mock
    private TypeService typeService;

    @Mock
    private RefundReason refundReason;

    @Mock
    private EnumerationValueModel enumValueModel;

    @Test
    public void testPopulate() {
        // Arrange
        when(refundReason.getCode()).thenReturn("reasonCode");
        when(typeService.getEnumerationValue(refundReason)).thenReturn(enumValueModel);
        when(enumValueModel.getName()).thenReturn("Reason Name");

        ReturnReasonData target = new ReturnReasonData();

        AdnocReturnReasonDataPopulator populator = new AdnocReturnReasonDataPopulator();
        populator.setTypeService(typeService);

        // Act
        populator.populate(refundReason, target);

        // Assert
        assertEquals("reasonCode", target.getCode());
        assertEquals("Reason Name", target.getName());
    }
}
