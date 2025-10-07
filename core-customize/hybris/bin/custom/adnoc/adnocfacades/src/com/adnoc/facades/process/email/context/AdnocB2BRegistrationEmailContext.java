package com.adnoc.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2bacceleratorservices.email.context.B2BRegistrationEmailContext;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocB2BRegistrationEmailContext extends B2BRegistrationEmailContext
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BRegistrationEmailContext.class);
    private static final String ADNOC_CSA_EMPLOYEE_UID = "adnoc.registration.workflow.assigned.employee.uid";

    private String csaEmployeeId;
    private String csaEmployeeName;

    private transient UserService userService;

    @Override
    public void init(final StoreFrontCustomerProcessModel businessProcessModel, final EmailPageModel emailPageModel)
    {
        LOG.info("appEvent=B2BRegistrationEmail, init method start");
        super.init(businessProcessModel, emailPageModel);
        initializeAssignedEmployee();
    }

    private void initializeAssignedEmployee()
    {
        try
        {
            LOG.info("appEvent=B2BRegistrationEmail,initialize assigned employee name for {}.", csaEmployeeId);
            final UserModel assignedEmployee = userService.getUserForUID(csaEmployeeId);
            csaEmployeeName = assignedEmployee.getName();
        }
        catch (final UnknownIdentifierException unknownIdentifierException)
        {
            LOG.error("appEvent=ReturnOrder,User with UID '{}' not found. Falling back to default CSA employee UID '{}'. Error: {}",
                    csaEmployeeId, ADNOC_CSA_EMPLOYEE_UID, unknownIdentifierException.getMessage());
            final UserModel assignedEmployee = userService.getUserForUID(ADNOC_CSA_EMPLOYEE_UID);
            csaEmployeeName = assignedEmployee.getName();
        }
    }

    public String getCsaEmployeeId()
    {
        return csaEmployeeId;
    }

    public void setCsaEmployeeId(final String csaEmployeeId)
    {
        this.csaEmployeeId = csaEmployeeId;
    }

    public String getCsaEmployeeName()
    {
        return csaEmployeeName;
    }

    public void setCsaEmployeeName(final String csaEmployeeName)
    {
        this.csaEmployeeName = csaEmployeeName;
    }

    protected UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }
}
