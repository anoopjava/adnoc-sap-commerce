package com.adnoc.service.validators;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.b2bunit.dao.AdnocB2BUnitDao;
import com.adnoc.service.config.AdnocConfigService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocB2BCartValidatorTest
{

    @InjectMocks
    private AdnocB2BCartValidator adnocB2BCartValidator=new AdnocB2BCartValidator();

    @Mock
    private CartService cartService= Mockito.mock(CartService.class);
    @Mock
    private AdnocConfigService adnocConfigService= Mockito.mock(AdnocConfigService.class);;
    @Mock
    private AdnocB2BUnitService adnocB2BUnitService= Mockito.mock(AdnocB2BUnitService.class);;
    @Mock
    private CartModel cartModel=Mockito.mock(CartModel.class);
    @Mock
    private Errors errors=Mockito.mock(Errors.class);
    @Mock
    private B2BUnitModel currentPayer=Mockito.mock(B2BUnitModel.class);

    @Test
    void testValidate()
    {
        adnocB2BCartValidator.setAdnocB2BUnitService(adnocB2BUnitService);
        adnocB2BCartValidator.setCartService(cartService);
        adnocB2BCartValidator.setAdnocConfigService(adnocConfigService);

        adnocB2BCartValidator.validate(cartModel,errors);
    }

    @Test
    void testValidateCartAgainstDivisionGrouping()
    {
        adnocB2BCartValidator.setAdnocB2BUnitService(adnocB2BUnitService);
        adnocB2BCartValidator.setCartService(cartService);
        adnocB2BCartValidator.setAdnocConfigService(adnocConfigService);
    }

    @Test
    void testValidateCartAgainstPayerDivision() throws CommerceCartModificationException
    {
        adnocB2BCartValidator.setAdnocB2BUnitService(adnocB2BUnitService);
        adnocB2BCartValidator.setCartService(cartService);
        adnocB2BCartValidator.setAdnocConfigService(adnocConfigService);
        Mockito.when(adnocB2BUnitService.getCurrentB2BUnit()).thenReturn(currentPayer);

        adnocB2BCartValidator.validateCartAgainstPayerDivision("test01");
    }
}