package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.PreferredCommunicationChannelData;
import com.adnoc.service.enums.PreferredCommunicationChannel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocPreferredCommunicationChannelDataPopulator implements Populator<PreferredCommunicationChannel, PreferredCommunicationChannelData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocPreferredCommunicationChannelDataPopulator.class);
    private static final String PREFERRED_COMMUNICATION_CHANNEL = "INT";

    private TypeService typeService;
    private EnumerationService enumerationService;

    @Override
    public void populate(final PreferredCommunicationChannel source, final PreferredCommunicationChannelData target)
    {
        PreferredCommunicationChannel preferredCommunicationChannel = source;
        if (Objects.isNull(preferredCommunicationChannel))
        {
            LOG.info("appEvent=AdnocPreferredCommunicationChannel,preferredCommunicationChannel is null!");
            preferredCommunicationChannel = getEnumerationService().getEnumerationValue(PreferredCommunicationChannel.class, PREFERRED_COMMUNICATION_CHANNEL);
        }
        target.setCode(preferredCommunicationChannel.getCode());
        target.setName(getTypeService().getEnumerationValue(preferredCommunicationChannel).getName());
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
