package com.adnoc.facades.overdueinvoices.impl;

import com.adnoc.facades.company.overdue.invoice.data.AdnocOverduePaymentRequestData;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceRequestData;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceResponseData;
import com.adnoc.facades.payment.AdnocPaymentFacade;
import com.adnoc.facades.payment.card.data.AdnocPaymentCardInitiateCheckoutResponseData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateRequestData;
import com.adnoc.facades.payment.data.AdnocPaymentInitiateResponseData;
import com.adnoc.service.order.payment.transaction.strategies.AdnocPaymentTransactionStrategy;
import com.adnoc.service.overdueinvoices.AdnocOverdueInvoicesService;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocOverdueInvoiceFacadeImplTest
{

    @Test
    public void testGetOverdueInvoices()
    {
        AdnocOverdueInvoiceFacadeImpl facade = new AdnocOverdueInvoiceFacadeImpl();
        AdnocOverdueInvoicesService overdueInvoicesService = mock(AdnocOverdueInvoicesService.class);

        AdnocOverdueInvoiceRequestData requestData = new AdnocOverdueInvoiceRequestData();
        AdnocOverdueInvoiceResponseData responseData = new AdnocOverdueInvoiceResponseData();

        Mockito.when(overdueInvoicesService.getOverdueInvoicesResponse(requestData)).thenReturn(responseData);
        facade.setAdnocOverdueInvoicesService(overdueInvoicesService);

        AdnocOverdueInvoiceResponseData result = facade.getOverdueInvoices(requestData);

        assertNotNull(result);
        assertEquals(responseData, result);
    }

    @Test
    public void testInitiatePayment()
    {
        AdnocOverdueInvoiceFacadeImpl facade = new AdnocOverdueInvoiceFacadeImpl();
        AdnocPaymentFacade paymentFacade = mock(AdnocPaymentFacade.class);
        AdnocPaymentTransactionStrategy paymentStrategy = mock(AdnocPaymentTransactionStrategy.class);

        AdnocOverduePaymentRequestData request = new AdnocOverduePaymentRequestData();
        request.setPayerId("P123");
        request.setPaymentType("CARD");
        request.setTotalAmount(250.0);

        AdnocPaymentCardInitiateCheckoutResponseData cardResponse = mock(AdnocPaymentCardInitiateCheckoutResponseData.class);
        Mockito.when(cardResponse.getUtrn()).thenReturn("UTRN001");
        Mockito.when(cardResponse.getSuccessIndicator()).thenReturn("SUCCESS");

        Mockito.when(paymentFacade.initiatePayment(any(AdnocPaymentInitiateRequestData.class), eq("overdueinvoice")))
                .thenReturn(cardResponse);

        facade.setAdnocPaymentFacade(paymentFacade);
        facade.setAdnocPaymentTransactionStrategy(paymentStrategy);

        AdnocPaymentInitiateResponseData result = facade.initiatePayment(request);

        assertNotNull(result);
        verify(paymentStrategy).createPaymentTransaction("UTRN001", "SUCCESS", request);
    }


}
