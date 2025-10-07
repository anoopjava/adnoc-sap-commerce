package com.adnoc.service.integration.outbound;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.AdnocOutboundOverduePaymentTransactionModel;
import com.adnoc.service.model.AdnocOverduePaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdnocOverduePaymentOutboundTaskRunnerActionTest {

    @Test
    public void testRun_Success() {
        // Mocks
        AdnocSapCpiOutboundService outboundService = mock(AdnocSapCpiOutboundService.class);
        AdnocSapCpiOutboundConversionService conversionService = mock(AdnocSapCpiOutboundConversionService.class);
        ModelService modelService = mock(ModelService.class);
        TaskService taskService = mock(TaskService.class);
        TaskModel taskModel = mock(TaskModel.class);
        AdnocOverduePaymentTransactionModel overdueModel = mock(AdnocOverduePaymentTransactionModel.class);
        AdnocOutboundOverduePaymentTransactionModel adnocOutboundOverduePaymentTransactionModel = mock(AdnocOutboundOverduePaymentTransactionModel.class);

        // Set up
        AdnocOverduePaymentOutboundTaskRunnerAction action = new AdnocOverduePaymentOutboundTaskRunnerAction();
        action.setAdnocSapCpiOutboundService(outboundService);
        action.setAdnocSapCpiOutboundConversionService(conversionService);
        action.setModelService(modelService);

        when(taskModel.getContextItem()).thenReturn(overdueModel);
        when(conversionService.convertToAdnocOutboundOverduePaymentTransaction(any()))
                .thenReturn(adnocOutboundOverduePaymentTransactionModel);

        Observable<ResponseEntity<Map>> observable = Observable.just(ResponseEntity.ok(Collections.singletonMap("status", "success")));
        when(outboundService.sendAdnocOutboundOverduePaymentTransaction(any())).thenReturn(observable);

        // Run
        action.run(taskService, taskModel);

        // Verify
        verify(conversionService).convertToAdnocOutboundOverduePaymentTransaction(overdueModel);
        verify(outboundService).sendAdnocOutboundOverduePaymentTransaction(adnocOutboundOverduePaymentTransactionModel);
    }
}
