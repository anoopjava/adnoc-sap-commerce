package com.adnoc.service.storeservice;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.storelocator.impl.DefaultPointOfServiceDao;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.apache.commons.collections4.CollectionUtils;

public class AdnocStoreDaoImpl extends DefaultPointOfServiceDao implements AdnocStoreDao
{
    @Override
    public PointOfServiceModel findPointOfServiceByPk(final String pk)
    {
        final String query = "SELECT {" + PointOfServiceModel.PK + "} FROM {" + PointOfServiceModel._TYPECODE + "} WHERE {"
                + PointOfServiceModel.PK + "} = ?" + PointOfServiceModel.PK;
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        searchQuery.addQueryParameter(PointOfServiceModel.PK, pk);
        final SearchResult<PointOfServiceModel> result = getFlexibleSearchService().search(searchQuery);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult().get(0) : null;
    }

}
