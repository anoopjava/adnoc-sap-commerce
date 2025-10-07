package com.adnoc.controllers;

import com.adnoc.b2bocc.user.data.IncoTermsWsDTO;
import com.adnoc.facades.AdnocCartFacade;
import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.service.enums.IncoTerms;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.annotation.SiteChannelRestriction;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static de.hybris.platform.b2bocc.constants.B2boccConstants.OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH;

@RestController
@ApiVersion("v2")
@Tag(name = "Adnoc B2B Carts")
public class AdnocB2BCartsController extends AdnocBaseController
{

    protected static final String API_COMPATIBILITY_B2B_CHANNELS = "api.compatibility.b2b.channels";

    @Resource(name = "dataMapper")
    protected DataMapper dataMapper;

    @Resource(name = "adnocCartFacade")
    private AdnocCartFacade adnocCartFacade;

    @Resource(name = "b2bCartFacade")
    private CartFacade cartFacade;

    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @PutMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH + "/carts/{cartId}/entries/updateforcheckout", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(operationId = "updateCartEntriesForCheckout", summary = "Updates the shipping address and requested delivery date of the specified entries in a cart.",
            description = "Adds specific products or overwrites the details of existing products in the cart, based either on the product code or the entry number. For existing products, attributes not provided in the request body will be defined again (set to null or default).")
    @RequestMappingOverride(priorityProperty = "b2bocc.CartResource.updateCartEntries.priority")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO updateCartEntriesForCheckout(
            @Parameter(description = "Base site identifier.") @PathVariable final String baseSiteId,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.FULL_LEVEL) final String fields,
            @Parameter(description = "List of entries containing the shipping address and requested delivery date for each entryNumber.")
            @RequestBody(required = true) final OrderEntryListWsDTO orderEntryListWsDTO) throws CommerceCartRestorationException, CommerceCartModificationException
    {
        final List<OrderEntryData> cartEntriesData = convertToData(orderEntryListWsDTO);
        final CartData cartData = adnocCartFacade.updateOrderEntryList(cartEntriesData);

        return dataMapper.map(cartData, CartWsDTO.class, fields);
    }

    @Operation(operationId = "addOrgCartEntry", summary = "Adds more quantity to the cart for a specific entry",
            description = "Adds more quantity to the cart for a specific entry based on it's product code, if the product is already in the cart the amount will be added to the existing quantity.")
    @RequestMappingOverride(priorityProperty = "b2bocc.CartResource.addCartEntry.priority")
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @PostMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH + "/carts/{cartId}/entries/addToCartEntry")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartModificationWsDTO addToCartEntry(
            @Parameter(description = "Base site identifier.", required = true) @PathVariable final String baseSiteId,
            @Parameter(description = "Code of the product to be added to the cart.", required = true) @RequestParam(required = true) final String code,
            @Parameter(description = "Amount to be added.", required = false) @RequestParam(required = false, defaultValue = "1") final long quantity,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
    {
        final OrderEntryData orderEntry = getOrderEntryData(quantity, code, null);
        return dataMapper.map(cartFacade.addOrderEntry(orderEntry), CartModificationWsDTO.class, fields);
    }

    protected OrderEntryData getOrderEntryData(final long quantity, final String productCode, final Integer entryNumber)
    {
        final OrderEntryData orderEntry = new OrderEntryData();
        orderEntry.setQuantity(quantity);
        orderEntry.setProduct(new ProductData());
        orderEntry.getProduct().setCode(productCode);
        orderEntry.setEntryNumber(entryNumber);

        return orderEntry;
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
        final IncoTermsData incotermsData = getIncoTermsData(incoTerms);
        orderEntry.setIncoTerms(incotermsData);
        if (Objects.nonNull(incotermsData) && StringUtils.equals(IncoTerms.PICKUP.getCode(), incotermsData.getCode()))
        {
            final PointOfServiceData pointOfServiceData = new PointOfServiceData();
            pointOfServiceData.setId(shippingAddressWsDto.getId());
            orderEntry.setDeliveryPointOfService(pointOfServiceData);
        }
        else
        {
            final AddressData shippingAddressData = new AddressData();
            shippingAddressData.setId(shippingAddressWsDto.getId());
            orderEntry.setDeliveryAddress(shippingAddressData);
        }
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


    protected CartModificationDataList getCartModificationDataList(final List<CartModificationData> result)
    {
        final CartModificationDataList cartModificationDataList = new CartModificationDataList();
        cartModificationDataList.setCartModificationList(result);
        return cartModificationDataList;
    }

}