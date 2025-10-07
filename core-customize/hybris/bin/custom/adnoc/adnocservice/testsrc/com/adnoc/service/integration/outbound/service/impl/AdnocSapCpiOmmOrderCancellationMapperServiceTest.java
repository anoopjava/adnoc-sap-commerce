package com.adnoc.service.integration.outbound.service.impl;

import com.adnoc.facades.company.data.SAPSalesOrganizationData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiConfig;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellation;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellationItem;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiTargetSystem;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderConversionService;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
public class AdnocSapCpiOmmOrderCancellationMapperServiceTest {

    @Test
    public void map() throws Exception {
        try (var mocks = MockitoAnnotations.openMocks(this)) {
            // Create service instance
            AdnocSapCpiOmmOrderCancellationMapperService service = new AdnocSapCpiOmmOrderCancellationMapperService();

            // Create mocks
            OrderCancelRecordEntryModel orderCancelRecordEntryModel = mock(OrderCancelRecordEntryModel.class);
            SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellationModel = mock(SAPCpiOutboundOrderCancellationModel.class);
            SapCpiOrderConversionService sapCpiOrderConversionService = mock(SapCpiOrderConversionService.class);
            SapCpiOrderCancellation sapCpiOrderCancellation = mock(SapCpiOrderCancellation.class);
            SapCpiConfig sapCpiConfig = mock(SapCpiConfig.class);
            SapCpiTargetSystem sapCpiTargetSystem = mock(SapCpiTargetSystem.class);
            SapCpiOrderCancellationItem sapCpiOrderCancellationItem = mock(SapCpiOrderCancellationItem.class);
            OrderModificationRecordModel orderModificationRecordModel = mock(OrderModificationRecordModel.class);
            OrderModel orderModel = mock(OrderModel.class);
            BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
            SAPSalesOrganizationData sapSalesOrganizationData = mock(SAPSalesOrganizationData.class);

            // Inject mocks
            service.setSapCpiOrderConversionService(sapCpiOrderConversionService);

            // Setup mocks
            when(sapCpiOrderCancellation.getSapCpiConfig()).thenReturn(sapCpiConfig);
            when(sapCpiConfig.getSapCpiTargetSystem()).thenReturn(sapCpiTargetSystem);
            when(sapCpiTargetSystem.getUrl()).thenReturn("url");
            when(sapCpiTargetSystem.getUsername()).thenReturn("username");
            when(sapCpiTargetSystem.getClient()).thenReturn("client");
            when(sapCpiTargetSystem.getSenderName()).thenReturn("senderName");
            when(sapCpiTargetSystem.getSenderPort()).thenReturn("senderPort");
            when(sapCpiTargetSystem.getReceiverName()).thenReturn("receiverName");
            when(sapCpiTargetSystem.getReceiverPort()).thenReturn("receiverPort");

            when(sapCpiOrderCancellation.getOrderId()).thenReturn("123456");
            when(sapCpiOrderCancellation.getSapOrderCode()).thenReturn("234567");
            when(sapCpiOrderCancellation.getSalesOrg()).thenReturn(sapSalesOrganizationData);
            when(sapSalesOrganizationData.getSalesOrganization()).thenReturn("SalesOrganization");
            when(sapSalesOrganizationData.getDivision()).thenReturn("Division");
            when(sapSalesOrganizationData.getDistributionChannel()).thenReturn("DistributionChannel");

            when(sapCpiOrderCancellationItem.getProductCode()).thenReturn("P1234");
            when(sapCpiOrderCancellationItem.getProductName()).thenReturn("Product_ABC");
            when(sapCpiOrderCancellationItem.getEntryNumber()).thenReturn("EntryNumber");
            when(sapCpiOrderCancellationItem.getRejectionReason()).thenReturn("PAYMENT_DUE");
            when(sapCpiOrderCancellationItem.getQuantity()).thenReturn("11");

            when(sapCpiOrderConversionService.convertCancelOrderToSapCpiCancelOrder(any()))
                    .thenReturn(List.of(sapCpiOrderCancellation));

            when(orderCancelRecordEntryModel.getModificationRecord()).thenReturn(orderModificationRecordModel);
            when(orderModificationRecordModel.getOrder()).thenReturn(orderModel);
            when(orderModel.getStore()).thenReturn(baseStoreModel);
            when(baseStoreModel.getUid()).thenReturn("baseStoreId");

            List<SAPCpiOutboundOrderCancellationModel> sapCpiOutboundOrderCancellationModels = new ArrayList<>();
            sapCpiOutboundOrderCancellationModels.add(sapCpiOutboundOrderCancellationModel);

            // Call method under test
            service.map(orderCancelRecordEntryModel, sapCpiOutboundOrderCancellationModels);

            // Verify interaction
            verify(sapCpiOrderConversionService, times(1)).convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntryModel);
        }
    }
}
