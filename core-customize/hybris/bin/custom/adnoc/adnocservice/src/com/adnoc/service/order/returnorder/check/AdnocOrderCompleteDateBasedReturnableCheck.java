package com.adnoc.service.order.returnorder.check;

import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.order.util.strategy.AdnocDateDifferenceCalculationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.strategy.ReturnableCheck;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocOrderCompleteDateBasedReturnableCheck implements ReturnableCheck
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderCompleteDateBasedReturnableCheck.class);
    public static final String MAXIMUM_RETURNABLE_DAYS = "maxReturnableDays";
    public static final int DEFAULT_AUTO_RETURNABLE_DAYS = 0;

    private AdnocDateDifferenceCalculationStrategy adnocDateDifferenceCalculationStrategy;
    private AdnocConfigService adnocConfigService;

    @Override
    public boolean perform(final OrderModel orderModel, final AbstractOrderEntryModel abstractOrderEntryModel, final long returnQuantity)
    {
        ServicesUtil.validateParameterNotNull(orderModel, "Parameter configuration must not be null");
        return anyLineItemReturnable(orderModel);
    }

    private boolean anyLineItemReturnable(final OrderModel orderModel)
    {
        if (CollectionUtils.isNotEmpty(orderModel.getEntries()))
        {
            final boolean isReturnable = orderModel.getEntries().stream().map(OrderEntryModel.class::cast)
                    .anyMatch(entry -> (Objects.equals(SAPOrderStatus.COMPLETED, entry.getSapLineItemOrderStatus())
                            || Objects.equals(SAPOrderStatus.PARTIAL_COMPLETED, entry.getSapLineItemOrderStatus())) && maxReturnQuantity(orderModel, entry) > 0);
            LOG.info("appEvent=AdnocOrder, anyLineItemReturnable: {}", isReturnable);
            return isReturnable;
        }
        return Boolean.FALSE;
    }

    @Override
    public long maxReturnQuantity(final OrderModel order, final AbstractOrderEntryModel entry)
    {
        final OrderEntryModel orderEntry = (OrderEntryModel) entry;
        return orderEntry.getQuantityShipped() - orderEntry.getQuantityReturned();
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
