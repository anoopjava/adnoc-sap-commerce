package com.adnoc.facades.adnocb2bfacade;

import com.adnoc.service.address.AdnocAddressService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BAddressFacadeImplTest {

    @Test
    public void testGetAddress() {

        String pk = "9988776655";
        AddressModel mockAddressModel = mock(AddressModel.class);
        AddressData expectedAddressData = new AddressData();
        expectedAddressData.setId("address123");

        AdnocAddressService addressService = mock(AdnocAddressService.class);
        Converter<AddressModel, AddressData> addressConverter = mock(Converter.class);

        Mockito.when(addressService.getAddress(pk)).thenReturn(mockAddressModel);
        Mockito.when(addressConverter.convert(mockAddressModel)).thenReturn(expectedAddressData);

        AdnocB2BAddressFacadeImpl facade = new AdnocB2BAddressFacadeImpl();
        facade.setAdnocAddressService(addressService);
        facade.setAddressConverter(addressConverter);

        AddressData actualAddressData = facade.getAddress(pk);

        assertEquals(expectedAddressData, actualAddressData);
        verify(addressService).getAddress(pk);
        verify(addressConverter).convert(mockAddressModel);
    }
}
