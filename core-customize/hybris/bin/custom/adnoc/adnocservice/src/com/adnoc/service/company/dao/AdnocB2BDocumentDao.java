package com.adnoc.service.company.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.dao.B2BDocumentDao;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;

import java.util.List;

public interface AdnocB2BDocumentDao extends B2BDocumentDao
{
    public List<B2BDocumentModel> findB2BDocuments(B2BUnitModel b2bUnit, List<B2BDocumentTypeModel> documentTypes, List<DocumentStatus> documentStatuses);
}
