/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.coupon.data.CouponData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Velocity context for a order notification email.
 */
public class OrderNotificationEmailContext extends AbstractEmailContext<OrderProcessModel>
{
    private transient Converter<OrderModel, OrderData> orderConverter;
    private OrderData orderData;
    private List<CouponData> giftCoupons;
    private String frontendUrl;
    private String payerID;

    @Override
    public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
    {
        super.init(orderProcessModel, emailPageModel);
        payerID = orderProcessModel.getOrder().getUnit().getUid();
        orderData = getOrderConverter().convert(orderProcessModel.getOrder());
        UserModel userModel = orderProcessModel.getOrder().getUser();
        if (userModel instanceof B2BCustomerModel b2BCustomerModel)
        {
            String firstName = b2BCustomerModel.getFirstName();
            put("firstName", firstName);
        }
        if (Objects.nonNull(orderData))
        {
            giftCoupons = orderData.getAppliedOrderPromotions().stream()
                    .filter(x -> CollectionUtils.isNotEmpty(x.getGiveAwayCouponCodes())).flatMap(p -> p.getGiveAwayCouponCodes().stream())
                    .collect(Collectors.toList());
            // Sets display name when the IT support team retries order replication to S4HANA
            if (Objects.equals(SAPOrderStatus.NOT_SENT_TO_ERP, orderProcessModel.getOrder().getSapOrderStatus()))
            {
                put(AbstractEmailContext.DISPLAY_NAME, getConfigurationService().getConfiguration().getString("adnoc.it.support.toAddress.display.name"));
            }
        }
    }

    @Override
    protected BaseSiteModel getSite(final OrderProcessModel orderProcessModel)
    {
        return orderProcessModel.getOrder().getSite();
    }

    @Override
    protected CustomerModel getCustomer(final OrderProcessModel orderProcessModel)
    {
        return (CustomerModel) orderProcessModel.getOrder().getUser();
    }

    protected Converter<OrderModel, OrderData> getOrderConverter()
    {
        return orderConverter;
    }

    @Required
    public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
    {
        this.orderConverter = orderConverter;
    }

    public OrderData getOrder()
    {
        return orderData;
    }

    @Override
    protected LanguageModel getEmailLanguage(final OrderProcessModel orderProcessModel)
    {
        return orderProcessModel.getOrder().getLanguage();
    }

    public List<CouponData> getCoupons()
    {
        return giftCoupons;
    }

    public String getFrontendUrl()
    {
        return frontendUrl;
    }

    public void setFrontendUrl(final String frontendUrl)
    {
        this.frontendUrl = frontendUrl;
    }

    public String getPayerID()
    {
        return payerID;
    }

    public void setPayerID(String payerID)
    {
        this.payerID = payerID;
    }
}
