package com.adnoc.service.company.service;

import com.adnoc.service.company.dao.AdnocB2BDocumentDao;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.company.impl.DefaultB2BDocumentService;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AdnocB2BDocumentServiceImpl extends DefaultB2BDocumentService implements AdnocB2BDocumentService
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BDocumentServiceImpl.class);

    private AdnocB2BDocumentDao adnocB2BDocumentDao;

    @Override
    public List<B2BDocumentModel> getB2BDocuments(final B2BUnitModel b2bUnit, final List<B2BDocumentTypeModel> documentTypes, final List<DocumentStatus> documentStatuses)
    {
        LOG.debug("appEvent=B2BDocuments, fetching list of B2BDocuments for B2BUnit:{}", b2bUnit.getUid());
        return getAdnocB2BDocumentDao().findB2BDocuments(b2bUnit, documentTypes, documentStatuses);
    }

    protected AdnocB2BDocumentDao getAdnocB2BDocumentDao()
    {
        return adnocB2BDocumentDao;
    }

    public void setAdnocB2BDocumentDao(final AdnocB2BDocumentDao adnocB2BDocumentDao)
    {
        this.adnocB2BDocumentDao = adnocB2BDocumentDao;
    }
}
