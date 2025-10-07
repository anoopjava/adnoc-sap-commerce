package com.adnoc.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.omsbackoffice.actions.returns.CancelReturnAction;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocCancelReturnAction extends CancelReturnAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocCancelReturnAction.class);

    @Override
    public boolean canPerform(final ActionContext<ReturnRequestModel> actionContext)
    {
        LOG.info("appEvent=AdnocCancelReturnAction, enabling reject button based on return status");
        final Object data = actionContext.getData();
        boolean decision = false;
        if (data instanceof ReturnRequestModel)
        {
            final ReturnStatus returnStatus = ((ReturnRequestModel) data).getStatus();
            decision = returnStatus.equals(ReturnStatus.APPROVAL_PENDING) || returnStatus.equals(ReturnStatus.WAIT) || returnStatus.equals(ReturnStatus.PAYMENT_REVERSAL_FAILED);
        }

        return decision;
    }
}
