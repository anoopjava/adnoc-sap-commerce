package com.adnoc.service.component;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;

/**
 * The interface Adnoc link component dao.
 */
public interface AdnocLinkComponentDao
{
    /**
     * Find cms link component by uid and catalog version cms link component model.
     *
     * @param uid            the uid
     * @param catalogVersion the catalog version
     * @return the cms link component model
     */
    CMSLinkComponentModel findCMSLinkComponentByUidAndCatalogVersion(String uid, CatalogVersionModel catalogVersion);
}
