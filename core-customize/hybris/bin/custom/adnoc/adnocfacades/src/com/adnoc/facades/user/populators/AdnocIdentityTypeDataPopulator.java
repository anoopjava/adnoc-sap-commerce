package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.IdentityTypeData;
import com.adnoc.service.enums.IdentityType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocIdentityTypeDataPopulator implements Populator<IdentityType, IdentityTypeData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocIdentityTypeDataPopulator.class);
    private static final String IDENTITY_TYPE = "FS0001";

    private TypeService typeService;
    private EnumerationService enumerationService;

    @Override
    public void populate(final IdentityType source, final IdentityTypeData target) throws ConversionException
    {

        IdentityType identityType = source;
        if (Objects.isNull(identityType))
        {
            LOG.info("appEvent=AdnocIdentityType,IdentityType is null!");
            identityType = getEnumerationService().getEnumerationValue(IdentityType.class, IDENTITY_TYPE);
        }
        target.setCode(identityType.getCode());
        target.setName(getTypeService().getEnumerationValue(identityType).getName());
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
