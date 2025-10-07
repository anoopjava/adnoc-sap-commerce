package com.adnoc.facades;


import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.service.address.AdnocAddressService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocCartFacadeImplTest
{
    @InjectMocks
    private AdnocCartFacadeImpl adnocCartFacadeImpl;
    @Mock
    private AbstractPopulatingConverter<CommerceCartModification, CartModificationData> cartModificationConverter;
    @Mock
    private CommerceCartService commerceCartService;
    @Mock
    private Converter<AddToCartParams, CommerceCartParameter> commerceCartParameterConverter;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private CartService cartService;
    @Mock
    private OrderEntryData orderEntryData;

    @Mock
    private AdnocAddressService adnocAddressService;
    @Mock
    private ModelService modelService;
    @Mock
    private DeliveryService deliveryService;
    @Mock
    private CalculationService calculationService;

    @Test
    public void testAddToCart() throws CommerceCartModificationException
    {
        final AddToCartParams addToCartParams = new AddToCartParams();
        final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        Mockito.when(commerceCartParameterConverter.convert(addToCartParams)).thenReturn(commerceCartParameter);

        final CommerceCartModification commerceCartModification = new CommerceCartModification();
        Mockito.when(commerceCartService.addToCart(commerceCartParameter)).thenReturn(commerceCartModification);

        final CartModificationData cartModificationData = new CartModificationData();
        Mockito.when(cartModificationConverter.convert(commerceCartModification)).thenReturn(cartModificationData);

        final Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);

        final CartModificationData cartModificationDataRes = adnocCartFacadeImpl.addToCart(addToCartParams);
        Assertions.assertThat(cartModificationDataRes).isEqualTo(cartModificationData);
        Assertions.assertThat(commerceCartParameter.isCreateNewEntry()).isTrue();
    }

    @Test
    public void testUpdateOrderEntryList() throws Exception
    {
        final CartModel sessionCartModel = Mockito.mock(CartModel.class);
        Mockito.when(cartService.getSessionCart()).thenReturn(sessionCartModel);

        final AbstractOrderEntryModel entryModel1 = Mockito.mock(AbstractOrderEntryModel.class);
        Mockito.when(entryModel1.getEntryNumber()).thenReturn(0);

        final ProductModel productModel = Mockito.mock(ProductModel.class);
        Mockito.when(entryModel1.getProduct()).thenReturn(productModel);

        final AddressModel addressModel1 = Mockito.mock(AddressModel.class);
        Mockito.when(entryModel1.getDeliveryAddress()).thenReturn(addressModel1);

        final Date namedDeliveryDate = new Date();
        Mockito.when(entryModel1.getNamedDeliveryDate()).thenReturn(namedDeliveryDate);

        final List<AbstractOrderEntryModel> listOfOrders = Collections.singletonList(entryModel1);
        Mockito.when(sessionCartModel.getEntries()).thenReturn(listOfOrders);

        Mockito.when(orderEntryData.getEntryNumber()).thenReturn(0);

        final IncoTermsData incoTermsData = Mockito.mock(IncoTermsData.class);
        Mockito.when(orderEntryData.getIncoTerms()).thenReturn(incoTermsData);
        Mockito.when(incoTermsData.getCode()).thenReturn("testIncoTermCode");

        final AddressData addressData = Mockito.mock(AddressData.class);
        Mockito.when(addressData.getId()).thenReturn("testPk");
        Mockito.when(orderEntryData.getDeliveryAddress()).thenReturn(addressData);

        final AddressModel addressModelFromService = Mockito.mock(AddressModel.class);
        Mockito.when(adnocAddressService.getAddress("testPk")).thenReturn(addressModelFromService);

        Mockito.when(orderEntryData.getNamedDeliveryDate()).thenReturn(namedDeliveryDate);

        final List<OrderEntryData> entries = Collections.singletonList(orderEntryData);

        final Date dates = new Date();
        Mockito.when(entryModel1.getNamedDeliveryDate()).thenReturn(dates);
        Mockito.when(orderEntryData.getNamedDeliveryDate()).thenReturn(dates);

        adnocCartFacadeImpl.setDeliveryService(deliveryService);

        final DeliveryModeModel deliveryModeModel = Mockito.mock(DeliveryModeModel.class);
        Mockito.when(deliveryService.getDeliveryModeForCode(Mockito.anyString())).thenReturn(deliveryModeModel);

        Mockito.doNothing().when(modelService).save(Mockito.any(CartModel.class));
        Mockito.doNothing().when(calculationService).recalculate(Mockito.any(CartModel.class));

        final Converter<CartModel, CartData> miniCartConverter = Mockito.mock(Converter.class);
        adnocCartFacadeImpl.setMiniCartConverter(miniCartConverter);

        final CartData cartData = adnocCartFacadeImpl.updateOrderEntryList(entries);
        assertNull(cartData);
        verify(cartService).getSessionCart();
    }

    @Test
    public void testValidateOrderEntryToUpdate_NoIncoTermFound() throws Exception
    {
        AbstractOrderEntryModel abstractOrderEntryModel1 = Mockito.mock(AbstractOrderEntryModel.class);
        final Method method = AdnocCartFacadeImpl.class.getDeclaredMethod("validateOrderEntryToUpdate", OrderEntryData.class, AbstractOrderEntryModel.class);
        method.setAccessible(true);

        try
        {
            method.invoke(adnocCartFacadeImpl, orderEntryData, abstractOrderEntryModel1);
            fail("Expected CommerceCartModificationException to be thrown");
        }
        catch (InvocationTargetException e)
        {
            Throwable cause = e.getCause();
            assertTrue("Expected exception to be CommerceCartModificationException", cause instanceof CommerceCartModificationException);
            assertEquals("No IncoTerms provided for EntryNumber=0.", cause.getMessage());
        }
    }

}
