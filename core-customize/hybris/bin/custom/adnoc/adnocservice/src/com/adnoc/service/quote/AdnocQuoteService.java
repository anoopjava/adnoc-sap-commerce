package com.adnoc.service.quote;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

public interface AdnocQuoteService
{
    /**
     * Validate quote against cross division.
     *
     * @throws CommerceCartModificationException the commerce cart modification exception
     */
    void validateQuoteAgainstCrossDivision() throws CommerceCartModificationException;
}
