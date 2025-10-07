package com.adnoc.facades.strategies;

import com.adnoc.service.config.AdnocConfigService;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteUpdateExpirationTimeStrategy;
import de.hybris.platform.core.model.order.QuoteModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdnocQuoteUpdateExpirationTimeStrategy extends DefaultQuoteUpdateExpirationTimeStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocQuoteUpdateExpirationTimeStrategy.class);

    public static final String QUOTE_EXPIRY_DAYS = "quoteExpiryDays";

    private AdnocConfigService adnocConfigService;

    @Override
    protected void updateExpirationTimeForSubmitAction(final QuoteModel quoteModel, final QuoteUserType quoteUserType)
    {
        final int quoteExpiryDays = adnocConfigService.getAdnocConfigValue(QUOTE_EXPIRY_DAYS, 3);
        final Date earliestDeliveryDate = Collections.min(quoteModel.getEntries().stream().map(entry -> entry.getNamedDeliveryDate())
                .filter(Objects::nonNull).collect(Collectors.toSet()));
        LOG.info("Calculated earliestDeliveryDate:{}", earliestDeliveryDate);

        final LocalDateTime earliestLocalDateTime = earliestDeliveryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        final LocalDateTime expirationLocalDateTime = earliestLocalDateTime.minusDays(quoteExpiryDays);
        LOG.info("Calculated expirationLocalDateTime:{}", expirationLocalDateTime);

        final Date expirationDate = Date.from(expirationLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        LOG.info("Calculated expirationDate:{}", expirationDate);
        quoteModel.setExpirationTime(expirationDate);
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

}
