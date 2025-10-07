package com.adnoc.service.event;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdnocReQuoteBuyerSubmissionEventListenerTest
{
    @InjectMocks
    private AdnocReQuoteBuyerSubmissionEventListener adnocReQuoteBuyerSubmissionEventListener;
    @Mock
    private ModelService modelService;
    @Mock
    private BusinessProcessService businessProcessService;
    @Mock
    private AdnocReQuoteBuyerSubmissionEvent adnocReQuoteBuyerSubmissionEvent;
    @Mock
    private QuoteModel quoteModel;

    @Test
    public void testOnEvent()
    {
        Mockito.when(adnocReQuoteBuyerSubmissionEvent.getQuote()).thenReturn(quoteModel);
        Mockito.when(quoteModel.getCode()).thenReturn("TEST_Quote_00002");
        final QuoteProcessModel quoteProcessModel = Mockito.mock(QuoteProcessModel.class);
        Mockito.when(businessProcessService.createProcess(anyString(), anyString())).thenReturn(quoteProcessModel);
        Mockito.when(quoteProcessModel.getCode()).thenReturn("TEST_QUOTE_PROCESS_CODE");
        adnocReQuoteBuyerSubmissionEventListener.onEvent(adnocReQuoteBuyerSubmissionEvent);
        verify(businessProcessService).createProcess(
                argThat(name -> name.startsWith("quoteBuyerProcess-")),
                eq("adnocRequoteBuyerSubmissionProcess")
        );
        verify(businessProcessService).startProcess(any(QuoteProcessModel.class));
        verify(modelService).save(any(QuoteProcessModel.class));
    }
}
