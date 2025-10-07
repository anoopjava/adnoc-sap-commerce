package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.PrimaryProductData;
import com.adnoc.service.enums.PrimaryProduct;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocPrimaryProductDataPopulatorTest {

    @Mock
    private TypeService typeService;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private PrimaryProduct primaryProduct;

    @Mock
    private PrimaryProduct defaultPrimaryProduct;

    @Mock
    private EnumerationValueModel enumValueModel;

    @InjectMocks
    private AdnocPrimaryProductDataPopulator populator;

    @Test
    public void testPopulateWithValidPrimaryProduct() {

        Mockito.when(primaryProduct.getCode()).thenReturn("FUEL");
        Mockito.when(typeService.getEnumerationValue(primaryProduct)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Fuel");

        PrimaryProductData target = new PrimaryProductData();

        populator.populate(primaryProduct, target);

        assertEquals("FUEL", target.getCode());
        assertEquals("Fuel", target.getName());
    }

    @Test
    public void testPopulateWithNullPrimaryProduct() {

        Mockito.when(enumerationService.getEnumerationValue(PrimaryProduct.class, "FUEL"))
                .thenReturn(defaultPrimaryProduct);
        Mockito.when(defaultPrimaryProduct.getCode()).thenReturn("FUEL");
        Mockito.when(typeService.getEnumerationValue(defaultPrimaryProduct)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Fuel");

        PrimaryProductData target = new PrimaryProductData();

        populator.populate(null, target);

        assertEquals("FUEL", target.getCode());
        assertEquals("Fuel", target.getName());
    }
}
