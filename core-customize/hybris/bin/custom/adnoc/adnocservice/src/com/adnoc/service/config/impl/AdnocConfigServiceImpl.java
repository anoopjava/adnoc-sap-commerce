package com.adnoc.service.config.impl;

import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.config.dao.AdnocConfigDao;
import com.adnoc.service.model.AdnocBackofficeApprovalRegistrationEnumMappingModel;
import com.adnoc.service.model.AdnocConfigModel;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import com.adnoc.service.model.AdnocSapIntegrationCodeMapModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdnocConfigServiceImpl implements AdnocConfigService
{
    private static final Logger LOG = LogManager.getLogger(AdnocConfigServiceImpl.class);

    private AdnocConfigDao adnocConfigDao;
    private EnumerationService enumerationService;

    @Override
    public List<AdnocConfigModel> getAdnocConfigs(final String... configKeys)
    {
        LOG.info("appEvent=AdnocConfig, getAdnocConfigs method called with the following configKeys:{}", configKeys);
        return getAdnocConfigDao().findAdnocConfig(configKeys);
    }

    @Override
    public AdnocConfigModel getAdnocConfig(final String configKey)
    {
        LOG.info("appEvent=AdnocConfig,getAdnocConfig method called with the following configKey:{}", configKey);
        final List<AdnocConfigModel> adnocConfigModels = getAdnocConfigDao().findAdnocConfig(configKey);
        return CollectionUtils.isNotEmpty(adnocConfigModels) ? adnocConfigModels.get(0) : null;
    }

    @Override
    public String getAdnocConfigValue(final String configKey, final String defaultValue)
    {
        LOG.info("appEvent=AdnocConfig, configuration value for key:{}", configKey);
        final AdnocConfigModel configModel = getAdnocConfig(configKey);
        return Objects.nonNull(configModel) && Objects.nonNull(configModel.getConfigValue())
                ? configModel.getConfigValue()
                : defaultValue;
    }

    @Override
    public int getAdnocConfigValue(final String configKey, final int defaultValue)
    {
        LOG.info("appEvent=AdnocConfig, invoking the getAdnocConfigValue method");
        final AdnocConfigModel configModel = getAdnocConfig(configKey);
        return Objects.nonNull(configModel) && Objects.nonNull(configModel.getConfigValue())
                ? NumberUtils.toInt(configModel.getConfigValue(), defaultValue)
                : defaultValue;
    }

    @Override
    public SAPSalesOrganizationModel getSalesOrgbyDivision(final String division)
    {
        LOG.info("appEvent=AdnocConfig, fetching sales Organization for division: {}", division);
        return getAdnocConfigDao().getSalesOrgbyDivision(division);
    }

    @Override
    public List<Object> getTargetEnumValues(final Class<?> sourceEnumType, final String sourceEnumValue, final Class<?> targetEnumType)
    {
        LOG.info("appEvent=AdnocConfig, fetching target enum values for sourceEnumType: {}, sourceEnumValue: {}, targetEnumType: {}", sourceEnumType, sourceEnumValue, targetEnumType);
        final List<AdnocBackofficeApprovalRegistrationEnumMappingModel> mappingModel = getAdnocConfigDao().getTargetEnumValues(sourceEnumType, sourceEnumValue, targetEnumType);
        return mappingModel.stream().map(adnocBackofficeApprovalRegistrationEnumMappingModel -> getEnumerationService().getEnumerationValue(targetEnumType.getSimpleName(), adnocBackofficeApprovalRegistrationEnumMappingModel.getTargetEnumValue()))
                .collect(Collectors.toList());
    }

    @Override
    public String getAdnocSapIntegrationCodeMap(final IntegrationType integrationType, final Class objType, final String enumCode)
    {
        LOG.debug("appEvent=AdnocConfig, fetching SAP Integration Map for type:{},objType: {},enumCode :{}", integrationType, objType, enumCode);
        final AdnocSapIntegrationCodeMapModel adnocSapIntegrationCodeMapModel = getAdnocConfigDao().findAdnocSapIntegrationCodeMap(integrationType, objType, enumCode);
        if (Objects.nonNull(adnocSapIntegrationCodeMapModel))
        {
            return Objects.equals(IntegrationType.OUTBOUND, integrationType) ? adnocSapIntegrationCodeMapModel.getSapCode() : adnocSapIntegrationCodeMapModel.getEnumCode();
        }
        LOG.warn("appEvent=AdnocConfig: No Integration Code found for type:{},obj Type: {},enum Code :{}", integrationType, objType, enumCode);
        return null;
    }

    @Override
    public List<AdnocCsTicketCategoryMapModel> getAdnocCsTicketCategoryMap()
    {
        LOG.info("appEvent=AdnocConfig, entering method getAdnocCsTicketCategoryMap().");
        return getAdnocConfigDao().findAdnocCsTicketCategoryMap();
    }

    @Override
    public AdnocCsTicketCategoryMapModel getAdnocCsTicketCategoryMap(final String csTicketCategoryMapId)
    {
        LOG.info("appEvent=AdnocConfig,fetching csTicket Category Map for Id: {} ", csTicketCategoryMapId);
        return getAdnocConfigDao().findAdnocCsTicketCategoryMap(csTicketCategoryMapId);
    }

    protected AdnocConfigDao getAdnocConfigDao()
    {
        return adnocConfigDao;
    }

    public void setAdnocConfigDao(final AdnocConfigDao adnocConfigDao)
    {
        this.adnocConfigDao = adnocConfigDao;
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
