package com.adnoc.service.validators;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.enums.PartnerFunction;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.Errors;

import java.util.*;

public class AdnocB2BCartValidator
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BCartValidator.class);

    private CartService cartService;
    private AdnocConfigService adnocConfigService;
    private AdnocB2BUnitService adnocB2BUnitService;

    public void validate(final Object target, final Errors errors)
    {
        final CartModel cartModel = getCartService().getSessionCart();
        final List<AbstractOrderEntryModel> entries = cartModel.getEntries();
        LOG.info("appEvent=AdnocCartValidator, validating cart with {} entries.", cartModel.getEntries());

        for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
        {
            try
            {
                LOG.debug("appEvent=AdnocCartValidator, validating cart for payer division entry: {}.", abstractOrderEntryModel.getProduct().getCode());
                validateCartAgainstPayerDivision(abstractOrderEntryModel.getDivision());
            }
            catch (final CommerceCartModificationException e)
            {
                LOG.error("appEvent=AdnocCartValidator, Payer division validation failed for product: {} with message: {}", abstractOrderEntryModel.getProduct().getCode(), e.getMessage());
                errors.reject("payer division is not available");
            }
            try
            {
                validateCartAgainstDivisionGrouping(cartModel, abstractOrderEntryModel.getDivision());
            }
            catch (final CommerceCartModificationException e)
            {
                LOG.error("appEvent=AdnocCartValidator, Division grouping validation failed for product: {} with message: {}", abstractOrderEntryModel.getProduct().getCode(), e.getMessage());
                errors.reject("Division grouping is not available");
            }
        }
    }

    /**
     * @param cartModel
     * @param productDivision
     * @throws CommerceCartModificationException
     */
    protected void validateCartAgainstDivisionGrouping(final CartModel cartModel, final String productDivision) throws CommerceCartModificationException
    {
        if (Objects.isNull(cartModel) || CollectionUtils.isEmpty(cartModel.getEntries()))
        {
            return;
        }
        final String configValue = getAdnocConfigService().getAdnocConfigValue("divisionGroupForAddToCart", StringUtils.EMPTY);
        LOG.debug("appEvent=AdnocCartValidator, config value for divisionGroupForAddToCart:{}", configValue);
        if (StringUtils.isBlank(configValue))
        {
            LOG.warn("Config value for division groups is blank.");
            return;
        }

        final Set<String> productDivisionsInCart = new HashSet<>();
        final String[] divisionGroups = configValue.split("\\|");
        for (final AbstractOrderEntryModel entry : cartModel.getEntries())
        {
            final String existingDivision = entry.getProduct().getDivision();
            productDivisionsInCart.add(existingDivision);
        }
        productDivisionsInCart.add(productDivision);
        final boolean canAdd = Arrays.stream(divisionGroups).anyMatch(divisionGroup -> {
            final Set<String> groupDivisions = new HashSet<>(Arrays.asList(divisionGroup.split(",")));
            return groupDivisions.containsAll(productDivisionsInCart);
        });

        final boolean isConflict = Arrays.stream(divisionGroups).anyMatch(divisionGroup -> {
            final Set<String> groupDivisions = new HashSet<>(Arrays.asList(divisionGroup.split(",")));
            return (groupDivisions.containsAll(productDivisionsInCart) && !groupDivisions.contains(productDivision));
        });

        if (!canAdd || isConflict)
        {
            LOG.error("appEvent=AdnocCartValidator, Product division {} is conflicting with existing product's division group in the cart.", productDivision);
            throw new CommerceCartModificationException("Product division " + productDivision + " is conflicting with existing product's division group in the cart.");
        }
        if (!isProductAllowed(configValue, productDivision))
        {
            LOG.error("appEvent=AdnocCartValidator, Product division {} is not allowed in the configuration.", productDivision);
            throw new CommerceCartModificationException("Product division is not allowed in the configuration.");
        }
    }

    /**
     * @param productDivision
     * @throws CommerceCartModificationException
     */
    protected void validateCartAgainstPayerDivision(final String productDivision) throws CommerceCartModificationException
    {
        LOG.info("appEvent=AdnocCartValidator, checking product division :{} against payer.", productDivision);
        if (StringUtils.isEmpty(productDivision))
        {
            throw new CommerceCartModificationException("Product is not associated to any division.");
        }
        final B2BUnitModel currentPayer = getAdnocB2BUnitService().getCurrentB2BUnit();
        if (Objects.isNull(currentPayer))
        {
            LOG.warn("appEvent=AdnocCartValidator, Rejecting cart no current payer configured");
            throw new CommerceCartModificationException("No DefaultB2BUnit configured as Payer.");
        }
        final Set<B2BUnitModel> childB2BUnits = getAdnocB2BUnitService().getChildB2BUnits(currentPayer, PartnerFunction.PY);
        if (CollectionUtils.isEmpty(childB2BUnits))
        {
            throw new CommerceCartModificationException("No child B2B units found for the current payer.");
        }

        final boolean isDivisionAllowed = childB2BUnits.stream().map(B2BUnitModel::getSalesOrg).filter(Objects::nonNull).map(SAPSalesOrganizationModel::getDivision)
                .anyMatch(salesOrgDivision -> StringUtils.equals(productDivision, salesOrgDivision));
        if (!isDivisionAllowed)
        {
            throw new CommerceCartModificationException("You are not authorized to purchase from this LOB. Please contact your sales manager for further assistance");
        }
    }

    /**
     * @param configValue
     * @param productDivision
     * @return
     */
    private boolean isProductAllowed(final String configValue, final String productDivision)
    {
        final Set<String> allowedDivisions = new HashSet<>();
        for (final String group : configValue.split("\\|"))
        {
            allowedDivisions.addAll(Arrays.asList(group.split(",")));
        }
        LOG.debug("appEvent=AdnocCartValidator, Allowed divisions: {}", allowedDivisions);
        return allowedDivisions.contains(productDivision);
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }

    protected CartService getCartService()
    {
        return cartService;
    }

    public void setCartService(final CartService cartService)
    {
        this.cartService = cartService;
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

}
