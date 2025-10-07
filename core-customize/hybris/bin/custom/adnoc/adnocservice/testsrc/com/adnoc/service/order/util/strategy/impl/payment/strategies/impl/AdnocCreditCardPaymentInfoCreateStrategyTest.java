package com.adnoc.service.order.util.strategy.impl.payment.strategies.impl;

import com.adnoc.service.constants.AdnocserviceConstants;
import com.adnoc.service.order.payment.strategies.impl.AdnocCreditCardPaymentInfoCreateStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocCreditCardPaymentInfoCreateStrategyTest
{

    @Mock
    private ModelService modelService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private UserModel userModel;

    @InjectMocks
    private AdnocCreditCardPaymentInfoCreateStrategy adnocCreditCardPaymentInfoCreateStrategy;

    @Captor
    private ArgumentCaptor<String> codeCaptor;

    @Test
    public void testCreatePaymentInfo()
    {
        final double amount = 100.0;
        final String userUid = "user123";
        when(userModel.getUid()).thenReturn(userUid);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(AdnocserviceConstants.CARDNUMBER)).thenReturn("1234567812345678");
        when(configuration.getString(AdnocserviceConstants.VALIDFROMMONTH)).thenReturn("01");
        when(configuration.getString(AdnocserviceConstants.VALIDTOMONTH)).thenReturn("12");
        when(configuration.getString(AdnocserviceConstants.VALIDYEAR)).thenReturn("2025");
        final CreditCardPaymentInfoModel creditCardPaymentInfoModelMock = mock(CreditCardPaymentInfoModel.class);
        when(modelService.create(CreditCardPaymentInfoModel.class)).thenReturn(creditCardPaymentInfoModelMock);
        final PaymentInfoModel result = adnocCreditCardPaymentInfoCreateStrategy.createPaymentInfo(amount, userModel);
        assertNotNull(result);
        assertTrue(result instanceof CreditCardPaymentInfoModel);
        verify(modelService).create(CreditCardPaymentInfoModel.class);
        verify(creditCardPaymentInfoModelMock).setAmount(amount);
        verify(creditCardPaymentInfoModelMock).setUser(userModel);
        verify(creditCardPaymentInfoModelMock).setNumber("1234567812345678");
        verify(creditCardPaymentInfoModelMock).setType(CreditCardType.MASTER);
        verify(creditCardPaymentInfoModelMock).setCcOwner(userUid);
        verify(creditCardPaymentInfoModelMock).setValidFromMonth("01");
        verify(creditCardPaymentInfoModelMock).setValidToMonth("12");
        verify(creditCardPaymentInfoModelMock).setValidToYear("2025");
        verify(creditCardPaymentInfoModelMock).setCode(codeCaptor.capture());
        final String capturedCode = codeCaptor.getValue();
        assertTrue(capturedCode.startsWith(userUid));
        assertTrue(capturedCode.contains("_"));
    }
}
