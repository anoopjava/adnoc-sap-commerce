package com.adnoc.service.company.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.dao.B2BDocumentDao;
import de.hybris.platform.b2bacceleratorservices.dao.impl.DefaultB2BDocumentDao;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocAccountSummaryDaoImpl extends DefaultB2BDocumentDao implements B2BDocumentDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocAccountSummaryDaoImpl.class);

    private static final String FIND_DOCUMENT_BY_ID = "SELECT {" + B2BDocumentModel._TYPECODE + ":pk}  FROM { "
            + B2BDocumentModel._TYPECODE + " as " + B2BDocumentModel._TYPECODE + " join " + B2BUnitModel._TYPECODE + " as "
            + B2BUnitModel._TYPECODE + " on { " + B2BDocumentModel._TYPECODE + ":unit } = {" + B2BUnitModel._TYPECODE + ":pk}} "
            + " where {" + B2BUnitModel._TYPECODE + ":uid} = ?unit and  {" + B2BDocumentModel._TYPECODE + ":documentNumber}=?documentNumber ";

    @Override
    public SearchResult<B2BDocumentModel> findDocument(final String b2bUnitCode, final String documentNumber)
    {
        LOG.info("appEvent=AdnocB2BDocument,findDocument method start");
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_DOCUMENT_BY_ID);
        query.addQueryParameter(B2BDocumentModel.UNIT, b2bUnitCode);
        query.addQueryParameter(B2BDocumentModel.DOCUMENTNUMBER, documentNumber);
        return getFlexibleSearchService().search(query);
    }
}
