package com.adnoc.service.quote;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.QuoteBuyerSubmitEvent;
import de.hybris.platform.commerceservices.event.QuoteSalesRepSubmitEvent;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceQuoteService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.util.DiscountValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class AdnocCommerceQuoteService extends DefaultCommerceQuoteService
{
    private static final Logger LOG = LogManager.getLogger(AdnocCommerceQuoteService.class);
    private AdnocB2BUnitService adnocB2BUnitService;
    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;

    @Override
    public QuoteModel submitQuote(final QuoteModel quoteModel, final UserModel userModel)
    {
        validateParameterNotNullStandardMessage("quoteModel", quoteModel);
        validateParameterNotNullStandardMessage("userModel", userModel);

        LOG.info("appEvent=AdnocCommerceQuote,starting quote submission.QuoteID:{}, UserID: {}", quoteModel.getCode(), userModel.getUid());
        getQuoteActionValidationStrategy().validate(QuoteAction.SUBMIT, quoteModel, userModel);

        QuoteModel updatedQuoteModel = isSessionQuoteSameAsRequestedQuote(quoteModel)
                ? updateQuoteFromCart(getCartService().getSessionCart(), userModel) : quoteModel;

        validateQuoteTotal(updatedQuoteModel);
        getQuoteMetadataValidationStrategy().validate(QuoteAction.SUBMIT, updatedQuoteModel, userModel);
        updatedQuoteModel = getQuoteUpdateExpirationTimeStrategy().updateExpirationTime(QuoteAction.SUBMIT, updatedQuoteModel,
                userModel);
        updatedQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.SUBMIT, updatedQuoteModel, userModel);
        final B2BCustomerModel currentB2BCustomer = (B2BCustomerModel) updatedQuoteModel.getUser();
        updatedQuoteModel.setUnit(getAdnocB2BUnitService().getParent(currentB2BCustomer));
        getModelService().save(updatedQuoteModel);
        getModelService().refresh(updatedQuoteModel);

        final QuoteUserType quoteUserType = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel).orElseThrow();
        if (QuoteUserType.BUYER.equals(quoteUserType))
        {

            LOG.debug("appEvent=AdnocCommerceQuote, publishing QuoteBuyerSubmitEvent for Quote ID:{}", quoteModel.getCode());
            final QuoteBuyerSubmitEvent quoteBuyerSubmitEvent = new QuoteBuyerSubmitEvent(updatedQuoteModel, userModel,
                    quoteUserType);
            getEventService().publishEvent(quoteBuyerSubmitEvent);
        }
        else if (QuoteUserType.SELLER.equals(quoteUserType))
        {
            LOG.debug("appEvent=AdnocCommerceQuote, publishing QuoteSalesRepSubmitEvent for Quote ID:{}", quoteModel.getCode());
            final QuoteSalesRepSubmitEvent quoteSalesRepSubmitEvent = new QuoteSalesRepSubmitEvent(updatedQuoteModel, userModel,
                    quoteUserType);
            getEventService().publishEvent(quoteSalesRepSubmitEvent);
        }
        LOG.info("appEvent=AdnocCommerceQuote, Quote submission completed for Quote ID:{}", updatedQuoteModel.getCode());
        getAdnocOutboundReplicationDirector().scheduleOutboundTask(updatedQuoteModel);
        return updatedQuoteModel;
    }

    @Override
    public void cancelQuote(final QuoteModel quoteModel, final UserModel userModel)
    {
        super.cancelQuote(quoteModel, userModel);
        removeExistingQuoteDiscount(quoteModel);
        removeExistingQuoteEntryDiscount(quoteModel);
    }

    @Override
    public CartModel loadQuoteAsSessionCart(final QuoteModel quoteModel, final UserModel userModel)
    {
        getQuoteActionValidationStrategy().validate(QuoteAction.EDIT, quoteModel, userModel);
        // if the quote is in offer state, remove quote related cart & quote discounts
        if (getQuoteActionValidationStrategy().isValidAction(QuoteAction.CHECKOUT, quoteModel, userModel))
        {
            removeQuoteCart(quoteModel);
            quoteModel.setPreviousEstimatedTotal(quoteModel.getTotalPrice());
            removeExistingQuoteDiscount(quoteModel);
            removeExistingQuoteEntryDiscount(quoteModel);
            getModelService().save(quoteModel);
            getModelService().refresh(quoteModel);
        }
        return updateAndLoadQuoteCartWithAction(quoteModel, QuoteAction.EDIT, userModel);
    }

    /**
     * Remove existing quote entry discount.
     *
     * @param abstractOrderModel the abstract order model
     */
    public void removeExistingQuoteEntryDiscount(final AbstractOrderModel abstractOrderModel)
    {
        if (Objects.nonNull(abstractOrderModel)
                && getModelService().isAttached(abstractOrderModel)
                && CollectionUtils.isNotEmpty(abstractOrderModel.getEntries()))
        {

            abstractOrderModel.getEntries().forEach(entry -> {
                entry.setDiscountValues(Collections.emptyList());
                setQuoteEntryDiscountValues(entry, Collections.emptyList());
                entry.setCalculated(Boolean.FALSE);
                getModelService().save(entry);
            });

            recalculateOrder(abstractOrderModel);
        }
    }

    /**
     * Remove existing quote discount.
     *
     * @param abstractOrderModel the abstract order model
     */
    private List<DiscountValue> removeExistingQuoteDiscount(final AbstractOrderModel abstractOrderModel)
    {
        if (Objects.nonNull(abstractOrderModel) && getModelService().isAttached(abstractOrderModel) && CollectionUtils.isNotEmpty(abstractOrderModel.getGlobalDiscountValues()))
        {
            final List<DiscountValue> discountList = new ArrayList<>(abstractOrderModel.getGlobalDiscountValues());
            abstractOrderModel.setGlobalDiscountValues(Collections.emptyList());
            getOrderQuoteDiscountValuesAccessor().setQuoteDiscountValues(abstractOrderModel, Collections.emptyList());
            recalculateOrder(abstractOrderModel);
            return discountList;
        }
        return Collections.emptyList();
    }

    private void recalculateOrder(final AbstractOrderModel abstractOrderModel)
    {
        try
        {
            getCalculationService().recalculate(abstractOrderModel);
        }
        catch (final CalculationException e)
        {
            throw new RuntimeException("Failed to recalculate the order model", e);
        }
    }

    public void setQuoteEntryDiscountValues(final AbstractOrderEntryModel abstractOrderEntryModel, final List<DiscountValue> discountValues)
    {
        final String discountValuesString = DiscountValue.toString(discountValues);
        abstractOrderEntryModel.setDiscountValuesInternal(discountValuesString);
        abstractOrderEntryModel.setCalculated(Boolean.FALSE);
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }

    protected AdnocOutboundReplicationDirector getAdnocOutboundReplicationDirector()
    {
        return adnocOutboundReplicationDirector;
    }

    public void setAdnocOutboundReplicationDirector(final AdnocOutboundReplicationDirector adnocOutboundReplicationDirector)
    {
        this.adnocOutboundReplicationDirector = adnocOutboundReplicationDirector;
    }
}
