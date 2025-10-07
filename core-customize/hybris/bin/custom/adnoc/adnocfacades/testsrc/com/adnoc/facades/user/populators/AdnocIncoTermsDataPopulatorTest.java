package com.adnoc.facades.user.populators;

import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.service.enums.IncoTerms;
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
public class AdnocIncoTermsDataPopulatorTest {

    @Mock
    private TypeService typeService;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private IncoTerms incoTerms;

    @Mock
    private EnumerationValueModel enumValueModel;

    @Test
    public void testPopulateWithValidIncoTerms() {
        // Create and configure the populator directly inside the test method
        AdnocIncoTermsDataPopulator populator = new AdnocIncoTermsDataPopulator();
        populator.setTypeService(typeService);
        populator.setEnumerationService(enumerationService);

        IncoTermsData target = new IncoTermsData();

        Mockito.when(incoTerms.getCode()).thenReturn("PICKUP");
        Mockito.when(typeService.getEnumerationValue(incoTerms)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Pick Up Store");

        populator.populate(incoTerms, target);

        assertEquals("PICKUP", target.getCode());
        assertEquals("Pick Up Store", target.getName());
    }
}
