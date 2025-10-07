package com.adnoc.facades.order.converter.populator;
import com.adnoc.service.category.AdnocCategoryService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class AdnocOrderHistoryPopulatorTest {

    @InjectMocks
    private AdnocOrderHistoryPopulator populator;

    @Mock
    private AdnocCategoryService adnocCategoryService;

    @Mock
    private Converter<CategoryModel, CategoryData> categoryConverter;

    @Mock
    private OrderModel orderModel;

    @Mock
    private OrderHistoryData orderHistoryData;

    @Mock
    private AbstractOrderEntryModel orderEntry;

    @Mock
    private ProductModel product;

    @Mock
    private CategoryModel categoryModel;

    @Mock
    private CategoryData categoryData;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Test
    public void testPopulate_withCategory() {
        // Arrange
        Mockito.when(orderModel.getEntries()).thenReturn(Collections.singletonList(orderEntry));
        Mockito.when(orderEntry.getProduct()).thenReturn(product);
        Mockito.when(product.getDivision()).thenReturn("division1");
        Mockito.when(adnocCategoryService.getCategoryForDivision("division1")).thenReturn(categoryModel);
        Mockito.when(categoryConverter.convert(categoryModel)).thenReturn(categoryData);
        populator.setPriceDataFactory(priceDataFactory);

        // Act
        populator.populate(orderModel, orderHistoryData);

        // Assert
        verify(adnocCategoryService).getCategoryForDivision("division1");
        verify(categoryConverter).convert(categoryModel);
        verify(orderHistoryData).setCategory(categoryData);
    }

    @Test
    public void testPopulate_noEntries() {
        // Arrange
        Mockito.when(orderModel.getEntries()).thenReturn(Collections.emptyList());

        // Act
        populator.setPriceDataFactory(priceDataFactory);
        populator.populate(orderModel, orderHistoryData);

        // Assert
        verify(orderHistoryData, never()).setCategory(any());
        verify(adnocCategoryService, never()).getCategoryForDivision(anyString());
    }

    @Test
    public void testPopulate_nullProduct() {
        // Arrange
        Mockito.when(orderModel.getEntries()).thenReturn(Collections.singletonList(orderEntry));
        Mockito.when(orderEntry.getProduct()).thenReturn(null);

        // Act
        populator.setPriceDataFactory(priceDataFactory);
        populator.populate(orderModel, orderHistoryData);

        // Assert
        verify(orderHistoryData, never()).setCategory(any());
        verify(adnocCategoryService, never()).getCategoryForDivision(anyString());
    }

    @Test
    public void testPopulate_nullCategoryModel() {
        // Arrange
        Mockito.when(orderModel.getEntries()).thenReturn(Collections.singletonList(orderEntry));
        Mockito.when(orderEntry.getProduct()).thenReturn(product);
        Mockito.when(product.getDivision()).thenReturn("division2");
        Mockito.when(adnocCategoryService.getCategoryForDivision("division2")).thenReturn(null);
        populator.setPriceDataFactory(priceDataFactory);

        // Act
        populator.populate(orderModel, orderHistoryData);

        // Assert
        verify(adnocCategoryService).getCategoryForDivision("division2");
        verify(categoryConverter, never()).convert(any());
        verify(orderHistoryData, never()).setCategory(any());
    }
}
