package com.adnoc.facades.order.converter.populator;

import com.adnoc.service.model.BankPaymentInfoModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionData;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.Objects;

public class AdnocPaymentTransactionPopulator implements Populator<PaymentTransactionModel, PaymentTransactionData>
{

    @Override
    public void populate(final PaymentTransactionModel source, final PaymentTransactionData target)
    {
        target.setCode(source.getCode());
        target.setPlannedAmount(source.getPlannedAmount());
        target.setRrnNumber(source.getRrnNumber());
        final CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();
        if (Objects.nonNull(source.getInfo()))
        {
            final PaymentInfoModel paymentInfo = source.getInfo();
            if (paymentInfo instanceof CreditCardPaymentInfoModel)
            {
                final CreditCardPaymentInfoModel creditCardModel = (CreditCardPaymentInfoModel) paymentInfo;
                ccPaymentInfoData.setCardNumber(creditCardModel.getNumber());
            }
            else if (paymentInfo instanceof BankPaymentInfoModel bankPaymentInfoModel)
            {
                ccPaymentInfoData.setAccount(bankPaymentInfoModel.getAccount());
                ccPaymentInfoData.setApprovalCode(bankPaymentInfoModel.getAuthorizationCode());
                ccPaymentInfoData.setTransactionId(bankPaymentInfoModel.getTransactionID());
            }
        }
        target.setPaymentInfo(ccPaymentInfoData);
    }
}
