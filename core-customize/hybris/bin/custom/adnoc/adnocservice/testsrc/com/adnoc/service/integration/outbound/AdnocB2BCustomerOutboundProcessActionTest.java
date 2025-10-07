package com.adnoc.service.integration.outbound;

import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundService;
import com.adnoc.service.model.AdnocB2BCustomerCreationProcessModel;
import com.adnoc.service.model.AdnocOutboundB2BCustomerModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BCustomerOutboundProcessActionTest
{
    @InjectMocks
    private AdnocB2BCustomerOutboundProcessAction adnocB2BCustomerOutboundProcessAction;
    @Mock
    private AdnocSapCpiOutboundService adnocSapCpiOutboundService;
    @Mock
    private AdnocSapCpiOutboundConversionService adnocSapCpiOutboundConversionService;
    @Mock
    private AdnocOutboundB2BCustomerModel adnocOutboundB2BCustomerModel;
    @Mock
    private AdnocB2BCustomerCreationProcessModel adnocB2BCustomerCreationProcessModel;
    @Mock
    private B2BCustomerModel b2BCustomerModel;

    @Test
    public void executeAction() throws Exception
    {
        adnocB2BCustomerOutboundProcessAction.setAdnocSapCpiOutboundService(adnocSapCpiOutboundService);
        when(adnocSapCpiOutboundConversionService.convertToOutboundB2BCustomer(any())).thenReturn(adnocOutboundB2BCustomerModel);
        when(adnocB2BCustomerCreationProcessModel.getCustomer()).thenReturn(b2BCustomerModel);

        // create observable
        Map entityMap = new HashMap<>();
        Map internalMap = new HashMap<>();
        internalMap.put("responseStatus", "success");
        entityMap.put("responseStatus", internalMap);
        ResponseEntity<Map> responseEntityMap = ResponseEntity.ok(entityMap);
        Observable<ResponseEntity<Map>> responseEntityObservable = Observable.just(responseEntityMap);
        when(adnocSapCpiOutboundService.sendAdnocB2BCustomer(any())).thenReturn(responseEntityObservable);
        when(b2BCustomerModel.getUid()).thenReturn("b2bCustomerUid");
        adnocB2BCustomerOutboundProcessAction.executeAction(adnocB2BCustomerCreationProcessModel);
        verify(adnocSapCpiOutboundConversionService, times(1)).convertToOutboundB2BCustomer(b2BCustomerModel);
    }

    @Test
    public void executeActionFailed() throws Exception
    {
        adnocB2BCustomerOutboundProcessAction.setAdnocSapCpiOutboundService(adnocSapCpiOutboundService);
        when(adnocSapCpiOutboundConversionService.convertToOutboundB2BCustomer(any())).thenReturn(adnocOutboundB2BCustomerModel);
        when(adnocB2BCustomerCreationProcessModel.getCustomer()).thenReturn(b2BCustomerModel);
        when(b2BCustomerModel.getUid()).thenReturn("b2bCustomerUid");

        // create internalMap
        Map internalMap = new HashMap<>();
        internalMap.put("responseStatus", "failed");
        // create entityMap
        Map entityMap = new HashMap<>();
        entityMap.put("responseStatus", internalMap);
        // create observable
        ResponseEntity<Map> responseEntityMap = ResponseEntity.ok(entityMap);
        Observable<ResponseEntity<Map>> responseEntityObservable = Observable.just(responseEntityMap);
        when(adnocSapCpiOutboundService.sendAdnocB2BCustomer(any())).thenReturn(responseEntityObservable);

        adnocB2BCustomerOutboundProcessAction.executeAction(adnocB2BCustomerCreationProcessModel);
        verify(adnocSapCpiOutboundConversionService, times(1)).convertToOutboundB2BCustomer(b2BCustomerModel);
    }
}