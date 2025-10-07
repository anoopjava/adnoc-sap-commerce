package com.adnoc.service.soa;

import com.adnoc.service.data.AdnocDownloadStatementRequestData;
import com.adnoc.service.data.AdnocDownloadStatementResponseData;
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
class AdnocDownloadStatementServiceImplTest
{
    @InjectMocks
    private AdnocDownloadStatementServiceImpl adnocDownloadStatementService=new AdnocDownloadStatementServiceImpl();

    @Mock
    private AdnocRestIntegrationService adnocRestIntegrationService= Mockito.mock(AdnocRestIntegrationService.class);

    @Test
    void testGetDownloadStatementResponse()
    {
        adnocDownloadStatementService.setAdnocRestIntegrationService(adnocRestIntegrationService);
        AdnocDownloadStatementRequestData adnocDownloadStatementRequestData=new AdnocDownloadStatementRequestData();
        adnocDownloadStatementRequestData.setB2bUnitUid("testUid");
        Mockito.when(adnocRestIntegrationService.restIntegration(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(new AdnocDownloadStatementResponseData());
        AdnocDownloadStatementResponseData adnocDownloadStatementResponseData= adnocDownloadStatementService.getDownloadStatementResponse(adnocDownloadStatementRequestData);
        Assertions.assertThat(adnocDownloadStatementResponseData).isNotNull();
    }
}