package com.adnoc.service.director;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdnocOutboundReplicationDirectorTest
{
    @InjectMocks
    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;

    @Mock
    private ModelService modelService;

    @Mock
    private TaskService taskService;

    @Mock
    private Map<String, String> requestTaskRunnerMap;

    @Test
    public void testScheduleOutboundTask()
    {
        final ItemModel item = Mockito.mock(ItemModel.class);
        Mockito.when(item.getItemtype()).thenReturn("testType");
        Mockito.when(requestTaskRunnerMap.get("testType")).thenReturn("testTaskRunner");

        final TaskModel task = new TaskModel();
        Mockito.when(modelService.create(TaskModel.class)).thenReturn(task);

        adnocOutboundReplicationDirector.scheduleOutboundTask(item);

        assertEquals("testTaskRunner", task.getRunnerBean());
        verify(taskService).scheduleTask(task);
    }
    @Test
    public void testShouldNotScheduleTask()
    {
        adnocOutboundReplicationDirector.scheduleOutboundTask(null);
        verify(taskService, never()).scheduleTask(any(TaskModel.class));
    }
}
