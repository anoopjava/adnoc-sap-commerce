package com.adnoc.service.email.impl;

import com.adnoc.service.email.util.*;
import com.sun.mail.util.CRLFOutputStream;

import javax.mail.MessagingException;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.Map.Entry;

/**
 * Main class providing a signature according to DKIM RFC 4871.
 */
public class AdnocDkimSigner
{

    private static String DKIMSIGNATUREHEADER = "DKIM-Signature";
    private static int MAXHEADERLENGTH = 67;

    private static ArrayList<String> minimumHeadersToSign = new ArrayList<>();

    static
    {
        minimumHeadersToSign.add("From");
    }

    private String[] defaultHeadersToSign = new String[]{
            "From",
            "To",
            "Subject"
    };
    private AdnocSigningAlgorithm signingAlgorithm = AdnocSigningAlgorithm.SHA256withRSA; // default
    private Signature signatureService;
    private MessageDigest messageDigest;
    private String signingDomain;
    private String selector;
    private AdnocCanonicalization headerCanonicalization = AdnocCanonicalization.RELAXED;
    private AdnocCanonicalization bodyCanonicalization = AdnocCanonicalization.RELAXED;
    private PrivateKey privkey;

    public AdnocDkimSigner(String signingDomain, String selector, PrivateKey privkey) throws Exception
    {
        initDKIMSigner(signingDomain, selector, privkey);
    }

