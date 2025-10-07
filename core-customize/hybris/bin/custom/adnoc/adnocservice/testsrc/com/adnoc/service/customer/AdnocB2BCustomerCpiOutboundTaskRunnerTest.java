package com.adnoc.service.customer;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.AdnocOutboundB2BCustomerModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BCustomerCpiOutboundTaskRunnerTest
{

    @InjectMocks
    private AdnocB2BCustomerCpiOutboundTaskRunner adnocB2BCustomerCpiOutboundTaskRunner = new AdnocB2BCustomerCpiOutboundTaskRunner();
    @Mock
    private AdnocSapCpiOutboundConversionService adnocSapCpiOutboundConversionService = Mockito.mock(AdnocSapCpiOutboundConversionService.class);
    @Mock
    private ModelService modelService;
    @Mock
    private AdnocSapCpiOutboundService adnocSapCpiOutboundService;
    @Mock
    private TaskService taskService;
    @Mock
    private TaskModel taskModel;
    @Mock
    private B2BCustomerModel b2BCustomerModel;
    @Mock
    private AdnocOutboundB2BCustomerModel adnocOutboundB2BCustomerModel;

    @Test
    public void testRun()
    {
        Mockito.when(taskModel.getContextItem()).thenReturn(b2BCustomerModel);
        Mockito.when(adnocSapCpiOutboundConversionService.convertToOutboundB2BCustomer(b2BCustomerModel))
                .thenReturn(adnocOutboundB2BCustomerModel);

        final Map<String, Object> responseEntityMap = new HashMap<>();
        responseEntityMap.put("status", "SUCCESS");

        Mockito.doReturn(Observable.just(responseEntityMap))
                .when(adnocSapCpiOutboundService)
                .sendAdnocB2BCustomer(any());

        adnocB2BCustomerCpiOutboundTaskRunner.run(taskService, taskModel);

        modelService.save(b2BCustomerModel);
    }

    @Test
    public void testHandleError()
    {
        Mockito.when(taskModel.getContextItem()).thenReturn(b2BCustomerModel);
        adnocB2BCustomerCpiOutboundTaskRunner.handleError(taskService, taskModel, new RuntimeException("Test Error"));
    }
}
