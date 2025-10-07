package com.adnoc.facades.cart.populator;

import com.adnoc.service.model.CreditLimitPaymentInfoModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdnocCartPopulatorTest {


    @Mock
    private CartModel cartModel;

    @Mock
    private CartData cartData;

    @Mock
    private CreditLimitPaymentInfoModel paymentInfo;

    @Mock
    private CurrencyModel currencyModel;

    @Mock
    private PriceDataFactory priceDataFactory;

    @InjectMocks
    private AdnocCartPopulator<CartData> adnocCartPopulator = new AdnocCartPopulator<>();

    @Test
    public void testPopulateWithCreditLimitPaymentInfo() {

        Mockito.when(cartModel.getCreditLimitPaymentInfo()).thenReturn(paymentInfo);
        Mockito.when(cartModel.getCurrency()).thenReturn(currencyModel);

        adnocCartPopulator.setPriceDataFactory(priceDataFactory);

        adnocCartPopulator.populate(cartModel, cartData);

        verify(cartData).setCreditLimitUsed(true);
        verify(cartModel).getCreditLimitPaymentInfo();
    }

    @Test
    public void testPopulateWithoutCreditLimitPaymentInfo() {
        Mockito.when(cartModel.getCreditLimitPaymentInfo()).thenReturn(null);
        Mockito.when(cartModel.getCurrency()).thenReturn(currencyModel);

        adnocCartPopulator.setPriceDataFactory(priceDataFactory);

        adnocCartPopulator.populate(cartModel, cartData);

        verify(cartData, never()).setCreditLimitUsed(true);
        verify(cartModel).getCreditLimitPaymentInfo();
    }
}
