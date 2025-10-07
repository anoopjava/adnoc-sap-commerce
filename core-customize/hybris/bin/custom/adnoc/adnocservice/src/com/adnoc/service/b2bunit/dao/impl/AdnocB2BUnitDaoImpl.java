package com.adnoc.service.b2bunit.dao.impl;

import com.adnoc.service.b2bunit.dao.AdnocB2BUnitDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BUnitDao;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class AdnocB2BUnitDaoImpl extends DefaultB2BUnitDao implements AdnocB2BUnitDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BUnitDaoImpl.class);
    private static final String FETCH_B2BUNITS_HAVING_CHILD =
            "SELECT {" + B2BUnitModel.PK + "} FROM {" + B2BUnitModel._TYPECODE + "} WHERE {" + B2BUnitModel.CHILDB2BUNITS + "} IS NOT NULL";

    @Override
    public Collection<B2BUnitModel> fetchB2BUnitsForChildMapping()
    {
        LOG.info("appEvent=AdnocB2BUnitChildMappingFetch, Fetching B2B units with child mapping");
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FETCH_B2BUNITS_HAVING_CHILD);
        final SearchResult<B2BUnitModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
        return searchResult.getResult();
    }
}
