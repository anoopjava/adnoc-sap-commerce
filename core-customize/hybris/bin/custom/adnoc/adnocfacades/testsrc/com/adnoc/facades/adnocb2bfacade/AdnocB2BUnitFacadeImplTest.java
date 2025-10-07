package com.adnoc.facades.adnocb2bfacade;

import com.adnoc.facades.b2b.unit.data.AdnocB2BUnitRegistrationData;
import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.enums.*;
import com.adnoc.service.model.AdnocB2BUnitRegistrationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservices.core.user.data.AddressDataList;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@UnitTest

@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BUnitFacadeImplTest
{

    @Mock
    private AdnocB2BUnitService adnocB2BUnitService;

    @Mock
    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService;

    @Mock
    private AbstractConverter<B2BUnitModel, B2BUnitData> b2BUnitConverter;

    @Mock
    private AbstractConverter<AddressModel, AddressData> addressConverter;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private Converter<IncoTerms, IncoTermsData> converter;

    @InjectMocks
    private AdnocB2BUnitFacadeImpl facade;

    @Mock
    private UserService userService;

    @Test
    public void testGetB2BUnitsWithValidCustomer()
    {
        final AdnocB2BUnitFacadeImpl b2BUnitFacade = new AdnocB2BUnitFacadeImpl();
        MockitoAnnotations.openMocks(this);

        final B2BCustomerModel mockCustomer = mock(B2BCustomerModel.class);
        final B2BUnitModel mockUnit = mock(B2BUnitModel.class);
        final B2BUnitData mockData = mock(B2BUnitData.class);

        final Set<B2BUnitModel> unitSet = Set.of(mockUnit);
        final List<B2BUnitData> convertedList = List.of(mockData);

        Mockito.when(b2BCustomerService.getCurrentB2BCustomer()).thenReturn(mockCustomer);
        Mockito.when(adnocB2BUnitService.getB2BUnits(mockCustomer, PartnerFunction.PY)).thenReturn(unitSet);
        Mockito.when(b2BUnitConverter.convertAll(unitSet)).thenReturn(convertedList);

        b2BUnitFacade.setAdnocB2BUnitService(adnocB2BUnitService);
        b2BUnitFacade.setB2BUnitConverter(b2BUnitConverter);
        b2BUnitFacade.setB2BCustomerService(b2BCustomerService);

        final List<B2BUnitData> result = b2BUnitFacade.getB2BUnits(PartnerFunction.PY);

        assertEquals(1, result.size());
        verify(b2BUnitConverter).convertAll(unitSet);
    }

    @Test
    public void testGetShippingAddressList()
    {
        final AdnocB2BUnitFacadeImpl bUnitFacade = new AdnocB2BUnitFacadeImpl();
        MockitoAnnotations.openMocks(this);

        final B2BUnitModel parentUnit = mock(B2BUnitModel.class);
        final B2BUnitModel childUnit = mock(B2BUnitModel.class);
        final AddressModel addressModel = mock(AddressModel.class);
        final AddressData addressData = mock(AddressData.class);
        final IncoTerms incoTerms = mock(IncoTerms.class);
        final SAPSalesOrganizationModel salesOrg = mock(SAPSalesOrganizationModel.class); // Actual sales org model

        final Set<B2BUnitModel> childUnits = Set.of(childUnit);
        final Set<AddressModel> addresses = Set.of(addressModel);

        Mockito.when(adnocB2BUnitService.getCurrentB2BUnit()).thenReturn(parentUnit);
        Mockito.when(adnocB2BUnitService.getChildB2BUnits(parentUnit, PartnerFunction.SH)).thenReturn(childUnits);


        Mockito.when(childUnit.getSalesOrg()).thenReturn(salesOrg);
        Mockito.when(salesOrg.getDivision()).thenReturn("23");

        Mockito.when(childUnit.getIncoTerms()).thenReturn(incoTerms);
        Mockito.when(enumerationService.getEnumerationValue(IncoTerms._TYPECODE, "DPU")).thenReturn(incoTerms);
        Mockito.when(childUnit.getShippingAddresses()).thenReturn(addresses);
        Mockito.when(addressConverter.convertAll(addresses)).thenReturn(List.of(addressData));

        // Set dependencies
        bUnitFacade.setAdnocB2BUnitService(adnocB2BUnitService);
        bUnitFacade.setAddressConverter(addressConverter);
        bUnitFacade.setEnumerationService(enumerationService);


        // Act
        final AddressDataList result = bUnitFacade.getShippingAddressList("23", "DPU");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getAddresses().size());
        verify(addressConverter).convertAll(addresses);
    }

    @Test
    public void testGetIncoTermsExcludesPickup()
    {
        // Setup facade and inject mocks
        final AdnocB2BUnitFacadeImpl adnocB2BUnitFacade = new AdnocB2BUnitFacadeImpl();
        adnocB2BUnitFacade.setAdnocB2BUnitService(adnocB2BUnitService);
        adnocB2BUnitFacade.setIncoTermsTypeDataConverter(converter);


        final B2BUnitModel unitModel = mock(B2BUnitModel.class);
        lenient().when(unitModel.getIncoTerms()).thenReturn(IncoTerms.PICKUP);

        final Set<B2BUnitModel> units = Set.of(unitModel);

        // Stub dependencies
        when(adnocB2BUnitService.getCurrentB2BUnit()).thenReturn(mock(B2BUnitModel.class));
        when(adnocB2BUnitService.getChildB2BUnits(any(), eq(PartnerFunction.SH))).thenReturn(units);
        when(converter.convertAll(Collections.emptySet())).thenReturn(Collections.emptyList());

        // Act
        final List<IncoTermsData> result = adnocB2BUnitFacade.getIncoTerms("23", false);  // false = exclude pickup

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(converter).convertAll(Collections.emptySet());

    }

    @Test
    public void testPopulateInfoWithAllFields() throws Exception
    {
        final AdnocB2BUnitRegistrationData data = new AdnocB2BUnitRegistrationData();
        data.setTitleCode("MR");
        data.setFirstName("John");
        data.setLastName("Doe");
        data.setGender("MALE");
        data.setCountryOfOrigin("UAE");
        data.setNationality("UAE");
        data.setIdentityType("EMIRATES_ID");
        data.setIdentificationNumber("123456");
        data.setIdentificationValidFrom(new Date());
        data.setIdentificationValidTo(new Date());
        data.setDesignation("MANAGER");
        data.setEmail("john.doe@example.com");
        data.setTelephone("024567890");
        data.setMobileNumber("0501234567");

        final TitleModel titleMock = mock(TitleModel.class);
        when(userService.getTitleForCode("MR")).thenReturn(titleMock);
        when(enumerationService.getEnumerationValue(Gender.class, "MALE")).thenReturn(Gender.MALE);
        when(enumerationService.getEnumerationValue(Nationality.class, "UAE")).thenReturn(mock(Nationality.class));
        when(enumerationService.getEnumerationValue(IdentityType.class, "EMIRATES_ID")).thenReturn(mock(IdentityType.class));
        when(enumerationService.getEnumerationValue(Designation.class, "MANAGER")).thenReturn(mock(Designation.class));

        final AdnocB2BUnitRegistrationModel model = new AdnocB2BUnitRegistrationModel();

        final Method method = AdnocB2BUnitFacadeImpl.class.getDeclaredMethod("populateCustomerInfo", AdnocB2BUnitRegistrationModel.class, AdnocB2BUnitRegistrationData.class);
        method.setAccessible(true);
        method.invoke(facade, model, data);

        assertEquals("John", model.getFirstName());
        assertEquals("Doe", model.getLastName());
        assertEquals("john.doe@example.com", model.getEmail());
        assertEquals("024567890", model.getTelephone());
        assertEquals("0501234567", model.getMobileNumber());
        assertEquals(titleMock, model.getTitle());
        assertEquals("123456", model.getIdentificationNumber());
    }


    // Minimal SalesOrg mock class to simulate getDivision()
    private static class SalesOrgMock
    {
        private final String division;

        public SalesOrgMock(final String division)
        {
            this.division = division;
        }

        public String getDivision()
        {
            return division;
        }
    }
}
