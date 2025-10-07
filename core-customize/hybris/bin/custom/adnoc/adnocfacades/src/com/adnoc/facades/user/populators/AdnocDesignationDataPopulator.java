package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.AdnocDesignationData;
import com.adnoc.service.enums.Designation;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocDesignationDataPopulator implements Populator<Designation, AdnocDesignationData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocDesignationDataPopulator.class);
    private static final String DESIGNATION_TYPE = "Manager";

    private TypeService typeService;
    private EnumerationService enumerationService;

    @Override
    public void populate(final Designation source, final AdnocDesignationData target) throws ConversionException
    {
        Designation designation = source;
        if (Objects.isNull(designation))
        {
            LOG.info("appEvent=AdnocDesignation,designation is null!");
            designation = getEnumerationService().getEnumerationValue(Designation.class, DESIGNATION_TYPE);
        }
        target.setCode(designation.getCode());
        target.setName(getTypeService().getEnumerationValue(designation).getName());
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
