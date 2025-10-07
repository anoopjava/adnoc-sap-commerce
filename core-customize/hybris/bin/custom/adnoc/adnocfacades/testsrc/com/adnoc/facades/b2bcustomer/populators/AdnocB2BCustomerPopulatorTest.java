package com.adnoc.facades.b2bcustomer.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class AdnocB2BCustomerPopulatorTest {

    @Test
    public void testPopulate_WithB2BAdminGroup() {
        AdnocB2BCustomerPopulator populator = new AdnocB2BCustomerPopulator();
        CustomerModel customerModel = new CustomerModel();
        CustomerData customerData = new CustomerData();
        customerData.setRoles(Arrays.asList("b2badmingroup"));

        populator.populate(customerModel, customerData);

        assertEquals("Super Admin", customerData.getUserRole());
    }

    @Test
    public void testPopulate_WithB2BCustomerGroup() {
        AdnocB2BCustomerPopulator populator = new AdnocB2BCustomerPopulator();
        CustomerModel customerModel = new CustomerModel();
        CustomerData customerData = new CustomerData();
        customerData.setRoles(Arrays.asList("b2bcustomergroup"));

        populator.populate(customerModel, customerData);

        assertEquals("Payer User", customerData.getUserRole());
    }

    @Test
    public void testPopulate_WithEmptyRoles() {
        AdnocB2BCustomerPopulator populator = new AdnocB2BCustomerPopulator();
        CustomerModel customerModel = new CustomerModel();
        CustomerData customerData = new CustomerData();
        customerData.setRoles(Collections.emptyList());

        populator.populate(customerModel, customerData);

        assertEquals("No Role Assigned", customerData.getUserRole());
    }

    @Test
    public void testPopulate_WithNoMatchingRole() {
        AdnocB2BCustomerPopulator populator = new AdnocB2BCustomerPopulator();
        CustomerModel customerModel = new CustomerModel();
        CustomerData customerData = new CustomerData();
        customerData.setRoles(Arrays.asList("unrelatedRole"));

        populator.populate(customerModel, customerData);

        assertEquals("No Role Assigned", customerData.getUserRole());
    }

    @Test
    public void testPopulate_WithNullRoles() {
        AdnocB2BCustomerPopulator populator = new AdnocB2BCustomerPopulator();
        CustomerModel customerModel = new CustomerModel();
        CustomerData customerData = new CustomerData();
        customerData.setRoles(null);

        populator.populate(customerModel, customerData);

        assertEquals("No Role Assigned", customerData.getUserRole());
    }
}
