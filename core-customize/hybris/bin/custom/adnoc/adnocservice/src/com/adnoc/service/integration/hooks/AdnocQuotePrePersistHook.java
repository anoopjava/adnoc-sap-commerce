package com.adnoc.service.integration.hooks;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.netty.util.internal.StringUtil.COMMA;

public class AdnocQuotePrePersistHook implements PrePersistHook
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuotePrePersistHook.class);

    private static final char COLON = ':';

    @Override
    public Optional<ItemModel> execute(final ItemModel item, final PersistenceContext context)
    {
        if (item instanceof final QuoteModel quoteModel)
        {
            LOG.info("appEvent=AdnocQuoteInbound, AdnocQuotePrePersistHook Started for quote: {}", quoteModel.getCode());
            for (final AbstractOrderEntryModel abstractOrderEntryModel : quoteModel.getEntries())
            {
                final String taxValueInternal = abstractOrderEntryModel.getTaxValuesInternal();
                LOG.info("appEvent=AdnocQuoteInbound, taxValueInternal:: {}", taxValueInternal);
                if (StringUtils.isNotBlank(taxValueInternal))
                {
                    final String[] taxValues = StringUtils.split(taxValueInternal, COMMA);
                    final Collection<TaxValue> entryTaxes = Arrays.stream(taxValues).map(taxValue -> StringUtils.split(taxValue, COLON))
                            .filter(parts -> parts.length >= 2)
                            .map(parts -> new TaxValue(parts[0], Double.parseDouble(parts[1]), true,
                                    Double.parseDouble(parts[1]), quoteModel.getCurrency().getIsocode())).collect(Collectors.toSet());
                    abstractOrderEntryModel.setTaxValuesInternal(null);
                    abstractOrderEntryModel.setTaxValues(entryTaxes);
                }

                final String discountValuesInternal = abstractOrderEntryModel.getDiscountValuesInternal();
                LOG.info("appEvent=AdnocQuoteInbound, discountValuesInternal:: {}", discountValuesInternal);
                if (StringUtils.isNotBlank(discountValuesInternal))
                {
                    final String[] discountValues = StringUtils.split(discountValuesInternal, COMMA);
                    final List<DiscountValue> entryDiscounts = Arrays.stream(discountValues).map(discountValue -> StringUtils.split(discountValue, COLON))
                            .filter(parts -> parts.length >= 2)
                            .map(parts -> new DiscountValue(parts[0], Double.parseDouble(parts[1]), true,
                                    Double.parseDouble(parts[1]), quoteModel.getCurrency().getIsocode())).toList();
                    abstractOrderEntryModel.setDiscountValuesInternal(null);
                    abstractOrderEntryModel.setDiscountValues(entryDiscounts);
                }
            }
        }
        return Optional.of(item);
    }
}
