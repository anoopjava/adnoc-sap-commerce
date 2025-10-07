package com.adnoc.service.company.dao;

import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;

public interface AdnocB2BDocumentTypeDao
{
    B2BDocumentTypeModel findB2BDocumentType(String code);
}
