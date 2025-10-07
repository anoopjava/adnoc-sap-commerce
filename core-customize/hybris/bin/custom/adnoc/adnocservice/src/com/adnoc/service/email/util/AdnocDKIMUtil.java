package com.adnoc.service.email.util;

import com.sun.mail.util.QPEncoderStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdnocDKIMUtil
{

    public static String[] splitHeader(String header) throws AdnocDKIMSignerException
    {
        int colonPos = header.indexOf(':');
        if (colonPos == -1)
        {
            throw new AdnocDKIMSignerException("Invalid header: " + header);
        }
        return new String[]{header.substring(0, colonPos), header.substring(colonPos + 1)};
    }

    public static String concatArray(ArrayList<String> list, String separator)
    {
        StringBuilder buf = new StringBuilder();
        Iterator<String> iter = list.iterator();
        while (iter.hasNext())
        {
            buf.append(iter.next()).append(separator);
        }
        return buf.substring(0, buf.length() - separator.length());
    }

    public static boolean isValidDomain(String domainname)
    {
        Pattern pattern = Pattern.compile("(.+)\\.(.+)");
        Matcher matcher = pattern.matcher(domainname);
        return matcher.matches();
    }

    public static String QuotedPrintable(String s)
    {
        try
        {
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            QPEncoderStream encodeStream = new QPEncoderStream(boas);
            encodeStream.write(s.getBytes());
            String encoded = boas.toString();
            encoded = encoded.replaceAll(";", "=3B").replaceAll(" ", "=20");
            return encoded;
        }
        catch (IOException e)
        {
            return null;
        }
    }


    public static String base64Encode(byte[] b)
    {
        String encoded = Base64.getEncoder().encodeToString(b);
        encoded = encoded.replace("\n", "").replace("\r", "");
        return encoded;
    }

}
