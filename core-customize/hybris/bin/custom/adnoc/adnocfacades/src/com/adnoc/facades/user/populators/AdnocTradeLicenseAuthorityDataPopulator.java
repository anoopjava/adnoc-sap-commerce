package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.AdnocTradeLicenseAuthorityData;
import com.adnoc.service.enums.TradeLicenseAuthority;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocTradeLicenseAuthorityDataPopulator implements Populator<TradeLicenseAuthority, AdnocTradeLicenseAuthorityData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocTradeLicenseAuthorityDataPopulator.class);
    private static final String TRADE_LICENSE_AUTHORITY_TYPE = "";

    private TypeService typeService;
    private EnumerationService enumerationService;

    @Override
    public void populate(final TradeLicenseAuthority source, final AdnocTradeLicenseAuthorityData target) throws ConversionException
    {
        TradeLicenseAuthority tradeLicenseAuthority = source;
        if (Objects.isNull(tradeLicenseAuthority))
        {
            LOG.info("appEvent=AdnocTradeLicenseAuthority,tradeLicenseAuthority is null!");
            tradeLicenseAuthority = getEnumerationService().getEnumerationValue(TradeLicenseAuthority.class, TRADE_LICENSE_AUTHORITY_TYPE);
        }
        target.setCode(tradeLicenseAuthority.getCode());
        target.setName(getTypeService().getEnumerationValue(tradeLicenseAuthority).getName());
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
