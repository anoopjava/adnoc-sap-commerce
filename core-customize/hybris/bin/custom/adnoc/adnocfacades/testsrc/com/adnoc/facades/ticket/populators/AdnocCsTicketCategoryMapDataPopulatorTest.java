package com.adnoc.facades.ticket.populators;

import com.adnoc.facades.ticket.data.AdnocCsTicketCategoryMapData;
import com.adnoc.service.enums.CsTicketRequestForCategory;
import com.adnoc.service.enums.CsTicketRequestForSubCategory;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocCsTicketCategoryMapDataPopulatorTest {

    @Test
    void testPopulate() {

        TypeService typeService = mock(TypeService.class);
        AdnocCsTicketCategoryMapDataPopulator populator = new AdnocCsTicketCategoryMapDataPopulator();
        populator.setTypeService(typeService);


        AdnocCsTicketCategoryMapModel mapModel = mock(AdnocCsTicketCategoryMapModel.class);
        CsTicketCategory requestTypeValue = mock(CsTicketCategory.class);
        CsTicketRequestForCategory requestForValue = mock(CsTicketRequestForCategory.class);
        CsTicketRequestForSubCategory subCategoryValue = mock(CsTicketRequestForSubCategory.class);

        EnumerationValueModel requestTypeEnum = mock(EnumerationValueModel.class);
        EnumerationValueModel requestForEnum = mock(EnumerationValueModel.class);
        EnumerationValueModel subCategoryEnum = mock(EnumerationValueModel.class);

        Mockito.when(mapModel.getCsTicketCategoryMapId()).thenReturn("MAP123");

        Mockito.when(mapModel.getRequestType()).thenReturn(requestTypeValue);
        Mockito.when(requestTypeValue.getCode()).thenReturn("general");
        Mockito.when(typeService.getEnumerationValue(requestTypeValue)).thenReturn(requestTypeEnum);
        Mockito.when(requestTypeEnum.getName()).thenReturn("General Request");

        Mockito.when(mapModel.getRequestFor()).thenReturn(requestForValue);
        Mockito.when(requestForValue.getCode()).thenReturn("customer");
        Mockito.when(typeService.getEnumerationValue(requestForValue)).thenReturn(requestForEnum);
        Mockito.when(requestForEnum.getName()).thenReturn("Customer");

        Mockito.when(mapModel.getSubCategory()).thenReturn(subCategoryValue);
        Mockito.when(subCategoryValue.getCode()).thenReturn("billing");
        Mockito.when(typeService.getEnumerationValue(subCategoryValue)).thenReturn(subCategoryEnum);
        Mockito.when(subCategoryEnum.getName()).thenReturn("Billing");

        // Execute
        AdnocCsTicketCategoryMapData data = new AdnocCsTicketCategoryMapData();
        populator.populate(mapModel, data);

        // Validate
        assertEquals("MAP123", data.getCsTicketCategoryMapId());
        assertEquals("GENERAL", data.getRequestType().getCode());
        assertEquals("General Request", data.getRequestType().getName());
        assertEquals("customer", data.getRequestFor().getCode());
    }
}
