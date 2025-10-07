package com.adnoc.service.integration.outbound;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.AdnocB2BCustomerCreationProcessModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

public class AdnocB2BCustomerOutboundProcessAction extends AbstractProceduralAction<AdnocB2BCustomerCreationProcessModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BCustomerOutboundProcessAction.class);

    private AdnocSapCpiOutboundService adnocSapCpiOutboundService;
    private AdnocSapCpiOutboundConversionService adnocSapCpiOutboundConversionService;

    @Override
    public void executeAction(final AdnocB2BCustomerCreationProcessModel adnocB2BCustomerCreationProcessModel) throws RetryLaterException, Exception
    {
        LOG.info("appEvent=AdnocB2BCustomerOutboundAction, executionAction method being called");
        getAdnocSapCpiOutboundService().sendAdnocB2BCustomer(adnocSapCpiOutboundConversionService.convertToOutboundB2BCustomer((B2BCustomerModel) adnocB2BCustomerCreationProcessModel.getCustomer())).subscribe(

                // onNext
                responseEntityMap -> {
                    if (isSentSuccessfully(responseEntityMap))
                    {
                        LOG.info(String.format("b2b customer [%s] has been sent to the SAP backend through SCPI!", adnocB2BCustomerCreationProcessModel.getCustomer().getUid()));
                    }
                    else
                    {
                        LOG.error(String.format("b2b customer [%s] has been sent to the SAP backend through SCPI!",
                                adnocB2BCustomerCreationProcessModel.getCustomer().getUid()));
                    }
                }
                // onError
                , error -> {
                    LOG.error(String.format("b2b registration [%s] has been sent to the SAP backend through SCPI! %n%s",
                            adnocB2BCustomerCreationProcessModel.getCustomer().getUid(), error.getMessage(), error));
                });
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
}
