package com.adnoc.facades.config;

import com.adnoc.facades.config.data.AdnocConfigData;
import com.adnoc.facades.ticket.data.AdnocCsTicketCategoryMapData;

import java.util.List;

/**
 * Interface providing configuration management functionality for ADNOC services.
 * Handles retrieval of configuration settings and ticket category mappings for the ADNOC system.
 * This facade encapsulates the configuration access logic and provides a clean API for config operations.
 */
public interface AdnocConfigFacade
{
    /**
     * Gets adnoc configs.
     *
     * @param configKeys the config keys
     * @return the adnoc config
     */
    List<AdnocConfigData> getAdnocConfigs(String... configKeys);

    /**
     * Gets adnoc config.
     *
     * @param configKey the config key
     * @return the adnoc config
     */
    AdnocConfigData getAdnocConfig(String configKey);

    /**
     * Gets adnoc cs ticket category map.
     *
     * @return the adnoc cs ticket category map
     */
    List<AdnocCsTicketCategoryMapData> getAdnocCsTicketCategoryMap();
}
