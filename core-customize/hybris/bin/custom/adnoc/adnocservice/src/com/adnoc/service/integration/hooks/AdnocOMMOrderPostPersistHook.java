package com.adnoc.service.integration.hooks;

import com.adnoc.service.config.AdnocConfigService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AdnocOMMOrderPostPersistHook implements PostPersistHook
{
    private static final Logger LOG = LogManager.getLogger(AdnocOMMOrderPostPersistHook.class);
    public static final String ADNOC_SITE = "adnoc";
    public static final String ADNOC_STORE = "adnoc";

    private ModelService modelService;
    private BusinessProcessService businessProcessService;
    private BaseSiteService baseSiteService;
    private CommonI18NService commonI18NService;
    private BaseStoreService baseStoreService;
    private EnumerationService enumerationService;
    private AdnocConfigService adnocConfigService;

    @Override
    public void execute(final ItemModel item, final PersistenceContext context)
    {
        if (item instanceof final OrderModel orderModel)
        {
            LOG.info("appEvent=AdnocOMMOrderInbound, Setting consignment status for order {}", orderModel.getCode());
            setConsignmentStatus(orderModel.getConsignments());
            LOG.info("appEvent=AdnocOMMOrderInbound, Setting completion date for order {}", orderModel.getCode());
            setOrderCompletionDate(orderModel);
            LOG.info("appEvent=AdnocOMMOrderInbound, Order status: {}", orderModel.getStatus());
            setCancelledOrderStatus(orderModel);

            if (Objects.equals(OrderStatus.CONFIRMED, orderModel.getStatus()))
            {
                startBusinessProcess(orderModel, "orderConfirmationEmailProcess", ADNOC_SITE, ADNOC_STORE);
            }
            else if (Objects.equals(OrderStatus.PARTIAL_COMPLETED, orderModel.getStatus()))
            {
                startBusinessProcess(orderModel, "adnocPartialOrderCompletionEmailProcess", ADNOC_SITE, ADNOC_STORE);
            }
            else if (Objects.equals(OrderStatus.COMPLETED, orderModel.getStatus()))
            {
                startBusinessProcess(orderModel, "adnocOrderCompletedEmailProcess", ADNOC_SITE, ADNOC_STORE);
            }
        }
    }

    private void setCancelledOrderStatus(final OrderModel orderModel)
    {
        if (CollectionUtils.isNotEmpty(orderModel.getEntries()))
        {
            LOG.info("appEvent=AdnocOMMOrderInbound, Checking for cancelled order status for order {}", orderModel.getCode());
            final boolean isCancelled = orderModel.getEntries().stream()
                    .filter(abstractOrderEntryModel -> abstractOrderEntryModel instanceof OrderEntryModel).map(OrderEntryModel.class::cast)
                    .allMatch(orderEntryModel -> Objects.equals(SAPOrderStatus.CANCELLED, orderEntryModel.getSapLineItemOrderStatus())
                            || Objects.equals(SAPOrderStatus.CANCELLED_FROM_ERP, orderEntryModel.getSapLineItemOrderStatus()));
            if (isCancelled)
            {
                LOG.info("appEvent=AdnocOMMOrderInbound, Setting order status to CANCELLED for order {}", orderModel.getCode());
                orderModel.setStatus(OrderStatus.CANCELLED);
                getModelService().save(orderModel);
            }
        }
    }

    private void setConsignmentStatus(final Set<ConsignmentModel> consignments)
    {
        if (CollectionUtils.isNotEmpty(consignments))
        {
            LOG.info("appEvent=AdnocOMMOrderInbound, Setting consignment status for consignments: {}", consignments.stream().map(ConsignmentModel::getCode).collect(Collectors.joining(", ")));
            final Set<ConsignmentModel> consignmentModels = consignments.stream()
                    .filter(consignment -> CollectionUtils.isNotEmpty(consignment.getConsignmentEntries()))
                    .filter(consignment -> consignment.getConsignmentEntries().stream()
                            .allMatch(entry -> Objects.equals(ConsignmentEntryStatus.SHIPPED, entry.getStatus())))
                    .collect(Collectors.toSet());
            consignmentModels.forEach(consignmentModel -> consignmentModel.setStatus(ConsignmentStatus.SHIPPED));
            getModelService().saveAll(consignmentModels);
        }
    }

    private void setOrderCompletionDate(final OrderModel orderModel)
    {
        LOG.info("appEvent=AdnocOMMOrderInbound, Setting completion date for order {}", orderModel.getCode());
        if (CollectionUtils.isNotEmpty(orderModel.getConsignments()) && orderModel.getConsignments().stream()
                .allMatch(consignment -> Objects.equals(ConsignmentStatus.SHIPPED, consignment.getStatus())))
        {
            orderModel.setCompletionDate(new Date());
            getModelService().save(orderModel);
            LOG.info("appEvent=AdnocOMMOrderInbound, Set completion date for order {} to {}", orderModel.getCode(), orderModel.getCompletionDate());
        }
    }

    private void startBusinessProcess(final OrderModel orderModel, final String processName, final String site, final String store)
    {
        LOG.info("appEvent=AdnocOMMOrderInbound,Creating business process:{} for Order:{} at site:{} and store:{}", processName, orderModel.getCode(), site, store);

        final OrderProcessModel orderProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
                processName + "-" + orderModel.getCode() + "-" + System.currentTimeMillis(),
                processName);

        final UserModel customer = orderModel.getUser();
        orderModel.setUser(customer);
        getModelService().save(orderModel);

        orderProcessModel.setOrder(orderModel);
        getModelService().save(orderProcessModel);

        LOG.debug("appEvent=AdnocOMMOrderInbound,start business process:{} for order:{} at site:{} and store:{}", processName, orderModel.getCode(), site, store);
        getBusinessProcessService().startProcess(orderProcessModel);

        LOG.info("appEvent=AdnocOMMOrderInbound, AdnocOMMOrderPostPersistHook executed....");
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
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

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    public AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }
}

