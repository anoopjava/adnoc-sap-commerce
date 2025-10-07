package com.adnoc.facades.strategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.services.impl.DefaultB2BCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.impl.DefaultTypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocCreateCartFromCartStrategyImplTest
{
    @InjectMocks
    private AdnocCreateCartFromCartStrategyImpl adnocCreateCartFromCartStrategy;
    @Mock
    private ModelService modelService;
    @Mock
    private KeyGenerator mockKeyGenerator;
    @Mock
    private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;
    @Mock
    private CartModel originalCart;
    @Mock
    private DefaultB2BCartService b2bCartService;
    @Mock
    private DefaultTypeService mockTypeService;

    @Test
    public void testCreateCartFromCart() throws Exception
    {
        final Method method = AdnocCreateCartFromCartStrategyImpl.class.getDeclaredMethod("getCartEntriesGroupByDivisionAddress", CartModel.class);
        method.setAccessible(true);
        final CartModel cartModel = mock(CartModel.class);
        final AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
        Mockito.when(cartModel.getEntries()).thenReturn(Collections.singletonList(entry));
        Mockito.when(entry.getDivision()).thenReturn("East");
        Mockito.when(entry.getDeliveryAddress()).thenReturn(mock(AddressModel.class));
        Mockito.when(mockKeyGenerator.generate()).thenReturn("0001");
        given(cloneAbstractOrderStrategy.clone(null, null, cartModel, "0001", CartModel.class, CartEntryModel.class))
                .willReturn(new CartModel());
        final AbstractOrderEntryModel clonedEntryModel = Mockito.mock(AbstractOrderEntryModel.class);
        Mockito.when(modelService.clone(entry)).thenReturn(clonedEntryModel);
        final List<CartModel> result2 = adnocCreateCartFromCartStrategy.createCartFromCart(cartModel);
        assertNotNull(result2);
    }

    @Test
    public void testPostProcess() throws Exception
    {
        final CartModel original = mock(CartModel.class);
        final CartModel cloned = mock(CartModel.class);
        testCloneOrderEntry();
        final Method method = AdnocCreateCartFromCartStrategyImpl.class.getDeclaredMethod("postProcess", CartModel.class, CartModel.class);
        method.setAccessible(true);
        method.invoke(adnocCreateCartFromCartStrategy, original, cloned);
        verify(cloned).setEntries(anyList());
        verify(modelService).saveAll(anyList());
        verify(modelService).save(cloned);
    }

    @Test
    public void testCartEntriesGroupByDivisionAddressNotFound() throws NoSuchMethodException
    {
        final Method method = AdnocCreateCartFromCartStrategyImpl.class.getDeclaredMethod("getCartEntriesGroupByDivisionAddress", CartModel.class);
        method.setAccessible(true);

        final List<CartModel> result = adnocCreateCartFromCartStrategy.createCartFromCart(originalCart);
        assertNotNull(result);
    }

    @Test
    public void testCloneOrderEntry() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        b2bCartService.setTypeService(mockTypeService);
        final AdnocCreateCartFromCartStrategyImpl strategy = new AdnocCreateCartFromCartStrategyImpl();
        final AbstractOrderEntryModel abstractOrderEntryModel = Mockito.mock(AbstractOrderEntryModel.class);
        final CartModel cartModel = Mockito.mock(CartModel.class);
        final Integer entryNumber = 30;

        final ModelService modelService1 = Mockito.mock(ModelService.class);
        final AbstractOrderEntryModel clonedEntryModel = Mockito.mock(AbstractOrderEntryModel.class);
        Mockito.when(modelService1.clone(abstractOrderEntryModel)).thenReturn(clonedEntryModel);

        strategy.setModelService(modelService1);
        final Method method = AdnocCreateCartFromCartStrategyImpl.class.getDeclaredMethod("cloneOrderEntry", AbstractOrderEntryModel.class, CartModel.class, Integer.class);
        method.setAccessible(true);

        final AbstractOrderEntryModel result = (AbstractOrderEntryModel) method.invoke(strategy, abstractOrderEntryModel, cartModel, entryNumber);
        assertNotNull(result);
    }
}
