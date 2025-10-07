package com.adnoc.backoffice.actions;

import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacadeOperationResult;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdnocOutboundReplicationCockpitAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<Object, Object>
{
    private static final Logger LOG = LogManager.getLogger(AdnocOutboundReplicationCockpitAction.class);

    @Resource(name = "notificationService")
    private NotificationService notificationService;

    @Resource(name = "objectFacade")
    public ObjectFacade objectFacade;

    @Resource(name = "adnocOutboundReplicationDirector")
    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;

    @Override
    public ActionResult<Object> perform(final ActionContext<Object> actionContext)
    {
        final List<Object> ctxObjects = getDataAsCollection(actionContext);

        ActionResult<Object> actionResult = null;
        if (CollectionUtils.isNotEmpty(ctxObjects))
        {
            LOG.info("appEvent=AdnocOutboundReplicationCockpitAction,Outbound replcaition which are selected in the list");
            try
            {
                ctxObjects.forEach(item -> adnocOutboundReplicationDirector.scheduleOutboundTask(item));
                notificationService.notifyUser("", "JustMessage", NotificationEvent.Level.SUCCESS, new Object[]{actionContext.getLabel("adnocbackoffice.item.outbound.replication.success")});
                actionResult = new ActionResult(ActionResult.SUCCESS);
                actionResult.getStatusFlags().add(ActionResult.StatusFlag.OBJECT_PERSISTED);
            }
            catch (final Exception exception)
            {
                LOG.debug("appEvent=AdnocOutboundReplicationCockpitAction, {}", exception.getMessage());
                notificationService.notifyUser("", "JustMessage", NotificationEvent.Level.FAILURE, new Object[]{actionContext.getLabel("adnocbackoffice.item.outbound.replication.failure")});
                actionResult = new ActionResult(ActionResult.ERROR);
            }
        }
        else
        {
            actionResult = new ActionResult(ActionResult.ERROR);
        }
        return actionResult;
    }

    @Override
    public boolean canPerform(final ActionContext<Object> ctx)
    {
        if (ctx.getData() == null)
        {
            return false;
        }
        else if (ctx.getData() instanceof Collection)
        {
            final Collection selectedItems = (Collection) ctx.getData();
            final ObjectFacadeOperationResult reloadResult = objectFacade.reload(selectedItems);
            final Collection refreshedItems = reloadResult.getSuccessfulObjects();
            return isCollectionOutboundReplication(refreshedItems);
        }
        return false;
    }

    protected boolean isCollectionOutboundReplication(final Collection<?> collection)
    {
        return CollectionUtils.isNotEmpty(collection);
    }

    private List<Object> getDataAsCollection(final ActionContext<Object> ctx)
    {
        final List<Object> ctxObjects = new ArrayList();
        if (ctx.getData() instanceof Collection)
        {
            ctxObjects.addAll((Collection) ctx.getData());
        }
        else
        {
            ctxObjects.add(ctx.getData());
        }

        return ctxObjects;
    }

}
