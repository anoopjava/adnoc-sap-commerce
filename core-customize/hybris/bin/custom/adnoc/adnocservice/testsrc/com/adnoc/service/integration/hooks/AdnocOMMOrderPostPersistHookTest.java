package com.adnoc.service.integration.hooks;

import com.adnoc.service.config.AdnocConfigService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocOMMOrderPostPersistHookTest {

    @Test
    public void executeConfirm() {
        AdnocOMMOrderPostPersistHook hook = new AdnocOMMOrderPostPersistHook();

        ModelService modelService = mock(ModelService.class);
        BusinessProcessService businessProcessService = mock(BusinessProcessService.class);
        BaseSiteService baseSiteService = mock(BaseSiteService.class);
        CommonI18NService commonI18NService = mock(CommonI18NService.class);
        BaseStoreService baseStoreService = mock(BaseStoreService.class);
        EnumerationService enumerationService = mock(EnumerationService.class);
        AdnocConfigService adnocConfigService = mock(AdnocConfigService.class);

        OrderModel orderModel = mock(OrderModel.class);
        OrderProcessModel orderProcessModel = mock(OrderProcessModel.class);
        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        LanguageModel languageModel = mock(LanguageModel.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        UserModel userModel = mock(UserModel.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        PersistenceContext context = mock(PersistenceContext.class);

        hook.setAdnocConfigService(adnocConfigService);
        hook.setBaseSiteService(baseSiteService);
        hook.setModelService(modelService);
        hook.setBusinessProcessService(businessProcessService);
        hook.setEnumerationService(enumerationService);
        hook.setCommonI18NService(commonI18NService);
        hook.setBaseStoreService(baseStoreService);

        when(orderModel.getSapOrderStatus()).thenReturn(SAPOrderStatus.CONFIRMED);
        when(adnocConfigService.getAdnocSapIntegrationCodeMap(any(), any(), anyString())).thenReturn("SapOrderStatus");
        when(enumerationService.getEnumerationValue(anyString(), anyString())).thenReturn(OrderStatus.CONFIRMED);
        when(orderModel.getCode()).thenReturn("1234");
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getStatus()).thenReturn(OrderStatus.CONFIRMED);
        when(baseSiteService.getBaseSiteForUID(anyString())).thenReturn(baseSiteModel);
        when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(baseStoreService.getBaseStoreForUid(anyString())).thenReturn(baseStoreModel);
        when(businessProcessService.createProcess(anyString(), anyString())).thenReturn(orderProcessModel);

        hook.execute(orderModel, context);

        verify(baseStoreService, times(1)).getBaseStoreForUid("adnoc");
        verify(businessProcessService).startProcess(orderProcessModel);
    }

    @Test
    public void executePC() {
        AdnocOMMOrderPostPersistHook hook = new AdnocOMMOrderPostPersistHook();

        ModelService modelService = mock(ModelService.class);
        BusinessProcessService businessProcessService = mock(BusinessProcessService.class);
        BaseSiteService baseSiteService = mock(BaseSiteService.class);
        CommonI18NService commonI18NService = mock(CommonI18NService.class);
        BaseStoreService baseStoreService = mock(BaseStoreService.class);
        EnumerationService enumerationService = mock(EnumerationService.class);
        AdnocConfigService adnocConfigService = mock(AdnocConfigService.class);

        OrderModel orderModel = mock(OrderModel.class);
        OrderProcessModel orderProcessModel = mock(OrderProcessModel.class);
        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        LanguageModel languageModel = mock(LanguageModel.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        UserModel userModel = mock(UserModel.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        PersistenceContext context = mock(PersistenceContext.class);

        hook.setAdnocConfigService(adnocConfigService);
        hook.setBaseSiteService(baseSiteService);
        hook.setModelService(modelService);
        hook.setBusinessProcessService(businessProcessService);
        hook.setEnumerationService(enumerationService);
        hook.setCommonI18NService(commonI18NService);
        hook.setBaseStoreService(baseStoreService);

        when(orderModel.getSapOrderStatus()).thenReturn(SAPOrderStatus.CONFIRMED);
        when(adnocConfigService.getAdnocSapIntegrationCodeMap(any(), any(), anyString())).thenReturn("SapOrderStatus");
        when(enumerationService.getEnumerationValue(anyString(), anyString())).thenReturn(OrderStatus.CONFIRMED);
        when(orderModel.getCode()).thenReturn("1234");
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getStatus()).thenReturn(OrderStatus.PARTIAL_COMPLETED);
        when(baseSiteService.getBaseSiteForUID(anyString())).thenReturn(baseSiteModel);
        when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(baseStoreService.getBaseStoreForUid(anyString())).thenReturn(baseStoreModel);
        when(businessProcessService.createProcess(anyString(), anyString())).thenReturn(orderProcessModel);

        hook.execute(orderModel, context);

        verify(baseStoreService, times(1)).getBaseStoreForUid("adnoc");
        verify(businessProcessService).startProcess(orderProcessModel);
    }

    @Test
    public void executeCompleted() {
        AdnocOMMOrderPostPersistHook hook = new AdnocOMMOrderPostPersistHook();

        ModelService modelService = mock(ModelService.class);
        BusinessProcessService businessProcessService = mock(BusinessProcessService.class);
        BaseSiteService baseSiteService = mock(BaseSiteService.class);
        CommonI18NService commonI18NService = mock(CommonI18NService.class);
        BaseStoreService baseStoreService = mock(BaseStoreService.class);
        EnumerationService enumerationService = mock(EnumerationService.class);
        AdnocConfigService adnocConfigService = mock(AdnocConfigService.class);

        OrderModel orderModel = mock(OrderModel.class);
        OrderProcessModel orderProcessModel = mock(OrderProcessModel.class);
        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        LanguageModel languageModel = mock(LanguageModel.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        UserModel userModel = mock(UserModel.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        PersistenceContext context = mock(PersistenceContext.class);

        hook.setAdnocConfigService(adnocConfigService);
        hook.setBaseSiteService(baseSiteService);
        hook.setModelService(modelService);
        hook.setBusinessProcessService(businessProcessService);
        hook.setEnumerationService(enumerationService);
        hook.setCommonI18NService(commonI18NService);
        hook.setBaseStoreService(baseStoreService);

        when(orderModel.getSapOrderStatus()).thenReturn(SAPOrderStatus.CONFIRMED);
        when(adnocConfigService.getAdnocSapIntegrationCodeMap(any(), any(), anyString())).thenReturn("SapOrderStatus");
        when(enumerationService.getEnumerationValue(anyString(), anyString())).thenReturn(OrderStatus.CONFIRMED);
        when(orderModel.getCode()).thenReturn("1234");
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getStatus()).thenReturn(OrderStatus.COMPLETED);
        when(baseSiteService.getBaseSiteForUID(anyString())).thenReturn(baseSiteModel);
        when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(baseStoreService.getBaseStoreForUid(anyString())).thenReturn(baseStoreModel);
        when(businessProcessService.createProcess(anyString(), anyString())).thenReturn(orderProcessModel);

        hook.execute(orderModel, context);

        verify(baseStoreService, times(1)).getBaseStoreForUid("adnoc");
        verify(businessProcessService).startProcess(orderProcessModel);
    }
}
