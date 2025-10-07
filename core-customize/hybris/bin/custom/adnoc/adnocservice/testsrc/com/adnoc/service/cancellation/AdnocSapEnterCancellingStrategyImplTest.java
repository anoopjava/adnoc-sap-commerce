package com.adnoc.service.cancellation;

import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocSapEnterCancellingStrategyImplTest
{
    @InjectMocks
    private AdnocSapEnterCancellingStrategyImpl adnocSapEnterCancellingStrategy;

    @Mock
    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;

    @Mock
    private OrderModel orderModel;
    @Mock
    private ModelService modelService;

    @Test
    public void testChangeOrderStatusAfterCancelOperation()
    {
        final OrderModificationRecordModel orderModificationRecordModel = Mockito.mock(OrderModificationRecordModel.class);
        Mockito.when(orderModificationRecordModel.getOrder()).thenReturn(orderModel);

        final OrderCancelRecordEntryModel orderCancelRecordEntryModel1 = Mockito.mock(OrderCancelRecordEntryModel.class);
        Mockito.when(orderCancelRecordEntryModel1.getModificationRecord()).thenReturn(orderModificationRecordModel);

        adnocSapEnterCancellingStrategy.changeOrderStatusAfterCancelOperation(orderCancelRecordEntryModel1, true);

        verify(adnocOutboundReplicationDirector).scheduleOutboundTask(orderCancelRecordEntryModel1);
        verify(orderModel).setStatus(OrderStatus.CANCELLING);
        verify(modelService).save(orderModel);
    }
}
