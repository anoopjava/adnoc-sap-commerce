package com.adnoc.service.cancellation;

import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.exceptions.OrderCancelRecordsHandlerException;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelRecordsHandler;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdnocOrderCancelRecordsHandlerImpl extends DefaultOrderCancelRecordsHandler
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderCancelRecordsHandlerImpl.class);

    @Override
    protected OrderCancelRecordEntryModel createCancelRecordEntry(final OrderCancelRequest request, final OrderModel order, final OrderCancelRecordModel cancelRecord, final OrderHistoryEntryModel snapshot, final Map<Integer, AbstractOrderEntryModel> originalOrderEntriesMapping) throws OrderCancelRecordsHandlerException
    {
        LOG.info("appEvent=AdnocOrderCancel, Removing header level cancel reason");
        final OrderCancelRecordEntryModel cancelRecordEntry = getModelService().create(OrderCancelRecordEntryModel.class);
        cancelRecordEntry.setTimestamp(new Date());
        cancelRecordEntry.setCode(generateEntryCode(snapshot));
        cancelRecordEntry.setOriginalVersion(snapshot);
        cancelRecordEntry.setModificationRecord(cancelRecord);
        cancelRecordEntry.setPrincipal(getUserService().getCurrentUser());
        cancelRecordEntry.setOwner(cancelRecord);
        cancelRecordEntry.setStatus(OrderModificationEntryStatus.INPROGRESS);
        cancelRecordEntry.setCancelResult(request.isPartialCancel() ? OrderCancelEntryStatus.PARTIAL : OrderCancelEntryStatus.FULL);
        cancelRecordEntry.setNotes(request.getNotes());
        getModelService().save(cancelRecordEntry);
        final List<OrderEntryModificationRecordEntryModel> orderEntriesRecords = new ArrayList<>();

        for (final OrderCancelEntry cancelRequestEntry : request.getEntriesToCancel())
        {
            final OrderEntryCancelRecordEntryModel orderEntryRecordEntry = getModelService().create(OrderEntryCancelRecordEntryModel.class);
            orderEntryRecordEntry.setCode(cancelRequestEntry.getOrderEntry().getPk().toString());
            orderEntryRecordEntry.setCancelRequestQuantity((int) cancelRequestEntry.getCancelQuantity());
            orderEntryRecordEntry.setModificationRecordEntry(cancelRecordEntry);
            orderEntryRecordEntry.setOrderEntry((OrderEntryModel) cancelRequestEntry.getOrderEntry());
            orderEntryRecordEntry.setOriginalOrderEntry(getOriginalOrderEntry(originalOrderEntriesMapping, cancelRequestEntry));
            orderEntryRecordEntry.setNotes(cancelRequestEntry.getNotes());
            orderEntryRecordEntry.setCancelReason(cancelRequestEntry.getCancelReason());
            getModelService().save(orderEntryRecordEntry);
            orderEntriesRecords.add(orderEntryRecordEntry);
        }

        cancelRecordEntry.setOrderEntriesModificationEntries(orderEntriesRecords);
        getModelService().save(cancelRecordEntry);
        return cancelRecordEntry;
    }
}
