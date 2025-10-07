package com.adnoc.facades.order.converter.populator;

import com.adnoc.facade.product.data.UnitData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.UnitModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest

@RunWith(MockitoJUnitRunner.class)
public class AdnocUnitPopulatorTest {

    @Mock
    private UnitModel unitModel;

    @Test
    public void testPopulate() {
        // Arrange
        String code = "LTR";
        String name = "Liter";
        when(unitModel.getCode()).thenReturn(code);
        when(unitModel.getName()).thenReturn(name);

        UnitData unitData = new UnitData();
        AdnocUnitPopulator populator = new AdnocUnitPopulator();

        // Act
        populator.populate(unitModel, unitData);

        // Assert
        assertEquals(code, unitData.getCode());
        assertEquals(name, unitData.getName());
    }
}
