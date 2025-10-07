package com.adnoc.service.calculation.impl.servicelayer;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.util.DiscountValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocSLFindOrderDiscountValuesStrategyTest
{
    @InjectMocks
    private AdnocSLFindOrderDiscountValuesStrategy adnocSLFindOrderDiscountValuesStrategy;
    @Mock
    private AbstractOrderEntryModel abstractOrderEntryModel;
    @Mock
    private CartModel cartModel;
    @Mock
    private QuoteModel quoteModel;

    @Test
    public void testFindDiscountValues() throws CalculationException {
        Mockito.when(abstractOrderEntryModel.getOrder()).thenReturn(cartModel);
        Mockito.when(cartModel.getQuoteReference()).thenReturn(quoteModel);
        Mockito.when(quoteModel.getCode()).thenReturn("QUOTE123");

        final List<DiscountValue> discountValues = Collections.singletonList(new DiscountValue("TestDiscount", 10, true, "AED"));
        Mockito.when(abstractOrderEntryModel.getDiscountValues()).thenReturn(discountValues);

        final List<DiscountValue> result = adnocSLFindOrderDiscountValuesStrategy.findDiscountValues(abstractOrderEntryModel);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("TestDiscount", result.get(0).getCode());
    }

    @Test
    public void testFindDiscountValuesWithoutQuoteReference() throws CalculationException
    {
        Mockito.when(abstractOrderEntryModel.getOrder()).thenReturn(cartModel);
        Mockito.when(cartModel.getQuoteReference()).thenReturn(null);
        final List<DiscountValue> discountValues = adnocSLFindOrderDiscountValuesStrategy.findDiscountValues(abstractOrderEntryModel);
        assertTrue(discountValues.isEmpty());
    }
}
