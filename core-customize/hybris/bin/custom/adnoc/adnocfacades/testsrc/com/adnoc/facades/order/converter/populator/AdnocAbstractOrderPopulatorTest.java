package com.adnoc.facades.order.converter.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

@UnitTest

@RunWith(MockitoJUnitRunner.class)
public class AdnocAbstractOrderPopulatorTest
{
    @Test
    public void testAddTotalsNoOverride()
    {
        // Mock the currency
        final CurrencyModel currencyModel1 = mock(CurrencyModel.class);
        Mockito.when(currencyModel1.getIsocode()).thenReturn("USD");

        // Mock order model
        final AbstractOrderModel orderModel = mock(AbstractOrderModel.class);
        Mockito.when(orderModel.getGrossTotalPrice()).thenReturn(100.0);
        Mockito.when(orderModel.getTotalDiscounts()).thenReturn(10.0);
        Mockito.when(orderModel.getNetTotalPrice()).thenReturn(90.0);
        Mockito.when(orderModel.getTotalTax()).thenReturn(5.0);
        Mockito.when(orderModel.getTotalPrice()).thenReturn(95.0);
        Mockito.when(orderModel.getDeliveryCost()).thenReturn(15.0);


        final PriceData priceData = mock(PriceData.class);
        Mockito.when(priceData.getValue()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(priceData.getCurrencyIso()).thenReturn("USD");

        final PriceDataFactory priceDataFactory1 = mock(PriceDataFactory.class);
        Mockito.when(priceDataFactory1.create(any(PriceDataType.class), any(BigDecimal.class), any(CurrencyModel.class)))
                .thenReturn(priceData);
        Mockito.when(orderModel.getCurrency()).thenReturn(currencyModel1);

        // Create anonymous subclass instance (no override)
        final AdnocAbstractOrderPopulator<AbstractOrderModel, AbstractOrderData> populator =
                new AdnocAbstractOrderPopulator<>()
                {
                    @Override
                    public void populate(final AbstractOrderModel abstractOrderModel, final AbstractOrderData abstractOrderData) throws ConversionException
                    {
                        // No override here!
                    }
                    // No override here!
                };

        final AbstractOrderData orderData = new AbstractOrderData();

        // Execute
        populator.setPriceDataFactory(priceDataFactory1);

        populator.addTotals(orderModel, orderData);

        // Assertions
        assertNotNull(orderData.getTotalPrice());
        assertNotNull(orderData.getTotalDiscounts());
        assertNotNull(orderData.getSubTotal());
        assertNotNull(orderData.getTotalTax());
        assertNotNull(orderData.getTotalPriceWithTax());
        assertNotNull(orderData.getDeliveryCost());

        assertEquals("USD", orderData.getTotalPrice().getCurrencyIso());
    }
}