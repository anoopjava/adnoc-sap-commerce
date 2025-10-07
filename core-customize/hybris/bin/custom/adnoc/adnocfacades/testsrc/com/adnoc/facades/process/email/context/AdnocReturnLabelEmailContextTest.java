package com.adnoc.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdnocReturnLabelEmailContextTest
{
    @InjectMocks
    private AdnocReturnLabelEmailContext adnocReturnLabelEmailContext;
    @Mock
    private ReturnProcessModel returnProcessModel;
    @Mock
    private ReturnRequestModel returnRequestModel;
    @Mock
    private EmailPageModel emailPageModel;
    @Mock
    private Converter<ReturnRequestModel, ReturnRequestData> returnConverter;
    @Mock
    private ReturnRequestData returnRequestData;
    @Mock
    private UserService userService;
    @Mock
    private OrderModel orderModel;

    private static final String CSA_EMPLOYEE_ID = "csa123";
    private static final String DEFAULT_CSA_EMPLOYEE_UID = "adnoc.registration.workflow.assigned.employee.uid";


    @Test
    public void testInit()
    {
        Mockito.when(returnProcessModel.getReturnRequest()).thenReturn(returnRequestModel);
        Mockito.when(returnConverter.convert(returnRequestModel)).thenReturn(returnRequestData);
        Mockito.when(returnRequestModel.getOrder()).thenReturn(orderModel);

        final String csaEmployeeId = "testadnoccsa";
        adnocReturnLabelEmailContext.setCsaEmployeeId(csaEmployeeId);
        adnocReturnLabelEmailContext.setReturnConverter(returnConverter);
        adnocReturnLabelEmailContext.setUserService(userService);
        final UserModel userModel = Mockito.mock(UserModel.class);
        Mockito.when(userService.getUserForUID(csaEmployeeId)).thenReturn(userModel);
        Mockito.when(userModel.getName()).thenReturn("Test Adnoc CSA");
        adnocReturnLabelEmailContext.init(returnProcessModel, emailPageModel);

        assertNotNull("Return request data should be initialized", adnocReturnLabelEmailContext.getReturnRequest());
    }

    @Test
    public void testInitializeAssignedEmployee_UserNotFound()
    {
        Mockito.when(returnRequestModel.getOrder()).thenReturn(orderModel);
        Mockito.when(returnProcessModel.getReturnRequest()).thenReturn(returnRequestModel);
        Mockito.when(returnConverter.convert(returnRequestModel)).thenReturn(returnRequestData);

        adnocReturnLabelEmailContext.setCsaEmployeeId(CSA_EMPLOYEE_ID);
        adnocReturnLabelEmailContext.setUserService(userService);
        Mockito.when(userService.getUserForUID(CSA_EMPLOYEE_ID)).thenThrow(new UnknownIdentifierException(""));

        final UserModel defaultUser = Mockito.mock(UserModel.class);
        Mockito.when(userService.getUserForUID(DEFAULT_CSA_EMPLOYEE_UID)).thenReturn(defaultUser);
        Mockito.when(defaultUser.getName()).thenReturn("Default CSA");

        adnocReturnLabelEmailContext.init(returnProcessModel, emailPageModel);

        Assert.assertEquals("Default CSA", adnocReturnLabelEmailContext.getCsaEmployeeName());

        verify(userService).getUserForUID(CSA_EMPLOYEE_ID);
        verify(userService).getUserForUID(DEFAULT_CSA_EMPLOYEE_UID);
    }
}
