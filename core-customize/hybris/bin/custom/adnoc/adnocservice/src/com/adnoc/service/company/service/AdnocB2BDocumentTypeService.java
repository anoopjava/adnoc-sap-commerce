package com.adnoc.service.company.service;

import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;

/**
 * Adnoc B2B Document Type Service interface.
 * This service provides methods to retrieve B2B document types.
 */
public interface AdnocB2BDocumentTypeService
{
    /**
     * Retrieves a B2B document type by its code.
     *
     * @param code the code of the B2B document type
     * @return the B2BDocumentTypeModel corresponding to the provided code
     */
    B2BDocumentTypeModel getB2BDocumentType(String code);
}

