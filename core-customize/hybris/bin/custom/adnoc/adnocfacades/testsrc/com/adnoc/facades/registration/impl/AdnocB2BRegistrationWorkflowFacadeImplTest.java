package com.adnoc.facades.registration.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BRegistrationWorkflowFacadeImplTest
{
    @InjectMocks
    private AdnocB2BRegistrationWorkflowFacadeImpl adnocB2BRegistrationWorkflowFacadeImpl;
    @Mock
    private WorkflowService workflowService;
    @Mock
    private UserService userService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private WorkflowProcessingService workflowProcessingService;
    @Mock
    private WorkflowTemplateModel workflowTemplateModel;
    @Mock
    private B2BRegistrationModel b2bRegistrationModel;

    @Test
    public void testLaunchWorkFlow()
    {
        final String ADNOC_CSA_EMPLOYEE_UID = "adnoccsa";
        final String ADNOC_CSM_EMPLOYEE_UID = "adnoccsm";
        final WorkflowModel workflow = new WorkflowModel();

        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        final String ADNOC_REGISTRATION_WORKFLOW_ASSIGNED_EMPLOYEE_UID = "adnoc.registration.workflow.assigned.employee.uid";
        Mockito.when(configuration.getString(ADNOC_REGISTRATION_WORKFLOW_ASSIGNED_EMPLOYEE_UID, ADNOC_CSA_EMPLOYEE_UID)).thenReturn("adnoccsm");

        final UserModel testUser = new EmployeeModel();
        Mockito.doReturn(workflow).when(workflowService).createWorkflow(workflowTemplateModel, b2bRegistrationModel, testUser);

        final UnknownIdentifierException unknownIdentifierException = new UnknownIdentifierException("User not found.");
        Mockito.when(userService.getUserForUID(ADNOC_CSM_EMPLOYEE_UID)).thenThrow(unknownIdentifierException);

        Mockito.doReturn(testUser).when(userService).getUserForUID(ADNOC_CSA_EMPLOYEE_UID);
        adnocB2BRegistrationWorkflowFacadeImpl.launchWorkflow(workflowTemplateModel, b2bRegistrationModel);

        Mockito.verify(workflowProcessingService).startWorkflow(workflow);
    }

}
