package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.NationalityData;
import com.adnoc.service.enums.Nationality;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocNationalityDataPopulatorTest {

    @Mock
    private TypeService typeService;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private Nationality nationality;

    @Mock
    private EnumerationValueModel enumValueModel;

    private AdnocNationalityDataPopulator populator;

    @Test
    public void testPopulateWithValidNationality() {
        populator = new AdnocNationalityDataPopulator();
        populator.setTypeService(typeService);
        populator.setEnumerationService(enumerationService);

        NationalityData target = new NationalityData();

        Mockito.when(nationality.getCode()).thenReturn("JORDANIAN");
        Mockito.when(typeService.getEnumerationValue(nationality)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Jordanian");

        populator.populate(nationality, target);

        assertEquals("JORDANIAN", target.getCode());
        assertEquals("Jordanian", target.getName());
    }
}
