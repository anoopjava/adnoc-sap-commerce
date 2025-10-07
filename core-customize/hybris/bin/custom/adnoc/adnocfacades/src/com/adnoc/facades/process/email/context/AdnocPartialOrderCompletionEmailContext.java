package com.adnoc.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocPartialOrderCompletionEmailContext extends AbstractEmailContext<OrderProcessModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocPartialOrderCompletionEmailContext.class);

    private transient Converter<OrderModel, OrderData> orderConverter;
    private OrderData orderData;

    @Override
    public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
    {
        super.init(orderProcessModel, emailPageModel);
        LOG.info("appEvent=AdnocPartialOrderCompletion,inti method start with order: {}", orderProcessModel.getOrder());

        orderData = getOrderConverter().convert(orderProcessModel.getOrder());
        UserModel userModel = orderProcessModel.getOrder().getUser();
        if (userModel instanceof B2BCustomerModel b2BCustomerModel)
        {
            String firstName = b2BCustomerModel.getFirstName();
            put("firstName", firstName);
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

    @Override
    protected LanguageModel getEmailLanguage(final OrderProcessModel orderProcessModel)
    {
        return getSite(orderProcessModel).getDefaultLanguage();
    }

    protected Converter<OrderModel, OrderData> getOrderConverter()
    {
        return orderConverter;
    }

    public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
    {
        this.orderConverter = orderConverter;
    }

    public OrderData getOrder()
    {
        return orderData;
    }
}
