package com.adnoc.facades;

import com.adnoc.service.address.AdnocAddressService;
import com.adnoc.service.enums.IncoTerms;
import com.adnoc.service.storeservice.AdnocStoreService;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class AdnocCartFacadeImpl extends DefaultCartFacade implements AdnocCartFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocCartFacadeImpl.class);

    public static final String PICKUP = "pickup";
    public static final String STANDARD_NET = "standard-net";

    private AdnocAddressService adnocAddressService;
    private CalculationService calculationService;
    private EnumerationService enumerationService;
    private AdnocStoreService adnocStoreService;

    @Override
    public CartModificationData addToCart(final AddToCartParams addToCartParams) throws CommerceCartModificationException
    {
        validateCartQuote();
        LOG.info("appEvent=AdnocCart, cart quote validated");

        final CommerceCartParameter parameter = getCommerceCartParameterConverter().convert(addToCartParams);
        Optional.ofNullable(parameter).ifPresent(p -> p.setCreateNewEntry(Boolean.TRUE));
        final CommerceCartModification modification = getCommerceCartService().addToCart(parameter);

        return getCartModificationConverter().convert(modification);
    }

    @Override
    public CartData updateOrderEntryList(final List<OrderEntryData> cartEntriesData) throws CommerceCartRestorationException, CommerceCartModificationException
    {
        if (Objects.isNull(getCartService().hasSessionCart()))
        {
            LOG.error("appEvent=AdnocCart, No Session Cart found");
            throw new CommerceCartRestorationException("No Session Cart.");
        }

        final CartModel cartModel = getCartService().getSessionCart();
        if (CollectionUtils.isEmpty(cartModel.getEntries()))
        {
            throw new CommerceCartModificationException("Session Cart is not having any entry.");
        }
        LOG.info("appEvent=AdnocCart, updating cart entries for session cart!");
        updateCartEntries(cartEntriesData, cartModel);
        return getSessionCart();
    }

    @Override
    public List<CartModificationData> updateCartEntries(final List<OrderEntryData> cartEntriesData, final CartModel cartModel) throws CommerceCartModificationException
    {
        final List<CartModificationData> cartModificationDataList = new ArrayList<>();
        final List<AbstractOrderEntryModel> modifiedEntries = new ArrayList<>();
        boolean isPickUpCart = false;
        for (final OrderEntryData orderEntryData : cartEntriesData)
        {
            final AbstractOrderEntryModel abstractOrderEntryModel = getEntryForNumber(cartModel, orderEntryData.getEntryNumber());
            validateOrderEntryToUpdate(orderEntryData, abstractOrderEntryModel);
            isPickUpCart = updateCartEntry(orderEntryData, abstractOrderEntryModel);
            modifiedEntries.add(abstractOrderEntryModel);
            final CartModificationData cartModificationData = createCartModificationData(orderEntryData, cartModel);
            cartModificationDataList.add(cartModificationData);
        }
        validateOrderEntriesForDuplicates(cartModel);
        getModelService().saveAll(modifiedEntries);
        updateCart(cartModel, isPickUpCart);
        return cartModificationDataList;
    }

    private boolean updateCartEntry(final OrderEntryData orderEntryData, final AbstractOrderEntryModel abstractOrderEntryModel) throws CommerceCartModificationException
    {
        boolean isPickUpCart = false;
        // setting pos or delivery address based on incoterms selection
        final boolean isPickup = Objects.nonNull(orderEntryData.getIncoTerms()) &&
                StringUtils.equals(IncoTerms.PICKUP.getCode(), orderEntryData.getIncoTerms().getCode());
        if (isPickup)
        {
            isPickUpCart = true;
            setPointOfServiceAddress(orderEntryData, abstractOrderEntryModel);
        }
        else
        {
            setAddress(abstractOrderEntryModel, orderEntryData.getDeliveryAddress());
            abstractOrderEntryModel.setDeliveryPointOfService(null);
        }
        abstractOrderEntryModel.setNamedDeliveryDate(orderEntryData.getNamedDeliveryDate());
        abstractOrderEntryModel.setIncoTerms(getEnumerationService().getEnumerationValue(IncoTerms._TYPECODE, orderEntryData.getIncoTerms().getCode()));
        return isPickUpCart;
    }

    private void validateOrderEntryToUpdate(final OrderEntryData orderEntryData, final AbstractOrderEntryModel abstractOrderEntryModel) throws CommerceCartModificationException
    {
        if (Objects.isNull(abstractOrderEntryModel))
        {
            LOG.error("appEvent=AdnocCart, No Cart entry found with entry number:{}", orderEntryData.getEntryNumber());
            throw new CommerceCartModificationException(String.format("No Cart Entry found with entry number %s.", orderEntryData.getEntryNumber()));
        }

        if (Objects.isNull(orderEntryData.getIncoTerms()) || StringUtils.isBlank(orderEntryData.getIncoTerms().getCode()))
        {
            throw new CommerceCartModificationException(String.format("No IncoTerms provided for EntryNumber=%s.", orderEntryData.getEntryNumber()));
        }
    }

    private void validateOrderEntriesForDuplicates(final CartModel cartModel) throws CommerceCartModificationException
    {
        // Grouping based on product, delivery address, and requested delivery date
        final Map<ProductModel, Map<AddressModel, Map<LocalDate, List<AbstractOrderEntryModel>>>> cartEntriesGroupByDeliveryAddressDate =
                cartModel.getEntries().stream().collect(
                        Collectors.groupingBy(AbstractOrderEntryModel::getProduct,
                                Collectors.groupingBy(AbstractOrderEntryModel::getDeliveryAddress,
                                        Collectors.groupingBy(entry -> toLocalDate(entry.getNamedDeliveryDate()))))
                );

        // Flatten and find duplicates
        final boolean hasDuplicates = cartEntriesGroupByDeliveryAddressDate.values().stream()
                .flatMap(deliveryAddressDateMap -> deliveryAddressDateMap.values().stream())
                .flatMap(deliveryDateMap -> deliveryDateMap.values().stream())
                .anyMatch(list -> list.size() > 1);

        if (hasDuplicates)
        {
            LOG.warn("Duplicate cart entries found with same product, delivery address and requested delivery date.");
            throw new CommerceCartModificationException("Duplicate entry exist in the cart. Please remove to proceed.");
        }
    }

    private LocalDate toLocalDate(final Date date)
    {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void updateCart(final CartModel cartModel, final boolean isPickUpCart)
    {
        cartModel.setDeliveryMode(getDeliveryService().getDeliveryModeForCode(isPickUpCart ? PICKUP : STANDARD_NET));
        cartModel.setDeliveryAddress(Objects.nonNull(cartModel.getEntries().get(0).getDeliveryAddress()) ?
                cartModel.getEntries().get(0).getDeliveryAddress() :
                cartModel.getEntries().get(0).getDeliveryPointOfService().getAddress());
        getModelService().save(cartModel);
        try
        {
            getCalculationService().recalculate(cartModel);
        }
        catch (final CalculationException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void setPointOfServiceAddress(final OrderEntryData orderEntryData, final AbstractOrderEntryModel abstractOrderEntryModel) throws CommerceCartModificationException
    {
        if (Objects.isNull(orderEntryData.getDeliveryPointOfService()) || Objects.isNull(orderEntryData.getDeliveryPointOfService().getId()))
        {
            throw new CommerceCartModificationException(String.format("No Pickup address provided for EntryNumber %s.", orderEntryData.getEntryNumber()));
        }
        final PointOfServiceModel posModel = getAdnocStoreService().getPointOfServicePk(orderEntryData.getDeliveryPointOfService().getId());
        if (Objects.isNull(posModel))
        {
            throw new CommerceCartModificationException(String.format("No Pickup Address found for POS ID: %s", orderEntryData.getDeliveryPointOfService().getId()));
        }
        abstractOrderEntryModel.setDeliveryPointOfService(posModel);
        abstractOrderEntryModel.setDeliveryAddress(posModel.getAddress());
    }

    private CartModificationData createCartModificationData(final OrderEntryData orderEntryData, final CartModel cartModel)
    {
        final CartModificationData cartModificationData = new CartModificationData();
        cartModificationData.setCartCode(cartModel.getCode());
        cartModificationData.setEntry(orderEntryData);
        cartModificationData.setStatusCode(CommerceCartModificationStatus.SUCCESS);
        LOG.info("appEvent=AdnocCart, CartModificationData created:{}", cartModificationData);
        return cartModificationData;
    }

    private AbstractOrderEntryModel getEntryForNumber(final AbstractOrderModel order, final int number)
    {
        return order.getEntries().stream().
                filter(entry -> Objects.nonNull(entry) && Objects.equals(number, entry.getEntryNumber())).findFirst().orElse(null);
    }

    private void setAddress(final AbstractOrderEntryModel entryModel, final AddressData addressData) throws CommerceCartModificationException
    {
        final AddressModel addressModel = getAdnocAddressService().getAddress(addressData.getId());
        if (Objects.isNull(addressModel))
        {
            throw new CommerceCartModificationException(String.format("No Address found with pk=%s.", addressData.getId()));
        }
        entryModel.setDeliveryAddress(addressModel);
    }

    protected AdnocAddressService getAdnocAddressService()
    {
        return adnocAddressService;
    }

    public void setAdnocAddressService(final AdnocAddressService adnocAddressService)
    {
        this.adnocAddressService = adnocAddressService;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    protected AdnocStoreService getAdnocStoreService()
    {
        return adnocStoreService;
    }

    public void setAdnocStoreService(final AdnocStoreService adnocStoreService)
    {
        this.adnocStoreService = adnocStoreService;
    }

    protected CalculationService getCalculationService()
    {
        return calculationService;
    }

    public void setCalculationService(final CalculationService calculationService)
    {
        this.calculationService = calculationService;
    }
}
