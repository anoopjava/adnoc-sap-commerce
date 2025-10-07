package com.adnoc.service.sms;

import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import com.adnoc.service.rest.integration.SmsRequestData;
import com.adnoc.service.rest.integration.SmsResponseData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocSmsServiceImplTest
{

    @InjectMocks
    private AdnocSmsServiceImpl adnocSmsService=Mockito.mock(AdnocSmsServiceImpl.class);

    @Mock
    private AdnocRestIntegrationService adnocRestIntegrationService= Mockito.mock(AdnocRestIntegrationService.class);


    @Test
    void testSendSms()
    {
        adnocSmsService.setAdnocRestIntegrationService(adnocRestIntegrationService);
        Mockito.when(adnocRestIntegrationService.restIntegration(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new SmsResponseData());
        SmsRequestData smsRequestData=new SmsRequestData();
        smsRequestData.setMobile_numbers(new ArrayList<>());
        SmsResponseData smsResponseData=adnocSmsService.sendSms(new SmsRequestData());
        Assertions.assertThat(smsResponseData).isNotNull();
    }
}