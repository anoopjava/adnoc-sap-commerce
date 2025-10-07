package com.adnoc.service.user.creditlimit.impl;

import com.adnoc.facades.company.request.data.AdnocCreditLimitRequestData;
import com.adnoc.facades.company.response.data.AdnocCreditLimitResponseData;
import com.adnoc.facades.creditsimulation.data.AdnocCreditSimulationRequestData;
import com.adnoc.facades.creditsimulation.data.AdnocCreditSimulationResponseData;
import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocCreditLimitServiceImplTest
{

    @InjectMocks
    private AdnocCreditLimitServiceImpl adnocCreditLimitService=new AdnocCreditLimitServiceImpl();

    @Mock
    private AdnocCreditLimitRequestData adnocCreditLimitRequestData= Mockito.mock(AdnocCreditLimitRequestData.class);

    @Mock
    private AdnocCreditSimulationRequestData adnocCreditSimulationRequestData=Mockito.mock(AdnocCreditSimulationRequestData.class);

    @Mock
    private AdnocRestIntegrationService adnocRestIntegrationService=Mockito.mock(AdnocRestIntegrationService.class);

    @Test
    void testGetCreditLimitResponse()
    {
        adnocCreditLimitService.setAdnocRestIntegrationService(adnocRestIntegrationService);
        Mockito.when(adnocRestIntegrationService.restIntegration(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new AdnocCreditLimitResponseData());
        AdnocCreditLimitResponseData adnocCreditLimitResponseData = adnocCreditLimitService.getCreditLimitResponse(adnocCreditLimitRequestData);
        Assertions.assertThat(adnocCreditLimitResponseData).isNotNull();
    }

    @Test
    void testGetCreditSimulationResponse()
    {
        adnocCreditLimitService.setAdnocRestIntegrationService(adnocRestIntegrationService);
        Mockito.when(adnocRestIntegrationService.restIntegration(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new AdnocCreditSimulationResponseData());

        AdnocCreditSimulationResponseData adnocCreditSimulationResponseData = adnocCreditLimitService.getCreditSimulationResponse(adnocCreditSimulationRequestData);
        Assertions.assertThat(adnocCreditSimulationResponseData).isNotNull();
    }
}