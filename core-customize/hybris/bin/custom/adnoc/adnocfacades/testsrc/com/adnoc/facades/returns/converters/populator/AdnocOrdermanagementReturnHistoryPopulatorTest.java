package com.adnoc.facades.returns.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@UnitTest

@RunWith(MockitoJUnitRunner.class)
public class AdnocOrdermanagementReturnHistoryPopulatorTest {

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private ReturnRequestModel returnRequestModel;

    @Mock
    private ReturnStatus returnStatus;

    @Mock
    private Converter<OrderModel, OrderData> orderConverter;

    @InjectMocks
    private AdnocOrdermanagementReturnHistoryPopulator populator;

    @Test
    public void testSetsStatusDisplay() {

        String statusDisplay = "Approved";

        Mockito.when(returnRequestModel.getStatus()).thenReturn(returnStatus);
        Mockito.when(enumerationService.getEnumerationName(returnStatus)).thenReturn(statusDisplay);

        ReturnRequestData target = new ReturnRequestData();

        // Act
        populator.setOrderConverter(orderConverter);
        populator.setEnumerationService(enumerationService);
        populator.populate(returnRequestModel, target);

        // Assert
        assertEquals(statusDisplay, target.getStatusDisplay());
    }
}
