package com.adnoc.service.process.sms.actions;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocOrderCancelledSmsAction extends AbstractAdnocB2bGenerateSmsAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderCancelledSmsAction.class.getName());

    private static final String ADNOC_ORDER_CANCELLED_SMS_BODY = "adnoc.order.cancelled.sms.body";


    @Override
    public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException, Exception
    {
        LOG.debug("appEvent=AdnocOrderCancelledSmsAction,executing SMS action for process:{}", businessProcessModel.getCode());
        executeSmsAction(businessProcessModel);

        LOG.debug("appEvent=AdnocOrderCancelledSmsAction,executed SMS action for process:{}", businessProcessModel.getCode());
        return Transition.OK;
    }

    protected void executeSmsAction(final BusinessProcessModel businessProcessModel)
    {
        if (businessProcessModel instanceof final OrderProcessModel orderProcessModel && Objects.nonNull(orderProcessModel.getOrder()))
        {
            final B2BCustomerModel b2BCustomerModel = (B2BCustomerModel) orderProcessModel.getOrder().getUser();
            String salutationWithFirstName = getSalutationWithFirstName(b2BCustomerModel);
            prepareAndSendSmsAction(AdnocOrderCancelledSmsAction.ADNOC_ORDER_CANCELLED_SMS_BODY, b2BCustomerModel.getMobileNumber(), salutationWithFirstName);
            LOG.info("appEvent=AdnocOrderCancelledSmsAction,SMS sent successfully for cancelled order:{} ", orderProcessModel.getCode());
        }
    }
}
