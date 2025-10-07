package com.adnoc.service.email.util;

import com.adnoc.service.email.impl.AdnocDkimSigner;
import com.sun.mail.smtp.SMTPMessage;
import com.sun.mail.util.LineOutputStream;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class AdnocSMTPDKIMMessage extends SMTPMessage
{

    private final AdnocDkimSigner signer;
    private String encodedBody;

    public AdnocSMTPDKIMMessage(Session session, AdnocDkimSigner signer)
    {
        super(session);
        this.signer = signer;
    }

    public AdnocSMTPDKIMMessage(MimeMessage message, AdnocDkimSigner signer) throws MessagingException
    {
        super(message);
        this.signer = signer;
    }

    public AdnocSMTPDKIMMessage(Session session, InputStream is, AdnocDkimSigner signer) throws MessagingException
    {
        super(session, is);
        this.signer = signer;
    }

    @Override
    public void writeTo(OutputStream os, String[] ignoreList) throws IOException, MessagingException
    {
        ByteArrayOutputStream osBody = new ByteArrayOutputStream();

        if (!saved)
        {
            saveChanges();
        }

        if (modified)
        {
            OutputStream osEncoding = MimeUtility.encode(osBody, this.getEncoding());
            this.getDataHandler().writeTo(osEncoding);
            osEncoding.flush();
        }
        else
        {
            if (content == null)
            {
                InputStream is = getContentStream();
                byte[] buf = new byte[8192];
                int len;
                while ((len = is.read(buf)) > 0)
                {
                    osBody.write(buf, 0, len);
                }
                is.close();
                buf = null;
            }
            else
            {
                osBody.write(content);
            }
            osBody.flush();
        }

        encodedBody = osBody.toString();

        String signatureHeaderLine;
        try
        {
            signatureHeaderLine = signer.sign(this);
        }
        catch (Exception e)
        {
            throw new MessagingException(e.getLocalizedMessage(), e);
        }

        LineOutputStream los = new LineOutputStream(os);
        los.writeln(signatureHeaderLine);

        Enumeration<?> hdrLines = getNonMatchingHeaderLines(ignoreList);
        while (hdrLines.hasMoreElements())
        {
            los.writeln((String) hdrLines.nextElement());
        }

        los.writeln();
        os.write(osBody.toByteArray());
        os.flush();
    }

    public String getEncodedBody()
    {
        return encodedBody;
    }

    public void setEncodedBody(String encodedBody)
    {
        this.encodedBody = encodedBody;
    }

    @Override
    public void setAllow8bitMIME(boolean allow)
    {
        // Intentionally disabled to preserve DKIM signature integrity.
    }
}
