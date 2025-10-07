package com.adnoc.service.integration.outbound.service.impl;

import com.adnoc.facades.data.AdnocOutboundDocumentData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.data.*;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderConversionService;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
public class AdnocSapCpiOmmOrderMapperServiceTest {

    @Test
    public void map() throws Exception {
        try (var mocks = MockitoAnnotations.openMocks(this)) {
            AdnocSapCpiOmmOrderMapperService service = new AdnocSapCpiOmmOrderMapperService();
            SapCpiOrderConversionService sapCpiOrderConversionService = mock(SapCpiOrderConversionService.class);
            SAPCpiOutboundOrderModel sapCpiOutboundOrderModel = mock(SAPCpiOutboundOrderModel.class);
            OrderModel orderModel = mock(OrderModel.class);
            SapCpiOrder sapCpiOrder = mock(SapCpiOrder.class);
            SapCpiConfig sapCpiConfig = mock(SapCpiConfig.class);
            SapCpiTargetSystem sapCpiTargetSystem = mock(SapCpiTargetSystem.class);
            AdnocOutboundDocumentData adnocOutboundDocumentData = mock(AdnocOutboundDocumentData.class);
            SapCpiOrderItem sapCpiOrderItem = mock(SapCpiOrderItem.class);
            SapCpiPartnerRole sapCpiPartnerRole = mock(SapCpiPartnerRole.class);
            SapCpiOrderAddress sapCpiOrderAddress = mock(SapCpiOrderAddress.class);
            SapCpiOrderPriceComponent sapCpiOrderPriceComponent = mock(SapCpiOrderPriceComponent.class);
            SapCpiCreditCardPayment sapCpiCreditCardPayment = mock(SapCpiCreditCardPayment.class);
            service.setSapCpiOrderConversionService(sapCpiOrderConversionService);
            when(sapCpiOrderConversionService.convertOrderToSapCpiOrder(any())).thenReturn(sapCpiOrder);
            when(sapCpiOrder.getPlantCode()).thenReturn("123");
            when(sapCpiOrder.getDeliveryType()).thenReturn("Express");
            when(sapCpiOrder.getPaymentInfo()).thenReturn("CreditCard");
            when(sapCpiOrder.getCardNumber()).thenReturn("4111111111111111");
            when(sapCpiOrder.getCardType()).thenReturn("VISA");
            when(sapCpiOrder.getUtrnNumber()).thenReturn("UTRN123");
            when(sapCpiOrder.getQuotation()).thenReturn("QUOTE456");
            when(sapCpiOrder.getMop()).thenReturn("Online");
            when(sapCpiOrder.getMopType1()).thenReturn("TypeA");
            when(sapCpiOrder.getRrnCode1()).thenReturn("RRN001");
            when(sapCpiOrder.getAuthCode1()).thenReturn("AUTH789");
            when(sapCpiOrder.getAmount1()).thenReturn(100.00);
            when(sapCpiOrder.getMopType2()).thenReturn("TypeB");
            when(sapCpiOrder.getAmount2()).thenReturn(50.00);
            when(sapCpiOrder.getTotalAmount()).thenReturn(150.00);
            when(sapCpiOrder.getPoDocument()).thenReturn(adnocOutboundDocumentData);
            when(sapCpiOrder.getSapCpiConfig()).thenReturn(sapCpiConfig);

            when(sapCpiConfig.getSapCpiTargetSystem()).thenReturn(sapCpiTargetSystem);
            when(sapCpiTargetSystem.getUrl()).thenReturn("url");
            when(sapCpiTargetSystem.getUsername()).thenReturn("cpi-user");
            when(sapCpiTargetSystem.getClient()).thenReturn("100");
            when(sapCpiTargetSystem.getSenderName()).thenReturn("SENDER_SYS");
            when(sapCpiTargetSystem.getSenderPort()).thenReturn("8000");
            when(sapCpiTargetSystem.getReceiverName()).thenReturn("RECEIVER_SYS");
            when(sapCpiTargetSystem.getReceiverPort()).thenReturn("9000");

            when(sapCpiOrderItem.getOrderId()).thenReturn("ORDER123");
            when(sapCpiOrderItem.getEntryNumber()).thenReturn("001");
            when(sapCpiOrderItem.getQuantity()).thenReturn("2");
            when(sapCpiOrderItem.getCurrencyIsoCode()).thenReturn("USD");
            when(sapCpiOrderItem.getUnit()).thenReturn("EA");
            when(sapCpiOrderItem.getProductCode()).thenReturn("PROD001");
            when(sapCpiOrderItem.getProductName()).thenReturn("Product Name");
            when(sapCpiOrderItem.getPlant()).thenReturn("PLANT01");
            when(sapCpiOrderItem.getNamedDeliveryDate()).thenReturn("2025-06-01");
            when(sapCpiOrderItem.getItemCategory()).thenReturn("Z001");
            when(sapCpiOrderItem.getIncoTerms()).thenReturn("IncoTerms");

            when(sapCpiPartnerRole.getOrderId()).thenReturn("Order234");
            when(sapCpiPartnerRole.getPartnerRoleCode()).thenReturn("PartnerCode");
            when(sapCpiPartnerRole.getPartnerId()).thenReturn("PartnerID");
            when(sapCpiPartnerRole.getDocumentAddressId()).thenReturn("DocumentAddress");
            when(sapCpiPartnerRole.getEntryNumber()).thenReturn("EntryNumber");

            when(sapCpiOrderAddress.getOrderId()).thenReturn("ORDER123");
            when(sapCpiOrderAddress.getDocumentAddressId()).thenReturn("ADDR456");
            when(sapCpiOrderAddress.getFaxNumber()).thenReturn("1234567890");
            when(sapCpiOrderAddress.getTitleCode()).thenReturn("MR");
            when(sapCpiOrderAddress.getTelNumber()).thenReturn("9876543210");
            when(sapCpiOrderAddress.getEmail()).thenReturn("test@example.com");
            when(sapCpiOrderAddress.getLanguageIsoCode()).thenReturn("EN");

            when(sapCpiOrderPriceComponent.getOrderId()).thenReturn("ORDER123");
            when(sapCpiOrderPriceComponent.getEntryNumber()).thenReturn("001");
            when(sapCpiOrderPriceComponent.getValue()).thenReturn("150.00");
            when(sapCpiOrderPriceComponent.getUnit()).thenReturn("EA");
            when(sapCpiOrderPriceComponent.getAbsolute()).thenReturn("true");
            when(sapCpiOrderPriceComponent.getConditionCode()).thenReturn("ZPR0");
            when(sapCpiOrderPriceComponent.getConditionCounter()).thenReturn("1");
            when(sapCpiOrderPriceComponent.getCurrencyIsoCode()).thenReturn("USD");
            when(sapCpiOrderPriceComponent.getPriceQuantity()).thenReturn("1.00");

            when(sapCpiCreditCardPayment.getOrderId()).thenReturn("ORDER123");
            when(sapCpiCreditCardPayment.getRequestId()).thenReturn("REQ789");
            when(sapCpiCreditCardPayment.getCcOwner()).thenReturn("John Doe");
            when(sapCpiCreditCardPayment.getValidToMonth()).thenReturn("12");
            when(sapCpiCreditCardPayment.getValidToYear()).thenReturn("2026");
            when(sapCpiCreditCardPayment.getSubscriptionId()).thenReturn("SUBS456");
            when(sapCpiCreditCardPayment.getPaymentProvider()).thenReturn("CyberSource");
            service.map(orderModel, sapCpiOutboundOrderModel);
            verify(sapCpiOrderConversionService, times(1)).convertOrderToSapCpiOrder(orderModel);
        }
    }
}
