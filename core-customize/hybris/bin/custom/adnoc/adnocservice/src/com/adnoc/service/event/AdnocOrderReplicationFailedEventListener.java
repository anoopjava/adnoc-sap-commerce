package com.adnoc.service.event;

import de.hybris.platform.b2bacceleratorservices.event.AbstractOrderEventListener;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AdnocOrderReplicationFailedEventListener extends AbstractOrderEventListener<AdnocOrderReplicationFailedEvent>
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderReplicationFailedEventListener.class);
    public static final String PROCESS_NAME = "adnocOrderReplicationFailedEmailProcess";

    private BaseSiteService baseSiteService;
    private BaseStoreService baseStoreService;
    private CommonI18NService commonI18NService;

    @Override
    protected void onEvent(final AdnocOrderReplicationFailedEvent event)
    {
        LOG.info("appEvent=AdnocOrderReplication, AdnocOrderReplicationFailedEventListener started....");
        final OrderModel order = event.getOrder();
        final OrderProcessModel orderProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
                String.format(PROCESS_NAME + "-%s-%s", order.getCode(), System.currentTimeMillis()), PROCESS_NAME);

        final UserModel customer = order.getUser();
        order.setUser(customer);
        getModelService().save(order);

        orderProcessModel.setOrder(order);
        getModelService().save(orderProcessModel);

        getBusinessProcessService().startProcess(orderProcessModel);

        LOG.info("appEvent=AdnocOrderReplication, AdnocOrderReplicationFailedEventListener executed....");

    }

    protected BaseSiteService getBaseSiteService()
    {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService)
    {
        this.baseSiteService = baseSiteService;
    }

    protected BaseStoreService getBaseStoreService()
    {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService)
    {
        this.baseStoreService = baseStoreService;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }
}
