package com.adnoc.service.company.service;

import com.adnoc.service.company.dao.AdnocB2BDocumentDao;
import com.adnoc.service.company.service.AdnocB2BDocumentServiceImpl;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BDocumentServiceImplTest
{
    @InjectMocks
    private AdnocB2BDocumentServiceImpl adnocB2BDocumentService = new AdnocB2BDocumentServiceImpl();

    @Mock
    private B2BUnitModel b2bUnit;
    @Mock
    private AdnocB2BDocumentDao adnocB2BDocumentDao;
    @Mock
    private DocumentStatus documentStatus;

    @Test
    public void testGetB2BDocuments()
    {
        final List<B2BDocumentTypeModel> b2BDocumentTypeModels = new ArrayList<>();
        final B2BDocumentTypeModel documentType = Mockito.mock(B2BDocumentTypeModel.class);
        b2BDocumentTypeModels.add(documentType);

        final List<DocumentStatus> documentStatuses = new ArrayList<>();
        documentStatuses.add(documentStatus);

        Mockito.when(b2bUnit.getUid()).thenReturn("unit123");

        final List<B2BDocumentModel> mockDocumentList = new ArrayList<>();
        mockDocumentList.add(Mockito.mock(B2BDocumentModel.class));
        Mockito.when(adnocB2BDocumentDao.findB2BDocuments(any(B2BUnitModel.class), any(List.class), any(List.class)))
                .thenReturn(mockDocumentList);

        List<B2BDocumentModel> result = adnocB2BDocumentService.getB2BDocuments(b2bUnit, b2BDocumentTypeModels, documentStatuses);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(adnocB2BDocumentDao).findB2BDocuments(b2bUnit, b2BDocumentTypeModels, documentStatuses);
    }
}
