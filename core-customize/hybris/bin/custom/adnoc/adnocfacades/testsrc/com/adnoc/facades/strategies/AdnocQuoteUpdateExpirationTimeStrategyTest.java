package com.adnoc.facades.strategies;

import com.adnoc.service.config.AdnocConfigService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocQuoteUpdateExpirationTimeStrategyTest
{
    @InjectMocks
    private AdnocQuoteUpdateExpirationTimeStrategy adnocQuoteUpdateExpirationTimeStrategy;
    @Mock
    private AdnocConfigService adnocConfigService;
    @Mock
    private QuoteModel quoteModel;
    @Mock
    private QuoteEntryModel quoteEntryModel;
    @Mock
    private Date namedDeliveryDate;

    @Test
    public void testUpdateExpirationTimeForSubmitAction()
    {
        Mockito.when(adnocConfigService.getAdnocConfigValue(AdnocQuoteUpdateExpirationTimeStrategy.QUOTE_EXPIRY_DAYS, 3))
                .thenReturn(3);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final LocalDate deliveryDate = LocalDate.parse("2025-06-01", formatter);
        namedDeliveryDate = Date.from(deliveryDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Mockito.when(quoteModel.getEntries()).thenReturn(Collections.singletonList(quoteEntryModel));
        Mockito.when(quoteEntryModel.getNamedDeliveryDate()).thenReturn(namedDeliveryDate);

        adnocQuoteUpdateExpirationTimeStrategy.updateExpirationTimeForSubmitAction(quoteModel, QuoteUserType.BUYER);

        final ArgumentCaptor<Date> expirationTimeCaptor = ArgumentCaptor.forClass(Date.class);
        verify(quoteModel).setExpirationTime(expirationTimeCaptor.capture());

        final LocalDateTime earliestLocalDateTime = deliveryDate.atStartOfDay();
        final LocalDateTime expirationLocalDateTime = earliestLocalDateTime.minusDays(3);
        final Date expectedExpirationDate = Date.from(expirationLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals(expectedExpirationDate, expirationTimeCaptor.getValue());

        verify(adnocConfigService).getAdnocConfigValue(AdnocQuoteUpdateExpirationTimeStrategy.QUOTE_EXPIRY_DAYS, 3);
        verify(quoteModel).getEntries();
    }
}
