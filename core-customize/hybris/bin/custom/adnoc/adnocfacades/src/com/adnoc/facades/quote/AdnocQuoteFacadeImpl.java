package com.adnoc.facades.quote;

import com.adnoc.facades.AdnocCartFacade;
import com.adnoc.service.quote.AdnocQuoteService;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

public class AdnocQuoteFacadeImpl extends DefaultQuoteFacade implements AdnocQuoteFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuoteFacadeImpl.class);

    private AdnocCartFacade adnocCartFacade;
    private AdnocQuoteService adnocQuoteService;

    @Override
    public List<CartModificationData> updateOrderEntryList(final List<OrderEntryData> cartEntriesData, final String quoteCode) throws CommerceCartModificationException
    {
        final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
        if (Objects.isNull(quoteModel))
        {
            LOG.info("appEvent=AdnocQuote,quote not found with quote Code: {}", quoteCode);
            throw new CommerceCartModificationException("No quote found with code=" + quoteCode);
        }
        if (Objects.nonNull(quoteModel.getCartReference()))
        {
            final CartModel cartModel = quoteModel.getCartReference();
            LOG.info("appEvent=AdnocQuote, updating cart with cartModel:{}", cartModel.getCode());
            return getAdnocCartFacade().updateCartEntries(cartEntriesData, cartModel);
        }
        throw new CommerceCartModificationException("No Cart Reference found for quote=" + quoteCode);
    }

    @Override
    public void validateQuoteAgainstCrossDivision() throws CommerceCartModificationException
    {
        getAdnocQuoteService().validateQuoteAgainstCrossDivision();
    }

    protected AdnocQuoteService getAdnocQuoteService()
    {
        return adnocQuoteService;
    }

    public void setAdnocQuoteService(final AdnocQuoteService adnocQuoteService)
    {
        this.adnocQuoteService = adnocQuoteService;
    }

    protected AdnocCartFacade getAdnocCartFacade()
    {
        return adnocCartFacade;
    }

    public void setAdnocCartFacade(final AdnocCartFacade adnocCartFacade)
    {
        this.adnocCartFacade = adnocCartFacade;
    }
}
