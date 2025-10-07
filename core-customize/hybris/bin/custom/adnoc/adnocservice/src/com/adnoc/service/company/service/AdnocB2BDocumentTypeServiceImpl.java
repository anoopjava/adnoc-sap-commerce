package com.adnoc.service.company.service;

import com.adnoc.service.company.dao.AdnocB2BDocumentTypeDao;
import de.hybris.platform.b2bacceleratorservices.company.impl.DefaultB2BDocumentTypeService;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocB2BDocumentTypeServiceImpl extends DefaultB2BDocumentTypeService implements AdnocB2BDocumentTypeService
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BDocumentTypeServiceImpl.class);

    private AdnocB2BDocumentTypeDao adnocB2BDocumentTypeDao;

    @Override
    public B2BDocumentTypeModel getB2BDocumentType(final String code)
    {
        LOG.info("appEvent=AdnocB2BDocumentType,Fetching B2BDocumentType with Code:{}", code);
        return getAdnocB2BDocumentTypeDao().findB2BDocumentType(code);
    }

    public AdnocB2BDocumentTypeDao getAdnocB2BDocumentTypeDao()
    {
        return adnocB2BDocumentTypeDao;
    }

    public void setAdnocB2BDocumentTypeDao(final AdnocB2BDocumentTypeDao adnocB2BDocumentTypeDao)
    {
        this.adnocB2BDocumentTypeDao = adnocB2BDocumentTypeDao;
    }
}
