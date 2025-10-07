package com.adnoc.service.customer;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.AdnocOutboundB2BCustomerModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

/**
 * This class is responsible for handling the task of sending New B2B customer data to CPI.
 */

public class AdnocB2BCustomerCpiOutboundTaskRunner implements TaskRunner<TaskModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BCustomerCpiOutboundTaskRunner.class);

    private AdnocSapCpiOutboundService adnocSapCpiOutboundService;
    private AdnocSapCpiOutboundConversionService adnocSapCpiOutboundConversionService;
    private ModelService modelService;

    @Override
    public void run(final TaskService taskService, final TaskModel taskModel) throws RetryLaterException
    {
        LOG.info("appEvent=AdnocB2BCustomerCpiOutbound, Running task for B2BCustomerModel");
        final B2BCustomerModel b2BCustomerModel = getB2BCustomerModelFromTask(taskModel);
        final AdnocOutboundB2BCustomerModel adnocOutboundB2BCustomerModel = getAdnocSapCpiOutboundConversionService().convertToOutboundB2BCustomer(b2BCustomerModel);
        getAdnocSapCpiOutboundService().sendAdnocB2BCustomer(adnocOutboundB2BCustomerModel).
                subscribe(responseEntityMap -> {
                            if (isSentSuccessfully(responseEntityMap))
                            {
                                b2BCustomerModel.setExported(Boolean.TRUE);
                                getModelService().save(b2BCustomerModel);
                                LOG.info(String.format("appEvent=AdnocB2BCustomerCpiOutbound, B2B customer [%s] has been sent to the SAP backend through SCPI!", b2BCustomerModel.getUid()));
                            }
                            else
                            {
                                LOG.error(String.format("appEvent=AdnocB2BCustomerCpiOutbound, B2B customer [%s] has NOT been sent to the SAP backend through SCPI!", b2BCustomerModel.getUid()));
                            }
                        },
                        error ->
                                LOG.error(String.format("appEvent=AdnocB2BCustomerCpiOutbound, B2B customer [%s] failed to be sent to the SAP backend through SCPI! %n%s", b2BCustomerModel.getUid(), error.getMessage())));
    }

    @Override
    public void handleError(final TaskService taskService, final TaskModel taskModel, final Throwable throwable)
    {
        LOG.error("appEvent=AdnocB2BCustomerCpiOutbound, Failed to sync B2BCustomerModel", throwable);
    }

    private B2BCustomerModel getB2BCustomerModelFromTask(final TaskModel taskModel)
    {
        return (B2BCustomerModel) taskModel.getContextItem();
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

    public void setModelService(ModelService modelService)
    {
        this.modelService = modelService;
    }
}
