package com.adnoc.backoffice.widgets;

import com.adnoc.service.enums.ReturnRejectionReason;
import com.adnoc.service.event.AdnocReturnOrderRejectedEvent;
import com.hybris.backoffice.i18n.BackofficeLocaleService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.notifications.NotificationService;
import de.hybris.platform.omsbackoffice.widgets.returns.cancelreturnpopup.ReturnCancellationController;
import de.hybris.platform.returns.OrderReturnException;
import de.hybris.platform.returns.ReturnActionResponse;
import de.hybris.platform.returns.ReturnCallbackService;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.event.EventService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AdnocReturnCancellationController extends ReturnCancellationController
{
    private static final Logger LOGGER = LogManager.getLogger(AdnocReturnCancellationController.class);

    private static final long serialVersionUID = 1L;
    protected static final String IN_SOCKET = "cancelReturnContextInput";
    protected static final String OUT_CONFIRM = "cancelReturnContext";
    protected static final Object COMPLETED = "completed";
    private final List<String> cancellationReasons = new ArrayList();
    @Wire
    private Combobox globalCancelReasons;
    @Wire
    private Textbox globalCancelComment;
    @WireVariable
    private transient BackofficeLocaleService cockpitLocaleService;
    @WireVariable
    private transient CockpitEventQueue cockpitEventQueue;
    @WireVariable
    private transient ReturnCallbackService returnCallbackService;
    @WireVariable
    private transient NotificationService notificationService;
    @WireVariable
    private EventService eventService;

    public AdnocReturnCancellationController()
    {
        // Empty constructor - dependencies injected through @WireVariable annotations super();
    }

    @Override
    @SocketEvent(socketId = "cancelReturnContextInput")
    public void initCancelReturnForm(final ReturnRequestModel inputObject)
    {
        cancellationReasons.clear();
        getReturnRequestCode().setValue(inputObject.getRMA());
        getCustomerName().setValue(inputObject.getOrder().getUser().getName());
        setReturnRequest(inputObject);
        final WidgetInstanceManager var10000 = getWidgetInstanceManager();
        final String var10001 = getWidgetInstanceManager().getLabel("customersupportbackoffice.cancelreturnpopup.title");
        var10000.setTitle(var10001 + " " + getReturnRequest().getRMA());
        final Locale locale = getCockpitLocaleService().getCurrentLocale();
        getEnumerationService().getEnumerationValues(ReturnRejectionReason.class).forEach((reason) -> cancellationReasons.add(getEnumerationService().getEnumerationName(reason, locale)));
        globalCancelReasons.setModel(new ListModelArray(cancellationReasons));
        globalCancelReasons.addEventListener("onCustomChange", (event) -> Events.echoEvent("onLaterCustomChange", globalCancelReasons, event.getData()));
        globalCancelReasons.addEventListener("onLaterCustomChange", (event) -> {
            Clients.clearWrongValue(globalCancelReasons);
            globalCancelReasons.invalidate();
            handleGlobalCancelReason(event);
        });
    }

    @Override
    @ViewEvent(componentID = "undocancelreturn", eventName = "onClick")
    public void undoCancelReturn()
    {
        globalCancelReasons.setSelectedItem((Comboitem) null);
        globalCancelComment.setValue("");
        initCancelReturnForm(getReturnRequest());
    }

    @Override
    @ViewEvent(componentID = "confirmcancelreturn", eventName = "onClick")
    public void confirmCancelReturn()
    {
        validateRequest();
        Messagebox.show(getWidgetInstanceManager().getLabel("customersupportbackoffice.cancelreturnpopup.confirm.message.question"), getWidgetInstanceManager().getLabel("customersupportbackoffice.cancelreturnpopup.title") + " " + getReturnRequest().getRMA(), new Messagebox.Button[]{Messagebox.Button.NO, Messagebox.Button.YES}, "z-messagebox-icon z-messagebox-question", this::processCancellation);
    }

    @Override
    protected void handleGlobalCancelReason(final Event event)
    {
        getSelectedReturnRejectionReason(event);
    }

    protected Optional<ReturnRejectionReason> matchingComboboxReturnRejectionReason(final String cancelReasonLabel)
    {
        return getEnumerationService().getEnumerationValues(ReturnRejectionReason.class).stream().filter((reason) -> getEnumerationService().getEnumerationName(reason, getCockpitLocaleService().getCurrentLocale()).equals(cancelReasonLabel)).findFirst();
    }

    protected Optional<ReturnRejectionReason> getSelectedReturnRejectionReason(final Event event)
    {
        Optional<ReturnRejectionReason> reason = Optional.empty();
        if (event.getTarget() instanceof Combobox)
        {
            final Object selectedValue = event.getData();
            reason = matchingComboboxReturnRejectionReason(selectedValue.toString());
        }

        return reason;
    }

    /**
     * Processes the cancellation of a return request upon receiving user confirmation.
     * This method handles updating the return request with relevant cancellation details,
     * such as rejection comments and reasons, and communicates with corresponding services to complete
     * the cancellation operation. Notifications are triggered based on the success or failure of the process.
     *
     * @param event the event object representing the user's action, such as a confirmation click event.
     */
    @Override
    protected void processCancellation(final Event event)
    {
        if (Messagebox.Button.YES.event.equals(event.getName()))
        {
            final String returnRejectionComment = globalCancelComment.getValue();
            final ReturnRequestModel returnRequest1 = getReturnRequest();
            if (StringUtils.isNotBlank(returnRejectionComment))
            {
                LOGGER.info(String.format("appEvent=AdnocReturnCancellation, fetch and setting rejection comment %s to ReturnRequest", returnRejectionComment));
                returnRequest1.setComment(returnRejectionComment);
            }

            final Optional<ReturnRejectionReason> rejectionReasonForName = matchingComboboxReturnRejectionReason(globalCancelReasons.getValue());
            rejectionReasonForName.ifPresent(returnRequest1::setReturnRejectionReason);
            final ReturnActionResponse returnActionResponse = new ReturnActionResponse(returnRequest1);

            try
            {
                getReturnCallbackService().onReturnCancelResponse(returnActionResponse);
                getNotificationService().notifyUser("", "JustMessage", NotificationEvent.Level.SUCCESS, new Object[]{getWidgetInstanceManager().getLabel("customersupportbackoffice.cancelreturnpopup.success.message")});
                final AdnocReturnOrderRejectedEvent createReturnEvent = new AdnocReturnOrderRejectedEvent();
                createReturnEvent.setReturnRequest(returnRequest1);
                getEventService().publishEvent(createReturnEvent);
            }
            catch (final OrderReturnException e)
            {
                LOGGER.error(e.getMessage(), e);
                getNotificationService().notifyUser("", "JustMessage", NotificationEvent.Level.FAILURE, new Object[]{getWidgetInstanceManager().getLabel("customersupportbackoffice.cancelreturnpopup.error.message")});
            }

            getCockpitEventQueue().publishEvent(new DefaultCockpitEvent("objectsUpdated", getReturnRequest(), (Object) null));
            getWidgetInstanceManager().sendOutput("cancelReturnContext", COMPLETED);
        }

    }

    @Override
    protected void validateRequest()
    {
        if (globalCancelReasons.getSelectedItem() == null)
        {
            throw new WrongValueException(globalCancelReasons, getLabel("customersupportbackoffice.cancelreturnpopup.decline.validation.missing.reason"));
        }
    }

    @Override
    protected CockpitEventQueue getCockpitEventQueue()
    {
        return cockpitEventQueue;
    }

    @Override
    protected ReturnCallbackService getReturnCallbackService()
    {
        return returnCallbackService;
    }

    @Override
    protected NotificationService getNotificationService()
    {
        return notificationService;
    }

    protected EventService getEventService()
    {
        return eventService;
    }

    public void setEventService(final EventService eventService)
    {
        this.eventService = eventService;
    }

    @Override
    protected BackofficeLocaleService getCockpitLocaleService()
    {
        return cockpitLocaleService;
    }

    public void setCockpitLocaleService(final BackofficeLocaleService cockpitLocaleService)
    {
        this.cockpitLocaleService = cockpitLocaleService;
    }
}
