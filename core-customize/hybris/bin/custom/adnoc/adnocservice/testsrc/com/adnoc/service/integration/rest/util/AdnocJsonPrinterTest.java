package com.adnoc.service.integration.rest.util;

import com.adnoc.service.integration.rest.util.AdnocJsonPrinter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdnocJsonPrinterTest {

    static class SimpleObject {
        public String name = "Test";
        public int value = 123;
    }

    static class ProblematicObject {
        public ProblematicObject self;

        ProblematicObject() {
            this.self = this;
        }
    }

    @Test
    void toJson_shouldConvertObjectToPrettyJson() {
        SimpleObject obj = new SimpleObject();

        String json = AdnocJsonPrinter.toJson(obj);
        assertNotNull(json);
        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"value\""));
        assertTrue(json.contains("\n"));
        assertTrue(json.contains("  "));
    }

    @Test
    void toJson_shouldReturnErrorMessageOnSerializationException() {
        ProblematicObject obj = new ProblematicObject();

        String json = AdnocJsonPrinter.toJson(obj);

        assertNotNull(json);
        assertTrue(json.startsWith("Error converting object to JSON:"));
    }
}
