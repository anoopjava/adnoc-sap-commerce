package com.adnoc.facades.order.impl;

import com.adnoc.facades.ordermanagement.data.CancelReasonData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;



@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class AdnocCancelReasonFacadeImplTest {

    @Test
    public void testGetCancelReasons() {

        EnumerationService enumerationService = mock(EnumerationService.class);
        Converter<CancelReason, CancelReasonData> cancelReasonConverter = mock(Converter.class);

        CancelReason reason1 = CancelReason.CUSTOMERREQUEST;
        CancelReason reason2 = CancelReason.OTHER;

        CancelReasonData data1 = new CancelReasonData();
        data1.setCode("CUSTOMERREQUEST");

        CancelReasonData data2 = new CancelReasonData();
        data2.setCode("OTHER");

        // Configure mocks
        Mockito.when(enumerationService.getEnumerationValues(CancelReason._TYPECODE))
                .thenReturn(Arrays.asList(reason1, reason2));
        Mockito.when(cancelReasonConverter.convert(reason1)).thenReturn(data1);
        Mockito.when(cancelReasonConverter.convert(reason2)).thenReturn(data2);

        AdnocCancelReasonFacadeImpl facade = new AdnocCancelReasonFacadeImpl();
        facade.setEnumerationService(enumerationService);
        facade.setCancelReasonDataConverter(cancelReasonConverter);

        // Execute
        List<CancelReasonData> result = facade.getCanceReasons();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CUSTOMERREQUEST", result.get(0).getCode());
        assertEquals("OTHER", result.get(1).getCode());

        // Ensure interactions
        verify(enumerationService).getEnumerationValues(CancelReason._TYPECODE);
        verify(cancelReasonConverter).convert(reason1);
        verify(cancelReasonConverter).convert(reason2);
    }
}
