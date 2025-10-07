package com.adnoc.facades.registration.impl;

import com.adnoc.service.model.AdnocPayerB2BUnitRegistrationModel;
import com.adnoc.service.model.AdnocShipToB2BUnitRegistrationModel;
import com.adnoc.service.model.AdnocSoldToB2BRegistrationModel;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.b2bacceleratorfacades.registration.B2BRegistrationWorkflowFacade;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.EntryMessage;

public class AdnocB2BRegistrationWorkflowFacadeImpl implements B2BRegistrationWorkflowFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BRegistrationWorkflowFacadeImpl.class);

    private static final String ADNOC_ECOMMERCE_SPECIALIST_UID = "ecommmercespelicalistapprover";
    private static final String ADNOC_REGISTRATION_WORKFLOW_ASSIGNED_EMPLOYEE_UID = "adnoc.registration.workflow.assigned.employee.uid";

    private WorkflowService workflowService;
    private WorkflowProcessingService workflowProcessingService;
    private UserService userService;
    private ModelService modelService;
    private ConfigurationService configurationService;

    @Override
    public void launchWorkflow(final WorkflowTemplateModel workflowTemplateModel, final B2BRegistrationModel b2bRegistrationModel)
    {
        final EntryMessage traceEntry = LOG.traceEntry("appEvent=B2BRegistration, Launching workflow for registration={}.", b2bRegistrationModel.getEmail());
        final String adnocRegistrationWorkflowEmployeeID = getConfigurationService().getConfiguration()
                .getString(ADNOC_REGISTRATION_WORKFLOW_ASSIGNED_EMPLOYEE_UID, ADNOC_ECOMMERCE_SPECIALIST_UID);
        LOG.debug("appEvent=B2BRegistration, configured value found to assign workflow={} for registration={}.", adnocRegistrationWorkflowEmployeeID, b2bRegistrationModel.getEmail());
        UserModel assignedEmployee;
        try
        {
            assignedEmployee = getUserService().getUserForUID(adnocRegistrationWorkflowEmployeeID);
            LOG.debug("appEvent=B2BRegistration, Customer found to assign workflow={} for registration={}.", assignedEmployee.getUid(), b2bRegistrationModel.getEmail());
        }
        catch (final UnknownIdentifierException unknownIdentifierException)
        {
            assignedEmployee = getUserService().getUserForUID(ADNOC_ECOMMERCE_SPECIALIST_UID);
            LOG.error(String.format("appEvent=B2BRegistration, Customer not found to assign workflow='%s' for registration='%s', so fallback to default employee.",
                    adnocRegistrationWorkflowEmployeeID, ADNOC_ECOMMERCE_SPECIALIST_UID), ExceptionUtils.getRootCauseMessage(unknownIdentifierException));
        }

        final WorkflowModel workflow = getWorkflowService().createWorkflow(workflowTemplateModel, b2bRegistrationModel, assignedEmployee);
        if (b2bRegistrationModel instanceof AdnocSoldToB2BRegistrationModel)
        {
            workflow.setName("Sold-to Registration");
        }
        else if (b2bRegistrationModel instanceof AdnocPayerB2BUnitRegistrationModel)
        {
            workflow.setName("Payer Registration");
        }
        else if (b2bRegistrationModel instanceof AdnocShipToB2BUnitRegistrationModel)
        {
            workflow.setName("Ship-to Registration");
        }
        else
        {
            workflow.setName("B2B Registration");
        }
        getModelService().save(workflow);

        LOG.debug("appEvent=B2BRegistration, Workflow created for registration={}.", b2bRegistrationModel.getEmail());
        getWorkflowProcessingService().startWorkflow(workflow);
        LOG.debug("appEvent=B2BRegistration, Workflow started for registration={}.", b2bRegistrationModel.getEmail());
        LOG.traceExit(traceEntry);
    }

    protected WorkflowService getWorkflowService()
    {
        return workflowService;
    }

    public void setWorkflowService(final WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }

    protected WorkflowProcessingService getWorkflowProcessingService()
    {
        return workflowProcessingService;
    }

    public void setWorkflowProcessingService(final WorkflowProcessingService workflowProcessingService)
    {
        this.workflowProcessingService = workflowProcessingService;
    }

    protected UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }
}
