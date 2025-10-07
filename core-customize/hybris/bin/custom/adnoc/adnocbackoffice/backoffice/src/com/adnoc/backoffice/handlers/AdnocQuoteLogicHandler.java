package com.adnoc.backoffice.handlers;

import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;
import com.hybris.cockpitng.widgets.baseeditorarea.DefaultEditorAreaLogicHandler;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocQuoteLogicHandler extends DefaultEditorAreaLogicHandler
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuoteLogicHandler.class);

    private CommerceQuoteService commerceQuoteService;
    private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;
    private NotificationService notificationService;
    private static final String ERROR_MESSAE = "Seller Comment is required for Quote Rejection with maxlenght 500 characters only";
    private static final int MAX_SELLER_COMMENT_LENGTH = 500;

    @Override
    public Object performSave(final WidgetInstanceManager widgetInstanceManager, final Object currentObject) throws ObjectSavingException
    {
        final QuoteModel quote = (QuoteModel) currentObject;
        LOG.info("appEvent=AdnocQuoteLogicHandler, performSave method called");
        try
        {
            if (Objects.equals(QuoteState.SELLERAPPROVER_REJECTED, quote.getState()))
            {
                validateRejectionComment(quote);
                super.performSave(widgetInstanceManager, currentObject);
                getCommerceQuoteService().rejectQuote(quote, getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
            }
            else if (Objects.equals(QuoteState.SELLERAPPROVER_APPROVED, quote.getState()))
            {
                super.performSave(widgetInstanceManager, currentObject);
                getCommerceQuoteService().approveQuote(quote, getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
            }
            else
            {
                super.performSave(widgetInstanceManager, currentObject);
            }
            return currentObject;
        }
        catch (final Exception exception)
        {
            getNotificationService().notifyUser(widgetInstanceManager, "JustMessage", NotificationEvent.Level.FAILURE, ERROR_MESSAE);
            if (exception instanceof ObjectSavingException)
            {
                throw exception;
            }
            else
            {
                throw new ObjectSavingException(quote.getCode(), exception.getMessage(), exception);
            }
        }
    }

    private void validateRejectionComment(final QuoteModel quote) throws ObjectSavingException
    {
        if ((StringUtils.isEmpty(quote.getSellerComment())) || (StringUtils.length(quote.getSellerComment()) > MAX_SELLER_COMMENT_LENGTH))
        {
            throw new ObjectSavingException(quote.getCode(), ERROR_MESSAE, (Throwable) null);
        }
    }

    protected CommerceQuoteService getCommerceQuoteService()
    {
        return commerceQuoteService;
    }

    public void setCommerceQuoteService(final CommerceQuoteService commerceQuoteService)
    {
        this.commerceQuoteService = commerceQuoteService;
    }

    protected QuoteUserIdentificationStrategy getQuoteUserIdentificationStrategy()
    {
        return quoteUserIdentificationStrategy;
    }

    public void setQuoteUserIdentificationStrategy(final QuoteUserIdentificationStrategy quoteUserIdentificationStrategy)
    {
        this.quoteUserIdentificationStrategy = quoteUserIdentificationStrategy;
    }

    protected NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(final NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }
}
