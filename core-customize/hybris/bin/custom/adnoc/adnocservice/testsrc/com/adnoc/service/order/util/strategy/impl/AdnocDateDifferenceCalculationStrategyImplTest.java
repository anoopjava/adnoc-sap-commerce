package com.adnoc.service.order.util.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocDateDifferenceCalculationStrategyImplTest
{
    @Mock
    private AbstractOrderEntryModel orderEntryModel;
    @Mock
    private OrderModel orderModel;
    @InjectMocks
    private AdnocDateDifferenceCalculationStrategyImpl adnocDateDifferenceCalculationStrategy;

    @Test
    public void testCalculateDiffWithCurrentDate()
    {
        final LocalDate currentDate = LocalDate.now();
        final LocalDate pastDate = currentDate.minusDays(5);
        final LocalDate futureDate = currentDate.plusDays(10);
        final Date pastDateConverted = Date.from(pastDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        final Date futureDateConverted = Date.from(futureDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        final long diffPast = AdnocDateDifferenceCalculationStrategyImpl.calculateDiffWithCurrentDate(pastDateConverted, ChronoUnit.DAYS);
        final long diffFuture = AdnocDateDifferenceCalculationStrategyImpl.calculateDiffWithCurrentDate(futureDateConverted, ChronoUnit.DAYS);
        assertEquals(5, diffPast);
        assertEquals(10, diffFuture);
    }

    @Test
    public void testIsCancellable()
    {
        final LocalDate deliveryDate = LocalDate.now().minusDays(10);
        when(orderEntryModel.getNamedDeliveryDate()).thenReturn(Date.from(deliveryDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        final int adnocConfigValue = 5;
        final boolean result = adnocDateDifferenceCalculationStrategy.isCancellable(orderEntryModel, adnocConfigValue);
        assertTrue(result);
    }

    @Test
    public void testIsNotCancellable()
    {
        final LocalDate deliveryDate = LocalDate.now().minusDays(3);
        when(orderEntryModel.getNamedDeliveryDate()).thenReturn(Date.from(deliveryDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        final int adnocConfigValue = 5;
        final boolean result = adnocDateDifferenceCalculationStrategy.isCancellable(orderEntryModel, adnocConfigValue);
        assertFalse(result);
    }

    @Test
    public void testIsReturnable()
    {
        final LocalDate completionDate = LocalDate.now().minusDays(2);
        when(orderModel.getCompletionDate()).thenReturn(Date.from(completionDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        final int adnocConfigValue = 5;
        final boolean result = adnocDateDifferenceCalculationStrategy.isReturnable(orderModel, adnocConfigValue);
        assertTrue(result);
    }

    @Test
    public void testIsNotReturnable()
    {
        final LocalDate completionDate = LocalDate.now().minusDays(6);
        when(orderModel.getCompletionDate()).thenReturn(Date.from(completionDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        final int adnocConfigValue = 5;
        final boolean result = adnocDateDifferenceCalculationStrategy.isReturnable(orderModel, adnocConfigValue);
        assertFalse(result);
    }
}