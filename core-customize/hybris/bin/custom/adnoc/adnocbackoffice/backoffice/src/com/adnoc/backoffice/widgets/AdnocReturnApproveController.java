package com.adnoc.backoffice.widgets;

import com.adnoc.backoffice.renderers.AdnocQuoteStateRenderer;
import com.hybris.backoffice.i18n.BackofficeLocaleService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.notifications.NotificationService;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.returns.OrderReturnException;
import de.hybris.platform.returns.ReturnActionResponse;
import de.hybris.platform.returns.ReturnCallbackService;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.service.UnsupportedAttachmentException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import java.io.IOException;
import java.util.UUID;

public class AdnocReturnApproveController extends DefaultWidgetController
{
    private static final Logger LOGGER = LogManager.getLogger(AdnocReturnApproveController.class);

    private static final long serialVersionUID = 1L;
    protected static final String IN_SOCKET = "approveReturnContextInput";
    protected static final String OUT_CONFIRM = "approveReturnContext";
    protected static final Object COMPLETED = "completed";
    protected static final String NOTIFICATION_TYPE_JUST_MESSAGE = "JustMessage";
    protected static final int NOTIFICATION_DISPLAY_TIME_MS = 2000;
    private ReturnRequestModel returnRequest;
    @Wire
    private Textbox returnRequestCode;
    @Wire
    private Textbox customerName;
    @Wire
    private Button uploadAttachment;
    @Wire
    private Textbox globalApproveComment;
    @WireVariable
    private transient EnumerationService enumerationService;
    @WireVariable
    private transient BackofficeLocaleService cockpitLocaleService;
    @WireVariable
    private transient CockpitEventQueue cockpitEventQueue;
    @WireVariable
    private transient ReturnCallbackService returnCallbackService;
    @WireVariable
    private transient NotificationService notificationService;
    @WireVariable
    private ModelService modelService;
    @WireVariable
    private MediaService mediaService;

    @SocketEvent(
            socketId = "approveReturnContextInput"
    )
    public void initApproveReturnForm(final ReturnRequestModel inputObject)
    {
        getReturnRequestCode().setValue(inputObject.getRMA());
        getCustomerName().setValue(inputObject.getOrder().getUser().getName());
        setReturnRequest(inputObject);
        final WidgetInstanceManager var10000 = getWidgetInstanceManager();
        final String var10001 = getWidgetInstanceManager().getLabel("adnocbackoffice.adnocapprovereturnpopup.title");
        var10000.setTitle(var10001 + " " + getReturnRequest().getRMA());
    }

    @ViewEvent(componentID = "uploadAttachment", eventName = Events.ON_UPLOAD)
    public void onUploadFile(final UploadEvent event)
    {
        final MediaModel mediaModel;
        try
        {
            mediaModel = createMediaModel(((UploadEvent) event).getMedia());
            returnRequest.setReturnApprovalDocument((DocumentMediaModel) mediaModel);
            uploadAttachment.setLabel(mediaModel.getRealFileName());
        }
        catch (final UnsupportedAttachmentException | IOException e)
        {
            LOGGER.error(((Exception) e).getMessage(), e);
            return;
        }
    }

    protected MediaModel createMediaModel(final Media media) throws IOException
    {
        final byte[] byteData;
        if (media.isBinary())
        {
            if (media.inMemory())
            {
                byteData = media.getByteData();
            }
            else
            {
                byteData = IOUtils.toByteArray(media.getStreamData());
            }
        }
        else
        {
            byteData = media.getStringData().getBytes("UTF-8");
        }

        return createAttachment(media.getName(), media.getContentType(), byteData);
    }

    private DocumentMediaModel createAttachment(final String fileName, final String contentType, final byte[] data)
    {
        LOGGER.info("appEvent=AdnocReturnApprovePopup, createAttachment method start");
        checkFileExtension(fileName);
        final DocumentMediaModel documentMediaModel = getModelService().create(DocumentMediaModel.class);
        documentMediaModel.setCode(UUID.randomUUID().toString());
        documentMediaModel.setMime(contentType);
        documentMediaModel.setRealFileName(fileName);
        getModelService().save(documentMediaModel);
        getMediaService().setDataForMedia(documentMediaModel, data);
        return documentMediaModel;
    }

    private void checkFileExtension(final String name)
    {
        if (!FilenameUtils.isExtension(name.toLowerCase(), "pdf,zip".replaceAll("\\s", "").toLowerCase().split(",")))
        {
            throw new WrongValueException(uploadAttachment, getLabel("adnocbackoffice.adnocapprovereturnpopup.attachment.supported.formats"));
        }
    }


