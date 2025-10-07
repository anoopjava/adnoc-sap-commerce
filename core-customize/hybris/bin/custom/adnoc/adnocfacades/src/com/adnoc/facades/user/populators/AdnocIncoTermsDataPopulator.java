package com.adnoc.facades.user.populators;


import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.service.enums.IncoTerms;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocIncoTermsDataPopulator implements Populator<IncoTerms, IncoTermsData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocIncoTermsDataPopulator.class);
    private static final String INCO_TERMS = "DAP";
    private TypeService typeService;
    private EnumerationService enumerationService;


    @Override
    public void populate(final IncoTerms source, final IncoTermsData target) throws ConversionException
    {
        IncoTerms incoTerms = source;
        if (Objects.isNull(incoTerms))
        {
            LOG.info("appEvent=AdnocIncoTerms,incoTerms is null!");
            incoTerms = getEnumerationService().getEnumerationValue(IncoTerms.class, INCO_TERMS);
        }
        target.setCode(incoTerms.getCode());
        target.setName(getTypeService().getEnumerationValue(incoTerms).getName());
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
