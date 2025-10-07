package com.adnoc.service.order.payment.strategies.impl;

import com.adnoc.service.constants.AdnocserviceConstants;
import com.adnoc.service.order.payment.strategies.AdnocPaymentInfoCreateStrategy;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class AdnocCreditCardPaymentInfoCreateStrategy implements AdnocPaymentInfoCreateStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocCreditCardPaymentInfoCreateStrategy.class);

    private ConfigurationService configurationService;
    private ModelService modelService;

    @Override
    public PaymentInfoModel createPaymentInfo(final double amount, final UserModel userModel)
    {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = getModelService().create(CreditCardPaymentInfoModel.class);
        final Configuration configuration = getConfigurationService().getConfiguration();
        creditCardPaymentInfoModel.setCode(userModel.getUid() + "_" + UUID.randomUUID());
        creditCardPaymentInfoModel.setAmount(amount);
        creditCardPaymentInfoModel.setUser(userModel);

        // Duummy config value to create credit card paymengt info
        creditCardPaymentInfoModel.setNumber(configuration.getString(AdnocserviceConstants.CARDNUMBER));
        creditCardPaymentInfoModel.setType(CreditCardType.MASTER);
        creditCardPaymentInfoModel.setCcOwner(userModel.getUid());
        creditCardPaymentInfoModel.setValidFromMonth(configuration.getString(AdnocserviceConstants.VALIDFROMMONTH));
        creditCardPaymentInfoModel.setValidToMonth(configuration.getString(AdnocserviceConstants.VALIDTOMONTH));
        creditCardPaymentInfoModel.setValidToYear(configuration.getString(AdnocserviceConstants.VALIDYEAR));
        LOG.info("appEvent=UpdateCheckoutCart, setting CreditCardPaymentInfo with value={}.", amount);
        return creditCardPaymentInfoModel;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }


}
