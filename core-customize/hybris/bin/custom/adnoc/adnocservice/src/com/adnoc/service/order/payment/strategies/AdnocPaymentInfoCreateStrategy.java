package com.adnoc.service.order.payment.strategies;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;

public interface AdnocPaymentInfoCreateStrategy
{
    /**
     * Create payment info payment info model.
     *
     * @param amount    the amount
     * @param userModel the user model
     * @return the payment info model
     */
    PaymentInfoModel createPaymentInfo(double amount, UserModel userModel);
}
