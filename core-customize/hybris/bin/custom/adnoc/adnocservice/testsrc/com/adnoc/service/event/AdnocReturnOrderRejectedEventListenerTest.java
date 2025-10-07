package com.adnoc.service.event;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdnocReturnOrderRejectedEventListenerTest
{
    @InjectMocks
    private AdnocReturnOrderRejectedEventListener adnocReturnOrderRejectedEventListener;
    @Mock
    private BusinessProcessService businessProcessService;
    @Mock
    private ModelService modelService;
    @Mock
    private AdnocReturnOrderRejectedEvent adnocReturnOrderRejectedEvent;
    @Mock
    private ReturnRequestModel returnRequestModel;
    @Mock
    private ReturnProcessModel returnProcessModel;

    @Test
    public void testOnEvent()
    {
        Mockito.when(adnocReturnOrderRejectedEvent.getReturnRequest()).thenReturn(returnRequestModel);
        Mockito.when(returnRequestModel.getCode()).thenReturn("returnOrderRejectedCode");
        Mockito.when(returnRequestModel.getOrder()).thenReturn(Mockito.mock(OrderModel.class));
        Mockito.when(returnRequestModel.getOrder().getStore()).thenReturn(Mockito.mock(BaseStoreModel.class));
        Mockito.when(returnRequestModel.getOrder().getStore().getUid()).thenReturn("testBaseStore");

        Mockito.when(businessProcessService.createProcess(anyString(), eq("adnocReturnOrderRejectedProcess")))
                .thenReturn(returnProcessModel);
        Mockito.when(returnProcessModel.getCode()).thenReturn("testReturnOrderRejectedProcess");
        adnocReturnOrderRejectedEventListener.onSiteEvent(adnocReturnOrderRejectedEvent);
        verify(returnProcessModel).setReturnRequest(returnRequestModel);
        verify(modelService).save(returnProcessModel);
        verify(businessProcessService).startProcess(returnProcessModel);
    }
}
