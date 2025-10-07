package com.adnoc.facades.payment.strategies.impl;

import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;


@UnitTest

@RunWith(MockitoJUnitRunner.class)
public class AdnocPaymentBankRegInitiateStrategyTest
{

    @Test
    public void testInitiatePayment_ShouldReturnNonNullResponse() {

        AdnocPaymentBankRegInitiateStrategy strategy = new AdnocPaymentBankRegInitiateStrategy();
        AdnocPaymentInitiateRequestData requestData = new AdnocPaymentInitiateRequestData();
        String paymentFlow="checkout";

        AdnocPaymentInitiateResponseData response = strategy.initiatePayment(requestData, paymentFlow);

        assertNotNull("Expected a non-null response", response);
    }
}
