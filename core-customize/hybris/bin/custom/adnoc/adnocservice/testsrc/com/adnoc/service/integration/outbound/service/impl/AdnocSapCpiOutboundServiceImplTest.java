package com.adnoc.service.integration.outbound.service.impl;

import com.adnoc.service.model.*;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AdnocSapCpiOutboundServiceImplTest {

    static class TestAdnocSapCpiOutboundServiceImpl extends AdnocSapCpiOutboundServiceImpl {
        private OutboundServiceFacade facade;
        public void setOutboundServiceFacade(OutboundServiceFacade facade) {
            this.facade = facade;
        }
        @Override
        protected OutboundServiceFacade getOutboundServiceFacade() {
            return facade;
        }
    }

    @Test
    void testSendAdnocB2BRegistration_SoldTo() {
        TestAdnocSapCpiOutboundServiceImpl service = new TestAdnocSapCpiOutboundServiceImpl();
        FlexibleSearchService flex = mock(FlexibleSearchService.class);
        service.setFlexibleSearchService(flex);
        OutboundServiceFacade facade = mock(OutboundServiceFacade.class);
        service.setOutboundServiceFacade(facade);

        // Mocking
        ConsumedDestinationModel dest = new ConsumedDestinationModel();
        dest.setId("adnocB2BRegistrationDestination");
        IntegrationObjectModel io = new IntegrationObjectModel();
        io.setCode("AdnocSoldToOutboundB2BRegistration");
        when(flex.getModelByExample(any(ConsumedDestinationModel.class))).thenReturn(dest);
        when(flex.getModelByExample(any(IntegrationObjectModel.class))).thenReturn(io);

        AdnocSoldToOutboundB2BRegistrationModel payload = mock(AdnocSoldToOutboundB2BRegistrationModel.class);
        Observable<ResponseEntity<Map>> mockObs = Observable.just(ResponseEntity.ok(new HashMap<>()));
        when(facade.send(any(SyncParameters.class))).thenReturn(mockObs);

        Observable<ResponseEntity<Map>> result = service.sendAdnocB2BRegistration(payload);
        assertNotNull(result);
        verify(facade).send(any(SyncParameters.class));
    }

    @Test
    void testSendAdnocB2BRegistration_Payer() {
        TestAdnocSapCpiOutboundServiceImpl service = new TestAdnocSapCpiOutboundServiceImpl();
        FlexibleSearchService flex = mock(FlexibleSearchService.class);
        service.setFlexibleSearchService(flex);
        OutboundServiceFacade facade = mock(OutboundServiceFacade.class);
        service.setOutboundServiceFacade(facade);

        ConsumedDestinationModel dest = new ConsumedDestinationModel();
        dest.setId("adnocPayerB2BUnitRegistrationDestination");
        IntegrationObjectModel io = new IntegrationObjectModel();
        io.setCode("AdnocPayerOutboundB2BUnitRegistration");
        when(flex.getModelByExample(any(ConsumedDestinationModel.class))).thenReturn(dest);
        when(flex.getModelByExample(any(IntegrationObjectModel.class))).thenReturn(io);

        AdnocPayerOutboundB2BUnitRegistrationModel payload = mock(AdnocPayerOutboundB2BUnitRegistrationModel.class);
        Observable<ResponseEntity<Map>> mockObs = Observable.just(ResponseEntity.ok(new HashMap<>()));
        when(facade.send(any(SyncParameters.class))).thenReturn(mockObs);

        Observable<ResponseEntity<Map>> result = service.sendAdnocB2BRegistration(payload);
        assertNotNull(result);
        verify(facade).send(any(SyncParameters.class));
    }

    @Test
    void testSendAdnocB2BRegistration_ShipTo() {
        TestAdnocSapCpiOutboundServiceImpl service = new TestAdnocSapCpiOutboundServiceImpl();
        FlexibleSearchService flex = mock(FlexibleSearchService.class);
        service.setFlexibleSearchService(flex);
        OutboundServiceFacade facade = mock(OutboundServiceFacade.class);
        service.setOutboundServiceFacade(facade);

        ConsumedDestinationModel dest = new ConsumedDestinationModel();
        dest.setId("adnocShipToB2BUnitRegistrationDestination");
        IntegrationObjectModel io = new IntegrationObjectModel();
        io.setCode("AdnocShipToOutboundB2BUnitRegistration");
        when(flex.getModelByExample(any(ConsumedDestinationModel.class))).thenReturn(dest);
        when(flex.getModelByExample(any(IntegrationObjectModel.class))).thenReturn(io);

        AdnocShipToOutboundB2BUnitRegistrationModel payload = mock(AdnocShipToOutboundB2BUnitRegistrationModel.class);
        Observable<ResponseEntity<Map>> mockObs = Observable.just(ResponseEntity.ok(new HashMap<>()));
        when(facade.send(any(SyncParameters.class))).thenReturn(mockObs);

        Observable<ResponseEntity<Map>> result = service.sendAdnocB2BRegistration(payload);
        assertNotNull(result);
        verify(facade).send(any(SyncParameters.class));
    }

    @Test
    void testSendAdnocB2BRegistration_UnknownType() {
        TestAdnocSapCpiOutboundServiceImpl service = new TestAdnocSapCpiOutboundServiceImpl();
        Observable<ResponseEntity<Map>> result = service.sendAdnocB2BRegistration("not-a-model");
        // Should return empty
        assertTrue(result.isEmpty().toBlocking().first());
    }

    @Test
    void testSendAdnocB2BCustomer() {
        TestAdnocSapCpiOutboundServiceImpl service = new TestAdnocSapCpiOutboundServiceImpl();
        FlexibleSearchService flex = mock(FlexibleSearchService.class);
        service.setFlexibleSearchService(flex);
        OutboundServiceFacade facade = mock(OutboundServiceFacade.class);
        service.setOutboundServiceFacade(facade);

        ConsumedDestinationModel dest = new ConsumedDestinationModel();
        dest.setId("adnocB2BCustomerDestination");
        IntegrationObjectModel io = new IntegrationObjectModel();
        io.setCode("AdnocOutboundB2BCustomer");
        when(flex.getModelByExample(any(ConsumedDestinationModel.class))).thenReturn(dest);
        when(flex.getModelByExample(any(IntegrationObjectModel.class))).thenReturn(io);

        AdnocOutboundB2BCustomerModel payload = mock(AdnocOutboundB2BCustomerModel.class);
        when(payload.getUid()).thenReturn("uid-123");
        Observable<ResponseEntity<Map>> mockObs = Observable.just(ResponseEntity.ok(new HashMap<>()));
        when(facade.send(any(SyncParameters.class))).thenReturn(mockObs);

        Observable<ResponseEntity<Map>> result = service.sendAdnocB2BCustomer(payload);
        assertNotNull(result);
        verify(facade).send(any(SyncParameters.class));
    }

    @Test
    void testSendCrmCsTicket() {
        TestAdnocSapCpiOutboundServiceImpl service = new TestAdnocSapCpiOutboundServiceImpl();
        FlexibleSearchService flex = mock(FlexibleSearchService.class);
        service.setFlexibleSearchService(flex);
        OutboundServiceFacade facade = mock(OutboundServiceFacade.class);
        service.setOutboundServiceFacade(facade);

        ConsumedDestinationModel dest = new ConsumedDestinationModel();
        dest.setId("adnocCrmCsTicketDestination");
        IntegrationObjectModel io = new IntegrationObjectModel();
        io.setCode("AdnocOutboundCsTicketIntegrationObject");
        when(flex.getModelByExample(any(ConsumedDestinationModel.class))).thenReturn(dest);
        when(flex.getModelByExample(any(IntegrationObjectModel.class))).thenReturn(io);

        AdnocOutboundCrmCsTicketModel payload = mock(AdnocOutboundCrmCsTicketModel.class);
        when(payload.getTicketID()).thenReturn("TICK1");
        Observable<ResponseEntity<Map>> mockObs = Observable.just(ResponseEntity.ok(new HashMap<>()));
        when(facade.send(any(SyncParameters.class))).thenReturn(mockObs);

        Observable<ResponseEntity<Map>> result = service.sendCrmCsTicket(payload);
        assertNotNull(result);
        verify(facade).send(any(SyncParameters.class));
    }

    @Test
    void testSendReturnOrder() {
        TestAdnocSapCpiOutboundServiceImpl service = new TestAdnocSapCpiOutboundServiceImpl();
        FlexibleSearchService flex = mock(FlexibleSearchService.class);
        service.setFlexibleSearchService(flex);
        OutboundServiceFacade facade = mock(OutboundServiceFacade.class);
        service.setOutboundServiceFacade(facade);

        ConsumedDestinationModel dest = new ConsumedDestinationModel();
        dest.setId("adnocReturnOrderDestination");
        IntegrationObjectModel io = new IntegrationObjectModel();
        io.setCode("AdnocOutboundReturnOrderIntegrationObject");
        when(flex.getModelByExample(any(ConsumedDestinationModel.class))).thenReturn(dest);
        when(flex.getModelByExample(any(IntegrationObjectModel.class))).thenReturn(io);

        AdnocReturnRequestOutboundModel payload = mock(AdnocReturnRequestOutboundModel.class);
        when(payload.getCode()).thenReturn("RET123");
        Observable<ResponseEntity<Map>> mockObs = Observable.just(ResponseEntity.ok(new HashMap<>()));
        when(facade.send(any(SyncParameters.class))).thenReturn(mockObs);

        Observable<ResponseEntity<Map>> result = service.sendReturnOrder(payload);
        assertNotNull(result);
        verify(facade).send(any(SyncParameters.class));
    }

    @Test
    void testSendAdnocOutboundOverduePaymentTransaction() {
        TestAdnocSapCpiOutboundServiceImpl service = new TestAdnocSapCpiOutboundServiceImpl();
        FlexibleSearchService flex = mock(FlexibleSearchService.class);
        service.setFlexibleSearchService(flex);
        OutboundServiceFacade facade = mock(OutboundServiceFacade.class);
        service.setOutboundServiceFacade(facade);

        ConsumedDestinationModel dest = new ConsumedDestinationModel();
        dest.setId("adnocOverduePaymentOutboundDestination");
        IntegrationObjectModel io = new IntegrationObjectModel();
        io.setCode("AdnocOutboundOverduePaymentTransaction");
        when(flex.getModelByExample(any(ConsumedDestinationModel.class))).thenReturn(dest);
        when(flex.getModelByExample(any(IntegrationObjectModel.class))).thenReturn(io);

        AdnocOutboundOverduePaymentTransactionModel payload = mock(AdnocOutboundOverduePaymentTransactionModel.class);
        when(payload.getCustomer()).thenReturn("CUST123");
        Observable<ResponseEntity<Map>> mockObs = Observable.just(ResponseEntity.ok(new HashMap<>()));
        when(facade.send(any(SyncParameters.class))).thenReturn(mockObs);

        Observable<ResponseEntity<Map>> result = service.sendAdnocOutboundOverduePaymentTransaction(payload);
        assertNotNull(result);
        verify(facade).send(any(SyncParameters.class));
    }

    @Test
    void testGetAndSetFlexibleSearchService() {
        TestAdnocSapCpiOutboundServiceImpl service = new TestAdnocSapCpiOutboundServiceImpl();
        FlexibleSearchService flex = mock(FlexibleSearchService.class);
        service.setFlexibleSearchService(flex);
        assertEquals(flex, service.getFlexibleSearchService());
    }
}
