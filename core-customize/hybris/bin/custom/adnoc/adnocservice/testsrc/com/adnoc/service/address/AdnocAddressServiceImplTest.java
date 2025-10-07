package com.adnoc.service.address;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocAddressServiceImplTest
{

    @InjectMocks
    private AdnocAddressServiceImpl adnocAddressService=new AdnocAddressServiceImpl();

    @Mock
    private AdnocAddressDao adnocAddressDao= Mockito.mock(AdnocAddressDaoImpl.class);

    @Mock
    private FlexibleSearchService flexibleSearchService= Mockito.mock(FlexibleSearchService.class);


    @Test
    void testGetAddress()
    {
        Mockito.when(adnocAddressDao.getAddress("test")).thenReturn(new AddressModel());
        adnocAddressService.setAdnocAddressDao(adnocAddressDao);
        AddressModel addressModel=adnocAddressService.getAddress("test");
        Assertions.assertThat(addressModel).isNotNull();
    }
}