package com.adnoc.service.integration.outbound.service.impl;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.*;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.sap.sapcpiadapter.service.impl.SapCpiOutboundServiceImpl;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.Map;

public class AdnocSapCpiOutboundServiceImpl extends SapCpiOutboundServiceImpl implements AdnocSapCpiOutboundService
{
    private static final Logger LOG = LogManager.getLogger(AdnocSapCpiOutboundServiceImpl.class);

    private static final String OUTBOUND_AdnocB2BRegistrationModel_OBJECT = "AdnocSoldToOutboundB2BRegistration";
    private static final String OUTBOUND_AdnocB2BRegistrationModel_DESTINATION = "adnocB2BRegistrationDestination";

    private static final String OUTBOUND_AdnocPayerB2BUnitRegistrationModel_OBJECT = "AdnocPayerOutboundB2BUnitRegistration";
    private static final String OUTBOUND_AdnocPayerB2BUnitRegistrationModel_DESTINATION = "adnocPayerB2BUnitRegistrationDestination";

    private static final String OUTBOUND_AdnocShipToB2BUnitRegistrationModel_OBJECT = "AdnocShipToOutboundB2BUnitRegistration";
    private static final String OUTBOUND_AdnocShipToB2BUnitRegistrationModel_DESTINATION = "adnocShipToB2BUnitRegistrationDestination";

    private static final String OUTBOUND_AdnocB2BCustomerModel_OBJECT = "AdnocOutboundB2BCustomer";
    private static final String OUTBOUND_AdnocB2CustomerModel_DESTINATION = "adnocB2BCustomerDestination";

    private static final String OUTBOUND_ADNOCOUTBOUNDCSTICKET_TOCRM_INTEGRATION_OBJECT = "AdnocOutboundCsTicketIntegrationObject";
    private static final String OUTBOUND_ADNOCOUTBOUNDCSTICKET_TOCRM_DESTINATION = "adnocCrmCsTicketDestination";

    private static final String OUTBOUND_RETURNORDER_INTEGRATION_OBJECT = "AdnocOutboundReturnOrderIntegrationObject";
    private static final String OUTBOUND_RETURNORDER_DESTINATION = "adnocReturnOrderDestination";

    private static final String OUTBOUND_OVERDUEPAYMENT_INTEGRATION_OBJECT = "AdnocOutboundOverduePaymentTransaction";
    private static final String OUTBOUND_OVERDUEPAYMENT_DESTINATION = "adnocOverduePaymentOutboundDestination";

    private static final String OUTBOUND_QUOTE_INTEGRATION_OBJECT = "AdnocOutboundQuote";
    private static final String OUTBOUND_QUOTE_DESTINATION = "adnocQuoteOutboundDestination";

    private FlexibleSearchService flexibleSearchService;

    @Override
    public Observable<ResponseEntity<Map>> sendAdnocB2BRegistration(final Object adnocOutboundB2BRegistrationModel)
    {
        LOG.debug("appEvent=AdnocSapCpiOutbound, started sending adnoc B2BRegistration with model:{}", adnocOutboundB2BRegistrationModel);
        if (adnocOutboundB2BRegistrationModel instanceof AdnocSoldToOutboundB2BRegistrationModel)
        {
            return getResponseEntityObservable(OUTBOUND_AdnocB2BRegistrationModel_DESTINATION, OUTBOUND_AdnocB2BRegistrationModel_OBJECT, adnocOutboundB2BRegistrationModel);
        }
        else if (adnocOutboundB2BRegistrationModel instanceof AdnocPayerOutboundB2BUnitRegistrationModel)
        {
            return getResponseEntityObservable(OUTBOUND_AdnocPayerB2BUnitRegistrationModel_DESTINATION,OUTBOUND_AdnocPayerB2BUnitRegistrationModel_OBJECT, adnocOutboundB2BRegistrationModel);
        }
        else if (adnocOutboundB2BRegistrationModel instanceof AdnocShipToOutboundB2BUnitRegistrationModel)
        {
            return getResponseEntityObservable(OUTBOUND_AdnocShipToB2BUnitRegistrationModel_DESTINATION, OUTBOUND_AdnocShipToB2BUnitRegistrationModel_OBJECT, adnocOutboundB2BRegistrationModel);
        }
        LOG.warn("appEvent=AdnocSapCpiOutbound,unknown model type for AdnocB2BRegistration:{}", adnocOutboundB2BRegistrationModel);
        return Observable.empty();
    }

    private Observable<ResponseEntity<Map>> getResponseEntityObservable(final String consumedDestinationId, final String integrationObjectCode, final Object adnocOutboundB2BRegistrationModel)
    {
        LOG.info("appEvent=AdnocSapCpiOutbound,getting responseEntity for destination:{} and integration object:{}", consumedDestinationId, integrationObjectCode);
        final ConsumedDestinationModel adnocB2BRConsumedDestination = getConsumedDestinationModel(consumedDestinationId);
        final IntegrationObjectModel integrationObjectModel = getIntegrationObjectModel(integrationObjectCode);

        return getResponseEntityObservable(adnocOutboundB2BRegistrationModel, integrationObjectModel, adnocB2BRConsumedDestination);
    }

    @Override
    public Observable<ResponseEntity<Map>> sendAdnocB2BCustomer(final AdnocOutboundB2BCustomerModel adnocOutboundB2BCustomerModel)
    {
        LOG.info("appEvent=AdnocSapCpiOutbound,sending Adnoc B2B Customer: {}", adnocOutboundB2BCustomerModel.getUid());
        final ConsumedDestinationModel consumedDestinationModel = getConsumedDestinationModel(OUTBOUND_AdnocB2CustomerModel_DESTINATION);
        final IntegrationObjectModel integrationObjectModel = getIntegrationObjectModel(OUTBOUND_AdnocB2BCustomerModel_OBJECT);

        return getResponseEntityObservable(adnocOutboundB2BCustomerModel, integrationObjectModel, consumedDestinationModel);
    }

