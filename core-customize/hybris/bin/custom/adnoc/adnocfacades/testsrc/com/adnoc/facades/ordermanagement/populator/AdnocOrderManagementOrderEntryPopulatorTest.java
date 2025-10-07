package com.adnoc.facades.ordermanagement.populator;
import com.adnoc.facade.product.data.UnitData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.junit.Test;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest

@RunWith(MockitoJUnitRunner.class)
public class AdnocOrderManagementOrderEntryPopulatorTest {

    @Mock
    private Converter<UnitModel, UnitData> unitConverter;

    @Mock
    private AbstractOrderEntryModel source;

    @Mock
    private CurrencyModel currency;

    @Mock
    private UnitModel unitModel;

    @Mock
    private UnitData unitData;

    @Mock

    private ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker;

    @Mock
    private Converter<ProductModel, ProductData> productConverter;
    @Mock
    private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;

    @Mock
    private AbstractOrderModel abstractOrder;
    @Mock
    private PriceDataFactory priceDataFactory;
    @Mock

    private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;
    @Mock
    private Converter<AbstractOrderEntryProductInfoModel, List<ConfigurationInfoData>> productConfigurationConverter;
    @Mock
    private EntryGroupService entryGroupService;
    @Mock
    private Converter<CommentModel, CommentData> orderCommentConverter;

    @Test
    public void testPopulateWithUnit() {
        // Arrange
        Mockito.when(source.getUnit()).thenReturn(unitModel);
        Mockito.when(unitConverter.convert(unitModel)).thenReturn(unitData);
        Date deliveryDate = new Date();
        Mockito.when(source.getNamedDeliveryDate()).thenReturn(deliveryDate);
        when(source.getOrder()).thenReturn(abstractOrder);
        when(abstractOrder.getCurrency()).thenReturn(currency);

        OrderEntryData target = new OrderEntryData();

        AdnocOrderManagementOrderEntryPopulator populator = new AdnocOrderManagementOrderEntryPopulator();
        populator.setAdnocUnitConverter(unitConverter);
        populator.setEntryOrderChecker(entryOrderChecker);
        populator.setProductConverter(productConverter);
        populator.setAdnocUnitConverter(unitConverter);
        populator.setEntryGroupService(entryGroupService);
        populator.setOrderCommentConverter(orderCommentConverter);
        populator.setDeliveryModeConverter(deliveryModeConverter);
        populator.setPriceDataFactory(priceDataFactory);
        populator.setProductConfigurationConverter(productConfigurationConverter);
        populator.setPointOfServiceConverter(pointOfServiceConverter);




        // Act
        populator.populate(source, target);

        // Assert
        assertEquals(unitData, target.getUnit());
        assertEquals(deliveryDate, target.getNamedDeliveryDate());
    }

    @Test
    public void testPopulateWithNoUnit() {
        // Arrange
        Mockito.when(source.getUnit()).thenReturn(null);
        Mockito.when(source.getNamedDeliveryDate()).thenReturn(null);
        when(source.getOrder()).thenReturn(abstractOrder);
        when(abstractOrder.getCurrency()).thenReturn(currency);

        OrderEntryData target = new OrderEntryData();

        AdnocOrderManagementOrderEntryPopulator populator = new AdnocOrderManagementOrderEntryPopulator();
        populator.setAdnocUnitConverter(unitConverter);
        populator.setEntryOrderChecker(entryOrderChecker);
        populator.setProductConverter(productConverter);
        populator.setAdnocUnitConverter(unitConverter);
        populator.setEntryGroupService(entryGroupService);
        populator.setOrderCommentConverter(orderCommentConverter);
        populator.setDeliveryModeConverter(deliveryModeConverter);
        populator.setPriceDataFactory(priceDataFactory);
        populator.setProductConfigurationConverter(productConfigurationConverter);
        populator.setPointOfServiceConverter(pointOfServiceConverter);

        // Act
        populator.populate(source, target);

        // Assert
        assertNull(target.getUnit());
        assertNull(target.getNamedDeliveryDate());
    }
}
