package com.adnoc.service.integration.outbound;

import com.adnoc.service.enums.AdnocOverDueStatus;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.AdnocOverduePaymentTransactionModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

public class AdnocOverduePaymentOutboundTaskRunnerAction implements TaskRunner<TaskModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocOverduePaymentOutboundTaskRunnerAction.class);

    private AdnocSapCpiOutboundService adnocSapCpiOutboundService;
    private AdnocSapCpiOutboundConversionService adnocSapCpiOutboundConversionService;
    private ModelService modelService;

    @Override
    public void run(final TaskService taskService, final TaskModel taskModel) throws RetryLaterException
    {
        LOG.info("appEvent=dnocOverduePaymentOutbound, executeAction method called");
        final AdnocOverduePaymentTransactionModel adnocOverduePaymentTransactionModel = (AdnocOverduePaymentTransactionModel) taskModel.getContextItem();
        LOG.info("appEvent=dnocOverduePaymentOutbound, executeAction method called");
        getAdnocSapCpiOutboundService().sendAdnocOutboundOverduePaymentTransaction(adnocSapCpiOutboundConversionService.convertToAdnocOutboundOverduePaymentTransaction(adnocOverduePaymentTransactionModel)).subscribe(

                // onNext
                responseEntityMap -> {
                    if (isSentSuccessfully(responseEntityMap))
                    {
                        settingOverDueStatus(adnocOverduePaymentTransactionModel, AdnocOverDueStatus.SUCCESS);
                        adnocOverduePaymentTransactionModel.setExported(Boolean.TRUE);
                        modelService.save(adnocOverduePaymentTransactionModel);
                        LOG.info(String.format("adnocOverduePaymentTransaction [%s] has been sent to the SAP backend through SCPI!", adnocOverduePaymentTransactionModel.getPayerId()));
                    }
                    else
                    {
                        settingOverDueStatus(adnocOverduePaymentTransactionModel, AdnocOverDueStatus.PAYMENT_IN_PROGRESS);
                        LOG.error(String.format("adnocOverduePaymentTransaction[%s] has been sent to the SAP backend through SCPI!",
                                adnocOverduePaymentTransactionModel.getPayerId()));
                    }
                }
                // onError
                , error -> {
                    settingOverDueStatus(adnocOverduePaymentTransactionModel, AdnocOverDueStatus.PAYMENT_IN_PROGRESS);
                    LOG.error(String.format("Error sending adnocOverduePaymentTransaction [%s] to SAP backend through SCPI! Error: %n%s",
                                    adnocOverduePaymentTransactionModel.getPayerId(), error.getMessage()));
                });

    }

    private void settingOverDueStatus(final AdnocOverduePaymentTransactionModel adnocOverduePaymentTransactionModel, final AdnocOverDueStatus adnocOverDueStatus)
    {
        adnocOverduePaymentTransactionModel.getInvoiceDetails()
                .forEach(adnocOverdueInvoiceDetailsModel ->
                        adnocOverdueInvoiceDetailsModel.setOverdueStatus(adnocOverDueStatus));
        modelService.saveAll(adnocOverduePaymentTransactionModel.getInvoiceDetails());
    }

    @Override
    public void handleError(final TaskService taskService, final TaskModel taskModel, final Throwable throwable)
    {
        LOG.error("Failed to sync AdnocOverduePaymentOutbound", throwable);
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
