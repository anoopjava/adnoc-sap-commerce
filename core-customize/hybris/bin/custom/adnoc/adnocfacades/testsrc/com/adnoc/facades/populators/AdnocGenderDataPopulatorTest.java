package com.adnoc.facades.populators;
import com.adnoc.facades.product.data.GenderData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@UnitTest

@RunWith(MockitoJUnitRunner.class)
public class AdnocGenderDataPopulatorTest {

    @Mock
    private TypeService typeService;

    @Mock
    private Gender gender;

    @Mock
    private EnumerationValueModel enumValueModel;

    @Test
    public void testPopulate() {
        // Arrange
        Mockito.when(gender.getCode()).thenReturn("male");
        Mockito.when(typeService.getEnumerationValue(gender)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Male");

        GenderData target = new GenderData();

        AdnocGenderDataPopulator populator = new AdnocGenderDataPopulator();
        populator.setTypeService(typeService);

        // Act
        populator.populate(gender, target);

        // Assert
        assertEquals("male", target.getCode());
        assertEquals("Male", target.getName());
    }
}
