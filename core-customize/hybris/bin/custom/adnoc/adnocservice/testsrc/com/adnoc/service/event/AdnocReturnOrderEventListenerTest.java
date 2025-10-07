package com.adnoc.service.event;

import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
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
public class AdnocReturnOrderEventListenerTest
{
    @InjectMocks
    private AdnocReturnOrderEventListener adnocReturnOrderEventListener;
    @Mock
    private BusinessProcessService businessProcessService;
    @Mock
    private ModelService modelService;
    @Mock
    private CreateReturnEvent createReturnEvent;
    @Mock
    private ReturnRequestModel returnRequestModel;
    @Mock
    private ReturnProcessModel returnProcessModel;
    @Mock
    private OrderModel orderModel;
    @Mock
    private BaseStoreModel storeModel;

    @Test
    public void testOnEvent()
    {
        Mockito.when(createReturnEvent.getReturnRequest()).thenReturn(returnRequestModel);
        Mockito.when(returnRequestModel.getCode()).thenReturn("testReturnOrderCode");
        Mockito.when(returnRequestModel.getOrder()).thenReturn(orderModel);
        Mockito.when(orderModel.getStore()).thenReturn(storeModel);
        Mockito.when(storeModel.getUid()).thenReturn("testBaseStore");
        Mockito.when(businessProcessService.createProcess(anyString(), eq("adnocReturnOrderSubmissionProcess")))
                .thenReturn(returnProcessModel);
        Mockito.when(returnProcessModel.getCode()).thenReturn("TestReturnOrderProcess");

        adnocReturnOrderEventListener.onSiteEvent(createReturnEvent);
        verify(businessProcessService).createProcess(
                argThat(name -> name.startsWith("ReturnOrderSubmissionProcess-testReturnOrderCode-testBaseStore")),
                eq("adnocReturnOrderSubmissionProcess")
        );
        verify(returnProcessModel).setReturnRequest(returnRequestModel);
        verify(modelService).save(returnProcessModel);
        verify(businessProcessService).startProcess(returnProcessModel);
    }

}
