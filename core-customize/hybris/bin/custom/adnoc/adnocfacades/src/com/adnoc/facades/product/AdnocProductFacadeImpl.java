package com.adnoc.facades.product;

import com.adnoc.service.product.service.AdnocProductService;
import de.hybris.platform.b2bacceleratorfacades.document.data.AttachmentData;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.commercefacades.product.impl.DefaultProductFacade;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

import java.util.Objects;

public class AdnocProductFacadeImpl extends DefaultProductFacade implements AdnocProductFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocProductFacadeImpl.class);

    private AdnocProductService adnocProductService;
    private MediaService mediaService;

    @Override
    public AttachmentData getAttachmentForProduct(final String productCode)
    {
        LOG.info("appEvent=Product,fetching attachment for product: {}", productCode);
        final ProductModel productModel = getAdnocProductService().getProductForCode(productCode);
        final DocumentMediaModel documentMediaModel = productModel.getProductDocument();
        if (Objects.isNull(documentMediaModel))
        {
            throw new NotFoundException(
                    String.format("Document with productCode = %s does not have an attachment.", productCode),
                    "The document does not have any attachments.");
        }
        LOG.info("appEvent=Product,document found, retrieving attachment data.");
        return getB2BDocumentAttachmentData(documentMediaModel);
    }

    protected AttachmentData getB2BDocumentAttachmentData(final DocumentMediaModel documentMediaModel)
    {
        LOG.info("appEvent=Product,fetching media data for document: {}", documentMediaModel.getCode());
        final AttachmentData attachmentData = new AttachmentData();

        final byte[] mediaFileContent = mediaService.getDataFromMedia(documentMediaModel);
        attachmentData.setFileContent(new ByteArrayResource(mediaFileContent));

        final String filename = documentMediaModel.getCode();
        attachmentData.setFileName(filename);

        if (documentMediaModel.getMime() != null)
        {
            attachmentData.setFileType(documentMediaModel.getMime());
        }
        else
        {
            attachmentData.setFileType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        }

        final MediaType mediaType = getMediaTypeforDocumentAttachment(attachmentData);
        attachmentData.setFileType(String.valueOf(mediaType));

        return attachmentData;
    }

    protected MediaType getMediaTypeforDocumentAttachment(final AttachmentData attachmentData) throws InvalidMediaTypeException
    {
        try
        {
            return MediaType.parseMediaType(attachmentData.getFileType());
        }
        catch (final InvalidMediaTypeException e)
        {
            throw new InvalidMediaTypeException(attachmentData.getFileType(),
                    "Cannot parse the attachment media type " + e.getMessage());
        }
    }

    protected AdnocProductService getAdnocProductService()
    {
        return adnocProductService;
    }

    public void setAdnocProductService(final AdnocProductService adnocProductService)
    {
        this.adnocProductService = adnocProductService;
    }

    protected MediaService getMediaService()
    {
        return mediaService;
    }

    public void setMediaService(final MediaService mediaService)
    {
        this.mediaService = mediaService;
    }
}
