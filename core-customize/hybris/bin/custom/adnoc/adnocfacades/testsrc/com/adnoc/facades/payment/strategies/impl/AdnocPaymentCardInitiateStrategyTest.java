package com.adnoc.facades.payment.strategies.impl;

import com.adnoc.facades.payment.card.data.*;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;
import com.adnoc.service.constants.AdnocserviceConstants;
import com.adnoc.service.payment.AdnocPaymentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocPaymentCardInitiateStrategyTest
{

    @InjectMocks
    private AdnocPaymentCardInitiateStrategy strategy;

    @Mock
    private AdnocPaymentService adnocPaymentService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Test
    public void testInitiatePayment() {
        AdnocPaymentInitiateRequestData requestData = new AdnocPaymentInitiateRequestData();
        requestData.setReturnUrl("http://return.url");
        requestData.setCancelUrl("http://cancel.url");
        requestData.setCurrency("AED");
        requestData.setDescription("Test Payment");
        requestData.setPaymentAmount(150.75);
        requestData.setPayerId("12345");

        AdnocPaymentCardInitiateCheckoutResponseData mockResponse = new AdnocPaymentCardInitiateCheckoutResponseData();
        mockResponse.setUtrn("HQ1-UTRN-123");

        when(adnocPaymentService.initiatePayment(any(AdnocPaymentCardInitiateCheckoutRequestData.class)))
                .thenReturn(mockResponse);


        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(AdnocserviceConstants.INITIATECHECKOUT)).thenReturn("INIT_CHECKOUT");
        when(configuration.getString(AdnocserviceConstants.OPERATION)).thenReturn("PURCHASE");
        when(configuration.getString(AdnocserviceConstants.MERCHANT_NAME)).thenReturn("Adnoc Merchant");
        when(configuration.getString(AdnocserviceConstants.MERCHANT_URL)).thenReturn("http://merchant.url");
        when(configuration.getString(AdnocserviceConstants.BILLING_ADDRESS)).thenReturn("true");
        when(configuration.getString("adnoc.payment.hqsite.code")).thenReturn("HQ1");
        when(configuration.getString("adnoc.payment.shift.code")).thenReturn("S1");
        when(configuration.getString("adnoc.payment.lob.code")).thenReturn("LOB1");

        // Act
        String paymentFlow = "checkout";
        AdnocPaymentInitiateResponseData response = strategy.initiatePayment(requestData, paymentFlow);

        // Assert
        assertNotNull(response);
        assertNotNull("UTRN code should be set", response.getUtrn());
        assertTrue("UTRN should contain HQ code", response.getUtrn().contains("HQ1"));
        verify(adnocPaymentService).initiatePayment(any(AdnocPaymentCardInitiateCheckoutRequestData.class));
    }
}