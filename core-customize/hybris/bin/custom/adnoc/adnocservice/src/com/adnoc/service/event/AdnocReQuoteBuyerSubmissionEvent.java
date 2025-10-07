package com.adnoc.service.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.AbstractQuoteSubmitEvent;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

public class AdnocReQuoteBuyerSubmissionEvent extends AbstractQuoteSubmitEvent<BaseSiteModel>
{
    public AdnocReQuoteBuyerSubmissionEvent(final QuoteModel quote, final UserModel userModel, final QuoteUserType quoteUserType)
    {
        super(quote, userModel, quoteUserType);
    }
}