    private void showSuccess(final String msg)
    {
        Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, null, null, NOTIFICATION_DISPLAY_TIME_MS);
    }

    private void showError(final String msg)
    {
        Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_ERROR, null, null, NOTIFICATION_DISPLAY_TIME_MS);
    }

    @ViewEvent(componentID = "undoapprovereturn", eventName = "onClick")
    public void undoapprovereturn()
    {
        globalApproveComment.setValue("");
        initApproveReturnForm(getReturnRequest());
    }

    @ViewEvent(componentID = "confirmapprovereturn", eventName = "onClick")
    public void confirmapprovereturn()
    {
        validateRequest();
        Messagebox.show(getWidgetInstanceManager().getLabel("adnocbackoffice.adnocapprovereturnpopup.confirm.message.question"), getWidgetInstanceManager().getLabel("adnocbackoffice.adnocapprovereturnpopup.title") + " " + getReturnRequest().getRMA(), new Messagebox.Button[]{Messagebox.Button.NO, Messagebox.Button.YES}, "z-messagebox-icon z-messagebox-question", this::processApprove);
    }

    protected void validateRequest()
    {
        if (StringUtils.isEmpty(globalApproveComment.getValue()))
        {
            throw new WrongValueException(globalApproveComment, getLabel("adnocbackoffice.adnocapprovereturnpopup.decline.validation.missing.comment"));
        }
    }

    protected void processApprove(final Event event)
    {
        if (Messagebox.Button.YES.event.equals(event.getName()))
        {
            LOGGER.info(String.format("appEvent=AdnocReturnApprove, confirming the return request approve %s", getReturnRequest().getRMA()));
            final ReturnRequestModel returnRequestToProcess = getReturnRequest();
            final ReturnRequestModel upToDateReturnRequest = (ReturnRequestModel) getModelService().get(returnRequestToProcess.getPk());
            if (returnRequestToProcess.getStatus().equals(upToDateReturnRequest.getStatus()))
            {
                returnRequestToProcess.getReturnEntries().forEach((entry) -> getModelService().save(entry));
                returnRequestToProcess.setComment(globalApproveComment.getValue());
                getModelService().save(returnRequestToProcess);
                final ReturnActionResponse returnActionResponse = new ReturnActionResponse(returnRequestToProcess);

                try
                {
                    getReturnCallbackService().onReturnApprovalResponse(returnActionResponse);
                    getNotificationService().notifyUser("", NOTIFICATION_TYPE_JUST_MESSAGE, NotificationEvent.Level.SUCCESS, new Object[]{getWidgetInstanceManager().getLabel("adnocbackoffice.adnocapprovereturnpopup.success.message")});
                }
                catch (final OrderReturnException e)
                {
                    LOGGER.error(e.getMessage());
                    getNotificationService().notifyUser("", NOTIFICATION_TYPE_JUST_MESSAGE, NotificationEvent.Level.FAILURE, new Object[]{getWidgetInstanceManager().getLabel("adnocbackoffice.adnocapprovereturnpopup.error.message")});
                }
            }
            else
            {
                getNotificationService().notifyUser("", NOTIFICATION_TYPE_JUST_MESSAGE, NotificationEvent.Level.FAILURE, new Object[]{getWidgetInstanceManager().getLabel("adnocbackoffice.adnocapprovereturnpopup.error.message")});
            }
            getNotificationService().notifyUser("", NOTIFICATION_TYPE_JUST_MESSAGE, NotificationEvent.Level.SUCCESS, new Object[]{getWidgetInstanceManager().getLabel("adnocbackoffice.adnocapprovereturnpopup.success.message")});

            getCockpitEventQueue().publishEvent(new DefaultCockpitEvent("objectsUpdated", getReturnRequest(), (Object) null));
            getWidgetInstanceManager().sendOutput(OUT_CONFIRM, COMPLETED);
        }

    }

    protected ReturnRequestModel getReturnRequest()
    {
        return returnRequest;
    }

    public void setReturnRequest(final ReturnRequestModel returnRequest)
    {
        this.returnRequest = returnRequest;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    protected BackofficeLocaleService getCockpitLocaleService()
    {
        return cockpitLocaleService;
    }

    protected CockpitEventQueue getCockpitEventQueue()
    {
        return cockpitEventQueue;
    }

    protected ReturnCallbackService getReturnCallbackService()
    {
        return returnCallbackService;
    }

    protected NotificationService getNotificationService()
    {
        return notificationService;
    }

    public Textbox getCustomerName()
    {
        return customerName;
    }

    public Textbox getReturnRequestCode()
    {
        return returnRequestCode;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public MediaService getMediaService()
    {
        return mediaService;
    }
}
