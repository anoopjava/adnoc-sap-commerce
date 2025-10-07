package com.adnoc.service.order.util.strategy.impl.strategy;

import com.adnoc.service.order.strategy.AdnocCommerceAddToCartStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.Random;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocCommerceAddToCartStrategyTest
{
    @Mock
    private CommerceCartParameter commerceCartParameter;
    @Mock
    private CommerceCartModification commerceCartModification;
    @Mock
    private AbstractOrderEntryModel abstractOrderEntryModel;
    @Mock
    private ModelService modelService;
    @InjectMocks
    private AdnocCommerceAddToCartStrategy adnocCommerceAddToCartStrategy;
    @Mock
    private Random random;

    @Test
    public void testAfterAddToCart() throws Exception
    {
        final String division = "21";
        final int randomNumber = 123;
        when(commerceCartModification.getEntry()).thenReturn(abstractOrderEntryModel);
        when(commerceCartParameter.getDivision()).thenReturn(division);
        when(random.nextInt(900)).thenReturn(randomNumber - 100);
        adnocCommerceAddToCartStrategy.setRandom(random);
        final Method method = AdnocCommerceAddToCartStrategy.class.getDeclaredMethod("afterAddToCart", CommerceCartParameter.class, CommerceCartModification.class);
        method.setAccessible(true);
        method.invoke(adnocCommerceAddToCartStrategy, commerceCartParameter, commerceCartModification);
        verify(abstractOrderEntryModel).setDivision(division);
        verify(abstractOrderEntryModel).setEntryCode(randomNumber);
        verify(modelService).save(abstractOrderEntryModel);
    }
}
