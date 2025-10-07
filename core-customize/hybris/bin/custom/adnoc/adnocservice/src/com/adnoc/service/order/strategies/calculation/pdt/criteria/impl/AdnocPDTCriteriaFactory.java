package com.adnoc.service.order.strategies.calculation.pdt.criteria.impl;

import com.adnoc.service.order.strategies.calculation.pdt.impl.converter.impl.AdnocPriceValueInfoCriteria;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.enums.ProductPriceGroup;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.pdt.criteria.impl.DefaultPDTCriteriaFactory;
import de.hybris.platform.order.strategies.calculation.pdt.impl.PDTEnumGroupsHelper;
import de.hybris.platform.product.BaseCriteria;
import de.hybris.platform.servicelayer.internal.i18n.I18NConstants;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;
import java.util.Objects;

public class AdnocPDTCriteriaFactory extends DefaultPDTCriteriaFactory
{
    private PDTEnumGroupsHelper pdtEnumGroupsHelper;
    private UserService userService;
    private SessionService sessionService;

    @Override
    public AdnocPriceValueInfoCriteria priceValueCriteriaFromOrderEntry(final AbstractOrderEntryModel abstractOrderEntryModel)
            throws CalculationException
    {
        final AbstractOrderModel abstractOrderModel = abstractOrderEntryModel.getOrder();
        final ProductModel product = abstractOrderEntryModel.getProduct();
        final boolean giveAwayMode = abstractOrderEntryModel.getGiveAway().booleanValue();
        final boolean entryRejected = abstractOrderEntryModel.getRejected().booleanValue();

        final ProductPriceGroup productGroup = pdtEnumGroupsHelper.getPPG(product);
        final UserModel user = abstractOrderModel.getUser();
        final UserPriceGroup userGroup = pdtEnumGroupsHelper.getUPG(abstractOrderEntryModel);
        long quantity = abstractOrderEntryModel.getQuantity().longValue();
        quantity = quantity == 0 ? 1 : quantity;
        final UnitModel unit = abstractOrderEntryModel.getUnit();
        final CurrencyModel currency = abstractOrderModel.getCurrency();
        final Date date = Objects.nonNull(abstractOrderEntryModel.getNamedDeliveryDate()) ?
                abstractOrderEntryModel.getNamedDeliveryDate() : abstractOrderModel.getDate();
        final boolean net = abstractOrderModel.getNet().booleanValue();


        final AddressModel shippingAddress = Objects.nonNull(abstractOrderEntryModel.getDeliveryAddress()) ?
                abstractOrderEntryModel.getDeliveryAddress() : abstractOrderModel.getDeliveryAddress();
        return AdnocPriceValueInfoCriteria.buildForValue()
                .withProduct(product)
                .withProductPriceGroup(productGroup)
                .withUser(user)
                .withUserPriceGroup(userGroup)
                .withCurrency(currency)
                .withQuantity(quantity)
                .withUnit(unit)
                .withDate(date)
                .withNet(net)
                .withGiveAwayMode(giveAwayMode)
                .withEntryRejected(entryRejected)
                .withShippingAddress(shippingAddress)
                .withIncoTerms(Objects.nonNull(abstractOrderEntryModel.getIncoTerms()) ? abstractOrderEntryModel.getIncoTerms().getCode() : null)
                .build();
    }

    @Override
    public AdnocPriceValueInfoCriteria priceInfoCriteriaFromBaseCriteria(final BaseCriteria baseCriteria) throws CalculationException
    {
        final ProductModel productModel = baseCriteria.getProduct();
        return AdnocPriceValueInfoCriteria.buildForInfo()
                .withProduct(productModel)
                .withProductPriceGroup(pdtEnumGroupsHelper.getPPG(productModel))
                .withUser(userService.getCurrentUser())
                .withUserPriceGroup(pdtEnumGroupsHelper.getUPG(userService.getCurrentUser()))
                .withCurrency(sessionService.getAttribute(I18NConstants.CURRENCY_SESSION_ATTR_KEY))
                .withQuantity(1)
                .withUnit(productModel.getUnit())
                .withNet(baseCriteria.isNet())
                .withDate(baseCriteria.getDate())
                .build();
    }

    @Override
    public void setPdtEnumGroupsHelper(final PDTEnumGroupsHelper pdtEnumGroupsHelper)
    {
        super.setPdtEnumGroupsHelper(pdtEnumGroupsHelper);
        this.pdtEnumGroupsHelper = pdtEnumGroupsHelper;
    }

    @Override
    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    @Override
    public void setSessionService(final SessionService sessionService)
    {
        this.sessionService = sessionService;
    }
}
