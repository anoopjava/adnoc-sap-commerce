package com.adnoc.facades.gigya;

import com.adnoc.facades.gigya.data.GigyaConfigData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;
import java.util.Optional;

public class AdnocGigyaConfigPopulator implements Populator<GigyaConfigModel, GigyaConfigData>
{
    private ConfigurationService configurationService;

    @Override
    public void populate(GigyaConfigModel source, GigyaConfigData target) throws ConversionException
    {
        target.setCdcApiKey(source.getGigyaApiKey());
        target.setCdcUserKey(source.getGigyaUserKey());
        target.setCdcUserSecret(source.getGigyaUserSecret());
        Optional.ofNullable(source.getMode()).ifPresent(c -> target.setLoginMode(c.getCode()));
        Optional.ofNullable(source.getGigyaSessionConfig()).ifPresent(session -> target.setSessionExpiration(String.valueOf(session.getSessionDuration())));
        target.setDataCenter(source.getGigyaDataCenter());
        target.setInclude(getConfigurationService().getConfiguration().getString("adnoc.cdc.include.options"));
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

}
