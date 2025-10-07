package com.adnoc.service.company.dao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BDocumentDaoImplTest
{
    @InjectMocks
    private AdnocB2BDocumentDaoImpl adnocB2BDocumentDao;
    @Mock
    private B2BDocumentModel b2BDocumentModel = new B2BDocumentModel();

    @Mock
    private B2BUnitModel b2bUnit;
    @Mock
    private FlexibleSearchService flexibleSearchService;
    @Mock
    private SearchResult searchResult = Mockito.mock(SearchResult.class);

    @Test
    public void testFindB2BDocuments()
    {
        Mockito.when(b2bUnit.getUid()).thenReturn("test300000");
        final List<B2BDocumentTypeModel> b2BDocumentTypeModels = new ArrayList<>();
        final B2BDocumentTypeModel documentType = Mockito.mock(B2BDocumentTypeModel.class);
        b2BDocumentTypeModels.add(documentType);

        final List<DocumentStatus> documentStatuses = new ArrayList<>();
        final DocumentStatus documentStatus = Mockito.mock(DocumentStatus.class);
        documentStatuses.add(documentStatus);

        final List<B2BDocumentModel> b2BDocumentModels = new ArrayList<>();
        b2BDocumentModels.add(b2BDocumentModel);
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);
        Mockito.when(searchResult.getResult()).thenReturn(b2BDocumentModels);

        final List<B2BDocumentModel> result = adnocB2BDocumentDao.findB2BDocuments(b2bUnit, b2BDocumentTypeModels, documentStatuses);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(flexibleSearchService).search(Mockito.any(FlexibleSearchQuery.class));
    }
}
