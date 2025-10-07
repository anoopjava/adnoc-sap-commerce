package com.adnoc.service.integration.hooks;

import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AdnocCpiInboundPriceRowTaskRunnerActionTest {

    @Test
    void shouldRemovePriceRowWhenDeletionIndicatorIsTrue() {
        ModelService modelService = mock(ModelService.class);
        TaskService taskService = mock(TaskService.class);
        TaskModel taskModel = mock(TaskModel.class);
        PriceRowModel priceRowModel = mock(PriceRowModel.class);
        when(taskModel.getContextItem()).thenReturn(priceRowModel);
        when(priceRowModel.getDeletionIndicator()).thenReturn(Boolean.TRUE);

        AdnocCpiInboundPriceRowTaskRunnerAction taskRunner = new AdnocCpiInboundPriceRowTaskRunnerAction();
        taskRunner.setModelService(modelService);

        taskRunner.run(taskService, taskModel);

        verify(modelService).remove(priceRowModel);
    }

    @Test
    void shouldNotRemovePriceRowWhenDeletionIndicatorIsFalse() {
        ModelService modelService = mock(ModelService.class);
        TaskService taskService = mock(TaskService.class);
        TaskModel taskModel = mock(TaskModel.class);
        PriceRowModel priceRowModel = mock(PriceRowModel.class);
        when(taskModel.getContextItem()).thenReturn(priceRowModel);
        when(priceRowModel.getDeletionIndicator()).thenReturn(Boolean.FALSE);

        AdnocCpiInboundPriceRowTaskRunnerAction taskRunner = new AdnocCpiInboundPriceRowTaskRunnerAction();
        taskRunner.setModelService(modelService);

        taskRunner.run(taskService, taskModel);

        verify(modelService, never()).remove(priceRowModel);
    }

    @Test
    void shouldNotRemovePriceRowWhenDeletionIndicatorIsNull() {
        ModelService modelService = mock(ModelService.class);
        TaskService taskService = mock(TaskService.class);
        TaskModel taskModel = mock(TaskModel.class);
        PriceRowModel priceRowModel = mock(PriceRowModel.class);
        when(taskModel.getContextItem()).thenReturn(priceRowModel);
        when(priceRowModel.getDeletionIndicator()).thenReturn(null);

        AdnocCpiInboundPriceRowTaskRunnerAction taskRunner = new AdnocCpiInboundPriceRowTaskRunnerAction();
        taskRunner.setModelService(modelService);

        taskRunner.run(taskService, taskModel);

        verify(modelService, never()).remove(priceRowModel);
    }

    @Test
    void shouldHandleErrorGracefully() {
        TaskService taskService = mock(TaskService.class);
        TaskModel taskModel = mock(TaskModel.class);
        Throwable throwable = new RuntimeException("Test exception");

        AdnocCpiInboundPriceRowTaskRunnerAction taskRunner = new AdnocCpiInboundPriceRowTaskRunnerAction();
        taskRunner.handleError(taskService, taskModel, throwable);
    }
}
