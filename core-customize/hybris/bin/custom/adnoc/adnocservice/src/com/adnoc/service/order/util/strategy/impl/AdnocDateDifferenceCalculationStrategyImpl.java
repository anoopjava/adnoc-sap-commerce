package com.adnoc.service.order.util.strategy.impl;

import com.adnoc.service.order.util.strategy.AdnocDateDifferenceCalculationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class AdnocDateDifferenceCalculationStrategyImpl implements AdnocDateDifferenceCalculationStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocDateDifferenceCalculationStrategyImpl.class);

    public static long calculateDiffWithCurrentDate(final Date date, final ChronoUnit chronoUnit)
    {
        LOG.debug("appEvent=AdnocDateDifferenceCalculation,calculating difference with current date={}", date);
        ServicesUtil.validateParameterNotNull(date, "Parameter date must not be null");
        final LocalDate deliveryDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final LocalDate currentDate = LocalDate.now();
        return Math.abs(chronoUnit.between(currentDate, deliveryDate));
    }

    @Override
    public boolean isCancellable(final AbstractOrderEntryModel orderEntryModel, final int adnocConfigValue)
    {
        LOG.debug("appEvent=AdnocDateDifferenceCalculation, isCancellable method called...");

        final long differenceInDays = calculateDiffWithCurrentDate(orderEntryModel.getNamedDeliveryDate(), ChronoUnit.DAYS);
        return differenceInDays > adnocConfigValue;
    }

    @Override
    public boolean isReturnable(final OrderModel orderModel, final int adnocConfigValue)
    {
        LOG.debug("appEvent=AdnocDateDifferenceCalculation, isReturnable method called...");

        final long returnableDays = calculateDiffWithCurrentDate(orderModel.getCompletionDate(), ChronoUnit.DAYS);
        return returnableDays <= adnocConfigValue;
    }
}
