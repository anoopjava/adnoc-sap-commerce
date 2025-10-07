package com.adnoc.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.core.impl.DefaultWidgetModel;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;
import de.hybris.platform.commerceservices.order.OrderQuoteDiscountValuesAccessor;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.Objects;

public class AdnocRecalculateQuoteTotalsAction implements CockpitAction<QuoteModel, Object>
{
    private static final Logger LOG = LogManager.getLogger(AdnocRecalculateQuoteTotalsAction.class);

    private static final String CURRENT_OBJECT = "currentObject";

    @Resource(name = "calculationService")
    private CalculationService calculationService;

    @Resource(name = "notificationService")
    private NotificationService notificationService;

    @Resource(name = "orderQuoteDiscountValuesAccessor")
    private OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor;

    public AdnocRecalculateQuoteTotalsAction()
    {
        // Default constructor - no initialization needed since dependencies are injected via @Resource
    }

    @Override
    public ActionResult<Object> perform(final ActionContext<QuoteModel> actionContext)
    {
        final Object data = actionContext.getData();
        if (Objects.nonNull(data) && (data instanceof QuoteModel))
        {
            final QuoteModel quoteModel = (QuoteModel) actionContext.getData();
            LOG.info(String.format("appEvent=QuoteRecalculateTotal, Recalculating quote totals %s from Backoffice!", quoteModel.getCode()));

            try
            {
                if (Objects.nonNull(quoteModel.getGlobalDiscountValues()))
                {
                    orderQuoteDiscountValuesAccessor.setQuoteDiscountValues(quoteModel,
                            quoteModel.getGlobalDiscountValues());
                }
                calculationService.calculateTotals(quoteModel, true);
            }
            catch (final CalculationException calculationException)
            {
                LOG.debug("appEvent=QuoteRecalculateTotal, " + calculationException.getMessage(), calculationException);

                notificationService.notifyUser(notificationService.getWidgetNotificationSource(actionContext), "Re-calculate Quote",
                        NotificationEvent.Level.FAILURE);
                return new ActionResult(ActionResult.ERROR);
            }

            final DefaultWidgetModel widget = (DefaultWidgetModel) actionContext.getParameter("parentWidgetModel");
            widget.setValue(CURRENT_OBJECT, quoteModel);
            notificationService.notifyUser("", "JustMessage", NotificationEvent.Level.SUCCESS, new Object[]{actionContext.getLabel("adnocbackoffice.quote.recalculation.success")});
            return new ActionResult<>(ActionResult.SUCCESS, quoteModel);
        }
        else
        {
            return new ActionResult(ActionResult.ERROR);
        }
    }

    @Override
    public boolean canPerform(final ActionContext<QuoteModel> ctx)
    {
        return true;
    }

    @Override
    public boolean needsConfirmation(final ActionContext<QuoteModel> ctx)
    {
        return true;
    }

    @Override
    public String getConfirmationMessage(final ActionContext<QuoteModel> ctx)
    {
        return ctx.getLabel("perform.recalculate");
    }
}
