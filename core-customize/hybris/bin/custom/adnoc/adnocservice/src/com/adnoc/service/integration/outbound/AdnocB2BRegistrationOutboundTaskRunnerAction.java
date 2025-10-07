package com.adnoc.service.integration.outbound;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.AdnocPayerB2BUnitRegistrationModel;
import com.adnoc.service.model.AdnocRegistrationModel;
import com.adnoc.service.model.AdnocShipToB2BUnitRegistrationModel;
import com.adnoc.service.model.AdnocSoldToB2BRegistrationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import rx.functions.Action1;

import java.util.Map;
import java.util.Objects;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;

public class AdnocB2BRegistrationOutboundTaskRunnerAction implements TaskRunner<TaskModel>
{

    private static final Logger LOG = LogManager.getLogger(AdnocB2BRegistrationOutboundTaskRunnerAction.class);

    public static final String WE = "WE";
    private final String responseMessage = "responseMessage";
    private String SUCCESS = "success";
    private String RESPONSE_STATUS = "responseStatus";
    private final String SAP_BUSINESS_PARTNER_ID = "sapBusinessPartnerId";
    private AdnocSapCpiOutboundService adnocSapCpiOutboundService;
    private AdnocSapCpiOutboundConversionService adnocSapCpiOutboundConversionService;
    private ModelService modelService;

    private Action1<Throwable> getThrowableAction1(final AdnocRegistrationModel adnocRegistrationModel)
    {
        return error -> LOG.error(String.format("Error sending b2b registration [%s] to SAP backend through SCPI: %s",
                                                adnocRegistrationModel.getEmail(), error.getMessage()));
    }

    private Action1<ResponseEntity<Map>> getResponseEntityAction1(final AdnocRegistrationModel adnocRegistrationModel)
    {
        return responseEntityMap -> {
            if (isSentSuccessfully(responseEntityMap))
            {
                adnocRegistrationModel.setExported(Boolean.TRUE);
                adnocRegistrationModel.setSapBusinessPartnerId(getPropertyValue(responseEntityMap, SAP_BUSINESS_PARTNER_ID));
                LOG.info(String.format("b2b registration [%s] has been sent to the SAP backend through SCPI!", adnocRegistrationModel.getEmail()));
            }
            else
            {
                LOG.error(String.format("b2b registration [%s] has been sent to the SAP backend through SCPI!", adnocRegistrationModel.getEmail()));
            }
            adnocRegistrationModel.setSapErrorMessage(getPropertyValue(responseEntityMap, responseMessage));
            getModelService().save(adnocRegistrationModel);
        };
    }

    private boolean isSentSuccessfully(final ResponseEntity<Map> responseEntityMap)
    {
        return SUCCESS.equalsIgnoreCase(getPropertyValue(responseEntityMap, RESPONSE_STATUS));
    }
    @Override
    public void run(final TaskService taskService, final TaskModel taskModel) throws RetryLaterException
    {
        LOG.info("appEvent=AdnocB2BRegistrationOutboundAction, executionAction method being called");

        final AdnocRegistrationModel adnocRegistrationModel = (AdnocRegistrationModel) taskModel.getContextItem();

        Object convertToOutboundModel = null;
        if (adnocRegistrationModel instanceof final AdnocSoldToB2BRegistrationModel adnocSoldToB2BRegistrationModel)
        {
            convertToOutboundModel = getAdnocSapCpiOutboundConversionService().convertToSoldToOutboundModel(adnocSoldToB2BRegistrationModel);
        }
        else if (adnocRegistrationModel instanceof final AdnocPayerB2BUnitRegistrationModel adnocPayerB2BUnitRegistrationModel)
        {
            LOG.info("appEvent=AdnocB2BRegistrationOutboundAction, converting AdnocPayerB2BUnitRegistrationModel to outbound model");
            convertToOutboundModel = getAdnocSapCpiOutboundConversionService().convertToPayerOutboundB2BUnitModel(adnocPayerB2BUnitRegistrationModel);
        }
        else if (adnocRegistrationModel instanceof final AdnocShipToB2BUnitRegistrationModel adnocShipToB2BUnitRegistrationModel)
        {
            LOG.info("appEvent=AdnocB2BRegistrationOutboundAction, converting AdnocShipToB2BUnitRegistrationModel to outbound model");
            convertToOutboundModel = getAdnocSapCpiOutboundConversionService().convertToShipToOutboundB2BUnitModel(adnocShipToB2BUnitRegistrationModel);
        }
        if (Objects.nonNull(convertToOutboundModel))
        {
            LOG.info("appEvent=AdnocB2BRegistrationOutboundAction,sending converted model to outbound service");
            getAdnocSapCpiOutboundService().sendAdnocB2BRegistration(convertToOutboundModel).subscribe(getResponseEntityAction1(adnocRegistrationModel), getThrowableAction1(adnocRegistrationModel));
        }
    }

    @Override
    public void handleError(final TaskService taskService, final TaskModel taskModel, final Throwable throwable)
    {
        final AdnocRegistrationModel adnocRegistrationModel = (AdnocRegistrationModel) taskModel.getContextItem();
        LOG.error("Failed to sync AdnocRegistrationModel", throwable);
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

