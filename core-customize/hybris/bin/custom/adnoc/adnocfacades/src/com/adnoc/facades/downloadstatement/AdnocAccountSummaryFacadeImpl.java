package com.adnoc.facades.downloadstatement;

import com.adnoc.facades.creditsimulation.data.MessageType;
import com.adnoc.facades.data.AdnocB2BInvoiceDocumentRequestData;
import com.adnoc.facades.data.AdnocB2BInvoiceDocumentResponseData;
import com.adnoc.service.exception.AdnocS4HanaException;
import com.adnoc.service.soa.AdnocAccountSummaryService;
import de.hybris.platform.b2bacceleratorfacades.company.impl.DefaultB2BAccountSummaryFacade;
import de.hybris.platform.b2bacceleratorfacades.document.data.AttachmentData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

import java.util.Base64;
import java.util.Objects;

public class AdnocAccountSummaryFacadeImpl extends DefaultB2BAccountSummaryFacade implements AdnocAccountSummaryFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocAccountSummaryFacadeImpl.class);

    private AdnocAccountSummaryService adnocAccountSummaryService;

    @Override
    public AttachmentData getB2BDocumentAttachmentData(final AdnocB2BInvoiceDocumentRequestData adnocB2BInvoiceDocumentRequestData)
    {
        LOG.info("appEvent=AdnocS4InvoiceDocument, getB2BDocumentAttachmentData method being called");
        final AdnocB2BInvoiceDocumentResponseData adnocB2BInvoiceDocumentResponseData = getAdnocAccountSummaryService().getS4InvoiceDocument(adnocB2BInvoiceDocumentRequestData);

        if (validateAdnocB2BInvoiceDocumentResponse(adnocB2BInvoiceDocumentResponseData))
        {
            final String s4InvoiceDoc = adnocB2BInvoiceDocumentResponseData.getPdfString();
            LOG.info("appEvent=AdnocS4InvoiceDocument, Decoding Base64 to PDF.");
            final byte[] pdfBytes = decodeBase64ToPdf(s4InvoiceDoc);
            final String fileName = adnocB2BInvoiceDocumentResponseData.getInvoiceNo();
            return createAttachment(pdfBytes, fileName);
        }
        LOG.error("appEvent=AdnocS4InvoiceDocument, getting failure response : {}", adnocB2BInvoiceDocumentResponseData.getMessage());
        throw new AdnocS4HanaException(adnocB2BInvoiceDocumentResponseData.getMessage());
    }

    private boolean validateAdnocB2BInvoiceDocumentResponse(final AdnocB2BInvoiceDocumentResponseData adnocB2BInvoiceDocumentResponseData)
    {
        LOG.info("appEvent=AdnocS4InvoiceDocument, validateAdnocB2BInvoiceDocumentResponse method called");
        return (Objects.equals(MessageType.S, adnocB2BInvoiceDocumentResponseData.getMessageType()) && StringUtils.isNotBlank(adnocB2BInvoiceDocumentResponseData.getPdfString()));
    }

    private byte[] decodeBase64ToPdf(final String base64Pdf)
    {
        try
        {
            return Base64.getDecoder().decode(base64Pdf);
        }
        catch (final IllegalArgumentException e)
        {
            throw new IllegalArgumentException("Failed to decode Base64 string. Ensure the input is a valid Base64-encoded PDF.", e);
        }
    }

    private AttachmentData createAttachment(final byte[] pdfBytes, final String fileName)
    {
        final AttachmentData attachmentData = new AttachmentData();
        attachmentData.setFileContent(new ByteArrayResource(pdfBytes));
        attachmentData.setFileName(fileName);
        LOG.info("appEvent=AdnocS4InvoiceDocument, attachment name:{}", attachmentData.getFileName());
        attachmentData.setFileType(MediaType.APPLICATION_PDF_VALUE);
        return attachmentData;
    }

    protected AdnocAccountSummaryService getAdnocAccountSummaryService()
    {
        return adnocAccountSummaryService;
    }

    public void setAdnocAccountSummaryService(final AdnocAccountSummaryService adnocAccountSummaryService)
    {
        this.adnocAccountSummaryService = adnocAccountSummaryService;
    }
}