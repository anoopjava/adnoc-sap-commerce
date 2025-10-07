package com.adnoc.facades.strategies;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.impl.GenericAbstractOrderCloningStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class AdnocCreateCartFromCartStrategyImpl extends GenericAbstractOrderCloningStrategy<CartModel, CartEntryModel, CartModel> implements AdnocCreateCartFromCartStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocCreateCartFromCartStrategyImpl.class);

    private ModelService modelService;
    private CalculationService calculationService;

    public AdnocCreateCartFromCartStrategyImpl()
    {
        super(CartModel.class, CartEntryModel.class, CartModel.class);
    }

    @Override
    public List<CartModel> createCartFromCart(final CartModel cartModel)
    {
        LOG.info("createCartFromCart method start!..");
        validateParameterNotNullStandardMessage("cart", cartModel);
        final Map<String, Map<AddressModel, List<AbstractOrderEntryModel>>> cartEntriesGroupByDivisionAddress = getCartEntriesGroupByDivisionAddress(cartModel);
        if (MapUtils.isEmpty(cartEntriesGroupByDivisionAddress))
        {
            LOG.warn("No entries found grouped by division address.");
            return Collections.emptyList();
        }
        final List<CartModel> cartModels = new ArrayList<>();
        for (final Map<AddressModel, List<AbstractOrderEntryModel>> addressEntriesMapCollection : cartEntriesGroupByDivisionAddress.values())
        {
            for (final Map.Entry<AddressModel, List<AbstractOrderEntryModel>> abstractOrderEntriesMapEntry : addressEntriesMapCollection.entrySet())
            {
                final CartModel clonedCart = clone(cartModel, Optional.empty());
                clonedCart.setEntries(abstractOrderEntriesMapEntry.getValue());
                clonedCart.setDeliveryAddress(abstractOrderEntriesMapEntry.getKey());
                postProcess(cartModel, clonedCart);
                cartModels.add(clonedCart);
            }
        }
        return cartModels;
    }

    private Map<String, Map<AddressModel, List<AbstractOrderEntryModel>>> getCartEntriesGroupByDivisionAddress(final CartModel cartModel)
    {
        return cartModel.getEntries().stream().collect(
                Collectors.groupingBy(AbstractOrderEntryModel::getDivision,
                        Collectors.groupingBy(AbstractOrderEntryModel::getDeliveryAddress)));
    }

    @Override
    protected void postProcess(final CartModel original, final CartModel clonedCartModel)
    {
        LOG.info("postProcess method called with original cartModel: {}, clonedCartModel: {}", original, clonedCartModel);
        final List<AbstractOrderEntryModel> newEntries = IntStream.range(0, clonedCartModel.getEntries().size())
                .mapToObj(entryNumber -> {
                    final AbstractOrderEntryModel copyEntry = clonedCartModel.getEntries().get(entryNumber);
                    return cloneOrderEntry(copyEntry, clonedCartModel, entryNumber);
                }).collect(Collectors.toList());

        clonedCartModel.setEntries(newEntries);
        getModelService().saveAll(newEntries);
        getModelService().save(clonedCartModel);
        try
        {
            getCalculationService().recalculate(clonedCartModel);
        }
        catch (final CalculationException e)
        {
            throw new RuntimeException(e);
        }
    }

    private AbstractOrderEntryModel cloneOrderEntry(final AbstractOrderEntryModel abstractOrderEntryModel,
                                                    final CartModel cloneCartModel, final Integer entryNumber)
    {
        LOG.info("cloneOrderEntry method called with abstractOrderEntryModel");
        final AbstractOrderEntryModel clonedEntryModel = getModelService().clone(abstractOrderEntryModel);
        clonedEntryModel.setOwner(cloneCartModel);
        clonedEntryModel.setOrder(cloneCartModel);
        clonedEntryModel.setDeliveryAddress(null);
        clonedEntryModel.setEntryNumber(entryNumber);
        return clonedEntryModel;
    }


    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
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
