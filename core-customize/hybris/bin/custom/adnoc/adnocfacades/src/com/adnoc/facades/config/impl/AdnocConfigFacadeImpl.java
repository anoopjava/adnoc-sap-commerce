package com.adnoc.facades.config.impl;

import com.adnoc.facades.config.AdnocConfigFacade;
import com.adnoc.facades.config.data.AdnocConfigData;
import com.adnoc.facades.ticket.data.AdnocCsTicketCategoryMapData;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.model.AdnocConfigModel;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AdnocConfigFacadeImpl implements AdnocConfigFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocConfigFacadeImpl.class);

    private AdnocConfigService adnocConfigService;
    private Converter<AdnocConfigModel, AdnocConfigData> adnocConfigDataConverter;
    private Converter<AdnocCsTicketCategoryMapModel, AdnocCsTicketCategoryMapData> adnocCsTicketCategoryMapDataConverter;

    @Override
    public List<AdnocConfigData> getAdnocConfigs(final String... configKeys)
    {
        LOG.info("appEvent=AdnocConfig, getAdnocConfigs method called");
        final List<AdnocConfigModel> adnocConfigModels = getAdnocConfigService().getAdnocConfigs(configKeys);
        return Converters.convertAll(adnocConfigModels, getAdnocConfigDataConverter());
    }

    @Override
    public AdnocConfigData getAdnocConfig(final String configKey)
    {
        LOG.info("appEvent=AdnocConfig, getAdnocConfig method called with key:{}", configKey);

        final AdnocConfigModel adnocConfigModel = getAdnocConfigService().getAdnocConfig(configKey);
        return getAdnocConfigDataConverter().convert(adnocConfigModel);
    }

    @Override
    public List<AdnocCsTicketCategoryMapData> getAdnocCsTicketCategoryMap()
    {
        LOG.info("appEvent=AdnocConfig, getAdnocCsTicketCategoryMap method called..");
        final List<AdnocCsTicketCategoryMapModel> adnocCsTicketCategoryMapModels = getAdnocConfigService().getAdnocCsTicketCategoryMap();
        return getAdnocCsTicketCategoryMapDataConverter().convertAll(adnocCsTicketCategoryMapModels);
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    protected Converter<AdnocConfigModel, AdnocConfigData> getAdnocConfigDataConverter()
    {
        return adnocConfigDataConverter;
    }

    public void setAdnocConfigDataConverter(final Converter<AdnocConfigModel, AdnocConfigData> adnocConfigDataConverter)
    {
        this.adnocConfigDataConverter = adnocConfigDataConverter;
    }

    protected Converter<AdnocCsTicketCategoryMapModel, AdnocCsTicketCategoryMapData> getAdnocCsTicketCategoryMapDataConverter()
    {
        return adnocCsTicketCategoryMapDataConverter;
    }

    public void setAdnocCsTicketCategoryMapDataConverter(final Converter<AdnocCsTicketCategoryMapModel, AdnocCsTicketCategoryMapData> adnocCsTicketCategoryMapDataConverter)
    {
        this.adnocCsTicketCategoryMapDataConverter = adnocCsTicketCategoryMapDataConverter;
    }
}
