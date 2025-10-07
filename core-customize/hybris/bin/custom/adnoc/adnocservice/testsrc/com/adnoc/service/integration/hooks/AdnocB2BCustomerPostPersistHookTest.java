package com.adnoc.service.integration.hooks;

import com.adnoc.service.enums.PartnerFunction;
import com.adnoc.service.model.AdnocB2BCustomerCreationProcessModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.enums.RetentionState;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdnocB2BCustomerPostPersistHookTest
{

    @InjectMocks
    private AdnocB2BCustomerPostPersistHook hook;

    @Mock
    private ModelService modelService;

    @Mock
    private BusinessProcessService businessProcessService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private BaseStoreService baseStoreService;

    @Mock
    private PersistenceContext persistenceContext;

    private void initMocksAndSetup()
    {
        MockitoAnnotations.openMocks(this);
        hook = new AdnocB2BCustomerPostPersistHook();
        hook.setModelService(modelService);
        hook.setBusinessProcessService(businessProcessService);
        hook.setBaseSiteService(baseSiteService);
        hook.setCommonI18NService(commonI18NService);
        hook.setBaseStoreService(baseStoreService);
    }

    @Test
    void testExecute_NullRetentionState_ShipToCustomer()
    {
        initMocksAndSetup();

        B2BCustomerModel customer = spy(new B2BCustomerModel());
        customer.setUid("shipToCustomer");
        B2BUnitModel shipToUnit = mock(B2BUnitModel.class);
        when(shipToUnit.getPartnerFunction()).thenReturn(PartnerFunction.SH);
        customer.setGroups(new HashSet<>(Collections.singletonList(shipToUnit)));
        customer.setRetentionState(null);

        AdnocB2BCustomerCreationProcessModel adnocB2BCustomerCreationProcessModel = mock(AdnocB2BCustomerCreationProcessModel.class);
        when(businessProcessService.createProcess(anyString(), anyString())).thenReturn(adnocB2BCustomerCreationProcessModel);
        when(baseSiteService.getBaseSiteForUID(anyString())).thenReturn(mock(BaseSiteModel.class));
        when(commonI18NService.getCurrentLanguage()).thenReturn(mock(LanguageModel.class));
        when(commonI18NService.getCurrentCurrency()).thenReturn(mock(CurrencyModel.class));

        hook.execute(customer, persistenceContext);

        verify(customer).setActive(false);
        verify(customer).setLoginDisabled(true);
        verify(modelService, times(2)).save(customer);
        verify(businessProcessService).createProcess(anyString(), eq("adnocShipToCreatedB2BCustomerProcess"));
        verify(businessProcessService).startProcess(any());
        assertEquals(RetentionState.PROCESSED, customer.getRetentionState());
    }

    @Test
    void testExecute_NullRetentionState_NonShipToCustomer()
    {
        initMocksAndSetup();

        B2BCustomerModel customer = spy(new B2BCustomerModel());
        customer.setUid("normalCustomer");
        customer.setGroups(Collections.emptySet());
        customer.setRetentionState(null);

        String generatedPassword = "password123";
        AdnocB2BCustomerCreationProcessModel processModel = mock(AdnocB2BCustomerCreationProcessModel.class);
        when(businessProcessService.createProcess(anyString(), eq("adnocB2BCustomerCreationInboundProcess"))).thenReturn(processModel);

        hook.execute(customer, persistenceContext);

        verify(customer).setPassword(generatedPassword);
        verify(customer).setActive(true);
        verify(customer).setLoginDisabled(false);
        verify(modelService, times(2)).save(customer);
        verify(businessProcessService).createProcess(anyString(), eq("adnocB2BCustomerCreationInboundProcess"));
        verify(businessProcessService).startProcess(processModel);
        assertEquals(RetentionState.PROCESSED, customer.getRetentionState());
    }

    @Test
    void testExecute_WithRetentionState_ShipToCustomer()
    {
        initMocksAndSetup();

        B2BCustomerModel customer = spy(new B2BCustomerModel());
        customer.setUid("shipToWithRetention");
        B2BUnitModel shipToUnit = mock(B2BUnitModel.class);
        when(shipToUnit.getPartnerFunction()).thenReturn(PartnerFunction.SH);
        customer.setGroups(new HashSet<>(Collections.singletonList(shipToUnit)));
        customer.setRetentionState(RetentionState.PROCESSED);
        hook.execute(customer, persistenceContext);
        verify(customer).setActive(false);
        verify(customer).setLoginDisabled(true);
        verify(modelService).save(customer);
    }

    @Test
    void testExecute_ItemNotB2BCustomer()
    {
        initMocksAndSetup();
        ItemModel item = mock(ItemModel.class);
        hook.execute(item, persistenceContext);
        verifyNoInteractions(modelService);
        verifyNoInteractions(businessProcessService);
    }
}
