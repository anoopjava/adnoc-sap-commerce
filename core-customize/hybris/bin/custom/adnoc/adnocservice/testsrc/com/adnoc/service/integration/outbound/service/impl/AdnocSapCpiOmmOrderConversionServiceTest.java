package com.adnoc.service.integration.outbound.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.adnoc.facades.data.AdnocOutboundDocumentData;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiCreditCardPayment;
import de.hybris.platform.servicelayer.media.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.YearMonth;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * JUnit 5 tests for private/helper methods in AdnocSapCpiOmmOrderConversionService.
 */
@ExtendWith(MockitoExtension.class)
class AdnocSapCpiOmmOrderConversionServiceTest {

    private AdnocSapCpiOmmOrderConversionService service;

    @Mock
    private MediaService mediaService; // we'll stub this

    @BeforeEach
    void setUp() {
        service = new AdnocSapCpiOmmOrderConversionService();
        service.setMediaService(mediaService);
    }

    /**
     * Helper to call a private/protected method by reflection.
     */
    private Object invokePrivate(
            Object target,
            String methodName,
            Class<?>[] parameterTypes,
            Object... args
    ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = target.getClass().getDeclaredMethod(methodName, parameterTypes);
        m.setAccessible(true);
        return m.invoke(target, args);
    }

    @Test
    void testGetFileNameAndGetFileType() throws Exception {
        DocumentMediaModel dummyMedia = mock(DocumentMediaModel.class);
        when(dummyMedia.getRealFileName()).thenReturn("myfile.pdf");
        when(dummyMedia.getMime()).thenReturn("application/pdf");

        Object fileName = invokePrivate(
                service,
                "getFileName",
                new Class[]{MediaModel.class},
                dummyMedia
        );
        assertTrue(fileName instanceof String);
        assertEquals("myfile.pdf", fileName);

        Object fileType = invokePrivate(
                service,
                "getFileType",
                new Class[]{MediaModel.class},
                dummyMedia
        );
        assertTrue(fileType instanceof String);
        assertEquals("application/pdf", fileType);
    }

    @Test
    void testGetMediaContentBase64Encoded() throws Exception {
        byte[] rawBytes = "Hello, World!".getBytes();
        DocumentMediaModel dummyMedia = mock(DocumentMediaModel.class);
        when(dummyMedia.getCode()).thenReturn("DOC123");
        when(mediaService.getDataFromMedia(dummyMedia)).thenReturn(rawBytes);

        Object base64result = invokePrivate(
                service,
                "getMediaContentBase64Encoded",
                new Class[]{MediaModel.class},
                dummyMedia
        );

        assertTrue(base64result instanceof String);
        String expectedBase64 = Base64.getEncoder().encodeToString(rawBytes);
        assertEquals(expectedBase64, base64result);
    }

    @Test
    void testSetSapCpiCreditCardPaymentValidityDate_withValidMonthYear() throws Exception {
        SapCpiCreditCardPayment ccPayment = new SapCpiCreditCardPayment();
        Map<String, Object> rawItem = new HashMap<>();
        rawItem.put("validToMonth", "02"); // Feb
        rawItem.put("validToYear", "2024");

        invokePrivate(
                service,
                "setSapCpiCreditCardPaymentValidityDate",
                new Class[]{SapCpiCreditCardPayment.class, Map.class},
                ccPayment,
                rawItem
        );

        assertEquals("20240229", ccPayment.getValidToYear());
        assertEquals("02", ccPayment.getValidToMonth());
    }

    @Test
    void testSetSapCpiCreditCardPaymentValidityDate_withMissingMonthOrYear() throws Exception {
        SapCpiCreditCardPayment ccPayment1 = new SapCpiCreditCardPayment();
        Map<String, Object> rawItem1 = new HashMap<>();
        rawItem1.put("validToMonth", null);
        rawItem1.put("validToYear", "2025");

        invokePrivate(
                service,
                "setSapCpiCreditCardPaymentValidityDate",
                new Class[]{SapCpiCreditCardPayment.class, Map.class},
                ccPayment1,
                rawItem1
        );
        assertNull(ccPayment1.getValidToYear());
        assertNull(ccPayment1.getValidToMonth());

        // Case 2: year is null
        SapCpiCreditCardPayment ccPayment2 = new SapCpiCreditCardPayment();
        Map<String, Object> rawItem2 = new HashMap<>();
        rawItem2.put("validToMonth", "07");
        rawItem2.put("validToYear", null);

        invokePrivate(
                service,
                "setSapCpiCreditCardPaymentValidityDate",
                new Class[]{SapCpiCreditCardPayment.class, Map.class},
                ccPayment2,
                rawItem2
        );
        assertNull(ccPayment2.getValidToYear());
        assertEquals("07", ccPayment2.getValidToMonth());
    }

    @Test
    void testConvertToOutboundDocumentData_setsAllFieldsCorrectly() throws Exception {
        // Arrange: stub a DocumentMediaModel
        DocumentMediaModel dummyMedia = mock(DocumentMediaModel.class);
        when(dummyMedia.getRealFileName()).thenReturn("invoice.pdf");
        when(dummyMedia.getMime()).thenReturn("application/pdf");
        byte[] contentBytes = {0x01, 0x02, 0x03, 0x04};
        when(mediaService.getDataFromMedia(dummyMedia)).thenReturn(contentBytes);

        Object result = invokePrivate(
                service,
                "convertToOutboundDocumentData",
                new Class[]{DocumentMediaModel.class, String.class},
                dummyMedia,
                "PO_Document"
        );

        assertNotNull(result);
        assertTrue(result instanceof AdnocOutboundDocumentData);
        AdnocOutboundDocumentData dto = (AdnocOutboundDocumentData) result;

        assertEquals("PO_Document", dto.getSupportedDocumentType());
        assertEquals("invoice.pdf", dto.getDocumentName());
        assertEquals("application/pdf", dto.getDocumentType());

        String expectedBase64 = Base64.getEncoder().encodeToString(contentBytes);
        assertEquals(expectedBase64, dto.getDocumentBase64());
    }
}
