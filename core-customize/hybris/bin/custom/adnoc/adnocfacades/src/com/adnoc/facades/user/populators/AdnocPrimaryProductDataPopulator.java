package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.PrimaryProductData;
import com.adnoc.service.enums.PrimaryProduct;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocPrimaryProductDataPopulator implements Populator<PrimaryProduct, PrimaryProductData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocPrimaryProductDataPopulator.class);
    private static final String PRIMARY_PRODUCT = "FUEL";

    private TypeService typeService;
    private EnumerationService enumerationService;

    @Override
    public void populate(final PrimaryProduct source, final PrimaryProductData target)
    {
        PrimaryProduct primaryProduct = source;
        if (Objects.isNull(primaryProduct))
        {
            LOG.info("appEvent=AdnocPrimaryProduct,primaryProduct is null!");
            primaryProduct = getEnumerationService().getEnumerationValue(PrimaryProduct.class, PRIMARY_PRODUCT);
        }
        target.setCode(primaryProduct.getCode());
        target.setName(getTypeService().getEnumerationValue(primaryProduct).getName());
    }

    protected TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(final TypeService typeService)
    {
        this.typeService = typeService;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

}
