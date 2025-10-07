package com.adnoc.facades.quote.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.util.DiscountValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocQuotePopulatorTest {



    @Mock
    private AbstractOrderModel abstractOrderModel;

    @Mock
    private AbstractOrderData abstractOrderData;

    @Mock
    private CurrencyModel currencyModel;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private PriceData priceData;

    @InjectMocks
    private AdnocQuotePopulator adnocQuotePopulator;

    @Test
    public void testAddTotalsWithDiscounts() {

        DiscountValue discount1 = new DiscountValue("DISC1", 10.0, true, 5.0, "USD");
        DiscountValue discount2 = new DiscountValue("DISC2", 5.0, true, 2.5, "USD");
        Mockito.when(abstractOrderModel.getGlobalDiscountValues()).thenReturn(Arrays.asList(discount1, discount2));

        Mockito.when(abstractOrderModel.getTotalPrice()).thenReturn(100.0);
        lenient().when(abstractOrderModel.getDeliveryCost()).thenReturn(10.0);
        Mockito.when(abstractOrderModel.getNet()).thenReturn(true);

        Mockito.when(abstractOrderModel.getCurrency()).thenReturn(currencyModel);

        double expectedTotal = 5.0 + 2.5 + 100.0;

        when(priceData.getValue()).thenReturn(BigDecimal.valueOf(expectedTotal));
        when(priceDataFactory.create(
                any(PriceDataType.class),
                eq(BigDecimal.valueOf(expectedTotal)),
                eq(currencyModel))
        ).thenReturn(priceData);

        adnocQuotePopulator.setPriceDataFactory(priceDataFactory);

        adnocQuotePopulator.addTotals(abstractOrderModel, abstractOrderData);

        verify(abstractOrderData, atLeast(1)).setSubTotalWithoutQuoteDiscounts(argThat(
                pd -> pd != null && pd.getValue().doubleValue() == expectedTotal
        ));
    }


    @Test
    public void testAddTotalsWithNoDiscounts() {
        Mockito.when(abstractOrderModel.getCurrency()).thenReturn(currencyModel);

        Mockito.when(abstractOrderModel.getGlobalDiscountValues()).thenReturn(Collections.emptyList());
        Mockito.when(abstractOrderModel.getTotalPrice()).thenReturn(100.0);
        lenient().when(abstractOrderModel.getDeliveryCost()).thenReturn(10.0);
        Mockito.when(abstractOrderModel.getNet()).thenReturn(true);

        Mockito.when(priceDataFactory.create(
                any(PriceDataType.class),
                any(BigDecimal.class),
                any(CurrencyModel.class))
        ).thenReturn(priceData);


        lenient().when(priceData.getValue()).thenReturn(BigDecimal.valueOf(110.0));


        adnocQuotePopulator.setPriceDataFactory(priceDataFactory);

        adnocQuotePopulator.addTotals(abstractOrderModel, abstractOrderData);

        verify(abstractOrderData, times(2)).setSubTotalWithoutQuoteDiscounts(any(PriceData.class));
    }
}
