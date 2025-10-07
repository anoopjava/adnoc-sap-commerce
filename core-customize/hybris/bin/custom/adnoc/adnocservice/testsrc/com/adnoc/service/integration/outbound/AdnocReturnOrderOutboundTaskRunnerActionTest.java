package com.adnoc.service.integration.outbound;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.AdnocReturnRequestOutboundModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdnocReturnOrderOutboundTaskRunnerActionTest {

    private AdnocReturnOrderOutboundTaskRunnerAction action;
    @Mock
    private AdnocReturnRequestOutboundModel adnocReturnRequestOutboundModel;

    @Mock
    private AdnocSapCpiOutboundService outboundService;
    @Mock
    private AdnocSapCpiOutboundConversionService conversionService;
    @Mock
    private ModelService modelService;
    @Mock
    private TaskService taskService;
    @Mock
    private TaskModel taskModel;
    @Mock
    private ReturnRequestModel returnRequestModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        action = new AdnocReturnOrderOutboundTaskRunnerAction();
        action.setAdnocSapCpiOutboundService(outboundService);
        action.setAdnocSapCpiOutboundConversionService(conversionService);
        action.setModelService(modelService);
    }

    @Test
    public void testRun() {
        when(taskModel.getContextItem()).thenReturn(returnRequestModel);
        when(conversionService.convertToOutboundReturnRequest(any())).thenReturn(adnocReturnRequestOutboundModel);

        // Observable simulates SAP call
        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("any", "any");
        Map<String, Object> response = new HashMap<>();
        response.put("root", rootMap);

        Observable<ResponseEntity<Map>> observable = Observable.just(ResponseEntity.ok(response));
        when(outboundService.sendReturnOrder(any())).thenReturn(observable);

        action.run(taskService, taskModel);

        verify(conversionService).convertToOutboundReturnRequest(returnRequestModel);
        verify(outboundService).sendReturnOrder(adnocReturnRequestOutboundModel);
    }
}
