package com.adnoc.facades.returnreason.impl;

import com.adnoc.facades.ordermanagement.data.ReturnReasonData;
import com.adnoc.facades.returnreason.AdnocReturnFacade;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.impl.DefaultOmsReturnFacade;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.ticket.service.UnsupportedAttachmentException;
import de.hybris.platform.util.localization.Localization;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

public class AdnocReturnFacadeImpl extends DefaultOmsReturnFacade implements AdnocReturnFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocReturnFacadeImpl.class);

    private Converter<RefundReason, ReturnReasonData> returnReasonDataConverter;
    private String allowedUploadedFormats;
    private MediaService mediaService;

    @Override
    public List<ReturnReasonData> getReturnReasons()
    {
        LOG.info("appEvent=AdnocReturnReasons, getReturnReasons method start");
        final List<RefundReason> refundReasons = getRefundReasons();
        return Converters.convertAll(refundReasons, getReturnReasonDataConverter());
    }

    @Override
    protected ReturnRequestModel createReturnRequestInContext(final OrderModel order, final ReturnRequestData returnRequestData)
    {
        LOG.info("appEvent=AdnocReturnFacade, createReturnRequestInContext called with order:{}", order);
        final Map<Integer, AbstractOrderEntryModel> orderEntries = new HashMap<>();
        returnRequestData.getReturnEntries().forEach(returnEntryData -> {
            final AbstractOrderEntryModel orderEntry = getOrderService()
                    .getEntryForNumber(order, returnEntryData.getOrderEntry().getEntryNumber());
            validateParameterNotNullStandardMessage("orderEntry", orderEntry);
            if (!getReturnService().isReturnable(order, orderEntry, returnEntryData.getExpectedQuantity()))
            {
                throw new IllegalArgumentException(
                        Localization.getLocalizedString("ordermanagementfacade.returns.validation.false.isReturnableQuantityPossible"));
            }
            orderEntries.put(returnEntryData.getOrderEntry().getEntryNumber(), orderEntry);
        });

        final boolean completeReturn = isCompleteReturn(order, returnRequestData);

        final ReturnRequestModel returnRequest = getReturnService().createReturnRequest(order);
        returnRequest.setRefundDeliveryCost(canRefundDeliveryCost(order.getCode(), returnRequestData.getRefundDeliveryCost()));

        returnRequestData.getReturnEntries().forEach(returnEntryData -> {
            final AbstractOrderEntryModel orderEntry = orderEntries.get(returnEntryData.getOrderEntry().getEntryNumber());
            final RefundEntryModel refundEntryToBeCreated = getReturnService()
                    .createRefund(returnRequest, orderEntry, returnEntryData.getNotes(), returnEntryData.getExpectedQuantity(),
                            returnEntryData.getAction(), returnRequestData.getRefundReason());
            refundEntryToBeCreated
                    .setAmount(calculateRefundEntryAmount(orderEntry, returnEntryData.getExpectedQuantity(), completeReturn));
            getModelService().save(refundEntryToBeCreated);
        });
        returnRequest.setSubtotal(recalculateSubtotal(returnRequest, completeReturn));
        //Saving returnRequestDocument into return request
        final MultipartFile returnRequestDocument = returnRequestData.getReturnRequestDocument();
        if (Objects.nonNull(returnRequestDocument))
        {
            try
            {
                LOG.info("appEvent=AdnocReturnFacade, setting attachment with {}", returnRequestDocument.getOriginalFilename());
                returnRequest.setReturnRequestDocument(createAttachment(returnRequestDocument.getOriginalFilename(), returnRequestDocument.getContentType(),
                        returnRequestDocument.getBytes()));
            }
            catch (final IOException ioException)
            {
                LOG.error(ioException.getMessage(), ioException);
                throw new RuntimeException("appEvent=AdnocReturnFacade, Failed setting attachment due to {}" + ExceptionUtils.getRootCauseMessage(ioException));
            }
        }
        Optional.ofNullable(returnRequestData.getRefundReason()).ifPresent(returnRequest::setReason);
        getModelService().save(returnRequest);

        try
        {
            LOG.info("appEvent=AdnocReturnFacade, refund for Order: {}", returnRequest.getOrder().getCode());
            getRefundService().apply(returnRequest.getOrder(), returnRequest);
        }
        catch (final OrderReturnRecordsHandlerException e)
        {
            LOG.info("appEvent=AdnocReturnFacade, Return record already in progress for Order {} ", order.getCode());
        }
        catch (final IllegalStateException ise)
        {
            LOG.info("appEvent=AdnocReturnFacade, Order {} Return record already in progress", order.getCode());
        }
        final CreateReturnEvent createReturnEvent = new CreateReturnEvent();
        createReturnEvent.setReturnRequest(returnRequest);
        getEventService().publishEvent(createReturnEvent);
        return returnRequest;
    }

    protected DocumentMediaModel createAttachment(final String fileName, final String contentType, final byte[] data)
    {
        LOGGER.info("appEvent=CartPoDocument, createAttachment method start");
        checkFileExtension(fileName);
        final DocumentMediaModel documentMediaModel = getModelService().create(DocumentMediaModel.class);
        documentMediaModel.setCode(UUID.randomUUID().toString());
        documentMediaModel.setMime(contentType);
        documentMediaModel.setRealFileName(fileName);
        getModelService().save(documentMediaModel);
        getMediaService().setDataForMedia(documentMediaModel, data);
        return documentMediaModel;
    }

    void checkFileExtension(final String name)
    {
        if (!FilenameUtils.isExtension(name.toLowerCase(), getAllowedUploadedFormats().replaceAll("\\s", "").toLowerCase().split(",")))
        {
            throw new UnsupportedAttachmentException(String.format("File %s has unsupported extension. Only [%s] allowed.", name, getAllowedUploadedFormats()));
        }
    }

    @Override
    protected void validateReturnEntryData(final ReturnEntryData returnEntryData)
    {
        notNull(returnEntryData.getExpectedQuantity(),
                Localization.getLocalizedString("ordermanagementfacade.returns.validation.null.expectedquantity"));
        notNull(returnEntryData.getAction(),
                Localization.getLocalizedString("ordermanagementfacade.returns.validation.null.action"));
        isTrue(getReturnActions().contains(returnEntryData.getAction()),
                Localization.getLocalizedString("ordermanagementfacade.returns.validation.false.returnaction"));

        validateOrderEntryForReturnEntry(returnEntryData.getOrderEntry());
    }


    @Override
    protected BigDecimal calculateRefundEntryAmount(final AbstractOrderEntryModel orderEntryModel, final Long expectedQuantity,
                                                    final boolean completeReturn)
    {
        final BigDecimal entryAmount;
        if (completeReturn)
        {
            entryAmount = BigDecimal.valueOf(orderEntryModel.getTotalPrice());
        }
        else
        {
            entryAmount = BigDecimal.valueOf(orderEntryModel.getBasePrice() * expectedQuantity);
        }
        return entryAmount.setScale(2, BigDecimal.ROUND_DOWN);
    }

    protected Converter<RefundReason, ReturnReasonData> getReturnReasonDataConverter()
    {
        return returnReasonDataConverter;
    }

    public void setReturnReasonDataConverter(final Converter<RefundReason, ReturnReasonData> returnReasonDataConverter)
    {
        this.returnReasonDataConverter = returnReasonDataConverter;
    }

    protected String getAllowedUploadedFormats()
    {
        return allowedUploadedFormats;
    }

    public void setAllowedUploadedFormats(final String allowedUploadedFormats)
    {
        this.allowedUploadedFormats = allowedUploadedFormats;
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




