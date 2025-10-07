package com.adnoc.facades.b2bcustomer.populators;

import com.adnoc.service.enums.Designation;
import com.adnoc.service.enums.IdentityType;
import com.adnoc.service.enums.Nationality;
import com.adnoc.service.enums.PreferredCommunicationChannel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocCustomerPopulatorTest {

    @Mock
    private CustomerNameStrategy customerNameStrategy;

    @InjectMocks
    private AdnocCustomerPopulator populator;

    @Test
    public void testPopulateWithB2BCustomerModel() {

        Mockito.when(customerNameStrategy.splitName(anyString())).thenReturn(new String[]{"First", "Last"});

        B2BCustomerModel b2bCustomer = new B2BCustomerModel();
        b2bCustomer.setName("First Last");
        b2bCustomer.setPreferredCommunicationChannel(PreferredCommunicationChannel.valueOf("EMAIL"));
        b2bCustomer.setGender(Gender.MALE);
        b2bCustomer.setNationality(Nationality.valueOf("UAE"));

        b2bCustomer.setIdentificationNumber("ID-12345");
        Date validFrom = new Date();
        Date validTo = new Date(System.currentTimeMillis() + 86400000L);
        b2bCustomer.setIdentificationValidFrom(validFrom);
        b2bCustomer.setIdentificationValidTo(validTo);

        b2bCustomer.setMobileNumber("0501234567");
        b2bCustomer.setTelephone("024567890");
        Date lastLogin = new Date();
        b2bCustomer.setLastLogin(lastLogin);
        b2bCustomer.setCompanyAddressStreet("Main St");
        b2bCustomer.setCompanyAddressStreetLine2("Suite 5");
        b2bCustomer.setCompanyAddressCity("Dubai");

        CustomerData customerData = new CustomerData();

        populator.populate(b2bCustomer, customerData);

        assertEquals("EMAIL", customerData.getPreferredCommunicationChannel());
        assertEquals("MALE", customerData.getGender());
        assertEquals("UAE", customerData.getNationality());
        assertEquals("0501234567", customerData.getMobileNumber());
        assertEquals("024567890", customerData.getTelephone());
        assertEquals(lastLogin, customerData.getLastlogin());
        assertEquals("Main St", customerData.getCompanyAddressStreet());
        assertEquals("Suite 5", customerData.getCompanyAddressStreetLine2());
        assertEquals("Dubai", customerData.getCompanyAddressCity());
    }

    @Test
    public void testPopulateWithNonB2BCustomerModel() {
        CustomerModel customerModel = new CustomerModel();
        customerModel.setName("First Last");
        Mockito.when(customerNameStrategy.splitName("First Last")).thenReturn(new String[]{"First", "Last"});

        CustomerData customerData = new CustomerData();
        populator.populate(customerModel, customerData);

        assertNull(customerData.getPreferredCommunicationChannel());
        assertNull(customerData.getGender());
        assertNull(customerData.getNationality());
        assertNull(customerData.getIdentityType());
        assertNull(customerData.getIdentificationNumber());
        assertNull(customerData.getDesignation());
        assertNull(customerData.getMobileNumber());
        assertNull(customerData.getCompanyAddressCity());
    }
}
