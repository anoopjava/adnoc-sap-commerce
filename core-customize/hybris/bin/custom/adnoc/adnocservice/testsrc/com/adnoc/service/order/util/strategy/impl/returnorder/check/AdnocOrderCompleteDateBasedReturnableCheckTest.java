package com.adnoc.service.order.util.strategy.impl.returnorder.check;

import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.order.returnorder.check.AdnocOrderCompleteDateBasedReturnableCheck;
import com.adnoc.service.order.util.strategy.AdnocDateDifferenceCalculationStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocOrderCompleteDateBasedReturnableCheckTest
{

    @Mock
    private AdnocConfigService adnocConfigService;

    @Mock
    private AdnocDateDifferenceCalculationStrategy adnocDateDifferenceCalculationStrategy;

    @Mock
    private OrderModel orderModel;

    @Mock
    private AbstractOrderEntryModel abstractOrderEntryModel;

    @InjectMocks
    private AdnocOrderCompleteDateBasedReturnableCheck adnocOrderCompleteDateBasedReturnableCheck;

    @Test
    public void testPerform_Returnable()
    {
        final int maxReturnableDays = 10;
        final Date completionDate = new Date();
        final long returnQuantity = 1L;
        when(orderModel.getCompletionDate()).thenReturn(completionDate);
        when(adnocConfigService.getAdnocConfigValue(AdnocOrderCompleteDateBasedReturnableCheck.MAXIMUM_RETURNABLE_DAYS, AdnocOrderCompleteDateBasedReturnableCheck.DEFAULT_AUTO_RETURNABLE_DAYS))
                .thenReturn(maxReturnableDays);
        when(adnocDateDifferenceCalculationStrategy.isReturnable(orderModel, maxReturnableDays)).thenReturn(true);
        final boolean result = adnocOrderCompleteDateBasedReturnableCheck.perform(orderModel, abstractOrderEntryModel, returnQuantity);
        assertTrue(result);
        verify(adnocConfigService).getAdnocConfigValue(AdnocOrderCompleteDateBasedReturnableCheck.MAXIMUM_RETURNABLE_DAYS, AdnocOrderCompleteDateBasedReturnableCheck.DEFAULT_AUTO_RETURNABLE_DAYS);
        verify(adnocDateDifferenceCalculationStrategy).isReturnable(orderModel, maxReturnableDays);
    }
}