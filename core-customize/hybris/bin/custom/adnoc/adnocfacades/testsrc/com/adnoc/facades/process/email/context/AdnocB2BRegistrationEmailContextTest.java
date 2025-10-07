package com.adnoc.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BRegistrationEmailContextTest
{
    @InjectMocks
    private AdnocB2BRegistrationEmailContext adnocB2BRegistrationEmailContext;
    @Mock
    private UserService userService;
    @Mock
    private StoreFrontCustomerProcessModel storeFrontCustomerProcessModel;
    @Mock
    private EmailPageModel emailPageModel;

    private static final String CSA_EMPLOYEE_ID = "csa123";
    private static final String DEFAULT_CSA_EMPLOYEE_UID = "adnoc.registration.workflow.assigned.employee.uid";

    @Test
    public void testInitializeAssignedEmployee_UserFound()
    {
        adnocB2BRegistrationEmailContext.setCsaEmployeeId(CSA_EMPLOYEE_ID);
        adnocB2BRegistrationEmailContext.setUserService(userService);
        final UserModel userModel = Mockito.mock(UserModel.class);
        Mockito.when(userService.getUserForUID(CSA_EMPLOYEE_ID)).thenReturn(userModel);
        Mockito.when(userModel.getName()).thenReturn("John Doe");

        adnocB2BRegistrationEmailContext.init(storeFrontCustomerProcessModel, emailPageModel);

        assertEquals("John Doe", adnocB2BRegistrationEmailContext.getCsaEmployeeName());
        verify(userService).getUserForUID(CSA_EMPLOYEE_ID);
    }

    @Test
    public void testInitializeAssignedEmployee_UserNotFound()
    {
        adnocB2BRegistrationEmailContext.setCsaEmployeeId(CSA_EMPLOYEE_ID);
        adnocB2BRegistrationEmailContext.setUserService(userService);
        Mockito.when(userService.getUserForUID(CSA_EMPLOYEE_ID)).thenThrow(new UnknownIdentifierException(""));

        final UserModel defaultUser = Mockito.mock(UserModel.class);
        Mockito.when(userService.getUserForUID(DEFAULT_CSA_EMPLOYEE_UID)).thenReturn(defaultUser);
        Mockito.when(defaultUser.getName()).thenReturn("Default CSA");

        adnocB2BRegistrationEmailContext.init(storeFrontCustomerProcessModel, emailPageModel);

        assertEquals("Default CSA", adnocB2BRegistrationEmailContext.getCsaEmployeeName());

        verify(userService).getUserForUID(CSA_EMPLOYEE_ID);
        verify(userService).getUserForUID(DEFAULT_CSA_EMPLOYEE_UID);
    }
}
