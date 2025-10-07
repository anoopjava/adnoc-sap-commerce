package com.adnoc.facades.user.creditlimit.impl;

import com.adnoc.facades.company.request.data.AdnocCreditLimitRequestData;
import com.adnoc.facades.company.response.data.AdnocCreditLimitData;
import com.adnoc.facades.company.response.data.AdnocCreditLimitResponseData;
import com.adnoc.facades.creditsimulation.data.AdnocCreditSimulationRequestData;
import com.adnoc.facades.creditsimulation.data.AdnocCreditSimulationResponseData;
import com.adnoc.facades.creditsimulation.data.ECheckConfirmation;
import com.adnoc.facades.creditsimulation.data.MessageType;
import com.adnoc.service.exception.AdnocS4HanaException;
import com.adnoc.service.user.creditlimit.AdnocCreditLimitService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocCreditLimitFacadeImplTest
{
    @InjectMocks
    private AdnocCreditLimitFacadeImpl adnocCreditLimitFacade = new AdnocCreditLimitFacadeImpl();

    @Mock
    private AdnocCreditLimitResponseData adnocCreditLimitResponseData;

    @Mock
    private AdnocCreditLimitData adnocCreditLimitData;

    @Mock
    private AdnocCreditLimitService adnocCreditLimitService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private Configuration configuration;

    @Test
    public void testGetCreditLimitDetailsValidResponse()
    {
        adnocCreditLimitFacade.setAdnocCreditLimitService(adnocCreditLimitService);

        final AdnocCreditLimitRequestData adnocCreditLimitRequestData = mock(AdnocCreditLimitRequestData.class);
        Mockito.when(adnocCreditLimitRequestData.getB2bUnitUid()).thenReturn("300000Payer");
        Mockito.when(adnocCreditLimitService.getCreditLimitResponse(adnocCreditLimitRequestData)).thenReturn(adnocCreditLimitResponseData);

        Mockito.when(adnocCreditLimitResponseData.getB2BCreditLimit()).thenReturn(adnocCreditLimitData);
        Mockito.when(adnocCreditLimitData.getMsgType()).thenReturn("S");
        final AdnocCreditLimitResponseData creditLimitResponseData = adnocCreditLimitFacade.getCreditLimitDetails(adnocCreditLimitRequestData);

        assertNotNull(creditLimitResponseData);
        verify(adnocCreditLimitService).getCreditLimitResponse(adnocCreditLimitRequestData);
    }

    @Test
    public void testCreditSimulationCheckValidResponse()
    {
        adnocCreditLimitFacade.setConfigurationService(configurationService);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getBoolean(AdnocCreditLimitFacadeImpl.ADNOC_CREDITSIMULATION_CHECK_MOCK_ENABLED, false)).thenReturn(false);
        Mockito.when(configuration.getString(AdnocCreditLimitFacadeImpl.ADNOC_CREDITSIMULATION_CREDITSEGMENT)).thenReturn("9300");

        final CurrencyModel currency = new CurrencyModel();
        currency.setIsocode("AED");
        Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

        final AdnocCreditSimulationRequestData adnocCreditSimulationRequestData = new AdnocCreditSimulationRequestData();
        adnocCreditSimulationRequestData.setPayer("0030000064");
        adnocCreditSimulationRequestData.setCurrency(currency.getIsocode());
        adnocCreditSimulationRequestData.setCreditSegment("9300");

        final AdnocCreditSimulationResponseData adnocCreditSimulationResponseData1 = new AdnocCreditSimulationResponseData();
        adnocCreditSimulationResponseData1.setMessageType(MessageType.S);
        adnocCreditSimulationResponseData1.setECheckConfirmation(ECheckConfirmation.T);
        adnocCreditSimulationResponseData1.setPayer("0030000064");
        adnocCreditSimulationResponseData1.setCurrency("AED");
        adnocCreditSimulationResponseData1.setCreditSegment("9300");

        Mockito.when(adnocCreditLimitService.getCreditSimulationResponse(Mockito.any(AdnocCreditSimulationRequestData.class)))
                .thenReturn(adnocCreditSimulationResponseData1);

        final Boolean result = adnocCreditLimitFacade.creditSimulationCheck("0030000064");

        assertNotNull(result);
        assertTrue(result);
    }

    @Test(expected = AdnocS4HanaException.class)
    public void testCreditSimulationCheckInvalidResponse()
    {
        adnocCreditLimitFacade.setConfigurationService(configurationService);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getBoolean(AdnocCreditLimitFacadeImpl.ADNOC_CREDITSIMULATION_CHECK_MOCK_ENABLED, false)).thenReturn(false);

        final CurrencyModel currency = new CurrencyModel();
        currency.setIsocode("AED");
        Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

        final AdnocCreditSimulationRequestData adnocCreditSimulationRequestData = new AdnocCreditSimulationRequestData();
        adnocCreditSimulationRequestData.setPayer("0030000064");
        adnocCreditSimulationRequestData.setCurrency(currency.getIsocode());
        adnocCreditSimulationRequestData.setCreditSegment("9300");
        adnocCreditLimitFacade.creditSimulationCheck("0030000064");
    }

    @Test(expected = AdnocS4HanaException.class)
    public void testCreditSimulationCheckNullResponse()
    {
        adnocCreditLimitFacade.setConfigurationService(configurationService);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getBoolean(AdnocCreditLimitFacadeImpl.ADNOC_CREDITSIMULATION_CHECK_MOCK_ENABLED, false)).thenReturn(false);

        final CurrencyModel currency = new CurrencyModel();
        currency.setIsocode("AED");
        Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(currency);

        Mockito.when(adnocCreditLimitService.getCreditSimulationResponse(any(AdnocCreditSimulationRequestData.class)))
                .thenReturn(null);
        adnocCreditLimitFacade.creditSimulationCheck("0030000064");
    }
}
