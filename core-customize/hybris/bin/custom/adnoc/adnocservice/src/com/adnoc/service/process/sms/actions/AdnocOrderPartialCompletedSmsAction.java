package com.adnoc.service.process.sms.actions;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocOrderPartialCompletedSmsAction extends AbstractAdnocB2bGenerateSmsAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderPartialCompletedSmsAction.class.getName());

    private static final String ADNOC_ORDER_PARTIALLY_COMPLETED_SMS_BODY = "adnoc.order.partially.completed.sms.body";

    @Override
    public AbstractSimpleDecisionAction.Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException, Exception
    {
        LOG.debug("appEvent=AdnocOrderPartialCompletedSmsAction,executing SMS action for process:{}", businessProcessModel.getCode());
        executeSmsAction(businessProcessModel, ADNOC_ORDER_PARTIALLY_COMPLETED_SMS_BODY);

        LOG.debug("appEvent=AdnocOrderPartialCompletedSmsAction,executed SMS action for process:{}", businessProcessModel.getCode());
        return AbstractSimpleDecisionAction.Transition.OK;
    }

    protected void executeSmsAction(final BusinessProcessModel businessProcessModel, final String adnocOrderPartialSmsBody)
    {
        if (businessProcessModel instanceof OrderProcessModel)
        {
            final OrderProcessModel orderProcessModel = (OrderProcessModel) businessProcessModel;
            if (orderProcessModel.getOrder() instanceof OrderModel)
            {
                final OrderModel orderModel = orderProcessModel.getOrder();
                final B2BCustomerModel b2BCustomerModel = (B2BCustomerModel) orderModel.getUser();
                String salutationWithFirstName = getSalutationWithFirstName(b2BCustomerModel);
                prepareAndSendSmsAction(adnocOrderPartialSmsBody, b2BCustomerModel.getMobileNumber(), salutationWithFirstName);
                LOG.info("appEvent=AdnocOrderPartialCompletedSmsAction,SMS sent successfully for partially completed order:{}", orderProcessModel.getCode());
            }
        }
    }
}
