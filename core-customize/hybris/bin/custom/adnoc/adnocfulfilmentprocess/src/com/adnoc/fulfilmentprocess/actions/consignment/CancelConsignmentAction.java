/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.fulfilmentprocess.actions.consignment;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Mark consignment as cancelled.
 */
public class CancelConsignmentAction extends AbstractProceduralAction<ConsignmentProcessModel>
{
    @Override
    public void executeAction(final ConsignmentProcessModel process)
    {
        final ConsignmentModel consignment = process.getConsignment();
        if (consignment != null)
        {
            final ConsignmentStatus cancellationStatus = getCancellationStatus(consignment);
            if (Objects.nonNull(cancellationStatus))
            {
                consignment.setStatus(cancellationStatus);
                getModelService().save(consignment);
            }
        }
    }

    private ConsignmentStatus getCancellationStatus(final ConsignmentModel consignment)
    {
        final Set<OrderModificationRecordModel> modificationRecords = ((OrderModel) consignment.getOrder()).getModificationRecords();
        if (CollectionUtils.isEmpty(modificationRecords))
        {
            return null;
        }
        // Flatten all modification entry order entries into a set
        final Set<?> modifiedOrderEntries = modificationRecords.stream()
                .flatMap(record -> record.getModificationRecordEntries().stream())
                .flatMap(entry -> entry.getOrderEntriesModificationEntries().stream())
                .map(OrderEntryModificationRecordEntryModel::getOrderEntry)
                .collect(Collectors.toSet());

        final boolean allMatched = consignment.getConsignmentEntries().stream()
                .map(ConsignmentEntryModel::getOrderEntry)
                .allMatch(modifiedOrderEntries::contains);

        return allMatched ? ConsignmentStatus.CANCELLED : ConsignmentStatus.CANCELLING;
    }

}
