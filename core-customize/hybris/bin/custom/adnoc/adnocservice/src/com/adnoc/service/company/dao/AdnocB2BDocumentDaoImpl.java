package com.adnoc.service.company.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.dao.impl.DefaultB2BDocumentDao;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdnocB2BDocumentDaoImpl extends DefaultB2BDocumentDao implements AdnocB2BDocumentDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BDocumentDaoImpl.class);

    @Override
    public List<B2BDocumentModel> findB2BDocuments(final B2BUnitModel b2bUnit, final List<B2BDocumentTypeModel> b2BDocumentTypeModels, final List<DocumentStatus> documentStatuses)
    {
        LOG.info("appEvent=AdnocB2BDocument,finding B2BDocuments for unit: {}", b2bUnit.getUid());
        // Build the query with conditions
        final StringBuilder query = new StringBuilder("SELECT {" + B2BDocumentModel.PK + "} " +
                "FROM {" + B2BDocumentModel._TYPECODE + "} WHERE {" + B2BDocumentModel.UNIT + "}=?" + B2BDocumentModel.UNIT);

        if (CollectionUtils.isNotEmpty(b2BDocumentTypeModels))
        {
            query.append(" AND {" + B2BDocumentModel.DOCUMENTTYPE + "} IN (?" + B2BDocumentModel.DOCUMENTTYPE + ")");
        }

        if (CollectionUtils.isNotEmpty(documentStatuses))
        {
            query.append(" AND {" + B2BDocumentModel.STATUS + "} IN (?" + B2BDocumentModel.STATUS + ")");
        }

        final Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(B2BDocumentModel.UNIT, b2bUnit);
        parameterMap.put(B2BDocumentModel.DOCUMENTTYPE, b2BDocumentTypeModels);
        parameterMap.put(B2BDocumentModel.STATUS, documentStatuses);
        LOG.info("appEvent=AdnocB2BDocument, Executing query :{} with parameters :{}", query, parameterMap);

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query.toString(), parameterMap);
        final SearchResult<B2BDocumentModel> result = getFlexibleSearchService().search(searchQuery);
        return result.getResult();
    }
}
