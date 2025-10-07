package com.adnoc.service.validators;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocDivisionGroupAddToCartValidatorTest
{
    @InjectMocks
    private AdnocDivisionGroupAddToCartValidator adnocDivisionGroupAddToCartValidator= Mockito.mock(AdnocDivisionGroupAddToCartValidator.class);
    @Mock
    private ProductModel product =Mockito.mock(ProductModel.class);
    @Mock
    private CartModel cartModel=Mockito.mock(CartModel.class);
    @Mock
    private CommerceCartParameter commerceCartParameter=Mockito.mock(CommerceCartParameter.class);
    @Mock
    private AdnocB2BUnitService adnocB2BUnitService=Mockito.mock(AdnocB2BUnitService.class);

    @Test
    void testSupports()
    {
        adnocDivisionGroupAddToCartValidator.supports(commerceCartParameter);
    }

    @Test
    void testValidate() throws CommerceCartModificationException
    {
        adnocDivisionGroupAddToCartValidator.setAdnocB2BUnitService(adnocB2BUnitService);
        Mockito.when(commerceCartParameter.getProduct()).thenReturn(product);
        Mockito.when(commerceCartParameter.getCart()).thenReturn(cartModel);
        Mockito.when(product.getDivision()).thenReturn("01");

        adnocDivisionGroupAddToCartValidator.validate(commerceCartParameter);
    }
}