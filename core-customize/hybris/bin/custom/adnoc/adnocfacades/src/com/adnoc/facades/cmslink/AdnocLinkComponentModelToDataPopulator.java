package com.adnoc.facades.cmslink;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cmsfacades.rendering.populators.LinkComponentModelToDataPopulator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.configuration.Configuration;

import java.util.Map;
import java.util.Objects;

import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.URL;

public class AdnocLinkComponentModelToDataPopulator extends LinkComponentModelToDataPopulator
{
    public static final String API_SWAGGER_URL_PATH = "adnoc.api.swagger.url";
    public static final String CUSTOMERGUIDE_UID = "adnoc.customer.guide.uid";

    private ConfigurationService configurationService;

    @Override
    public void populate(final CMSItemModel cmsItemModel, final Map<String, Object> stringObjectMap) throws ConversionException
    {
        final Configuration configuration = getConfigurationService().getConfiguration();
        if (cmsItemModel instanceof final CMSLinkComponentModel cmsLinkComponentModel && Objects.equals(cmsLinkComponentModel.getUid(), configuration.getString(CUSTOMERGUIDE_UID)))
        {
            final String originalUrl = cmsLinkComponentModel.getUrl();
            if (Objects.nonNull(originalUrl))
            {
                stringObjectMap.put(URL, configuration.getString(API_SWAGGER_URL_PATH) + originalUrl);
            }
            return;
        }
        super.populate(cmsItemModel, stringObjectMap);
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }
}
