package com.adnoc.facades.process.email.context;

import de.hybris.platform.acceleratorfacades.email.context.ReturnLabelEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocReturnLabelEmailContext extends ReturnLabelEmailContext
{
    private static final Logger LOG = LogManager.getLogger(AdnocReturnLabelEmailContext.class);
    private static final String ADNOC_CSA_EMPLOYEE_UID = "adnoc.registration.workflow.assigned.employee.uid";

    private String backOfficeLink;
    private String csaEmployeeId;
    private String csaEmployeeName;
    private String frontendUrl;

    private transient Converter<ReturnRequestModel, ReturnRequestData> returnConverter;
    private transient ReturnRequestData returnRequest;
    private transient UserService userService;

    @Override
    public void init(final ReturnProcessModel returnProcessModel, final EmailPageModel emailPageModel)
    {
        LOG.info("appEvent=AdnocReturnRequest,getting return request from return process model:{}", returnProcessModel.getReturnRequest());
        super.init(returnProcessModel, emailPageModel);
        UserModel userModel  = returnProcessModel.getReturnRequest().getOrder().getUser();
        if (userModel instanceof B2BCustomerModel b2BCustomerModel)
        {
            String firstName = b2BCustomerModel.getFirstName();
            put("firstName", firstName);
        }
        if (Objects.nonNull(returnProcessModel.getReturnRequest()))
        {
            returnRequest = getReturnConverter().convert(returnProcessModel.getReturnRequest());
            LOG.debug("appEvent=AdnocReturnRequest,start method initializeAssignedEmployee...");
            initializeAssignedEmployee();
        }
    }

    private void initializeAssignedEmployee()
    {
        try
        {
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

    protected Converter<ReturnRequestModel, ReturnRequestData> getReturnConverter()
    {
        return returnConverter;
    }

    public void setReturnConverter(final Converter<ReturnRequestModel, ReturnRequestData> returnConverter)
    {
        this.returnConverter = returnConverter;
    }

    public String getFrontendUrl()
    {
        return frontendUrl;
    }

    public void setFrontendUrl(String frontendUrl)
    {
        this.frontendUrl = frontendUrl;
    }

    public ReturnRequestData getReturnRequest()
    {
        return returnRequest;
    }

    public void setReturnRequest(final ReturnRequestData returnRequest)
    {
        this.returnRequest = returnRequest;
    }

    protected UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    public String getBackOfficeLink()
    {
        return backOfficeLink;
    }

    public void setBackOfficeLink(final String backOfficeLink)
    {
        this.backOfficeLink = backOfficeLink;
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
}
