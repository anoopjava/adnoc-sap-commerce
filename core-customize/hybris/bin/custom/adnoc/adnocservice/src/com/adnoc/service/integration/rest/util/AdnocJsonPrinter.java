package com.adnoc.service.integration.rest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class AdnocJsonPrinter
{
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private AdnocJsonPrinter()
    {
        // Prevent instantiation
    }

    public static String toJson(final Object object)
    {
        try
        {
            return mapper.writeValueAsString(object);
        }
        catch (final Exception exception)
        {
            return "Error converting object to JSON: " + exception.getMessage();
        }
    }
}

