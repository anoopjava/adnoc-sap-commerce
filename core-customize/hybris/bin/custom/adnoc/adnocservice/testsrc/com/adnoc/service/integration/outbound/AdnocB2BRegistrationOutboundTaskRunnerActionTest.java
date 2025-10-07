package com.adnoc.service.integration.outbound;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.AdnocRegistrationModel;
import com.adnoc.service.model.AdnocSoldToB2BRegistrationModel;
import com.adnoc.service.model.AdnocSoldToOutboundB2BRegistrationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rx.Observable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdnocB2BRegistrationOutboundTaskRunnerActionTest {

    @InjectMocks
    private AdnocB2BRegistrationOutboundTaskRunnerAction action;

    @Mock
    private AdnocSapCpiOutboundService outboundService;
    @Mock
    private AdnocSapCpiOutboundConversionService conversionService;
    @Mock
    private ModelService modelService;

    @Test
    void testRun_SoldTo_Success() {
        // Arrange: Use @Mock for dependencies and input
        TaskService taskService = mock(TaskService.class);
        TaskModel taskModel = mock(TaskModel.class);
        AdnocSoldToB2BRegistrationModel regModel = mock(AdnocSoldToB2BRegistrationModel.class);
        AdnocSoldToOutboundB2BRegistrationModel outboundModel = mock(AdnocSoldToOutboundB2BRegistrationModel.class);

        when(taskModel.getContextItem()).thenReturn(regModel);
        when(regModel.getEmail()).thenReturn("abc@xyz.com");
        when(conversionService.convertToSoldToOutboundModel(regModel)).thenReturn(outboundModel);

        Observable<ResponseEntity<Map>> observable = Observable.create(subscriber -> {
            subscriber.onNext(ResponseEntity.ok(Collections.singletonMap("status", "OK")));
            subscriber.onCompleted();
        });
        when(outboundService.sendAdnocB2BRegistration(outboundModel)).thenReturn(observable);

        // Act
        action.run(taskService, taskModel);

        // Assert
        verify(conversionService).convertToSoldToOutboundModel(regModel);
        verify(outboundService).sendAdnocB2BRegistration(outboundModel);
        verify(modelService, atLeast(0)).save(any(AdnocRegistrationModel.class));
    }
}
