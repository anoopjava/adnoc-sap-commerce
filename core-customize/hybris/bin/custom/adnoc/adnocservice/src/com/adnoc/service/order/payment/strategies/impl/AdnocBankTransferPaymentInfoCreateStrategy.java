package com.adnoc.service.order.payment.strategies.impl;

import com.adnoc.service.model.BankPaymentInfoModel;
import com.adnoc.service.order.payment.strategies.AdnocPaymentInfoCreateStrategy;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class AdnocBankTransferPaymentInfoCreateStrategy implements AdnocPaymentInfoCreateStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocBankTransferPaymentInfoCreateStrategy.class);

    private ModelService modelService;

    @Override
    public PaymentInfoModel createPaymentInfo(final double amount, final UserModel userModel)
    {
        final BankPaymentInfoModel bankPaymentInfoModel = getModelService().create(BankPaymentInfoModel.class);
        bankPaymentInfoModel.setAmount(amount);
        bankPaymentInfoModel.setCode(userModel.getUid() + "_" + UUID.randomUUID());
        bankPaymentInfoModel.setUser(userModel);
        LOG.info("appEvent=AdnocBankTransferPaymentCreateStrategy, setting BankPaymentInfoModel with value={}.", amount);
        return bankPaymentInfoModel;
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
