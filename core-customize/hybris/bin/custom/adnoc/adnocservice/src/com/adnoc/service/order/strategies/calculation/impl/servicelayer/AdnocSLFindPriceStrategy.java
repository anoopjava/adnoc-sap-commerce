package com.adnoc.service.order.strategies.calculation.impl.servicelayer;

import com.adnoc.service.order.strategies.calculation.pdt.criteria.impl.AdnocPDTCriteriaFactory;
import com.adnoc.service.order.strategies.calculation.pdt.impl.AdnocFindPriceValueInfoStrategy;
import com.adnoc.service.order.strategies.calculation.pdt.impl.converter.impl.AdnocPriceValueInfoCriteria;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.impl.servicelayer.DefaultSLFindPriceStrategy;
import de.hybris.platform.product.BaseCriteria;
import de.hybris.platform.util.PriceValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AdnocSLFindPriceStrategy extends DefaultSLFindPriceStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocSLFindPriceStrategy.class);

    private AdnocFindPriceValueInfoStrategy adnocFindPriceValueInfoStrategy;
    private AdnocPDTCriteriaFactory adnocPDTCriteriaFactory;

    @Override
    public PriceValue findBasePrice(final AbstractOrderEntryModel entry) throws CalculationException
    {
        LOG.info("appEvent=AdnocSapPricing, finding pricing for entryNumber={}.", entry.getEntryNumber());
        final AdnocPriceValueInfoCriteria adnocPriceValueInfoCriteria = getAdnocPDTCriteriaFactory().priceValueCriteriaFromOrderEntry(entry);
        final List<PriceValue> pdtValues = getAdnocFindPriceValueInfoStrategy().getPDTValues(adnocPriceValueInfoCriteria);
        return pdtValues.isEmpty() ? null : pdtValues.get(0);
    }

    @Override
    public List<PriceInformation> getPriceInformation(final BaseCriteria priceCriteria) throws CalculationException
    {
        LOG.info("appEvent=AdnocSapPricing, finding pricing for product={}.", priceCriteria.getProduct().getCode());
        final AdnocPriceValueInfoCriteria adnocPriceValueInfoCriteria = getAdnocPDTCriteriaFactory().priceInfoCriteriaFromBaseCriteria(priceCriteria);
        return getAdnocFindPriceValueInfoStrategy().getPDTInformation(adnocPriceValueInfoCriteria);
    }

    protected AdnocFindPriceValueInfoStrategy getAdnocFindPriceValueInfoStrategy()
    {
        return adnocFindPriceValueInfoStrategy;
    }

    public void setAdnocFindPriceValueInfoStrategy(final AdnocFindPriceValueInfoStrategy adnocFindPriceValueInfoStrategy)
    {
        this.adnocFindPriceValueInfoStrategy = adnocFindPriceValueInfoStrategy;
    }

    protected AdnocPDTCriteriaFactory getAdnocPDTCriteriaFactory()
    {
        return adnocPDTCriteriaFactory;
    }

    public void setAdnocPDTCriteriaFactory(AdnocPDTCriteriaFactory adnocPDTCriteriaFactory)
    {
        this.adnocPDTCriteriaFactory = adnocPDTCriteriaFactory;
    }
}
