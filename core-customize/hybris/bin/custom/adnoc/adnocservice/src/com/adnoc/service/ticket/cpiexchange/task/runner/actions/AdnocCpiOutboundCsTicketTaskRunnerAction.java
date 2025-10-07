package com.adnoc.service.ticket.cpiexchange.task.runner.actions;

import com.adnoc.service.enums.CsTicketTargetSystem;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.AdnocOutboundCrmCsTicketModel;
import com.adnoc.service.model.AdnocOutboundCsTicketModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import rx.functions.Action1;

import java.util.Map;
import java.util.Objects;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

public class AdnocCpiOutboundCsTicketTaskRunnerAction implements TaskRunner<TaskModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocCpiOutboundCsTicketTaskRunnerAction.class);

    private static final String EXTERNAL_TICKET_ID = "externalTicketId";

    private AdnocSapCpiOutboundService adnocSapCpiOutboundService;
    private AdnocSapCpiOutboundConversionService adnocSapCpiOutboundConversionService;
    private ModelService modelService;

    @Override
    public void run(final TaskService taskService, final TaskModel taskModel) throws RetryLaterException
    {
        final CsTicketModel csTicketModel = (CsTicketModel) taskModel.getContextItem();
        if (Objects.equals(csTicketModel.getTargetSystem(), CsTicketTargetSystem.CRM))
        {

            final AdnocOutboundCrmCsTicketModel adnocOutboundCrmCsTicketModel =
                    getAdnocSapCpiOutboundConversionService().convertToOutboundCrmCsTicket(csTicketModel);

            LOG.debug("appEvent=AdnocCpiOutboundCsTicket_CRM, converted CsTicketModel to AdnocOutboundCsTicketModel: {}",
                    csTicketModel.getTicketID());

            getAdnocSapCpiOutboundService()
                    .sendCrmCsTicket(adnocOutboundCrmCsTicketModel)
                    .subscribe(getResponseEntityAction1(csTicketModel), getThrowableAction1(csTicketModel));

        }
    }

    @Override
    public void handleError(final TaskService paramTaskService, final TaskModel taskModel, final Throwable paramThrowable)
    {
        LOG.error("Failed to sync CsTicketModel", paramThrowable);
        final CsTicketModel csTicketModel = (CsTicketModel) taskModel.getContextItem();
        getModelService().save(csTicketModel);
    }

    private Action1<ResponseEntity<Map>> getResponseEntityAction1(final CsTicketModel csTicketModel)
    {
        return responseEntityMap -> {
            if (isSentSuccessfully(responseEntityMap))
            {
                if (StringUtils.isEmpty(csTicketModel.getExternalTicketId()))
                {
                    csTicketModel.setExternalTicketId(getPropertyValue(responseEntityMap, EXTERNAL_TICKET_ID));
                    csTicketModel.setState(CsTicketState.OPEN);
                }
                csTicketModel.setExported(Boolean.TRUE);
                getModelService().save(csTicketModel);
                LOG.error(String.format("CsTicket [%s] has sent to the SAP backend through SCPI! %n",
                        csTicketModel.getTicketID()));
            }
            else
            {
                LOG.error(String.format("CsTicket [%s] has not been sent to the SAP backend through SCPI! %n",
                        csTicketModel.getTicketID()));
            }
        };
    }

    private Action1<Throwable> getThrowableAction1(final CsTicketModel csTicketModel)
    {
        return error -> LOG.error(String.format("An error occurred during CSTicket = %s outbound to SCPI with errorRootCause = %s and errorMessage = %s.", csTicketModel.getTicketID(), ExceptionUtils.getRootCauseMessage(error), error.getMessage()), error);
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
