package com.adnoc.service.company.dao;

import de.hybris.platform.b2bacceleratorservices.dao.impl.DefaultB2BDocumentTypeDao;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AdnocB2BDocumentTypeDaoImpl extends DefaultB2BDocumentTypeDao implements AdnocB2BDocumentTypeDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BDocumentTypeDaoImpl.class);

    @Override
    public B2BDocumentTypeModel findB2BDocumentType(final String code)
    {
        LOG.info("appEvent=AdnocB2BDocument, finding B2B document type with code: {} ", code);
        final String query = "SELECT {" + B2BDocumentTypeModel.PK + "} FROM {" + B2BDocumentTypeModel._TYPECODE
                + "} WHERE {" + B2BDocumentTypeModel.CODE + "} = ?" + B2BDocumentTypeModel.CODE;

        final Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(B2BDocumentTypeModel.CODE, code);
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, parameterMap);
        LOG.info("appEvent=AdnocB2BDocument, Executing query :{} with parameters :{}", query, parameterMap);

        final SearchResult<B2BDocumentTypeModel> result = getFlexibleSearchService().search(searchQuery);
        if (CollectionUtils.isNotEmpty(result.getResult()))
        {
            return result.getResult().get(0);
        }
        return null;
    }
}
