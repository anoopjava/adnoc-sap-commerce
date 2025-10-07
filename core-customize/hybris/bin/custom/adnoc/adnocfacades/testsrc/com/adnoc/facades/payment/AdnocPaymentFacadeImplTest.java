package com.adnoc.facades.payment;

import com.adnoc.facades.payment.card.data.AdnocPaymentResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;
import com.adnoc.facades.payment.strategies.AdnocInitiatePaymentStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocPaymentFacadeImplTest
{

    @InjectMocks
    private AdnocPaymentFacadeImpl paymentFacade;

    @Mock
    private CartService cartService;
    @Mock
    private ModelService modelService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private AdnocInitiatePaymentStrategy cardStrategy;

    @Mock
    private CartModel cartModel;
    @Mock
    private CreditCardPaymentInfoModel paymentInfoModel;
    String paymentType;


    @Test
    public void testInitiateCheckoutPayment_Success()
    {
        final AdnocPaymentInitiateRequestData requestData = new AdnocPaymentInitiateRequestData();
        requestData.setPaymentType("CARD");

        final AdnocPaymentInitiateResponseData responseData = new AdnocPaymentInitiateResponseData();
        responseData.setUtrn("UTRN123");

        final Map<CheckoutPaymentType, AdnocInitiatePaymentStrategy> strategyMap = new HashMap<>();
        strategyMap.put(CheckoutPaymentType.CARD, cardStrategy);
        paymentFacade.setInitiatePaymentStrategiesMap(strategyMap);

        Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
        Mockito.when(cartModel.getPaymentInfo()).thenReturn(paymentInfoModel);
        Mockito.when(cardStrategy.initiatePayment(Mockito.any(AdnocPaymentInitiateRequestData.class), Mockito.anyString()))
                .thenReturn(responseData);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getString(anyString())).thenReturn("dummyUrl");

        final AdnocPaymentInitiateResponseData result = paymentFacade.initiateCheckoutPayment(requestData);

        assertNotNull(result);
        assertEquals("UTRN123", result.getUtrn());
        verify(modelService).save(paymentInfoModel);
        verify(modelService).save(cartModel);
    }

    @Test
    public void testPaymentWithUnsupportedPayment()
    {
        final AdnocPaymentInitiateRequestData requestData = new AdnocPaymentInitiateRequestData();
        requestData.setPaymentType("INVALID");

        paymentFacade.setInitiatePaymentStrategiesMap(new HashMap<>());
        paymentFacade.initiatePayment(requestData, "checkout");
    }


    @Test
    public void testRetrieveCheckoutPaymentNoSessionCart()
    {
        Mockito.when(cartService.hasSessionCart()).thenReturn(false);

        final AdnocPaymentResponseData result = paymentFacade.retrieveCheckoutPayment("DUMMY");
        assertNull(result);
    }
}
