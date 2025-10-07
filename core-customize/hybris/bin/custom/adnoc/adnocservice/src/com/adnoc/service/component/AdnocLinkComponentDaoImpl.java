package com.adnoc.service.component;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.HashMap;
import java.util.Map;

public class AdnocLinkComponentDaoImpl implements AdnocLinkComponentDao
{
    private FlexibleSearchService flexibleSearchService;


    @Override
    public CMSLinkComponentModel findCMSLinkComponentByUidAndCatalogVersion(final String uid, final CatalogVersionModel catalogVersion)
    {
        final String query = "SELECT {pk} FROM {CMSLinkComponent} WHERE {uid}=?uid AND {catalogVersion}=?catalogVersion";
        final Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("catalogVersion", catalogVersion);
        final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(query, params);
        return getFlexibleSearchService().searchUnique(fsQuery);
    }

    protected FlexibleSearchService getFlexibleSearchService()
    {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
    {
        this.flexibleSearchService = flexibleSearchService;
    }
}
