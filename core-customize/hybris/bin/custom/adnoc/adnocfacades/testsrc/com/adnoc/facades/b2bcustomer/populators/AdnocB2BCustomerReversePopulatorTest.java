package com.adnoc.facades.b2bcustomer.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.Mockito.*;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BCustomerReversePopulatorTest {

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private ModelService modelService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private CountryModel countryModel;

    @Mock
    private B2BUnitData b2BUnitData;

    @Mock
    private RegionModel regionModel;

    @Mock
    private DocumentMediaModel documentMediaModel;

    @Mock
    private B2BCustomerModel b2bCustomerModel;

    @Mock
    private CustomerNameStrategy customerNameStrategy;

    @Mock

    private B2BUnitService<B2BUnitModel, UserModel> b2BUnitService;
    @Mock
    private B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService;
    @Mock
    private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;


    @InjectMocks
    private AdnocB2BCustomerReversePopulator populator = new AdnocB2BCustomerReversePopulator();

    @Test
    public void populateAllFields() throws IOException {
        populator.setCustomerNameStrategy(customerNameStrategy);
        populator.setAllowedUploadedFormats("pdf,jpg,png");
        populator.setB2BUnitService(b2BUnitService);
        populator.setB2BCommerceB2BUserGroupService(b2BCommerceB2BUserGroupService);
        populator.setB2BUserGroupsLookUpStrategy(b2BUserGroupsLookUpStrategy);
        CustomerData customerData = new CustomerData();
        customerData.setPreferredCommunicationChannel("EMAIL");
        customerData.setFirstName("John");
        customerData.setLastName("Doe");
        customerData.setGender("MALE");
        customerData.setCountryOfOrigin("UAE");
        customerData.setNationality("UAE");
        customerData.setIdentityType("EMIRATES_ID");
        customerData.setIdentificationNumber("ID123");
        customerData.setIdentificationValidFrom(null);
        customerData.setIdentificationValidTo(null);
        customerData.setDesignation("MANAGER");
        customerData.setMobileNumber("0501234567");
        customerData.setTelephone("024567890");
        customerData.setCompanyAddressStreet("Main St");
        customerData.setCompanyAddressStreetLine2("Suite 5");
        customerData.setCompanyAddressCountryIso("AE");
        customerData.setCompanyAddressRegion("DXB");
        customerData.setCompanyAddressCity("Dubai");
        customerData.setUid("user123");

        Mockito.when(b2BUnitData.getUid()).thenReturn("unit123");
        customerData.setUnit(b2BUnitData);

        customerData.setIdentificationNumberDocument(multipartFile);


        Mockito.when(enumerationService.getEnumerationValue(Gender.class, "MALE"))
                .thenReturn(Gender.MALE);



        Mockito.when(commonI18NService.getCountry("AE")).thenReturn(countryModel);
        Mockito.when(commonI18NService.getRegion(countryModel, "DXB")).thenReturn(regionModel);

        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("doc.pdf");
        Mockito.when(multipartFile.getContentType()).thenReturn("application/pdf");
        Mockito.when(multipartFile.getBytes()).thenReturn("dummy data".getBytes());

        Mockito.when(modelService.create(DocumentMediaModel.class)).thenReturn(documentMediaModel);

        populator.populate(customerData, b2bCustomerModel);


        verify(b2bCustomerModel).setFirstName("John");
        verify(b2bCustomerModel).setLastName("Doe");
        verify(b2bCustomerModel).setGender(Gender.MALE);

        verify(b2bCustomerModel).setIdentificationNumber("ID123");

        verify(b2bCustomerModel).setMobileNumber("0501234567");
        verify(b2bCustomerModel).setTelephone("024567890");
        verify(b2bCustomerModel).setCompanyAddressStreet("Main St");
        verify(b2bCustomerModel).setCompanyAddressStreetLine2("Suite 5");
        verify(b2bCustomerModel).setCompanyAddressCountry(countryModel);
        verify(b2bCustomerModel).setCompanyAddressRegion(regionModel);
        verify(b2bCustomerModel).setCompanyAddressCity("Dubai");

        verify(b2bCustomerModel, never()).setActive(false);
        verify(b2bCustomerModel, never()).setLoginDisabled(true);
    }

    @Test
    public void populateShouldDisableLogin() {
        CustomerData customerData = new CustomerData();
        customerData.setUid("");
        Mockito.when(b2BUnitData.getUid()).thenReturn("unit123");
        customerData.setUnit(b2BUnitData);
        populator.setCustomerNameStrategy(customerNameStrategy);
        populator.setB2BCommerceB2BUserGroupService(b2BCommerceB2BUserGroupService);
        populator.setB2BUserGroupsLookUpStrategy(b2BUserGroupsLookUpStrategy);
        populator.populate(customerData, b2bCustomerModel);

        verify(b2bCustomerModel).setActive(false);
        verify(b2bCustomerModel).setLoginDisabled(true);
    }
}
