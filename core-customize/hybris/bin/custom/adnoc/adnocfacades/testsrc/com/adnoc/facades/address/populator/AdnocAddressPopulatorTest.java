package com.adnoc.facades.address.populator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Map;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocAddressPopulatorTest {


    @Mock
    private AddressModel source;

    @Mock
    private AddressData target;

    @Mock
    private Map<String, Converter<AddressModel, StringBuilder>> addressFormatConverterMap;

    @Mock
    private Converter<AddressModel, StringBuilder> defaultAddressFormatConverter;

    @Spy
    @InjectMocks
    private final AdnocAddressPopulator populator = new AdnocAddressPopulator();


    @Test
    public void testPopulateWithSapCustomerID() {

        final String sapCustomerID = "12345";
        populator.setAddressFormatConverterMap(addressFormatConverterMap);
        populator.setDefaultAddressFormatConverter(defaultAddressFormatConverter);
        Mockito.when(source.getSapCustomerID()).thenReturn(sapCustomerID);


        populator.populate(source, target);


        verify(target, times(1)).setSapCustomerID(eq(sapCustomerID));
        verify(populator).populate(source, target);
    }

    @Test
    public void testPopulateWithBlankSapCustomerID() {

        Mockito.when(source.getSapCustomerID()).thenReturn("");
        populator.setAddressFormatConverterMap(addressFormatConverterMap);
        populator.setDefaultAddressFormatConverter(defaultAddressFormatConverter);


        populator.populate(source, target);


        verify(target, never()).setSapCustomerID(any());
    }

    @Test
    public void testPopulateWithNullSapCustomerID() {

        Mockito.when(source.getSapCustomerID()).thenReturn(null);
        populator.setAddressFormatConverterMap(addressFormatConverterMap);
        populator.setDefaultAddressFormatConverter(defaultAddressFormatConverter);


        populator.populate(source, target);


        verify(target, never()).setSapCustomerID(any()); // Ensure SapCustomerID is not set
    }

    @Test
    public void testPopulateCallsSuperClassPopulate() {

        final String sapCustomerID = "12345";
        Mockito.when(source.getSapCustomerID()).thenReturn(sapCustomerID);
        populator.setAddressFormatConverterMap(addressFormatConverterMap);
        populator.setDefaultAddressFormatConverter(defaultAddressFormatConverter);

        populator.populate(source, target);


        verify(populator, times(1)).populate(source, target);
    }


}