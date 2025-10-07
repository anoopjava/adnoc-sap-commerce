package com.adnoc.service.company.service;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.company.B2BDocumentService;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;

import java.util.List;

/**
 * AdnocB2BDocumentService interface.
 * This service extends the B2BDocumentService to provide additional functionality specific to ADNOC B2B documents.
 */
public interface AdnocB2BDocumentService extends B2BDocumentService
{
    /**
     * Retrieves a list of B2B documents for a given B2B unit, document types, and document statuses.
     *
     * @param b2bUnit          the B2B unit for which documents are to be retrieved
     * @param documentTypes    the list of document types to filter the documents
     * @param documentStatuses the list of document statuses to filter the documents
     * @return a list of B2BDocumentModel objects matching the criteria
     */
    List<B2BDocumentModel> getB2BDocuments(B2BUnitModel b2bUnit, List<B2BDocumentTypeModel> documentTypes,
                                           List<DocumentStatus> documentStatuses);
}
