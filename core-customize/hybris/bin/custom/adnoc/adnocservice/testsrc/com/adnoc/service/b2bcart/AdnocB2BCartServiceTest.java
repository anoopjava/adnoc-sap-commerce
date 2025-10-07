package com.adnoc.service.b2bcart;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.bootstrap.ddl.model.ComposedType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocB2BCartServiceTest
{
    @InjectMocks
    private AdnocB2BCartService adnocB2BCartService=new AdnocB2BCartService();

    @Mock
    private AbstractOrderModel order= Mockito.mock(AbstractOrderModel.class);
    @Mock
    private TypeService typeService=Mockito.mock(TypeService.class);
    @Mock
    private KeyGenerator keyGenerator=Mockito.mock(KeyGenerator.class);
    @Mock
    private CloneAbstractOrderStrategy cloneAbstractOrderStrategy=Mockito.mock(CloneAbstractOrderStrategy.class);

    @Test
    void testCreateCartFromAbstractOrder()
    {
        adnocB2BCartService.setTypeService(typeService);
        adnocB2BCartService.setKeyGenerator(keyGenerator);
        adnocB2BCartService.setCloneAbstractOrderStrategy(cloneAbstractOrderStrategy);
        Mockito.when(keyGenerator.generate()).thenReturn("000000");
        CartModel cartModel=new CartModel();
        List<CartEntryModel> entryModels=new ArrayList<>();
        entryModels.add(new CartEntryModel());
        Mockito.when(adnocB2BCartService.clone(Mockito.any(ComposedTypeModel.class),Mockito.any(ComposedTypeModel.class),Mockito.any(AbstractOrderModel.class),Mockito.anyString())).thenReturn(cartModel);
        CartModel cart=adnocB2BCartService.createCartFromAbstractOrder(order);
        Assertions.assertThat(cart).isNotNull();
    }
}