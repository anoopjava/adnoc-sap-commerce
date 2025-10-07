package com.adnoc.facades.process.email.context;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.model.AdnocB2BCustomerCreationProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BCustomerCreationContextTest
{
    @InjectMocks
    private AdnocB2BCustomerCreationContext adnocB2BCustomerCreationContext;
    @Mock
    private AdnocB2BUnitService adnocB2BUnitService;
    @Mock
    private EmailPageModel emailPageModel;
    @Mock
    private B2BCustomerModel b2BCustomerModel;
    @Mock
    private CustomerEmailResolutionService customerEmailResolutionService;
    @Mock
    private Converter<UserModel, CustomerData> customerConverter;
    @Mock
    private CustomerData customerData;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Test
    public void testInit()
    {
        Mockito.when(customerEmailResolutionService.getEmailForCustomer(b2BCustomerModel)).thenReturn("test@b.com");
        Mockito.when(customerConverter.convert(b2BCustomerModel)).thenReturn(customerData);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getString("adnoc.b2bcustomer.portal.login.link")).thenReturn("http://localhost:4200/adnoc/en/AED/login");

        final AdnocB2BCustomerCreationProcessModel processModel = mock(AdnocB2BCustomerCreationProcessModel.class);
        Mockito.when(processModel.getCustomer()).thenReturn(b2BCustomerModel);
        Mockito.when(b2BCustomerModel.getName()).thenReturn("Test Customer");
        final B2BCustomerModel adminCustomer1 = mock(B2BCustomerModel.class);
        final B2BCustomerModel adminCustomer2 = mock(B2BCustomerModel.class);
        final PrincipalGroupModel adminGroup = mock(PrincipalGroupModel.class);

        Mockito.when(adminGroup.getUid()).thenReturn(B2BConstants.B2BADMINGROUP);
        Mockito.when(adminCustomer1.getGroups()).thenReturn(Set.of(adminGroup));
        Mockito.when(adminCustomer2.getGroups()).thenReturn(Set.of(adminGroup));

        Mockito.when(adminCustomer1.getDisplayName()).thenReturn("100000admin");
        Mockito.when(adminCustomer2.getDisplayName()).thenReturn("adminTest");
        Mockito.when(adnocB2BUnitService.getB2BCustomers(any())).thenReturn(Set.of(adminCustomer1, adminCustomer2));

        adnocB2BCustomerCreationContext.init(processModel, emailPageModel);

        final Set<String> expectedAdminNames = new HashSet<>(Arrays.asList("100000admin", "adminTest"));
        final Set<String> actualAdminNames = new HashSet<>(Arrays.asList(adnocB2BCustomerCreationContext.getAdnocB2BAdminName().split(", ")));
        assertEquals(expectedAdminNames, actualAdminNames);
    }
}
