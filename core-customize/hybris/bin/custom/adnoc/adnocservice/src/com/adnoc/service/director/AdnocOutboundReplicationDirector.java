package com.adnoc.service.director;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class AdnocOutboundReplicationDirector
{
    private static final Logger LOG = LogManager.getLogger(AdnocOutboundReplicationDirector.class);

    private TaskService taskService;
    private ModelService modelService;
    private Map<String, String> requestTaskRunnerMap;

    public void scheduleOutboundTask(final Object item)
    {
        if (Objects.nonNull(item))
        {
            final String itemType = ((ItemModel) item).getItemtype();
            final String taskRunnerBean = getRequestTaskRunnerMap().get(itemType);
            LOG.info("appEvent=AdnocOutboundReplication, start creating task for {} outbound task runner {} action.", itemType, taskRunnerBean);
            scheduleTask((ItemModel) item, taskRunnerBean);
        }
    }

    private void scheduleTask(final ItemModel item, final String taskRunnerBean)
    {
        final TaskModel task = getModelService().create(TaskModel.class);
        task.setRunnerBean(taskRunnerBean);
        task.setExecutionDate(new Date()); // Execute ASAP
        task.setContextItem(item);
        getTaskService().scheduleTask(task);
        LOG.debug("appEvent=AdnocOutboundReplication, new {} created and task scheduled", item);
    }


    protected Map<String, String> getRequestTaskRunnerMap()
    {
        return requestTaskRunnerMap;
    }

    public void setRequestTaskRunnerMap(final Map<String, String> requestTaskRunnerMap)
    {
        this.requestTaskRunnerMap = requestTaskRunnerMap;
    }

    protected TaskService getTaskService()
    {
        return taskService;
    }

    public void setTaskService(final TaskService taskService)
    {
        this.taskService = taskService;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }
}
