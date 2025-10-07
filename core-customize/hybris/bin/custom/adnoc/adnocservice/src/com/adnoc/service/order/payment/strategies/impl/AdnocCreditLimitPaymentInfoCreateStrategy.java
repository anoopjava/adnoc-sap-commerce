package com.adnoc.service.order.payment.strategies.impl;

import com.adnoc.service.model.CreditLimitPaymentInfoModel;
import com.adnoc.service.order.payment.strategies.AdnocPaymentInfoCreateStrategy;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class AdnocCreditLimitPaymentInfoCreateStrategy implements AdnocPaymentInfoCreateStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocCreditLimitPaymentInfoCreateStrategy.class);

    private ModelService modelService;

    @Override
    public PaymentInfoModel createPaymentInfo(final double amount, final UserModel userModel)
    {
        final CreditLimitPaymentInfoModel creditLimitPaymentInfoModel = getModelService().create(CreditLimitPaymentInfoModel.class);
        creditLimitPaymentInfoModel.setAmount(amount);
        creditLimitPaymentInfoModel.setCode(userModel.getUid() + "_" + UUID.randomUUID());
        creditLimitPaymentInfoModel.setUser(userModel);
        LOG.info("appEvent=UpdateCheckoutCart, setting CreditLimitPaymentInfo with value={}.", amount);
        return creditLimitPaymentInfoModel;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }
}
