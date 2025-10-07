package com.adnoc.service.integration.outbound;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

public class AdnocReturnOrderOutboundTaskRunnerAction implements TaskRunner<TaskModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocReturnOrderOutboundTaskRunnerAction.class);

    private final String SapOrderCode = "sapOrderCode";
    private final String SapErrorMessage = "sapErrorMessage";

    private AdnocSapCpiOutboundService adnocSapCpiOutboundService;
    private AdnocSapCpiOutboundConversionService adnocSapCpiOutboundConversionService;
    private ModelService modelService;

    @Override
    public void run(final TaskService taskService, final TaskModel taskModel) throws RetryLaterException
    {
        LOG.info("appEvent=AdnocReturnOrderOutboundTaskRunnerAction, executeAction method called");
        final ReturnRequestModel returnRequestModel = (ReturnRequestModel) taskModel.getContextItem();
        LOG.info("appEvent=AdnocReturnOrderOutboundAction, executeAction method called");
        getAdnocSapCpiOutboundService().sendReturnOrder(adnocSapCpiOutboundConversionService.convertToOutboundReturnRequest(returnRequestModel)).subscribe(

                // onNext
                responseEntityMap -> {
                    if (isSentSuccessfully(responseEntityMap))
                    {
                        final String sapOrderCode = getPropertyValue(responseEntityMap, SapOrderCode);
                        if (StringUtils.isNotEmpty(sapOrderCode))
                        {
                            returnRequestModel.setSapReturnOrderCode(sapOrderCode);
                            setReturnRequestStatus(returnRequestModel);
                        }
                        returnRequestModel.setExported(Boolean.TRUE);
                        modelService.save(returnRequestModel);
                        LOG.info(String.format("return order [%s] has been sent to the SAP backend through SCPI!", returnRequestModel.getCode()));
                    }
                    else
                    {
                        returnRequestModel.setSapErrorMessage(getPropertyValue(responseEntityMap, SapErrorMessage));
                        modelService.save(returnRequestModel);
                        LOG.error(String.format("return order [%s] has been sent to the SAP backend through SCPI!",
                                returnRequestModel.getCode()));
                    }
                }
                // onError
                , error ->
                        LOG.error("Failed to send return order [{}] to SAP backend through SCPI: {}", returnRequestModel.getCode(), error.getMessage(), error));
    }

    private void setReturnRequestStatus(final ReturnRequestModel returnRequestModel)
    {
        returnRequestModel.setStatus(ReturnStatus.RECEIVED);
        final List<ReturnEntryModel> returnEntries = returnRequestModel.getReturnEntries();
        if (CollectionUtils.isNotEmpty(returnEntries))
        {
            returnEntries.forEach(entry -> {
                entry.setReceivedQuantity(entry.getExpectedQuantity());
                entry.setStatus(ReturnStatus.COMPLETED);
            });
            getModelService().saveAll(returnEntries);
        }
    }

    @Override
    public void handleError(final TaskService taskService, final TaskModel taskModel, final Throwable throwable)
    {
        LOG.error("Failed to sync ReturnRequestModel", throwable);
    }


    protected AdnocSapCpiOutboundService getAdnocSapCpiOutboundService()
    {
        return adnocSapCpiOutboundService;
    }

    public void setAdnocSapCpiOutboundService(final AdnocSapCpiOutboundService adnocSapCpiOutboundService)
    {
        this.adnocSapCpiOutboundService = adnocSapCpiOutboundService;
    }

    protected AdnocSapCpiOutboundConversionService getAdnocSapCpiOutboundConversionService()
    {
        return adnocSapCpiOutboundConversionService;
    }

    public void setAdnocSapCpiOutboundConversionService(final AdnocSapCpiOutboundConversionService adnocSapCpiOutboundConversionService)
    {
        this.adnocSapCpiOutboundConversionService = adnocSapCpiOutboundConversionService;
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
