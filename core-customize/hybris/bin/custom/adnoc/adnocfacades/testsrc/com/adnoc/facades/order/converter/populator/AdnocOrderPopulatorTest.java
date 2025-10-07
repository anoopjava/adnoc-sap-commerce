package com.adnoc.facades.order.converter.populator;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;


import com.adnoc.service.enums.DeliveryType;
import com.adnoc.service.enums.ShippingCondition;
import com.adnoc.service.model.CreditLimitPaymentInfoModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class AdnocOrderPopulatorTest {

    @InjectMocks
    private AdnocOrderPopulator populator;

    @Mock
    private AdnocB2BUnitService b2BUnitService;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private OrderModel source;

    @Mock
    private B2BUnitModel b2BUnitModel;

    @Mock
    private DeliveryType deliveryType;

    @Mock
    private ShippingCondition shippingCondition;

    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModel;

    @Mock
    private CreditLimitPaymentInfoModel creditLimitPaymentInfoModel;

    @Mock
    private de.hybris.platform.core.model.user.AddressModel deliveryAddress;

    @Test
    public void testPopulateWithAllFields() {
        // Arrange
        OrderData target = new OrderData();

        Mockito.when(source.getDeliveryAddress()).thenReturn(deliveryAddress);
        Mockito.when(deliveryAddress.getSapCustomerID()).thenReturn("sapId123");

        Mockito.when(b2BUnitService.getUnitForUid("sapId123")).thenReturn(b2BUnitModel);

        Mockito.when(b2BUnitModel.getDeliveryTypes()).thenReturn(java.util.Collections.singleton(deliveryType));
        Mockito.when(deliveryType.getCode()).thenReturn("DEL_CODE");
        Mockito.when(enumerationService.getEnumerationName(deliveryType)).thenReturn("DeliveryTypeName");

        Mockito.when(b2BUnitModel.getShippingCondition()).thenReturn(shippingCondition);
        Mockito.when(shippingCondition.getCode()).thenReturn("SHIP_CODE");
        Mockito.when(enumerationService.getEnumerationName(shippingCondition)).thenReturn("ShippingConditionName");

        Mockito.when(source.getSapOrderCode()).thenReturn("ORDER123");
        Mockito.when(source.getSapErrorMessage()).thenReturn("Some Error");

        Mockito.when(source.getPaymentInfo()).thenReturn(creditCardPaymentInfoModel);
        Mockito.when(creditCardPaymentInfoModel.getNumber()).thenReturn("4111111111111111");
        Mockito.when(creditCardPaymentInfoModel.getAmount()).thenReturn(Double.valueOf(100.0));
        Mockito.when(creditCardPaymentInfoModel.getRrnNumber()).thenReturn("RRN123");

        Mockito.when(source.getCreditLimitPaymentInfo()).thenReturn(creditLimitPaymentInfoModel);
        Mockito.when(creditLimitPaymentInfoModel.getAmount()).thenReturn(Double.valueOf(500.0));

        // Act
        populator.populate(source, target);

        // Assert
        assertNotNull(target.getDeliveryType());
        assertEquals("DEL_CODE", target.getDeliveryType().getCode());
        assertEquals("DeliveryTypeName", target.getDeliveryType().getName());

        assertNotNull(target.getShippingCondition());
        assertEquals("SHIP_CODE", target.getShippingCondition().getCode());
        assertEquals("ShippingConditionName", target.getShippingCondition().getName());

        assertEquals("ORDER123", target.getSapOrderCode());
        assertEquals("Some Error", target.getSapErrorMessage());

        assertNotNull(target.getPaymentInfo());
        CCPaymentInfoData paymentInfoData = target.getPaymentInfo();
        assertEquals("4111111111111111", paymentInfoData.getCardNumber());
        assertEquals("RRN123", paymentInfoData.getRrnNumber());
    }


}
