package com.adnoc.service.cancellation;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.saporderexchangeoms.cancellation.SapOmsOrderCancelStateMappingStrategy;

import java.util.List;

public class AdnocSapOmsOrderCancelStateMappingStrategy extends SapOmsOrderCancelStateMappingStrategy
{
    final List<OrderStatus> nonCancellableStatuses = List.of(
            OrderStatus.CANCELLED, OrderStatus.COMPLETED, OrderStatus.EVERYTHING_REJECTED,
            OrderStatus.CREATED, OrderStatus.PARTIAL_REJECTED, OrderStatus.ORDER_SPLIT,
            OrderStatus.CHECKED_INVALID, OrderStatus.CHECKED_VALID);

    @Override
    public OrderCancelState getOrderCancelState(final OrderModel order)
    {
        final boolean isConsignmentCancellable = order.getConsignments().stream()
                .noneMatch(consignment -> //
                        consignment.getStatus().equals(ConsignmentStatus.PICKPACK) ||
                                consignment.getStatus().equals(ConsignmentStatus.SHIPPED) ||
                                consignment.getStatus().equals(ConsignmentStatus.CANCELLED));

        final boolean isOrderCancellable = !nonCancellableStatuses.contains(order.getStatus());

        return isOrderCancellable && isConsignmentCancellable ? OrderCancelState.SENTTOWAREHOUSE
                : OrderCancelState.CANCELIMPOSSIBLE;

    }
}
