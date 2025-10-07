package com.adnoc.service.email.util;

import java.io.IOException;

public class AdnocCanonicalization
{

    public static AdnocCanonicalization SIMPLE = new AdnocCanonicalization()
    {

        @Override
        public String getType()
        {
            return "simple";
        }

        @Override
        public String canonicalizeHeader(String name, String value)
        {
            return name + ":" + value;
        }

        @Override
        public String canonicalizeBody(String body) throws IOException
        {
            if (body == null || "".equals(body))
            {
                return "\r\n";
            }

            if (!"\r\n".equals(body.substring(body.length() - 2)))
            {
                return body + "\r\n";
            }

            while ("\r\n\r\n".equals(body.substring(body.length() - 4)))
            {
                body = body.substring(0, body.length() - 2);
                if (body.length() < 4)
                {
                    break;
                }
            }

            return body;
        }
    };

    public static AdnocCanonicalization RELAXED = new AdnocCanonicalization()
    {

        @Override
        public String getType()
        {
            return "relaxed";
        }

        @Override
        public String canonicalizeHeader(String name, String value)
        {
            name = name.trim().toLowerCase();
            value = value.replaceAll("\\s+", " ").trim();
            return name + ":" + value;
        }

        @Override
        public String canonicalizeBody(String body) throws IOException
        {
            if (body == null || "".equals(body))
            {
                return "\r\n";
            }

            body = body.replaceAll("[ \\t\\x0B\\f]+", " ");
            body = body.replaceAll(" \r\n", "\r\n");

            if (!"\r\n".equals(body.substring(body.length() - 2)))
            {
                return body + "\r\n";
            }

            while ("\r\n\r\n".equals(body.substring(body.length() - 4)))
            {
                body = body.substring(0, body.length() - 2);
                if (body.length() < 4)
                {
                    break;
                }
            }

            return body;
        }
    };

    public AdnocCanonicalization()
    {
    }

    public String getType()
    {
        return "unknown";
    }

    public String canonicalizeHeader(String name, String value)
    {
        return null;
    }

    public String canonicalizeBody(String body) throws IOException
    {
        return null;
    }
}
