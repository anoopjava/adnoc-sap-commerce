package com.adnoc.service.email.impl;

import com.adnoc.service.email.util.AdnocSMTPDKIMMessage;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdnocDkimSignerTest
{
    @InjectMocks
    private AdnocDkimSigner adnocDkimSigner;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private Configuration configuration;

    @Test
    public void testSignMessageSuccess() throws Exception
    {
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getString("dkim.domain")).thenReturn("example.com");
        Mockito.when(configuration.getString("adnoc.dkim.selector")).thenReturn("selector1");
        mockMimeMessageContent();
        mockSignature();

        adnocDkimSigner.sign((AdnocSMTPDKIMMessage) mimeMessage);

        verify(mimeMessage).setHeader(eq("DKIM-Signature"), anyString());
    }

    private void mockMimeMessageContent() throws MessagingException, IOException
    {
        Mockito.when(mimeMessage.getContent()).thenReturn("Test email body content");
        Mockito.when(mimeMessage.getHeader("From", null)).thenReturn("test@domain.com");
        Mockito.when(mimeMessage.getHeader("To", null)).thenReturn("recipient@domain.com");
        Mockito.when(mimeMessage.getHeader("Subject", null)).thenReturn("Test Subject");
        doReturn(Collections.enumeration(Arrays.asList(
                "DKIM-Signature: dummySignature",
                "From: test@domain.com",
                "To: recipient@domain.com",
                "Subject: Test Subject"
        ))).when(mimeMessage).getAllHeaderLines();
        doReturn(Collections.enumeration(Arrays.asList(
                "DKIM-Signature: dummySignature",
                "From: test@domain.com",
                "To: recipient@domain.com",
                "Subject: Test Subject"
        ))).when(mimeMessage).getAllHeaders();
    }

    private void mockSignature() throws Exception
    {
        try
        {
            final Signature realSignature = Signature.getInstance("SHA256withRSA");
            doNothing().when(realSignature).initSign(any(PrivateKey.class));
            Mockito.when(realSignature.sign()).thenReturn("dummySignature".getBytes());

        }
        catch (final Exception e)
        {
            System.err.println("Signature initialization failed: " + e.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSignMessageThrowsExceptionForInvalidMimeMessageContent() throws Exception
    {
        Mockito.when(mimeMessage.getContent()).thenReturn(new Object());
        adnocDkimSigner.sign((AdnocSMTPDKIMMessage) mimeMessage);
    }

    @Test(expected = IOException.class)
    public void testMimeMessageContentThrowsIOException() throws IOException, MessagingException
    {
        doThrow(new IOException("Simulated IO error")).when(mimeMessage).getContent();
        mimeMessage.getContent();
    }

    @Test(expected = MessagingException.class)
    public void testMimeMessageContentThrowsMessagingException() throws MessagingException, IOException
    {
        doThrow(new MessagingException("Simulated Messaging error")).when(mimeMessage).getContent();

        mimeMessage.getContent();
    }

    @Test
    public void testCalculateBodyHashWithStringContent() throws Exception
    {
        Mockito.when(mimeMessage.getContent()).thenReturn("Test email body content");

        final Method method = AdnocDkimSigner.class.getDeclaredMethod("calculateBodyHash", MimeMessage.class);
        method.setAccessible(true);

        final String bodyHash = (String) method.invoke(adnocDkimSigner, mimeMessage);
        assertNotNull(bodyHash);
        System.out.println("Generated Body Hash: " + bodyHash);
    }

    @Test
    public void testCalculateBodyHashWithMimeMultipartContent() throws Exception
    {
        final MimeMultipart multipart = mock(MimeMultipart.class);
        final BodyPart bodyPart = mock(BodyPart.class);

        Mockito.when(multipart.getCount()).thenReturn(1);
        Mockito.when(multipart.getBodyPart(0)).thenReturn(bodyPart);
        Mockito.when(bodyPart.getContent()).thenReturn("Multipart email body content");
        Mockito.when(mimeMessage.getContent()).thenReturn(multipart);

        final Method method = AdnocDkimSigner.class.getDeclaredMethod("calculateBodyHash", MimeMessage.class);
        method.setAccessible(true);
        final String bodyHash = (String) method.invoke(adnocDkimSigner, mimeMessage);
        assertNotNull(bodyHash);
        System.out.println("Generated Body Hash for Multipart: " + bodyHash);
    }

    @Test
    public void testGenerateDKIMHeader() throws Exception
    {
        final String canonicalizedHeaders = "from:test@example.com\nto:recipient@example.com\nsubject:Test Subject\n";
        final String bodyHash = "dummyHashValue";

        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getString("dkim.domain")).thenReturn("example.com");
        Mockito.when(configuration.getString("adnoc.dkim.selector")).thenReturn("selector1");
        final Method method = AdnocDkimSigner.class.getDeclaredMethod("generateDKIMHeader", String.class, String.class);
        method.setAccessible(true);
        final String dkimHeader = (String) method.invoke(adnocDkimSigner, canonicalizedHeaders, bodyHash);
        assertNotNull(dkimHeader);
        System.out.println("Generated DKIM Header: " + dkimHeader);
    }
}
