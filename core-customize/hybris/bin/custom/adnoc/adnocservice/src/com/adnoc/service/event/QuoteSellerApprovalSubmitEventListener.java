/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.event;

import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.commerceservices.event.QuoteSellerApprovalSubmitEvent;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static com.adnoc.service.constants.AdnocserviceConstants.QUOTE_SELLER_APPROVER_PROCESS;


/**
 * Event listener that listens to {@link QuoteSellerApprovalSubmitEvent} which is used to trigger seller approval
 * process.
 */
public class QuoteSellerApprovalSubmitEventListener extends AbstractEventListener<QuoteSellerApprovalSubmitEvent>
{
    private static final Logger LOG = LogManager.getLogger(QuoteSellerApprovalSubmitEventListener.class);

    public static final String QUOTE_EMAIL_ATTACHMENT = "quoteEmailAttachment";

    private ModelService modelService;
    private BusinessProcessService businessProcessService;
    private MediaService mediaService;

    @Override
    protected void onEvent(final QuoteSellerApprovalSubmitEvent event)
    {
        QuoteModel quoteModel = event.getQuote();
        String quoteCode = quoteModel.getCode();
        LOG.info("appEvent=QuoteSellerApproval, quoteCode initiating email for quote={}",quoteCode);

        final QuoteProcessModel quoteSellerApprovalProcess = getBusinessProcessService()
                .createProcess("quoteSellerApprovalProcess" + "-" + quoteCode + "-"
                        + quoteModel.getStore().getUid() + "-" + System.currentTimeMillis(), QUOTE_SELLER_APPROVER_PROCESS);
        quoteSellerApprovalProcess.setQuoteCode(quoteCode);
        quoteSellerApprovalProcess.setUser(quoteModel.getUser());
        if (StringUtils.isNotEmpty(quoteModel.getQuoteDocument()))
        {
            LOG.info("appEvent=QuoteSellerApproval, Quote Document found for quote={}",quoteCode);
            EmailAttachmentModel emailAttachmentModel = createEmailAttachment(quoteModel);
            LOG.info("appEvent=QuoteSellerApproval, Email attachment created for quote={}",quoteCode);
            updateBusinessProcessParameter(emailAttachmentModel,quoteSellerApprovalProcess);
            LOG.info("appEvent=QuoteSellerApproval, business process parameter updated for quote={}",quoteCode);
        }
        getModelService().save(quoteSellerApprovalProcess);
        //start the business process
        getBusinessProcessService().startProcess(quoteSellerApprovalProcess);
    }

    private EmailAttachmentModel createEmailAttachment(QuoteModel quoteModel)
    {
        String quoteDocumentBase64 = quoteModel.getQuoteDocument();
        final byte[] decodedBytes = Base64.getDecoder().decode(quoteDocumentBase64);
        final EmailAttachmentModel emailAttachment = getModelService().create(EmailAttachmentModel.class);
        emailAttachment.setCode("attachment-" + UUID.randomUUID() + System.currentTimeMillis());
        emailAttachment.setMime("application/pdf");
        emailAttachment.setRealFileName(quoteModel.getName() + ".pdf");
        getModelService().save(emailAttachment);
        getMediaService().setDataForMedia(emailAttachment, decodedBytes);
        return emailAttachment;
    }

    private void updateBusinessProcessParameter(EmailAttachmentModel emailAttachment, final QuoteProcessModel quoteSellerApprovalProcess)
    {
        final BusinessProcessParameterModel businessProcessParameterModel = modelService.create(BusinessProcessParameterModel.class);
        businessProcessParameterModel.setValue(emailAttachment);
        businessProcessParameterModel.setName(QUOTE_EMAIL_ATTACHMENT);
        businessProcessParameterModel.setProcess(quoteSellerApprovalProcess);
        getModelService().save(businessProcessParameterModel);
        quoteSellerApprovalProcess.setContextParameters(List.of(businessProcessParameterModel));
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected BusinessProcessService getBusinessProcessService()
    {
        return businessProcessService;
    }

    public void setBusinessProcessService(final BusinessProcessService businessProcessService)
    {
        this.businessProcessService = businessProcessService;
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
