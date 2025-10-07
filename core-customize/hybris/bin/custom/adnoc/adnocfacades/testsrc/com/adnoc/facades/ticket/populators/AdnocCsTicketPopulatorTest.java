package com.adnoc.facades.ticket.populators;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.enums.CsTicketRequestForCategory;
import com.adnoc.service.enums.CsTicketRequestForSubCategory;
import com.adnoc.service.enums.CsTicketTargetSystem;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocCsTicketPopulatorTest {

    @InjectMocks
    private AdnocCsTicketPopulator populator;

    @Mock
    private AdnocConfigService adnocConfigService;

    @Mock
    private AdnocB2BUnitService adnocB2BUnitService;

    @Mock
    private CsTicketParameter source;

    @Mock
    private CsTicketModel target;

    @Mock
    private AdnocCsTicketCategoryMapModel ticketCategoryMapModel;

    @Mock
    private B2BUnitModel b2bUnit;

    @Test
    public void testSetsExpectedFields() {

        Mockito.when(source.getRequestFor()).thenReturn(CsTicketRequestForCategory.valueOf("customer"));
        Mockito.when(source.getSubCategory()).thenReturn(CsTicketRequestForSubCategory.valueOf("billing"));
        Mockito.when(source.getAssociated()).thenReturn("someAssociatedValue");
        Mockito.when(source.getCsTicketCategoryMapId()).thenReturn("MAP123");

        Mockito.when(adnocConfigService.getAdnocCsTicketCategoryMap("MAP123")).thenReturn(ticketCategoryMapModel);
        Mockito.when(ticketCategoryMapModel.getTargetSystem()).thenReturn(CsTicketTargetSystem.valueOf("CRM"));

        Mockito.when(adnocB2BUnitService.getCurrentB2BUnit()).thenReturn(b2bUnit);


        populator.populate(source, target);

        // Verify interactions and field sets
        verify(target).setRequestFor(CsTicketRequestForCategory.valueOf("customer"));
        verify(target).setSubCategory(CsTicketRequestForSubCategory.valueOf("billing"));
        verify(target).setAssociatedTo("someAssociatedValue");
        verify(target).setTargetSystem(CsTicketTargetSystem.valueOf("CRM"));
        verify(target).setB2bUnit(b2bUnit);
    }

    @Test
    public void testHandlesNullCategoryMapAndB2BUnit() {

        Mockito.when(source.getRequestFor()).thenReturn(CsTicketRequestForCategory.valueOf("customer"));
        Mockito.when(source.getSubCategory()).thenReturn(CsTicketRequestForSubCategory.valueOf("billing"));
        Mockito.when(source.getAssociated()).thenReturn("associated");
        Mockito.when(source.getCsTicketCategoryMapId()).thenReturn("MAP999");

        Mockito.when(adnocConfigService.getAdnocCsTicketCategoryMap("MAP999")).thenReturn(null);
        Mockito.when(adnocB2BUnitService.getCurrentB2BUnit()).thenReturn(null);

        populator.populate(source, target);

        verify(target).setRequestFor(CsTicketRequestForCategory.valueOf("customer"));
        verify(target).setSubCategory(CsTicketRequestForSubCategory.valueOf("billing"));
        verify(target).setAssociatedTo("associated");
        verify(target).setTargetSystem(null);
        verify(target, never()).setB2bUnit(any());
    }
}
