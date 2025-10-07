package com.adnoc.facades.product.converters.populator;

import com.adnoc.facade.product.data.UnitData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocProductPopulatorTest {

    @InjectMocks
    private AdnocProductPopulator populator;

    @Mock
    private Converter<UnitModel, UnitData> adnocUnitConverter;

    @Mock
    private ProductModel productModel;

    @Mock
    private UnitModel unitModel;

    @Mock
    private UnitData unitData;

    @Test
    public void testPopulateWithUnit() {
        ProductData productData = new ProductData();

        Mockito.when(productModel.getDivision()).thenReturn("Retail");
        Mockito.when(productModel.getUnit()).thenReturn(unitModel);

        Mockito.when(productModel.getMinOrderQuantity()).thenReturn(1);
        Mockito.when(productModel.getMaxOrderQuantity()).thenReturn(10);
        Mockito.when(adnocUnitConverter.convert(unitModel)).thenReturn(unitData);

        populator.populate(productModel, productData);

        assertEquals("Retail", productData.getDivision());
        assertEquals(unitData, productData.getUnit());
        assertEquals(Integer.valueOf(1), productData.getMinOrderQuantity());
        assertEquals(Integer.valueOf(10), productData.getMaxOrderQuantity());
    }

    @Test
    public void testPopulateWithoutUnit() {
        ProductData productData = new ProductData();

        Mockito.when(productModel.getDivision()).thenReturn("Commercial");
        Mockito.when(productModel.getUnit()).thenReturn(null);
        Mockito.when(productModel.getMinOrderQuantity()).thenReturn(2);
        Mockito.when(productModel.getMaxOrderQuantity()).thenReturn(20);

        populator.populate(productModel, productData);

        assertEquals("Commercial", productData.getDivision());
        assertNull(productData.getUnit());
        assertEquals(Integer.valueOf(2), productData.getMinOrderQuantity());
        assertEquals(Integer.valueOf(20), productData.getMaxOrderQuantity());


        verify(adnocUnitConverter, never()).convert(any(UnitModel.class));
    }
}
