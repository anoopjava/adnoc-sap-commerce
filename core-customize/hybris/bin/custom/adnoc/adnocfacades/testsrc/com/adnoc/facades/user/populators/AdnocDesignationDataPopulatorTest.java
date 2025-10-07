package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.AdnocDesignationData;
import com.adnoc.service.enums.Designation;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocDesignationDataPopulatorTest {

    private final TypeService typeService = Mockito.mock(TypeService.class);
    private final EnumerationService enumerationService = Mockito.mock(EnumerationService.class);
    private final Designation designation = Mockito.mock(Designation.class);
    private final EnumerationValueModel enumValueModel = Mockito.mock(EnumerationValueModel.class);

    @Test
    void testPopulateWithValidDesignation() {

        AdnocDesignationDataPopulator populator = new AdnocDesignationDataPopulator();
        populator.setTypeService(typeService);
        populator.setEnumerationService(enumerationService);

        AdnocDesignationData target = new AdnocDesignationData();

        Mockito.when(designation.getCode()).thenReturn("MANAGER");
        Mockito.when(typeService.getEnumerationValue(designation)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Manager");


        populator.populate(designation, target);


        assertEquals("MANAGER", target.getCode());
        assertEquals("Manager", target.getName());
    }

    @Test
    void testPopulateWithNullUsesDefault() {

        AdnocDesignationDataPopulator populator = new AdnocDesignationDataPopulator();
        populator.setTypeService(typeService);
        populator.setEnumerationService(enumerationService);

        AdnocDesignationData target = new AdnocDesignationData();

        Designation defaultDesignation = Mockito.mock(Designation.class);

        Mockito.when(enumerationService.getEnumerationValue(Designation.class, "Manager")).thenReturn(defaultDesignation);
        Mockito.when(defaultDesignation.getCode()).thenReturn("DEFAULT");
        Mockito.when(typeService.getEnumerationValue(defaultDesignation)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Default Manager");
        populator.populate(null, target);

        assertEquals("DEFAULT", target.getCode());
        assertEquals("Default Manager", target.getName());
    }
}
