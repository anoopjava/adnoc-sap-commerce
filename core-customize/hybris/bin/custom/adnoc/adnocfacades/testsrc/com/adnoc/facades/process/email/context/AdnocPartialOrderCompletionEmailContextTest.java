package com.adnoc.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerEmailResolutionService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AdnocPartialOrderCompletionEmailContextTest
{
    @InjectMocks
    private AdnocPartialOrderCompletionEmailContext adnocPartialOrderCompletionEmailContext;
    @Mock
    private OrderProcessModel orderProcessModel;
    @Mock
    private BaseSiteModel baseSiteModel;
    @Mock
    private CustomerModel customerModel;
    @Mock
    private LanguageModel languageModel;
    @Mock
    private EmailPageModel emailPageModel;
    @Mock
    private Converter<OrderModel, OrderData> orderConverter;
    @Mock
    private OrderModel orderModel;
    @Mock
    private DefaultCustomerEmailResolutionService customerEmailResolutionService;

    @Test
    public void testInit()
    {
        Mockito.when(orderProcessModel.getOrder()).thenReturn(orderModel);
        Mockito.when(orderModel.getSite()).thenReturn(baseSiteModel);
        Mockito.when(orderModel.getUser()).thenReturn(customerModel);
        Mockito.when(baseSiteModel.getDefaultLanguage()).thenReturn(languageModel);
        Mockito.when(languageModel.getIsocode()).thenReturn("en");
        final OrderData orderData = Mockito.mock(OrderData.class);
        Mockito.when(orderConverter.convert(orderModel)).thenReturn(orderData);

        customerEmailResolutionService.getEmailForCustomer(customerModel);
        adnocPartialOrderCompletionEmailContext.init(orderProcessModel, emailPageModel);

    }
}
