package com.adnoc.service.config.impl;

import com.adnoc.service.config.dao.AdnocConfigDao;
import com.adnoc.service.model.AdnocConfigModel;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import com.adnoc.service.model.AdnocSapIntegrationCodeMapModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdnocConfigServiceImplTest
{
    @InjectMocks
    private AdnocConfigServiceImpl adnocConfigService;
    @Mock
    private AdnocConfigDao adnocConfigDao;
    @Mock
    private AdnocConfigModel adnocConfigModel;
    @Mock
    private SAPSalesOrganizationModel sapSalesOrganizationModel;
    @Mock
    private AdnocSapIntegrationCodeMapModel adnocSapIntegrationCodeMapModel;
    @Mock
    private AdnocCsTicketCategoryMapModel adnocCsTicketCategoryMapModel;

    @Test
    public void testGetAdnocConfigs()
    {
        final String[] configKeys = {"key1", "key2"};
        final List<AdnocConfigModel> mockConfigList = Collections.singletonList(adnocConfigModel);
        Mockito.when(adnocConfigDao.findAdnocConfig(configKeys)).thenReturn(mockConfigList);
        final List<AdnocConfigModel> result = adnocConfigService.getAdnocConfigs(configKeys);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(adnocConfigDao).findAdnocConfig(configKeys);
    }

    @Test
    public void testGetAdnocConfig()
    {
        final String configKey = "key1";
        Mockito.when(adnocConfigDao.findAdnocConfig(configKey)).thenReturn(Collections.singletonList(adnocConfigModel));
        final AdnocConfigModel result = adnocConfigService.getAdnocConfig(configKey);
        assertNotNull(result);
        verify(adnocConfigDao).findAdnocConfig(configKey);
    }

    @Test
    public void testGetAdnocConfigValue()
    {
        final String configKey = "key1";
        final String defaultValue = "default";
        Mockito.when(adnocConfigDao.findAdnocConfig(configKey)).thenReturn(Collections.singletonList(adnocConfigModel));
        Mockito.when(adnocConfigModel.getConfigValue()).thenReturn("someValue");
        final String result = adnocConfigService.getAdnocConfigValue(configKey, defaultValue);
        assertEquals("someValue", result);
    }

    @Test
    public void testGetAdnocConfigValueDefault()
    {
        final String configKey = "key1";
        final String defaultValue = "default";
        Mockito.when(adnocConfigDao.findAdnocConfig(configKey)).thenReturn(Collections.singletonList(adnocConfigModel));
        Mockito.when(adnocConfigModel.getConfigValue()).thenReturn(null);
        final String result = adnocConfigService.getAdnocConfigValue(configKey, defaultValue);
        assertEquals(defaultValue, result);
    }

    @Test
    public void testGetAdnocConfigValueInt()
    {
        final String configKey = "key1";
        final int defaultValue = 42;
        Mockito.when(adnocConfigDao.findAdnocConfig(configKey)).thenReturn(Collections.singletonList(adnocConfigModel));
        Mockito.when(adnocConfigModel.getConfigValue()).thenReturn("123");
        final int result = adnocConfigService.getAdnocConfigValue(configKey, defaultValue);
        assertEquals(123, result);
    }

    @Test
    public void testGetSalesOrgByDivision()
    {
        final String division = "divisionX";
        Mockito.when(adnocConfigDao.getSalesOrgbyDivision(division)).thenReturn(sapSalesOrganizationModel);
        final SAPSalesOrganizationModel result = adnocConfigService.getSalesOrgbyDivision(division);
        assertNotNull(result);
        verify(adnocConfigDao).getSalesOrgbyDivision(division);
    }

    @Test
    public void testGetAdnocSapIntegrationCodeMap()
    {
        final IntegrationType integrationType = IntegrationType.OUTBOUND;
        final Class<?> objType = String.class;
        final String enumCode = "SAPCODE";
        Mockito.when(adnocConfigDao.findAdnocSapIntegrationCodeMap(integrationType, objType, enumCode))
                .thenReturn(adnocSapIntegrationCodeMapModel);
        Mockito.when(adnocSapIntegrationCodeMapModel.getSapCode()).thenReturn("SAP123");
        final String result = adnocConfigService.getAdnocSapIntegrationCodeMap(integrationType, objType, enumCode);
        assertEquals("SAP123", result);
    }

    @Test
    public void testGetAdnocCsTicketCategoryMap()
    {
        Mockito.when(adnocConfigDao.findAdnocCsTicketCategoryMap()).thenReturn(Collections.singletonList(adnocCsTicketCategoryMapModel));
        final List<AdnocCsTicketCategoryMapModel> result = adnocConfigService.getAdnocCsTicketCategoryMap();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(adnocConfigDao).findAdnocCsTicketCategoryMap();
    }

    @Test
    public void testGetAdnocCsTicketCategoryMapById()
    {
        final String csTicketCategoryMapId = "id123";
        Mockito.when(adnocConfigDao.findAdnocCsTicketCategoryMap(csTicketCategoryMapId)).thenReturn(adnocCsTicketCategoryMapModel);
        final AdnocCsTicketCategoryMapModel result = adnocConfigService.getAdnocCsTicketCategoryMap(csTicketCategoryMapId);
        assertNotNull(result);
        verify(adnocConfigDao).findAdnocCsTicketCategoryMap(csTicketCategoryMapId);
    }
}