    @Override
    public Observable<ResponseEntity<Map>> sendCrmCsTicket(final AdnocOutboundCrmCsTicketModel adnocOutboundCrmCsTicketModel)
    {
        LOG.info("appEvent=AdnocSapCpiOutbound,sending CsTicket: {}", adnocOutboundCrmCsTicketModel.getTicketID());
        final ConsumedDestinationModel consumedDestinationModel = getConsumedDestinationModel(OUTBOUND_ADNOCOUTBOUNDCSTICKET_TOCRM_DESTINATION);
        final IntegrationObjectModel integrationObjectModel = getIntegrationObjectModel(OUTBOUND_ADNOCOUTBOUNDCSTICKET_TOCRM_INTEGRATION_OBJECT);

        return getResponseEntityObservable(adnocOutboundCrmCsTicketModel, integrationObjectModel, consumedDestinationModel);
    }

    @Override
    public Observable<ResponseEntity<Map>> sendReturnOrder(final AdnocReturnRequestOutboundModel adnocReturnRequestOutboundModel)
    {
        LOG.info("appEvent=AdnocSapCpiOutbound,sending ReturnOrder: {}", adnocReturnRequestOutboundModel.getCode());
        final ConsumedDestinationModel consumedDestinationModel = getConsumedDestinationModel(OUTBOUND_RETURNORDER_DESTINATION);
        final IntegrationObjectModel integrationObjectModel = getIntegrationObjectModel(OUTBOUND_RETURNORDER_INTEGRATION_OBJECT);

        return getResponseEntityObservable(adnocReturnRequestOutboundModel, integrationObjectModel, consumedDestinationModel);
    }

    @Override
    public Observable<ResponseEntity<Map>> sendAdnocOutboundOverduePaymentTransaction(AdnocOutboundOverduePaymentTransactionModel adnocOutboundOverduePaymentTransactionModel)
    {
        LOG.info("appEvent=AdnocOutboundOverduePaymentTransactionModel,sending customer: {}", adnocOutboundOverduePaymentTransactionModel.getCustomer());
        final ConsumedDestinationModel consumedDestinationModel = getConsumedDestinationModel(OUTBOUND_OVERDUEPAYMENT_DESTINATION);
        final IntegrationObjectModel integrationObjectModel = getIntegrationObjectModel(OUTBOUND_OVERDUEPAYMENT_INTEGRATION_OBJECT);

        return getResponseEntityObservable(adnocOutboundOverduePaymentTransactionModel, integrationObjectModel, consumedDestinationModel);

    }

    @Override
    public Observable<ResponseEntity<Map>> sendAdnocQuote(final AdnocOutboundQuoteModel adnocOutboundQuoteModel)
    {
        final ConsumedDestinationModel consumedDestinationModel = getConsumedDestinationModel(OUTBOUND_QUOTE_DESTINATION);
        final IntegrationObjectModel integrationObjectModel = getIntegrationObjectModel(OUTBOUND_QUOTE_INTEGRATION_OBJECT);

        return getResponseEntityObservable(adnocOutboundQuoteModel, integrationObjectModel, consumedDestinationModel);
    }

    private ConsumedDestinationModel getConsumedDestinationModel(final String consumedDestinationId)
    {
        LOG.debug("appEvent=AdnocSapCpiOutbound,getting ConsumedDestinationModel for ID: {}", consumedDestinationId);
        final ConsumedDestinationModel consumedDestinationModel = new ConsumedDestinationModel();
        consumedDestinationModel.setId(consumedDestinationId);
        final ConsumedDestinationModel adnocB2BConsumedDestination = getFlexibleSearchService().getModelByExample(consumedDestinationModel);
        return adnocB2BConsumedDestination;
    }

    private IntegrationObjectModel getIntegrationObjectModel(final String integrationObjectCode)
    {
        LOG.debug("appEvent=AdnocSapCpiOutbound,getting IntegrationObjectModel for ID: {}", integrationObjectCode);
        final IntegrationObjectModel integrationObjectModel = new IntegrationObjectModel();
        integrationObjectModel.setCode(integrationObjectCode);
        final IntegrationObjectModel adnocB2BIntegrationObject = getFlexibleSearchService().getModelByExample(integrationObjectModel);
        return adnocB2BIntegrationObject;
    }

    private Observable<ResponseEntity<Map>> getResponseEntityObservable(final Object payload, final IntegrationObjectModel integrationObjectModel, final ConsumedDestinationModel consumedDestinationModel)
    {
        LOG.debug("appEvent=AdnocSapCpiOutbound,getting responseEntity for payload: {}, IntegrationObjectModel {},ConsumedDestinationModel{}", payload, integrationObjectModel, consumedDestinationModel);
        final SyncParameters syncParameters = SyncParameters.syncParametersBuilder()
                .withPayloadObject(payload)
                .withIntegrationObject(integrationObjectModel)
                .withDestination(consumedDestinationModel)
                .withSource(OutboundSource.UNKNOWN)
                .build();
        return getOutboundServiceFacade().send(syncParameters);
    }

    protected FlexibleSearchService getFlexibleSearchService()
    {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
    {
        this.flexibleSearchService = flexibleSearchService;
    }
}