package com.adnoc.service.validators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
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
class AdnocPayerDivisionAddToCartValidatorTest
{

    @InjectMocks
    private AdnocPayerDivisionAddToCartValidator adnocPayerDivisionAddToCartValidator= new AdnocPayerDivisionAddToCartValidator();

    @Mock
    private ProductModel product =Mockito.mock(ProductModel.class);
    @Mock
    private CartModel cartModel=Mockito.mock(CartModel.class);

    @Mock
    private CommerceCartParameter commerceCartParameter= Mockito.mock(CommerceCartParameter.class);



    @Test
    void testSupports()
    {
        adnocPayerDivisionAddToCartValidator.supports(commerceCartParameter);
    }

    @Test
    void testValidate() throws CommerceCartModificationException
    {
        Mockito.when(commerceCartParameter.getProduct()).thenReturn(product);
        Mockito.when(commerceCartParameter.getCart()).thenReturn(cartModel);

        adnocPayerDivisionAddToCartValidator.validate(commerceCartParameter);
    }
}