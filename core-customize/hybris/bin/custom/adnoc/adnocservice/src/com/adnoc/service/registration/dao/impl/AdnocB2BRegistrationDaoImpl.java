package com.adnoc.service.registration.dao.impl;

import com.adnoc.service.enums.B2BRegistrationStatus;
import com.adnoc.service.model.AdnocRegistrationModel;
import com.adnoc.service.registration.dao.AdnocB2BRegistrationDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BRegistrationDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdnocB2BRegistrationDaoImpl extends DefaultB2BRegistrationDao implements AdnocB2BRegistrationDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BRegistrationDaoImpl.class);

    private FlexibleSearchService flexibleSearchService;

    @Override
    public AdnocRegistrationModel findAdnocB2BRegistration(final Map<String, String> duplicateCheckParams, final String adnocRegistrationType)
    {
        LOG.info("appEvent=AdnocB2BRegistration,start findAdnocB2BRegistration with parameters :{}", duplicateCheckParams);
        final List<B2BRegistrationStatus> b2BRegistrationStatus = Arrays.asList(B2BRegistrationStatus.APPROVED, B2BRegistrationStatus.PENDING, B2BRegistrationStatus.COMPLETED);
        String query = "SELECT {" + AdnocRegistrationModel.PK + "} FROM {" + adnocRegistrationType + "}";
        if (MapUtils.isNotEmpty(duplicateCheckParams))
        {
            final List<String> entries = duplicateCheckParams.entrySet().stream()
                    .map(duplicacyCheckParamEntry -> "{" + duplicacyCheckParamEntry.getKey() + "}=?" + duplicacyCheckParamEntry.getKey())
                    .collect(Collectors.toList());
            query = query + " WHERE (" + String.join(" OR ", entries) + ") AND {" + AdnocRegistrationModel.STATUS + "} IN ( ?" + AdnocRegistrationModel.STATUS + ")";
        }

        final Map<String, Object> parameterMap = new HashMap<>(duplicateCheckParams);
        parameterMap.put(AdnocRegistrationModel.STATUS, b2BRegistrationStatus);
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, parameterMap);

        LOG.debug("appEvent=AdnocB2BRegistration,executing flexibleSearchQuery with parameters :{}", parameterMap);
        final SearchResult<AdnocRegistrationModel> result = getFlexibleSearchService().search(searchQuery);
        if (CollectionUtils.isNotEmpty(result.getResult()))
        {
            LOG.info("appEvent=AdnocB2BRegistration, found {} results. Returning the first result.", result.getResult());
            return result.getResult().get(0);
        }
        LOG.info("appEvent=AdnocB2BRegistration,No Results found for the given parameters");
        return null;
    }

    protected FlexibleSearchService getFlexibleSearchService()
    {
        return flexibleSearchService;
    }

    @Override
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
    {
        super.setFlexibleSearchService(flexibleSearchService);
        this.flexibleSearchService = flexibleSearchService;
    }
}

