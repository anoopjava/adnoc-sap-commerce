package com.adnoc.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.omsbackoffice.actions.returns.ApproveReturnAction;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;

public class AdnocApproveReturnAction extends ApproveReturnAction
{
    protected static final String CUSTOMER_SUPPORT_SOCKET_OUT_CONTEXT = "csApproveReturnContext";

    @Override
    public boolean canPerform(final ActionContext<ReturnRequestModel> actionContext)
    {
        boolean result = false;
        final ReturnRequestModel returnRequest = (ReturnRequestModel) actionContext.getData();
        if (returnRequest != null)
        {
            result = CollectionUtils.isNotEmpty(returnRequest.getReturnEntries()) && Objects.equals(returnRequest.getStatus(), ReturnStatus.APPROVAL_PENDING);
        }

        return result;
    }

    @Override
    public String getConfirmationMessage(final ActionContext<ReturnRequestModel> actionContext)
    {
        return null;
    }

    @Override
    public boolean needsConfirmation(final ActionContext<ReturnRequestModel> actionContext)
    {
        return false;
    }

    @Override
    public ActionResult<ReturnRequestModel> perform(final ActionContext<ReturnRequestModel> actionContext)
    {
        final ReturnRequestModel returnToUpdate = (ReturnRequestModel) actionContext.getData();
        sendOutput(CUSTOMER_SUPPORT_SOCKET_OUT_CONTEXT, returnToUpdate);
        final ActionResult<ReturnRequestModel> actionResult = new ActionResult("success");
        actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
        return actionResult;
    }

}
