package com.adnoc.service.integration.outbound.service.impl;

import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.sapcpiorderexchange.actions.SapCpiOmmOrderOutboundCancellationAction;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;

public class AdnocSapCpiOmmOrderOutboundCancellationAction extends SapCpiOmmOrderOutboundCancellationAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocSapCpiOmmOrderOutboundCancellationAction.class);
    public static final String ADNOC_SITE = "adnoc";
    public static final String ADNOC_STORE = "adnoc";
    private EnumerationService enumerationService;
    private BusinessProcessService businessProcessService;
    private BaseSiteService baseSiteService;
    private CommonI18NService commonI18NService;
    private BaseStoreService baseStoreService;

    @Override
    public void run(final TaskService paramTaskService, final TaskModel taskModel) throws RetryLaterException
    {
        LOG.info("appEvent=AdnocSapCpiOmmOrderOutboundCancellation, cancel order task runner started");
        final OrderCancelRecordEntryModel orderCancelRecordEntry = (OrderCancelRecordEntryModel) taskModel.getContextItem();

        getSapCpiOutboundService().sendOrderCancellation(getSapCpiOrderOutboundConversionService().convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntry).iterator().next()).subscribe(

                // onNext
                responseEntityMap -> {
                    if (isSentSuccessfully(responseEntityMap))
                    {
                        processSuccessfulOrderCancellation(orderCancelRecordEntry, responseEntityMap);
                    }
                    else
                    {
                        handleOrderCancellationFailure(orderCancelRecordEntry);
                        LOG.error(String.format("The OMM order [%s] cancellation request has not been sent to the SAP backend! %n%s",
                                orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));
                    }
                }
                // onError
                , error -> {
                    handleOrderCancellationFailure(orderCancelRecordEntry);
                    LOG.error(String.format("The OMM order [%s] cancellation request has not been sent to the SAP backend through SCPI! %n%s",
                            orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), error.getMessage()));
                }
        );

    }

    private void processSuccessfulOrderCancellation(final OrderCancelRecordEntryModel cancelEntry, final ResponseEntity<Map> responseEntityMap)
    {
        final OrderModificationRecordModel modificationRecord = cancelEntry.getModificationRecord();
        cancelEntry.setExported(Boolean.TRUE);
        final OrderModel order = modificationRecord.getOrder();

        // Mark modification entries as SUCCESSFUL and update model
        markAllModificationEntriesSuccessful(modificationRecord);

        // Update order-level statuses
        final String rejectionStatusHeader = getPropertyValue(responseEntityMap, "rejectionStatusHeader");
        final String overAllOrderStatus = getPropertyValue(responseEntityMap, "overAllOrderStatus");
        final OrderStatus orderLevelStatuses = getOrderLevelStatuses(order);
        final SAPOrderStatus sapOrderStatus = StringUtils.isNotEmpty(rejectionStatusHeader) ? SAPOrderStatus.valueOf(rejectionStatusHeader)
                : Objects.equals(orderLevelStatuses, OrderStatus.PARTIAL_REJECTED) ? SAPOrderStatus.PARTIAL_REJECTED : SAPOrderStatus.EVERYTHING_REJECTED;
        order.setStatus(StringUtils.isNotEmpty(overAllOrderStatus) ? OrderStatus.valueOf(overAllOrderStatus) : orderLevelStatuses);
        order.setSapOrderStatus(sapOrderStatus);

        // Update order entries based on SAP response items
        updateOrderEntriesFromSapResponse(order, modificationRecord, responseEntityMap, sapOrderStatus);

        getModelService().saveAll(order, modificationRecord, cancelEntry);
        //Triggering email for order cancellation confirmation

       if (Objects.equals(OrderStatus.PARTIAL_COMPLETED, order.getStatus()))
        {
            startBusinessProcess(order, "adnocPartialOrderCancelledEmailProcess");
        }
        else if (Objects.equals(OrderStatus.CANCELLED, order.getStatus()))
        {
            startBusinessProcess(order, "adnocOrderCancelledEmailProcess");
        }
        LOG.info(String.format("The OMM order [%s] cancellation request has been successfully sent to the SAP backend through SCPI! %n%s", order.getCode(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));
    }

    private void markAllModificationEntriesSuccessful(final OrderModificationRecordModel modificationRecord)
    {
        modificationRecord.getModificationRecordEntries().forEach(entry -> {
            entry.setStatus(OrderModificationEntryStatus.SUCCESSFULL);
            getModelService().save(entry);
        });
        modificationRecord.setInProgress(Boolean.FALSE);
    }

    private void updateOrderEntriesFromSapResponse(final OrderModel order,
                                                   final OrderModificationRecordModel modificationRecord,
                                                   final ResponseEntity<Map> responseEntityMap, final SAPOrderStatus orderLevelStatuses)
    {
        final List<Map<String, String>> items = extractSapCpiOutboundOrderItems(responseEntityMap);
        for (final Map<String, String> item : items)
        {
            final ConsignmentEntryStatus notDelivered = getEnumerationService().getEnumerationValue(ConsignmentEntryStatus.class, "NOT_DELIVERED");
            final Integer entryNumber = Integer.valueOf(item.get("entryNumber"));
            final SAPOrderStatus lineItemOrderStatus = ObjectUtils.isNotEmpty(item.get("lineItemOrderStatus"))
                    ? SAPOrderStatus.valueOf(item.get("lineItemOrderStatus")) : orderLevelStatuses;
            final ConsignmentEntryStatus deliveryStatus = ObjectUtils.isNotEmpty(item.get("deliveryStatus"))
                    ? ConsignmentEntryStatus.valueOf(item.get("deliveryStatus")) : notDelivered;

            if (ObjectUtils.allNotNull(entryNumber, lineItemOrderStatus, deliveryStatus))
            {
                order.getEntries().stream()
                        .filter(e -> Objects.equals(e.getEntryNumber(), entryNumber))
                        .map(OrderEntryModel.class::cast)
                        .findFirst().ifPresent(entry -> {
                            // Set SAP item-level status
                            entry.setSapLineItemOrderStatus(lineItemOrderStatus);

                            // Update latest consignment entry, if present
                            updateLatestConsignmentEntryStatus(entry, deliveryStatus);

                            // Set cancelled quantity
                            setCancelledQuantity(modificationRecord, entry);
                            getModelService().save(entry);

                        });
            }
        }
    }

    private void updateLatestConsignmentEntryStatus(final AbstractOrderEntryModel entry, final ConsignmentEntryStatus deliveryStatus)
    {
        if (CollectionUtils.isNotEmpty(entry.getConsignmentEntries()))
        {
            entry.getConsignmentEntries().stream()
                    .filter(Objects::nonNull)
                    .max(Comparator.comparing(ConsignmentEntryModel::getCreationtime))
                    .ifPresent(latest -> {
                        latest.setStatus(deliveryStatus);
                        getModelService().save(latest);
                    });
        }
    }

    private void setCancelledQuantity(final OrderModificationRecordModel modificationRecord,
                                      final AbstractOrderEntryModel entry)
    {
        modificationRecord.getModificationRecordEntries().stream()
                .flatMap(mre -> mre.getOrderEntriesModificationEntries().stream())
                .filter(OrderEntryCancelRecordEntryModel.class::isInstance).map(OrderEntryCancelRecordEntryModel.class::cast)
                .filter(orderEntryCancelRecordEntryModel -> Objects.equals(orderEntryCancelRecordEntryModel.getOrderEntry(), entry))
                .forEach(orderEntryCancelRecordEntryModel -> {
                    orderEntryCancelRecordEntryModel.setCancelledQuantity(Math.toIntExact(orderEntryCancelRecordEntryModel.getCancelRequestQuantity()));
                    getModelService().save(orderEntryCancelRecordEntryModel);
                });
    }


    @SuppressWarnings("unchecked")
    public static Object getRawPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property)
    {
        if (responseEntityMap != null && responseEntityMap.getBody() != null)
        {
            final Object rootKey = responseEntityMap.getBody().keySet().stream().findFirst().orElse(null);
            if (ObjectUtils.isNotEmpty(rootKey))
            {
                final Object nestedMap = responseEntityMap.getBody().get(rootKey);
                if (nestedMap instanceof Map)
                {
                    return ((Map<String, Object>) nestedMap).get(property);
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, String>> extractSapCpiOutboundOrderItems(final ResponseEntity<Map> responseEntityMap)
    {
        final Object sapCpiOutboundOrderItemsRaw = getRawPropertyValue(responseEntityMap, "sapCpiOutboundOrderItems");

        if (sapCpiOutboundOrderItemsRaw instanceof Map)
        {
            final Map<String, Object> sapCpiOutboundOrderItems = (Map<String, Object>) sapCpiOutboundOrderItemsRaw;
            final Object results = sapCpiOutboundOrderItems.get("results");

            if (results instanceof List)
            {
                return ((List<?>) results).stream()
                        .filter(item -> item instanceof Map)
                        .map(item -> (Map<String, String>) item)
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    private void startBusinessProcess(final OrderModel orderModel, final String processName)
    {
        final OrderProcessModel orderProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
                processName + "-" + orderModel.getCode() + "-" + System.currentTimeMillis(),
                processName);

        final UserModel customer = orderModel.getUser();
        orderModel.setUser(customer);
        getModelService().save(orderModel);

        orderProcessModel.setOrder(orderModel);
        getModelService().save(orderProcessModel);

        getBusinessProcessService().startProcess(orderProcessModel);

        LOG.info("appEvent=AdnocSapCpiOmmOrderOutboundCancellation, AdnocOMMOrderPostPersistHook executed....");
    }

    private OrderStatus getOrderLevelStatuses(final OrderModel order)
    {
        // Flatten all modification entry order entries into a set
        final Set<?> modifiedOrderEntries = order.getModificationRecords().stream()
                .flatMap(record -> record.getModificationRecordEntries().stream())
                .flatMap(entry -> entry.getOrderEntriesModificationEntries().stream())
                .map(OrderEntryModificationRecordEntryModel::getOrderEntry)
                .collect(Collectors.toSet());

        final boolean allMatched = order.getEntries().stream().allMatch(modifiedOrderEntries::contains);

        return allMatched ? OrderStatus.EVERYTHING_REJECTED : OrderStatus.PARTIAL_REJECTED;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    protected BusinessProcessService getBusinessProcessService()
    {
        return businessProcessService;
    }

    public void setBusinessProcessService(final BusinessProcessService businessProcessService)
    {
        this.businessProcessService = businessProcessService;
    }

    protected BaseSiteService getBaseSiteService()
    {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService)
    {
        this.baseSiteService = baseSiteService;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }

    protected BaseStoreService getBaseStoreService()
    {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService)
    {
        this.baseStoreService = baseStoreService;
    }
}
