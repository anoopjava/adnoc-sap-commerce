package com.adnoc.facades.user.impl;

import com.adnoc.facades.product.data.GenderData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BUserFacadeImplTest
{
    @InjectMocks
    private AdnocB2BUserFacadeImpl adnocB2BUserFacadeImpl;
    @Mock
    private EnumerationService enumerationService;
    @Mock
    private Converter<Gender, GenderData> genderDataConverter;

    @Test
    public void testGetGenders()
    {
        Mockito.when(enumerationService.getEnumerationValues(Gender._TYPECODE)).thenReturn(List.of(Gender.MALE));
        final GenderData genderData = new GenderData();
        Mockito.when(genderDataConverter.convert(Gender.MALE)).thenReturn(genderData);
        Assertions.assertThat(adnocB2BUserFacadeImpl.getGenders()).isNotEmpty();
    }

    @Test
    public void testGetNationalities()
    {
        Assertions.assertThat(adnocB2BUserFacadeImpl.getNationalities()).isNotEmpty();
    }

    @Test
    public void testGetPreferredCommunicationChannels()
    {
        Assertions.assertThat(adnocB2BUserFacadeImpl.getPreferredCommunicationChannels()).isNotEmpty();
    }
}
