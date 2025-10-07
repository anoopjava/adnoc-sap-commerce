package com.adnoc.service.event;

import de.hybris.platform.commerceservices.event.QuoteBuyerOrderPlacedEvent;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdnocQuoteBuyerOrderPlacedEventListenerTest
{
    @InjectMocks
    private AdnocQuoteBuyerOrderPlacedEventListener adnocQuoteBuyerOrderPlacedEventListener;

    @Mock
    private ModelService modelService;

    @Mock
    private CommerceQuoteService commerceQuoteService;

    @Mock
    private BusinessProcessService businessProcessService;

    @Mock
    private QuoteBuyerOrderPlacedEvent quoteBuyerOrderPlacedEvent;

    @Mock
    private QuoteModel quoteModel;
    @Mock
    private OrderModel orderModel = Mockito.mock(OrderModel.class);
    @Mock
    private BaseStoreModel baseStoreModel;

    @Test
    public void testOnEvent()
    {
        Mockito.when(quoteBuyerOrderPlacedEvent.getOrder()).thenReturn(orderModel);
        Mockito.when(quoteBuyerOrderPlacedEvent.getQuote()).thenReturn(quoteModel);

        Mockito.when(commerceQuoteService.createQuoteSnapshotWithState(quoteModel, QuoteState.BUYER_ORDERED))
                .thenReturn(quoteModel);
        Mockito.when(quoteBuyerOrderPlacedEvent.getQuote()).thenReturn(quoteModel);
        Mockito.when(quoteModel.getCode()).thenReturn("TEST_Quote_00001");
        Mockito.when(quoteModel.getStore()).thenReturn(baseStoreModel);
        Mockito.when(baseStoreModel.getUid()).thenReturn("TEST_STORE_ADNOC");

        final QuoteProcessModel quoteProcessModel = Mockito.mock(QuoteProcessModel.class);
        Mockito.when(businessProcessService.createProcess(anyString(), anyString())).thenReturn(quoteProcessModel);
        Mockito.when(quoteProcessModel.getCode()).thenReturn("TEST_PROCESS_CODE");

        adnocQuoteBuyerOrderPlacedEventListener.onEvent(quoteBuyerOrderPlacedEvent);

        verify(businessProcessService).createProcess(
                argThat(name -> name.startsWith("QuoteBuyerOrderPlacedProcess-TEST_Quote_00001-TEST_STORE_ADNOC")),
                eq("adnocQuoteBuyerOrderPlacedEmailProcess")
        );

        verify(businessProcessService).startProcess(any(QuoteProcessModel.class));

        verify(modelService).save(any(QuoteProcessModel.class));
    }
}
