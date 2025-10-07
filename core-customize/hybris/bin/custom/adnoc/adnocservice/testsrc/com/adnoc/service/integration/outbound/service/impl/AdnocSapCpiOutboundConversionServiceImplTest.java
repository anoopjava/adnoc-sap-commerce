package com.adnoc.service.integration.outbound.service.impl;

import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdnocSapCpiOutboundConversionServiceImplTest {

    @Test
    void testConvertToOutboundDocumentModel() throws Exception {
        MediaService mediaService = mock(MediaService.class);

        AdnocSapCpiOutboundConversionServiceImpl service = new AdnocSapCpiOutboundConversionServiceImpl();
        service.setMediaService(mediaService);

        DocumentMediaModel mediaModel = mock(DocumentMediaModel.class);
        when(mediaModel.getRealFileName()).thenReturn("test.pdf");
        when(mediaModel.getMime()).thenReturn("application/pdf");
        when(mediaModel.getCode()).thenReturn("MEDIA123");
        byte[] fakeBytes = "Hello world".getBytes();
        when(mediaService.getDataFromMedia(mediaModel)).thenReturn(fakeBytes);

        Method m = AdnocSapCpiOutboundConversionServiceImpl.class
                .getDeclaredMethod("convertToOutboundDocumentModel", DocumentMediaModel.class, String.class);
        m.setAccessible(true);

        Object result = m.invoke(service, mediaModel, "SomeDocType");

        assertNotNull(result);
        Class<?> docClass = result.getClass();
        assertEquals("SomeDocType", docClass.getMethod("getSupportedDocumentType").invoke(result));
        assertEquals("test.pdf", docClass.getMethod("getDocumentName").invoke(result));
        assertEquals("application/pdf", docClass.getMethod("getDocumentType").invoke(result));
        assertEquals(Base64.getEncoder().encodeToString(fakeBytes), docClass.getMethod("getDocumentBase64").invoke(result));
    }
}
