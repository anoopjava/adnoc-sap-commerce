package com.adnoc.facades.config.populators;

import com.adnoc.facades.config.data.AdnocConfigData;
import com.adnoc.service.model.AdnocConfigModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocConfigDataPopulator implements Populator<AdnocConfigModel, AdnocConfigData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocConfigDataPopulator.class);

    @Override
    public void populate(final AdnocConfigModel adnocConfigModel, final AdnocConfigData adnocConfigData)
            throws ConversionException
    {
        LOG.info("appEvent=AdnocConfigData, populating adnocConfigModel to adnocConfigData ");
        adnocConfigData.setConfigKey(adnocConfigModel.getConfigKey());
        adnocConfigData.setConfigValue(adnocConfigModel.getConfigValue());
    }
}
