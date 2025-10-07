package com.adnoc.service.event;

import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AdnocReQuoteBuyerSubmissionEventTest
{
    @Test
    public void testEventInitialization()
    {
        final QuoteModel quoteModel = new QuoteModel();
        final UserModel userModel = new UserModel();
        final QuoteUserType quoteUserType = QuoteUserType.BUYER;

        final AdnocReQuoteBuyerSubmissionEvent adnocReQuoteBuyerSubmissionEvent = new AdnocReQuoteBuyerSubmissionEvent(quoteModel, userModel, quoteUserType);

        assertEquals(quoteModel, adnocReQuoteBuyerSubmissionEvent.getQuote());
        assertEquals(userModel, adnocReQuoteBuyerSubmissionEvent.getUserModel());
        assertEquals(quoteUserType, adnocReQuoteBuyerSubmissionEvent.getQuoteUserType());
    }
}
