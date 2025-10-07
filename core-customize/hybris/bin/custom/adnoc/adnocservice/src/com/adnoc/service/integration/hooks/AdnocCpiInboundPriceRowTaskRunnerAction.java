package com.adnoc.service.integration.hooks;

import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocCpiInboundPriceRowTaskRunnerAction implements TaskRunner<TaskModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocCpiInboundPriceRowTaskRunnerAction.class);

    private ModelService modelService;

    @Override
    public void run(final TaskService taskService, final TaskModel taskModel) throws RetryLaterException
    {
        LOG.info("appEvent=AdnocCpiInboundPriceRow,Running task for PriceRowModel.");

        final PriceRowModel priceRowModel = (PriceRowModel) taskModel.getContextItem();
        LOG.debug("appEvent=AdnocCpiInboundPriceRow,Processing PriceRowModel for product:{}", priceRowModel.getProduct());

        if (BooleanUtils.isTrue(priceRowModel.getDeletionIndicator()))
        {
            getModelService().remove(priceRowModel);
        }
    }

    @Override
    public void handleError(final TaskService paramTaskService, final TaskModel taskModel, final Throwable paramThrowable)
    {
        final PriceRowModel priceRowModel = (PriceRowModel) taskModel.getContextItem();
        LOG.error("appEvent=AdnocCpiInboundPriceRow, Failed to remove Price Row Model with pk: {} due to: {}", priceRowModel.getPk(), paramThrowable.getMessage());
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
