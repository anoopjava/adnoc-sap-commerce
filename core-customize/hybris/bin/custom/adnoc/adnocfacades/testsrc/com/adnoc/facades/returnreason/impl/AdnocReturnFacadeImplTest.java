package com.adnoc.facades.returnreason.impl;

import com.adnoc.facades.ordermanagement.data.ReturnReasonData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.impl.DefaultOmsReturnFacade;
import de.hybris.platform.refund.RefundService;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.service.UnsupportedAttachmentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocReturnFacadeImplTest
{
    @InjectMocks
    private AdnocReturnFacadeImpl adnocReturnFacadeImpl;
    @Mock
    private Converter<RefundReason, ReturnReasonData> returnReasonDataConverter;
    @Mock
    private EnumerationService enumerationService;
    @Mock
    private OrderService orderService;
    @Mock
    private ReturnService returnService;
    @Mock
    private ModelService modelService;
    @Mock
    private EventService eventService;
    @Mock
    private MultipartFile returnRequestDocument;
    @Mock
    private RefundEntryModel refundEntryModel;
    @Mock
    private ReturnRequestModel returnRequestModel;
    @Mock
    private OrderModel orderModel;
    @InjectMocks
    private DefaultOmsReturnFacade omsReturnFacade;
    @Mock
    private OrderEntryModel orderEntryModel;
    @Mock
    private MediaService mediaService;
    @Mock
    private RefundService refundService;

    @Test
    public void testGetReturnReasons()
    {
        adnocReturnFacadeImpl = Mockito.spy(adnocReturnFacadeImpl);
        Mockito.when(enumerationService.getEnumerationValues("RefundReason"))
                .thenReturn(Arrays.asList(RefundReason.DAMAGEDINTRANSIT, RefundReason.LATEDELIVERY));
        final ReturnReasonData returnReasonData1 = mock(ReturnReasonData.class);
        final ReturnReasonData returnReasonData2 = mock(ReturnReasonData.class);
        Mockito.when(returnReasonDataConverter.convert(RefundReason.DAMAGEDINTRANSIT)).thenReturn(returnReasonData1);
        Mockito.when(returnReasonDataConverter.convert(RefundReason.LATEDELIVERY)).thenReturn(returnReasonData2);
        final List<ReturnReasonData> returnReasons = adnocReturnFacadeImpl.getReturnReasons();
        assertNotNull(returnReasons);
        assertEquals(2, returnReasons.size());
    }

    @Test
    public void testCreateReturnRequestInContext() throws IOException
    {
        final int entryNumber = 1;
        final long expectedQty = 2L;

        final OrderEntryData orderEntryData = new OrderEntryData();
        orderEntryData.setEntryNumber(entryNumber);
        final ReturnEntryData returnEntryData = new ReturnEntryData();
        returnEntryData.setExpectedQuantity(expectedQty);
        returnEntryData.setNotes("notes");
        returnEntryData.setAction(ReturnAction.IMMEDIATE);
        returnEntryData.setOrderEntry(orderEntryData);
        final ReturnRequestData returnRequestData = new ReturnRequestData();
        returnRequestData.setReturnEntries(List.of(returnEntryData));
        returnRequestData.setRefundDeliveryCost(Boolean.TRUE);
        returnRequestData.setRefundReason(RefundReason.DAMAGEDINTRANSIT);
        returnRequestData.setReturnRequestDocument(returnRequestDocument);

        Mockito.doReturn(orderEntryModel).when(orderService).getEntryForNumber(orderModel, entryNumber);
        Mockito.when(returnService.isReturnable(orderModel, orderEntryModel, expectedQty)).thenReturn(true);
        Mockito.when(returnService.createReturnRequest(orderModel)).thenReturn(returnRequestModel);
        Mockito.when(returnService.createRefund(any(), any(), any(), anyLong(), any(), any()))
                .thenReturn(refundEntryModel);
        Mockito.when(returnRequestModel.getOrder()).thenReturn(orderModel);

        final String fileName = "invoice.pdf";
        Mockito.when(returnRequestDocument.getOriginalFilename()).thenReturn("invoice.pdf");
        adnocReturnFacadeImpl.setAllowedUploadedFormats("pdf,jpg,png");
        try
        {
            adnocReturnFacadeImpl.checkFileExtension(fileName);
        }
        catch (final UnsupportedAttachmentException e)
        {
            fail("Exception should not be thrown for a valid file extension.");
        }

        final DocumentMediaModel documentMediaModel = new DocumentMediaModel();
        documentMediaModel.setRealFileName("testPDF");
        documentMediaModel.setMime("text/xml");
        documentMediaModel.setCode(UUID.randomUUID().toString());

        Mockito.when(modelService.create(DocumentMediaModel.class)).thenReturn(documentMediaModel);
        doNothing().when(modelService).save(documentMediaModel);
        refundService = Mockito.mock(RefundService.class);
        final ReturnRequestModel result = adnocReturnFacadeImpl.createReturnRequestInContext(orderModel, returnRequestData);

        assertNotNull(result);
        verify(orderService).getEntryForNumber(orderModel, entryNumber);
        verify(returnService).isReturnable(orderModel, orderEntryModel, expectedQty);
        verify(returnService).createReturnRequest(orderModel);
        verify(eventService).publishEvent(any(CreateReturnEvent.class));

    }

    @Test
    public void recalculateSubtotal_completeReturn() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Mockito.when(orderModel.getSubtotal()).thenReturn(10D);
        Mockito.when(returnRequestModel.getOrder()).thenReturn(orderModel);
        final Method method = DefaultOmsReturnFacade.class.getDeclaredMethod("recalculateSubtotal", ReturnRequestModel.class, boolean.class);
        method.setAccessible(true);
        final BigDecimal amount = (BigDecimal) method.invoke(omsReturnFacade, returnRequestModel, true);
        assertEquals(BigDecimal.valueOf(10.0), amount);
    }

    @Test
    public void testCreateAttachment_validFile()
    {
        final String fileName = "invoice.pdf";
        final String contentType = "application/pdf";
        final byte[] fileContent = "fileContent".getBytes();

        adnocReturnFacadeImpl.setAllowedUploadedFormats("pdf,jpg,png");
        adnocReturnFacadeImpl.checkFileExtension(fileName);
        final DocumentMediaModel documentMediaModel = new DocumentMediaModel();
        documentMediaModel.setRealFileName("testPDF");
        documentMediaModel.setMime("text/xml");
        documentMediaModel.setCode(UUID.randomUUID().toString());

        Mockito.when(modelService.create(DocumentMediaModel.class)).thenReturn(documentMediaModel);
        doNothing().when(modelService).save(documentMediaModel);
        doNothing().when(mediaService).setDataForMedia(documentMediaModel, fileContent);
        final DocumentMediaModel result = adnocReturnFacadeImpl.createAttachment(fileName, contentType, fileContent);
        assertNotNull(result);
        assertEquals(fileName, result.getRealFileName());
        assertEquals(contentType, result.getMime());
        assertNotNull(result.getCode());
        verify(modelService).create(DocumentMediaModel.class);
        verify(modelService).save(documentMediaModel);
        verify(mediaService).setDataForMedia(documentMediaModel, fileContent);
    }

    @Test
    public void testCheckFileExtension_validExtension()
    {
        final String fileName = "invoice.pdf";
        adnocReturnFacadeImpl.setAllowedUploadedFormats("pdf,jpg,png");
        try
        {
            adnocReturnFacadeImpl.checkFileExtension(fileName);
        }
        catch (final UnsupportedAttachmentException e)
        {
            fail("Exception should not be thrown for a valid file extension.");
        }
    }
}
