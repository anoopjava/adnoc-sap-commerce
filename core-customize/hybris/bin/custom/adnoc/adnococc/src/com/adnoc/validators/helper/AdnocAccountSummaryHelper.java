package com.adnoc.validators.helper;

import com.adnoc.facades.data.AdnocB2BInvoiceDocumentRequestData;
import com.adnoc.facades.downloadstatement.AdnocAccountSummaryFacade;
import de.hybris.platform.b2bacceleratorfacades.document.data.AttachmentData;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.b2bocc.v2.helper.AccountSummaryHelper;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.Objects;

public class AdnocAccountSummaryHelper extends AccountSummaryHelper
{
    private static final Logger LOG = LogManager.getLogger(AdnocAccountSummaryHelper.class);
    public static final String ADNOC_S4_DOCUMENT_TYPE = "adnoc.s4document.%s.type";

    @Resource(name = "adnocAccountSummaryFacade")
    private AdnocAccountSummaryFacade adnocAccountSummaryFacade;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Override
    public AttachmentData getMediaForDocument(final String orgUnitId, final String documentNumber, final String documentMediaId) throws NotFoundException
    {
        LOG.info("appEvent=AdnocS4InvoiceDocument,getMediaForDocument method called");
        // retrieve DocumentModel by documentNumber
        final B2BDocumentModel documentModel = adnocAccountSummaryFacade.getDocumentByIdForUnit(orgUnitId, documentNumber);

        // get documentMedia model and retrieve mediaId (media code)
        final DocumentMediaModel documentMediaModel = documentModel.getDocumentMedia();
        if (StringUtils.isBlank(documentMediaId) || Objects.isNull(documentMediaModel))
        {
            LOG.warn("appEvent=AdnocS4InvoiceDocument,Document media ID is blank or media model is null for document number {}", documentNumber);
            final AdnocB2BInvoiceDocumentRequestData adnocB2BInvoiceDocumentRequestData = new AdnocB2BInvoiceDocumentRequestData();
            adnocB2BInvoiceDocumentRequestData.setInvoiceNumber(documentNumber);
            final String documentTypeConfig = String.format(ADNOC_S4_DOCUMENT_TYPE, StringUtils.lowerCase(documentModel.getDocumentType().getCode()));
            adnocB2BInvoiceDocumentRequestData.setType(configurationService.getConfiguration().getString(documentTypeConfig, "ZRD6"));

            /* RestIntegration Implementation */
            return adnocAccountSummaryFacade.getB2BDocumentAttachmentData(adnocB2BInvoiceDocumentRequestData);
        }
        LOG.info("appEvent=AdnocS4InvoiceDocument, Found document media for document number {}", documentNumber);
        return super.getMediaForDocument(orgUnitId, documentNumber, documentMediaId);
    }
}