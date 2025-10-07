package com.adnoc.service.process.sms.actions;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocOrderCompletedSmsAction extends AbstractAdnocB2bGenerateSmsAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderCompletedSmsAction.class.getName());

    private static final String ADNOC_ORDER_COMPLETED_SMS_BODY = "adnoc.order.completed.sms.body";

    @Override
    public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException, Exception
    {
        LOG.debug("appEvent=AdnocOrderCompletedSmsAction,executing SMS action for process:{}", businessProcessModel.getCode());
        executeSmsAction(businessProcessModel, ADNOC_ORDER_COMPLETED_SMS_BODY);

        LOG.debug("appEvent=AdnocOrderCompletedSmsAction,executed SMS action for process:{}", businessProcessModel.getCode());
        return Transition.OK;
    }

    protected void executeSmsAction(final BusinessProcessModel businessProcessModel, final String adnocOrderCompletedSmsBody)
    {
        if (businessProcessModel instanceof OrderProcessModel )
        {
            final OrderProcessModel orderProcessModel = (OrderProcessModel) businessProcessModel;
            if (orderProcessModel.getOrder() instanceof OrderModel)
            {
                final OrderModel orderModel = orderProcessModel.getOrder();
                UserModel userModel = orderModel.getUser();
                if(userModel instanceof  B2BCustomerModel b2BCustomerModel)
                {
                    String salutationWithFirstName = getSalutationWithFirstName(b2BCustomerModel);
                    prepareAndSendSmsAction(adnocOrderCompletedSmsBody, b2BCustomerModel.getMobileNumber(), salutationWithFirstName);
                }
                LOG.info("appEvent=AdnocOrderCompletedSmsAction,SMS sent successfully for completed order:{} ", orderProcessModel.getCode());
            }
        }
    }
}
