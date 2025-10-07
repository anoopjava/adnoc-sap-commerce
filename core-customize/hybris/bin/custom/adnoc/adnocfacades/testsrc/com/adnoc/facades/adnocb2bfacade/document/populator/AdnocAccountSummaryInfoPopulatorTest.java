package com.adnoc.facades.adnocb2bfacade.document.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocAccountSummaryInfoPopulatorTest {

    @Test
    public void testWhenAddressListIsNotEmpty() {

        AdnocAccountSummaryInfoPopulator populator = new AdnocAccountSummaryInfoPopulator();

        AddressData address1 = new AddressData();
        address1.setId("address1");
        AddressData address2 = new AddressData();
        address2.setId("address2");

        B2BUnitData b2bUnitData = mock(B2BUnitData.class);
        Mockito.when(b2bUnitData.getAddresses()).thenReturn(List.of(address1, address2));

        AddressData result = populator.getDefaultAddress(b2bUnitData);


        assertNotNull(result);
        assertEquals("address1", result.getId());
    }

    @Test
    public void testWhenAddressListIsEmpty() {

        AdnocAccountSummaryInfoPopulator populator = new AdnocAccountSummaryInfoPopulator();

        B2BUnitData b2bUnitData = mock(B2BUnitData.class);
        Mockito.when(b2bUnitData.getAddresses()).thenReturn(Collections.emptyList());

        AddressData result = populator.getDefaultAddress(b2bUnitData);


        assertNull(result);
    }

    @Test
    public void testWhenAddressListIsNull() {

        AdnocAccountSummaryInfoPopulator populator = new AdnocAccountSummaryInfoPopulator();

        B2BUnitData b2bUnitData = mock(B2BUnitData.class);
        Mockito.when(b2bUnitData.getAddresses()).thenReturn(null);

        AddressData result = populator.getDefaultAddress(b2bUnitData);

        assertNull(result);
    }
}
