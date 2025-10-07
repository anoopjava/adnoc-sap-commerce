package com.adnoc.service.order.payment.transaction.strategies;

import com.adnoc.facades.company.overdue.invoice.data.AdnocOverduePaymentRequestData;
import com.adnoc.service.company.overdue.invoice.data.AdnocOverdueInvoiceDetailsData;
import com.adnoc.service.model.AdnocOverdueInvoiceDetailsModel;
import com.adnoc.service.model.AdnocOverduePaymentTransactionModel;
import com.adnoc.service.model.CreditLimitPaymentInfoModel;
import com.adnoc.service.order.payment.strategies.AdnocPaymentInfoCreateStrategy;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;

public class AdnocPaymentTransactionStrategy
{
    private ModelService modelService;
    private Map<String, AdnocPaymentInfoCreateStrategy> adnocPaymentInfoCreateStrategyMap;
    private UserService userService;
    private CommonI18NService commonI18NService;

    /**
     * payment transaction creation for order.
     *
     * @param orderModel the order model
     * @return the list
     */
    public List<PaymentTransactionModel> createPaymentTransactions(final OrderModel orderModel)
    {
        final List<PaymentTransactionModel> paymentTransactionModels = new ArrayList<>();
        final CreditLimitPaymentInfoModel creditLimitPaymentInfoModel = orderModel.getCreditLimitPaymentInfo();
        double remainingToPay = orderModel.getTotalPrice();
        if (Objects.nonNull(creditLimitPaymentInfoModel))
        {
            final Double creditLimitAmount = ObjectUtils.defaultIfNull(creditLimitPaymentInfoModel.getAmount(), 0.0);
            if (creditLimitAmount > 0)
            {
                final double creditLimitAmountUsed = Math.min(creditLimitAmount, orderModel.getTotalPrice());  // Use as much wallet balance as possible
                final double creditLimitAmountBalance = creditLimitAmount - creditLimitAmountUsed; // Deduct the used amount
                remainingToPay = orderModel.getTotalPrice() - creditLimitAmountUsed;  // Calculate remaining payment needed

                final PaymentTransactionModel paymentTransactionModel = getPaymentTransactionModel(orderModel, creditLimitPaymentInfoModel, creditLimitAmountUsed);
                paymentTransactionModels.add(paymentTransactionModel);
                creditLimitPaymentInfoModel.setAmount(creditLimitAmountBalance);
            }
        }
        if (Objects.nonNull(orderModel.getPaymentInfo()) && remainingToPay > 0)
        {
            final PaymentTransactionModel paymentTransactionModel = getPaymentTransactionModel(orderModel, orderModel.getPaymentInfo(), remainingToPay);
            paymentTransactionModels.add(paymentTransactionModel);
        }
        else
        {
            orderModel.setPaymentInfo(creditLimitPaymentInfoModel);
        }
        return paymentTransactionModels;
    }

    private PaymentTransactionModel getPaymentTransactionModel(final OrderModel orderModel, final PaymentInfoModel paymentInfoModel, final double amount)
    {
        final PaymentTransactionModel paymentTransactionModel = getModelService().create(PaymentTransactionModel.class);
        paymentTransactionModel.setCode(orderModel.getCode() + "-" + orderModel.getUser().getUid() + "-" + paymentInfoModel.getItemtype() + "-" + amount + "_" + UUID.randomUUID());
        paymentTransactionModel.setCurrency(orderModel.getCurrency());
        paymentTransactionModel.setOrder(orderModel);
        paymentTransactionModel.setInfo(paymentInfoModel);
        paymentTransactionModel.setPlannedAmount(BigDecimal.valueOf(amount));
        paymentTransactionModel.setRequestId(paymentInfoModel.getUtrnNumber());
        paymentTransactionModel.setRequestToken(paymentInfoModel.getAuthorizationCode());
        paymentTransactionModel.setRrnNumber(paymentInfoModel.getRrnNumber());
        return paymentTransactionModel;
    }

    /**
     * payment transaction creation for overdue payment.
     *
     * @param utrnCode                       the utrn code
     * @param sessionIndicator               the session indicator
     * @param adnocOverduePaymentRequestData the adnoc overdue payment request data
     */
    public void createPaymentTransaction(final String utrnCode, final String sessionIndicator, final AdnocOverduePaymentRequestData adnocOverduePaymentRequestData)
    {
        final AdnocOverduePaymentTransactionModel adnocOverduePaymentTransactionModel = getModelService().create(AdnocOverduePaymentTransactionModel.class);
        adnocOverduePaymentTransactionModel.setCode(sessionIndicator);
        adnocOverduePaymentTransactionModel.setRequestId(utrnCode);
        adnocOverduePaymentTransactionModel.setPayerId(adnocOverduePaymentRequestData.getPayerId());
        adnocOverduePaymentTransactionModel.setPlannedAmount(BigDecimal.valueOf(adnocOverduePaymentRequestData.getTotalAmount()));
        final String currencyCode = adnocOverduePaymentRequestData.getCurrency();
        final CurrencyModel currencyModel = getCommonI18NService().getCurrency(currencyCode);
        adnocOverduePaymentTransactionModel.setCurrency(currencyModel);

        final AdnocPaymentInfoCreateStrategy adnocPaymentInfoCreateStrategy = getAdnocPaymentInfoCreateStrategyMap().get(adnocOverduePaymentRequestData.getPaymentType());
        if (Objects.isNull(adnocPaymentInfoCreateStrategy))
        {
            throw new RuntimeException(String.format("No AdnocPaymentInfoCreateStrategy mapped for %s", CheckoutPaymentType.CREDIT_LIMIT.getCode()));
        }
        final PaymentInfoModel paymentInfoModel = adnocPaymentInfoCreateStrategy.createPaymentInfo(adnocOverduePaymentRequestData.getTotalAmount(), getUserService().getCurrentUser());
        adnocOverduePaymentTransactionModel.setInfo(paymentInfoModel);

        final List<AdnocOverdueInvoiceDetailsModel> adnocOverdueInvoiceDetailList = new ArrayList<>();
        for (final AdnocOverdueInvoiceDetailsData adnocOverdueInvoiceDetailsData : adnocOverduePaymentRequestData.getInvoiceDetails())
        {
            final AdnocOverdueInvoiceDetailsModel adnocOverdueInvoiceDetailsModel = new AdnocOverdueInvoiceDetailsModel();
            adnocOverdueInvoiceDetailsModel.setInvoiceNumber(adnocOverdueInvoiceDetailsData.getInvoiceNumber());
            adnocOverdueInvoiceDetailsModel.setCompanyCode(adnocOverdueInvoiceDetailsData.getCompanyCode());
            adnocOverdueInvoiceDetailsModel.setFiscalYear(adnocOverdueInvoiceDetailsData.getFiscalYear());
            adnocOverdueInvoiceDetailList.add(adnocOverdueInvoiceDetailsModel);
        }
        adnocOverduePaymentTransactionModel.setInvoiceDetails(adnocOverdueInvoiceDetailList);
        modelService.save(adnocOverduePaymentTransactionModel);
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected Map<String, AdnocPaymentInfoCreateStrategy> getAdnocPaymentInfoCreateStrategyMap()
    {
        return adnocPaymentInfoCreateStrategyMap;
    }

    public void setAdnocPaymentInfoCreateStrategyMap(final Map<String, AdnocPaymentInfoCreateStrategy> adnocPaymentInfoCreateStrategyMap)
    {
        this.adnocPaymentInfoCreateStrategyMap = adnocPaymentInfoCreateStrategyMap;
    }

    protected UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }
}
