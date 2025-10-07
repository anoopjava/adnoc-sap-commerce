package com.adnoc.facades.order.converter.populator;

import com.adnoc.facades.order.converter.populator.AdnocCommerceCartParameterBasicPopulator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@UnitTest

@RunWith(MockitoJUnitRunner.class)
public class AdnocCommerceCartParameterBasicPopulatorTest {

    @Mock
    private AddToCartParams addToCartParams;

    @Mock
    private CommerceCartParameter commerceCartParameter;

    @Mock
    private ProductModel productModel;
    @Mock

    private ProductService productService;
    @Mock
    private CartService cartService;
    @Mock
    private PointOfServiceService pointOfServiceService;

    @Test
    public void testWithProductHavingDivision() {
        // Arrange
        AdnocCommerceCartParameterBasicPopulator populator = new AdnocCommerceCartParameterBasicPopulator();

        Mockito.when(commerceCartParameter.getProduct()).thenReturn(productModel);
        Mockito.when(productModel.getDivision()).thenReturn("FUEL");

        // Act
        populator.setCartService(cartService);
        populator.setProductService(productService);
        populator.setPointOfServiceService(pointOfServiceService);
        populator.populate(addToCartParams, commerceCartParameter);

        // Assert
        verify(commerceCartParameter).setDivision("FUEL");
    }

    @Test
    public void testWithNullProduct() {
        // Arrange
        AdnocCommerceCartParameterBasicPopulator populator = new AdnocCommerceCartParameterBasicPopulator();

        Mockito.when(commerceCartParameter.getProduct()).thenReturn(null);
        populator.setCartService(cartService);
        populator.setProductService(productService);
        populator.setPointOfServiceService(pointOfServiceService);

        // Act
        populator.populate(addToCartParams, commerceCartParameter);

        // Assert
        verify(commerceCartParameter, never()).setDivision(anyString());
    }
}
