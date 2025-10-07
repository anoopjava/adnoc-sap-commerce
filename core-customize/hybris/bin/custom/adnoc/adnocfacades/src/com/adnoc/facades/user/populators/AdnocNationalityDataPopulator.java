package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.NationalityData;
import com.adnoc.service.enums.Nationality;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocNationalityDataPopulator implements Populator<Nationality, NationalityData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocNationalityDataPopulator.class);
    private static final String NATIONALITY = "EMIRATI";

    private TypeService typeService;
    private EnumerationService enumerationService;

    @Override
    public void populate(final Nationality source, final NationalityData target)
    {
        Nationality nationality = source;
        if (Objects.isNull(nationality))
        {
            LOG.info("appEvent=AdnocNationality,nationality is null!");
            nationality = getEnumerationService().getEnumerationValue(Nationality.class, NATIONALITY);
        }
        target.setCode(nationality.getCode());
        target.setName(getTypeService().getEnumerationValue(nationality).getName());
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
