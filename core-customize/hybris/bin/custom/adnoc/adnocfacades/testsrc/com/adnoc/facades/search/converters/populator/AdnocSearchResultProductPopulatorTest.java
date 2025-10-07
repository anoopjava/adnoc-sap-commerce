package com.adnoc.facades.search.converters.populator;

import com.adnoc.facade.product.data.UnitData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocSearchResultProductPopulatorTest {

    private final AdnocSearchResultProductPopulator populator = new AdnocSearchResultProductPopulator();

    @Test
    public void testPopulate_withValidValues() {
        // Arrange
        SearchResultValueData source = new SearchResultValueData();
        ProductData target = new ProductData();

        Map<String, Object> values = new HashMap<>();
        values.put("unit", "Litre");
        values.put("minOrderQuantity", 5);
        values.put("maxOrderQuantity", 100);
        source.setValues(values);

        // Act
        populator.populate(source, target);

        // Assert
        assertNotNull(target.getUnit());
        assertEquals("Litre", target.getUnit().getCode());
        assertEquals("Litre", target.getUnit().getName());
        assertEquals(Integer.valueOf(5), target.getMinOrderQuantity());
        assertEquals(Integer.valueOf(100), target.getMaxOrderQuantity());
    }

}
