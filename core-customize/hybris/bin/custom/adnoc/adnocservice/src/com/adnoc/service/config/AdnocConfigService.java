package com.adnoc.service.config;

import com.adnoc.service.model.AdnocConfigModel;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;

import java.util.List;

/**
 * Service interface for managing ADNOC configurations, including integration codes,
 * ticket categories, and sales organization data.
 */
public interface AdnocConfigService
{
    /**
     * Gets adnoc configs.
     *
     * @param configKeys the config keys
     * @return the adnoc config
     */
    List<AdnocConfigModel> getAdnocConfigs(String... configKeys);

    /**
     * Gets adnoc config.
     *
     * @param configKey the config key
     * @return the adnoc config
     */
    AdnocConfigModel getAdnocConfig(String configKey);

    /**
     * Gets the string configuration value for the given key.
     *
     * @param configKey    the configuration key to look up
     * @param defaultValue the default value to return if the key is not found
     * @return the string configuration value or the default value if not found
     */
    String getAdnocConfigValue(String configKey, String defaultValue);

    /**
     * Gets the integer configuration value for the given key.
     *
     * @param configKey    the configuration key to look up
     * @param defaultValue the default value to return if the key is not found
     * @return the integer configuration value or the default value if not found
     */
    int getAdnocConfigValue(String configKey, int defaultValue);

    /**
     * Gets adnoc sap integration code map.
     *
     * @param integrationType the integration type
     * @param objType         the object type
     * @param enumCode        the enum code
     * @return the adnoc sap integration code
     */
    String getAdnocSapIntegrationCodeMap(IntegrationType integrationType, Class objType, String enumCode);

    /**
     * Gets adnoc cs ticket category map.
     *
     * @return the adnoc cs ticket category map
     */
    List<AdnocCsTicketCategoryMapModel> getAdnocCsTicketCategoryMap();

    /**
     * Gets adnoc cs ticket category map.
     *
     * @param csTicketCategoryMapId the cs ticket category map id
     * @return the adnoc cs ticket category map
     */
    AdnocCsTicketCategoryMapModel getAdnocCsTicketCategoryMap(String csTicketCategoryMapId);

    /**
     * Gets sales organization by division.
     *
     * @param division the division
     * @return the sales organization model
     */
    SAPSalesOrganizationModel getSalesOrgbyDivision(String division);
    /**
     * Gets target enum values based on source enum type and value.
     *
     * @param sourceEnumType  the source enum type
     * @param sourceEnumValue the source enum value
     * @param targetEnumType  the target enum type
     * @return the list of target enum values
     */
    List<Object> getTargetEnumValues(final Class<?> sourceEnumType, final String sourceEnumValue, final Class<?> targetEnumType);
}