    public AdnocDkimSigner(String signingDomain, String selector, String privkeyFilename) throws Exception
    {
        File privKeyFile = new File(privkeyFilename);
        byte[] privKeyBytes = new byte[(int) privKeyFile.length()];
        try (DataInputStream dis = new DataInputStream(new FileInputStream(privKeyFile)))
        {
            dis.readFully(privKeyBytes);
        }
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privKeyBytes);
            RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);
            initDKIMSigner(signingDomain, selector, privKey);
        } finally
        {
            Arrays.fill(privKeyBytes, (byte) 0);
        }
    }

    private void initDKIMSigner(String signingDomain, String selector, PrivateKey privkey) throws AdnocDKIMSignerException
    {
        if (!AdnocDKIMUtil.isValidDomain(signingDomain))
        {
            throw new AdnocDKIMSignerException(signingDomain + " is an invalid signing domain");
        }
        this.signingDomain = signingDomain;
        this.selector = selector.trim();
        this.privkey = privkey;
        this.setSigningAlgorithm(this.signingAlgorithm);
    }


    public AdnocCanonicalization getBodyCanonicalization()
    {
        return bodyCanonicalization;
    }

    public void setBodyCanonicalization(AdnocCanonicalization bodyCanonicalization) throws AdnocDKIMSignerException
    {
        this.bodyCanonicalization = bodyCanonicalization;
    }

    public AdnocCanonicalization getHeaderCanonicalization()
    {
        return headerCanonicalization;
    }

    public void setHeaderCanonicalization(AdnocCanonicalization headerCanonicalization) throws AdnocDKIMSignerException
    {
        this.headerCanonicalization = headerCanonicalization;
    }

    public String[] getDefaultHeadersToSign()
    {
        return defaultHeadersToSign;
    }

    public void setSigningAlgorithm(AdnocSigningAlgorithm signingAlgorithm) throws AdnocDKIMSignerException
    {
        try
        {
            this.messageDigest = MessageDigest.getInstance(signingAlgorithm.getJavaHashNotation());
        }
        catch (NoSuchAlgorithmException nsae)
        {
            throw new AdnocDKIMSignerException("Hashing algorithm " + signingAlgorithm.getJavaHashNotation() + " not known", nsae);
        }
        try
        {
            this.signatureService = Signature.getInstance(signingAlgorithm.getJavaSecNotation());
        }
        catch (NoSuchAlgorithmException nsae)
        {
            throw new AdnocDKIMSignerException("Signing algorithm " + signingAlgorithm.getJavaSecNotation() + " not known", nsae);
        }
        try
        {
            this.signatureService.initSign(privkey);
        }
        catch (InvalidKeyException ike)
        {
            throw new AdnocDKIMSignerException("Provided private key invalid", ike);
        }
        this.signingAlgorithm = signingAlgorithm;
    }

    private String serializeDKIMSignature(Map<String, String> dkimSignature)
    {
        Set<Entry<String, String>> entries = dkimSignature.entrySet();
        StringBuffer buf = new StringBuffer(), fbuf;
        int pos = 0;
        for (Entry<String, String> entry : entries)
        {
            fbuf = new StringBuffer();
            fbuf.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
            if (pos + fbuf.length() + 1 > MAXHEADERLENGTH)
            {
                pos = fbuf.length();
                buf.append("\r\n\t").append(fbuf);
            }
            else
            {
                buf.append(" ").append(fbuf);
                pos += fbuf.length() + 1;
            }
        }
        buf.append("\r\n\tb=");
        return buf.toString().trim();
    }

    private String foldSignedSignature(String s, int offset)
    {
        int i = 0;
        StringBuffer buf = new StringBuffer();
        while (true)
        {
            if (offset > 0 && s.substring(i).length() > MAXHEADERLENGTH - offset)
            {
                buf.append(s, i, i + MAXHEADERLENGTH - offset);
                i += MAXHEADERLENGTH - offset;
                offset = 0;
            }
            else if (s.substring(i).length() > MAXHEADERLENGTH)
            {
                buf.append("\r\n\t").append(s, i, i + MAXHEADERLENGTH);
                i += MAXHEADERLENGTH;
            }
            else
            {
                buf.append("\r\n\t").append(s.substring(i));
                break;
            }
        }
        return buf.toString();
    }

    public String sign(AdnocSMTPDKIMMessage message) throws AdnocDKIMSignerException, MessagingException
    {
        Map<String, String> dkimSignature = new LinkedHashMap<>();
        dkimSignature.put("v", "1");
        dkimSignature.put("a", this.signingAlgorithm.getRfc4871Notation());
        dkimSignature.put("q", "dns/txt");
        dkimSignature.put("c", getHeaderCanonicalization().getType() + "/" + getBodyCanonicalization().getType());
        dkimSignature.put("t", String.valueOf(new Date().getTime() / 1000));
        dkimSignature.put("s", this.selector);
        dkimSignature.put("d", this.signingDomain);

        ArrayList<String> assureHeaders = new ArrayList<>(minimumHeadersToSign);
        StringBuilder headerList = new StringBuilder();
        StringBuilder headerContent = new StringBuilder();

        Enumeration headerLines = message.getMatchingHeaderLines(defaultHeadersToSign);
        while (headerLines.hasMoreElements())
        {
            String header = (String) headerLines.nextElement();
            String[] headerParts = AdnocDKIMUtil.splitHeader(header);
            headerList.append(headerParts[0]).append(":");
            headerContent.append(this.headerCanonicalization.canonicalizeHeader(headerParts[0], headerParts[1])).append("\r\n");
            assureHeaders.remove(headerParts[0]);
        }

        if (!assureHeaders.isEmpty())
        {
            throw new AdnocDKIMSignerException("Could not find header fields " + AdnocDKIMUtil.concatArray(assureHeaders, ", ") + " for signing");
        }

        dkimSignature.put("h", headerList.substring(0, headerList.length() - 1));

        // Process body
        String body = message.getEncodedBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CRLFOutputStream crlfos = new CRLFOutputStream(baos))
        {
            crlfos.write(body.getBytes());
        }
        catch (IOException e)
        {
            throw new AdnocDKIMSignerException("Body conversion failed", e);
        }
        body = baos.toString();

        try
        {
            body = this.bodyCanonicalization.canonicalizeBody(body);
        }
        catch (IOException ioe)
        {
            throw new AdnocDKIMSignerException("Body canonicalization failed", ioe);
        }

        dkimSignature.put("bh", (AdnocDKIMUtil.base64Encode(this.messageDigest.digest(body.getBytes()))));
        dkimSignature.get("bh").replace("==", "=");

        String serializedSignature = serializeDKIMSignature(dkimSignature);
        byte[] signedSignature;
        try
        {
            signatureService.update(headerContent.append(this.headerCanonicalization.canonicalizeHeader(DKIMSIGNATUREHEADER, " " + serializedSignature)).toString().getBytes());
            signedSignature = signatureService.sign();
        }
        catch (SignatureException se)
        {
            throw new AdnocDKIMSignerException("Signing operation failed", se);
        }

        return DKIMSIGNATUREHEADER + ": " + serializedSignature + foldSignedSignature(AdnocDKIMUtil.base64Encode(signedSignature), 3);
    }
}
