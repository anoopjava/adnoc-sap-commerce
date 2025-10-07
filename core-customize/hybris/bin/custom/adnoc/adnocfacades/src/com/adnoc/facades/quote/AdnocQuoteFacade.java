package com.adnoc.facades.quote;

import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import java.util.List;

/**
 * Adnoc Quote Facade interface.
 * This interface extends the QuoteFacade to provide additional functionality specific to ADNOC quotes.
 */
public interface AdnocQuoteFacade extends QuoteFacade
{

    /**
     * Update order entry list.
     *
     * @param cartEntriesData the cart entries data
     * @param quoteCode       the quote code
     * @return the list of cart modifications
     * @throws CommerceCartModificationException the commerce cart modification exception
     */
    List<CartModificationData> updateOrderEntryList(List<OrderEntryData> cartEntriesData, String quoteCode) throws CommerceCartModificationException;

    /**
     * Validate quote against cross division.
     *
     * @throws CommerceCartModificationException the commerce cart modification exception
     */
    void validateQuoteAgainstCrossDivision() throws CommerceCartModificationException;
}
