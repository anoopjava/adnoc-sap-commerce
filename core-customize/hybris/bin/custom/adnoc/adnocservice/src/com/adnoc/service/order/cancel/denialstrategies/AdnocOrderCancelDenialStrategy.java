package com.adnoc.service.order.cancel.denialstrategies;

import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.order.util.strategy.AdnocDateDifferenceCalculationStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.AbstractCancelDenialStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocOrderCancelDenialStrategy extends AbstractCancelDenialStrategy implements OrderCancelDenialStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderCancelDenialStrategy.class);
    public static final String MINIMUM_CANCELLABLE_DAYS = "minCancellableDays";
    public static final int DEFAULT_AUTO_CANCELLATION_DAYS = 0;

    private AdnocDateDifferenceCalculationStrategy adnocDateDifferenceCalculationStrategy;
    private AdnocConfigService adnocConfigService;

    @Override
    public OrderCancelDenialReason getCancelDenialReason(final OrderCancelConfigModel configuration, final OrderModel order, final PrincipalModel requester, final boolean b, final boolean b1)
    {
        LOG.info("appEvent=AdnocOrderCancel, getCancelDenialReason being Method called");
        ServicesUtil.validateParameterNotNull(configuration, "Parameter configuration must not be null");
        final int adnocConfigValue = getAdnocConfigService().getAdnocConfigValue(MINIMUM_CANCELLABLE_DAYS, DEFAULT_AUTO_CANCELLATION_DAYS);
        final boolean isAnyEntriesCancellable = order.getEntries().stream().filter(abstractOrderEntryModel -> Objects.nonNull(abstractOrderEntryModel.getNamedDeliveryDate()))
                .anyMatch(orderEntryModel -> getAdnocDateDifferenceCalculationStrategy().isCancellable(orderEntryModel, adnocConfigValue));
        return isAnyEntriesCancellable ? null : getReason();
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    protected AdnocDateDifferenceCalculationStrategy getAdnocDateDifferenceCalculationStrategy()
    {
        return adnocDateDifferenceCalculationStrategy;
    }

    public void setAdnocDateDifferenceCalculationStrategy(final AdnocDateDifferenceCalculationStrategy adnocDateDifferenceCalculationStrategy)
    {
        this.adnocDateDifferenceCalculationStrategy = adnocDateDifferenceCalculationStrategy;
    }

}
