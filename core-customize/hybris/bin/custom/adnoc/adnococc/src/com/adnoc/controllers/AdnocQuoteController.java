package com.adnoc.controllers;

import com.adnoc.b2bocc.user.data.IncoTermsWsDTO;
import com.adnoc.facades.AdnocCartFacade;
import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.facades.quote.AdnocQuoteFacade;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.b2bocc.v2.helper.QuoteHelper;
import de.hybris.platform.b2bocc.v2.requestfrom.RequestFromValueSetter;
import de.hybris.platform.b2bocc.v2.skipfield.SkipQuoteFieldValueSetter;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteActionWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteStarterWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

import static de.hybris.platform.b2bocc.constants.B2boccConstants.OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH;
import static de.hybris.platform.commercefacades.order.constants.OrderOccControllerRequestFromConstants.QUOTE_CONTROLLER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@ApiVersion("v2")
@Tag(name = "Adnoc B2B Quote")
public class AdnocQuoteController extends AdnocBaseController
{

    @Resource(name = "quoteHelper")
    private QuoteHelper quoteHelper;

    @Resource(name = "skipQuoteFieldValueSetter")
    private SkipQuoteFieldValueSetter skipQuoteFieldValueSetter;

    @Resource(name = "b2bRequestFromValueSetter")
    private RequestFromValueSetter requestFromValueSetter;

    @Resource(name = "adnocQuoteFacade")
    private AdnocQuoteFacade adnocQuoteFacade;

    @Resource(name = "adnocCartFacade")
    private AdnocCartFacade adnocCartFacade;

    @PostMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH + "/carts/{cartId}/adnocQuotes", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @Operation(summary = "Creates a adnoc quote.", description = "Creates a quote by linking a cart using the cart identifier (cartId) to the quote. To trigger a requote, provide a value to the quoteCode parameter, instead of the cartId parameter inside the request body. The response body will contain the new data for the quote.", operationId = "createQuote")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public QuoteWsDTO createAdnocQuote(
            @Parameter(description = "Object representing ways of creating new quote - by cartId for creating a new quote from the cart, by quoteCode for the requote action ", required = true) @RequestBody @Nonnull @Valid final QuoteStarterWsDTO quoteStarter,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
            throws VoucherOperationException, CommerceCartModificationException, CommerceCartRestorationException
    {
        final boolean quoteCodePresent = StringUtils.hasText(quoteStarter.getQuoteCode());
        final boolean cartIdPresent = StringUtils.hasText(quoteStarter.getCartId());

        skipQuoteFieldValueSetter.setValue(fields);
        requestFromValueSetter.setRequestFrom(QUOTE_CONTROLLER);

        if (cartIdPresent && !quoteCodePresent)
        {
            adnocQuoteFacade.validateQuoteAgainstCrossDivision();
            final List<OrderEntryData> cartEntriesData = convertToData(quoteStarter.getOrderEntryList());
            adnocCartFacade.updateOrderEntryList(cartEntriesData);
            return getQuoteHelper().initiateQuote(quoteStarter.getCartId(), fields);
        }
        if (!cartIdPresent && quoteCodePresent)
        {
            return getQuoteHelper().requote(quoteStarter.getQuoteCode(), fields);
        }
        throw new IllegalArgumentException("Either cartId or quoteCode must be provided");
    }

    @PostMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH + "/quotes/{quoteCode}/adnocAction", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Creates workflow actions for the quote.", description = "Creates workflow action during the quote editing process. Possible values are: CANCEL, SUBMIT, EDIT, CHECKOUT, APPROVE, or REJECT.", operationId = "performQuoteAction")
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @ApiBaseSiteIdAndUserIdParam
    public void performAdnocQuoteAction(
            @Parameter(description = "Code of the quote.", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
            @Parameter(description = "The action with the quote. The quote action field is mandatory.", required = true) @RequestBody @Nonnull @Valid final QuoteActionWsDTO quoteAction)
            throws VoucherOperationException, CommerceCartModificationException, CommerceCartRestorationException
    {
        if (quoteAction.getAction() == null)
        {
            throw new IllegalArgumentException("Provided action cannot be null");
        }

        final String action = quoteAction.getAction().toUpperCase(Locale.ENGLISH);
        switch (action)
        {
            case "SUBMIT":
                final List<OrderEntryData> cartEntriesData = convertToData(quoteAction.getOrderEntryList());
                adnocQuoteFacade.updateOrderEntryList(cartEntriesData, quoteCode);
                getQuoteHelper().submitQuote(quoteCode);
                break;

            case "CANCEL":
                getQuoteHelper().cancelQuote(quoteCode);
                break;

            case "APPROVE":
                getQuoteHelper().approveQuote(quoteCode);
                break;

            case "REJECT":
                getQuoteHelper().rejectQuote(quoteCode);
                break;

            case "CHECKOUT":
                getQuoteHelper().acceptAndPrepareCheckout(quoteCode);
                break;

            case "EDIT":
                getQuoteHelper().enableQuoteEdit(quoteCode);
                break;

            default:
                throw new IllegalArgumentException("Provided action not supported");
        }
    }

    protected List<OrderEntryData> convertToData(final OrderEntryListWsDTO entriesWS)
    {
        final List<OrderEntryData> entriesData = new ArrayList<>();

        for (final OrderEntryWsDTO orderEntryWsDTO : entriesWS.getOrderEntries())
        {
            final OrderEntryData entryData = getOrderEntryData(orderEntryWsDTO.getEntryNumber(),
                    orderEntryWsDTO.getDeliveryAddress(), orderEntryWsDTO.getNamedDeliveryDate(), orderEntryWsDTO.getIncoTerms());
            entriesData.add(entryData);
        }
        return entriesData;
    }

    protected OrderEntryData getOrderEntryData(final Integer entryNumber, final AddressWsDTO shippingAddressWsDto,
                                               final Date requestedDeliveryDate, final IncoTermsWsDTO incoTerms)
    {
        final OrderEntryData orderEntry = new OrderEntryData();
        orderEntry.setEntryNumber(entryNumber);
        orderEntry.setNamedDeliveryDate(requestedDeliveryDate);
        final AddressData shippingAddressData = new AddressData();
        shippingAddressData.setId(shippingAddressWsDto.getId());
        orderEntry.setDeliveryAddress(shippingAddressData);
        final IncoTermsData incotermsData = getIncoTermsData(incoTerms);
        orderEntry.setIncoTerms(incotermsData);

        return orderEntry;
    }

    private IncoTermsData getIncoTermsData(final IncoTermsWsDTO incoTermsWsDTO)
    {
        if (Objects.isNull(incoTermsWsDTO))
        {
            return null;
        }
        final IncoTermsData incotermsData = new IncoTermsData();
        incotermsData.setCode(incoTermsWsDTO.getCode());
        incotermsData.setName(incoTermsWsDTO.getName());

        return incotermsData;
    }

    protected QuoteHelper getQuoteHelper()
    {
        return quoteHelper;
    }

    public void setQuoteHelper(final QuoteHelper quoteHelper)
    {
        this.quoteHelper = quoteHelper;
    }

}
