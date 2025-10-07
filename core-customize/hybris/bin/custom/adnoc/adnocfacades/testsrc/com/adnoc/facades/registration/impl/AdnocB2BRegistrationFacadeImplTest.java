package com.adnoc.facades.registration.impl;

import com.adnoc.facades.b2b.data.AdnocB2BRegistrationData;
import com.adnoc.service.model.AdnocSoldToB2BRegistrationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BRegistrationFacadeImplTest
{
    @InjectMocks
    private AdnocB2BRegistrationFacadeImpl adnocB2BRegistrationFacadeImpl;
    @Mock
    private ModelService modelService;
    @Mock
    private UserService userService;

    @Test
    public void registerTest()
    {
        final AdnocB2BRegistrationData adnocB2BRegistrationData = createB2BRegistrationData();

        final TitleModel titleModel = new TitleModel();
        titleModel.setCode("Mr.");
        Mockito.when(userService.getTitleForCode(adnocB2BRegistrationData.getTitleCode())).thenReturn(titleModel);

        final AdnocSoldToB2BRegistrationModel adnocSoldToB2BRegistrationModel = new AdnocSoldToB2BRegistrationModel();
        Mockito.when(modelService.create(AdnocSoldToB2BRegistrationModel.class)).thenReturn(adnocSoldToB2BRegistrationModel);

        final DocumentMediaModel documentMediaModel = new DocumentMediaModel();
        Mockito.when(modelService.create(DocumentMediaModel.class)).thenReturn(documentMediaModel);

        adnocB2BRegistrationFacadeImpl.toRegistrationModel(adnocB2BRegistrationData);
    }

    private AdnocB2BRegistrationData createB2BRegistrationData()
    {
        final AdnocB2BRegistrationData adnocB2BRegistrationData = new AdnocB2BRegistrationData();
        adnocB2BRegistrationData.setFirstName("testname");
        adnocB2BRegistrationData.setLastName("testname");
        adnocB2BRegistrationData.setEmail("test@gmail.com");
        adnocB2BRegistrationData.setTelephone("3636636363");
        adnocB2BRegistrationData.setMobileNumber("367272772");
        adnocB2BRegistrationData.setPrimaryProduct("LPG");
        adnocB2BRegistrationData.setGender("Male");
        adnocB2BRegistrationData.setFaxNumber("testno");
        adnocB2BRegistrationData.setName("test");
        adnocB2BRegistrationData.setVatId("vatNo");
        adnocB2BRegistrationData.setCompanyAddressStreet("test strret");
        adnocB2BRegistrationData.setCompanyAddressStreetLine2("test2 street");

        adnocB2BRegistrationData.setCompanyAddressCity("testcity");
        adnocB2BRegistrationData.setCompanyAddressPostalCode("test62772");
        adnocB2BRegistrationData.setCompanyName("testcompany");
        adnocB2BRegistrationData.setCompanyAddressStreet("test city");
        adnocB2BRegistrationData.setCompanyAddressStreetLine2("test 3939");
        adnocB2BRegistrationData.setCompanyMobileNumber("test no");
        adnocB2BRegistrationData.setCompanyEmail("testc@gmail.com");
        adnocB2BRegistrationData.setCompanyPhoneNumber("3737737737");
        adnocB2BRegistrationData.setCompanyAddressPostalCode("test PO Box");
        adnocB2BRegistrationData.setCompanyWebsite("test adnoc");
        adnocB2BRegistrationData.setTradeLicenseNumber("TradeLicenseNumber");
        adnocB2BRegistrationData.setNationality("EMIRATI");
        adnocB2BRegistrationData.setPreferredCommunicationChannel("EMAIL");

        final MediaData mediaData = new MediaData();
        mediaData.setCode("test");
        mediaData.setUrl("test");
        adnocB2BRegistrationData.setOtherDocument((MultipartFile) mediaData);

        adnocB2BRegistrationData.setTitleCode("MR.");
        adnocB2BRegistrationData.setCompanyAddressRegion("CN");
        adnocB2BRegistrationData.setCompanyAddressCountryIso("CN-32");

        return adnocB2BRegistrationData;
    }

    @Test
    public void testGetPrimaryProducts()
    {
        Assertions.assertThat(adnocB2BRegistrationFacadeImpl.getPrimaryProducts()).isNotEmpty();
    }

}
