package com.adnoc.service.process.sms.actions;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocOrderConfirmationSmsAction extends AbstractAdnocB2bGenerateSmsAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderConfirmationSmsAction.class.getName());

    private static final String ADNOC_ORDER_CONFIRMATION_RECEIVED_SMS_BODY = "adnoc.order.confirmation.sms.body";

    @Override
    public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException, Exception
    {
        LOG.debug("appEvent=AdnocOrderConfirmationSmsAction,executing SMS action for process:{}", businessProcessModel.getCode());
        executeSmsAction(businessProcessModel, ADNOC_ORDER_CONFIRMATION_RECEIVED_SMS_BODY);

        LOG.debug("appEvent=AdnocOrderConfirmationSmsAction,executed SMS action for process:{}", businessProcessModel.getCode());
        return Transition.OK;
    }

    protected void executeSmsAction(final BusinessProcessModel businessProcessModel, final String adnocOrderConfirmationSmsBody)
    {
        if (businessProcessModel instanceof OrderProcessModel)
        {
            final OrderProcessModel orderProcessModel = (OrderProcessModel) businessProcessModel;
            if (orderProcessModel.getOrder() instanceof OrderModel)
            {
                final OrderModel orderModel = orderProcessModel.getOrder();
                final B2BCustomerModel b2BCustomerModel = (B2BCustomerModel) orderModel.getUser();
                String salutationWithFirstName = getSalutationWithFirstName(b2BCustomerModel);
                prepareAndSendSmsAction(adnocOrderConfirmationSmsBody, b2BCustomerModel.getMobileNumber(), salutationWithFirstName, orderModel.getCode());
                LOG.info("appEvent=AdnocOrderConfirmationSmsAction,SMS sent successfully for order:{}", orderProcessModel.getCode());
            }
        }
    }
}
