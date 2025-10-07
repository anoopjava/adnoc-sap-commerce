package com.adnoc.service.company.service;

import com.adnoc.service.company.dao.AdnocB2BDocumentTypeDao;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BDocumentTypeServiceImplTest
{
    @InjectMocks
    private AdnocB2BDocumentTypeServiceImpl adnocB2BDocumentTypeServiceImpl = new AdnocB2BDocumentTypeServiceImpl();

    @Mock
    private B2BDocumentTypeModel b2BDocumentTypeModel = Mockito.mock(B2BDocumentTypeModel.class);

    @Mock
    private AdnocB2BDocumentTypeDao adnocB2BDocumentTypeDao = Mockito.mock(AdnocB2BDocumentTypeDao.class);

    @Test
    public void getTestB2BDocumentType()
    {
        final String documentCode = "INV123";
        Mockito.when(adnocB2BDocumentTypeDao.findB2BDocumentType(documentCode))
                .thenReturn(b2BDocumentTypeModel);
        adnocB2BDocumentTypeServiceImpl.getB2BDocumentType(documentCode);
        Assertions.assertThat(b2BDocumentTypeModel).isNotNull();
        verify(adnocB2BDocumentTypeDao).findB2BDocumentType(documentCode);
    }

    @Test
    public void getB2BDocumentTypeNotFound()
    {
        final B2BDocumentTypeModel result = adnocB2BDocumentTypeServiceImpl.getB2BDocumentType("null");
        Assertions.assertThat(result).isNull();
    }
}
