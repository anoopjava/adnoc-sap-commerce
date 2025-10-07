package com.adnoc.service.integration.outbound;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

public class AdnocQuoteOutboundTaskRunnerAction implements TaskRunner<TaskModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuoteOutboundTaskRunnerAction.class);

    private final String SapOrderCode = "sapOrderCode";
    private final String SapErrorMessage = "sapErrorMessage";
    private static final String SUCCESS = "success";
    private static final String RESPONSE_STATUS = "responseStatus";

    private AdnocSapCpiOutboundService adnocSapCpiOutboundService;
    private AdnocSapCpiOutboundConversionService adnocSapCpiOutboundConversionService;
    private ModelService modelService;

    @Override
    public void run(final TaskService taskService, final TaskModel taskModel) throws RetryLaterException
    {
        LOG.info("appEvent=AdnocQuoteOutboundTaskRunnerAction, executeAction method called");
        final QuoteModel quoteModel = (QuoteModel) taskModel.getContextItem();
        LOG.info("appEvent=AdnocQuoteOutboundTaskRunnerAction, executeAction method called");
        getAdnocSapCpiOutboundService().sendAdnocQuote(adnocSapCpiOutboundConversionService.convertToOutboundQuote(quoteModel)).subscribe(

                // onNext
                responseEntityMap -> {
                    if (isSentSuccessfully(responseEntityMap))
                    {
                        quoteModel.setSapOrderCode(getPropertyValue(responseEntityMap, SapOrderCode));
                        modelService.save(quoteModel);
                        LOG.info(String.format("Quote [%s] has been sent to the SAP backend through SCPI!", quoteModel.getCode()));
                    }
                    else
                    {
                        quoteModel.setSapErrorMessage(getPropertyValue(responseEntityMap,SapErrorMessage));
                        modelService.save(quoteModel);
                        LOG.error(String.format("Quote [%s] has been sent to the SAP backend through SCPI!",
                                quoteModel.getCode()));
                    }
                }
                // onError
                , error -> {
                    LOG.error(String.format("Quote [%s] has been sent to the SAP backend through SCPI! %n%s",
                            quoteModel.getCode(), error.getMessage(), error));
                });

    }

    static boolean isSentSuccessfully(ResponseEntity<Map> responseEntityMap)
    {
        return SUCCESS.equalsIgnoreCase(getPropertyValue(responseEntityMap, RESPONSE_STATUS));
    }

    @Override
    public void handleError(final TaskService taskService, final TaskModel taskModel, final Throwable throwable)
    {
        final QuoteModel quoteModel = (QuoteModel) taskModel.getContextItem();
        LOG.error("Failed to sync quoteModel", throwable);
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
