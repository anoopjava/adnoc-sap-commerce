package com.adnoc.service.address;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.daos.impl.DefaultAddressDao;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AdnocAddressDaoImpl extends DefaultAddressDao implements AdnocAddressDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocAddressDaoImpl.class);

    @Override
    public AddressModel getAddress(final String pk)
    {
        LOG.debug("appEvent=AddressModel, Started getAddress method with pk: {}", pk);
        final Map<String, Object> params = new HashMap<>();
        params.put("pk", pk);
        final String searchQuery =
                "SELECT {" + AddressModel.PK + "} " +
                        "FROM {" + AddressModel._TYPECODE + " AS a} " +
                        "WHERE {a." + AddressModel.PK + "} = ?pk";
        final FlexibleSearchQuery query = new FlexibleSearchQuery(searchQuery);
        query.addQueryParameters(params);
        final SearchResult<AddressModel> searchResult = getFlexibleSearchService().search(query);
        return CollectionUtils.isNotEmpty(searchResult.getResult()) ? searchResult.getResult().get(0) : null;
    }
}
