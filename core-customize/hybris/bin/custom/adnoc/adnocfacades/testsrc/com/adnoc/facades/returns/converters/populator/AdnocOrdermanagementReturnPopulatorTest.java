package com.adnoc.facades.returns.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@UnitTest

@RunWith(MockitoJUnitRunner.class)
public class AdnocOrdermanagementReturnPopulatorTest {

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private ReturnRequestModel returnRequestModel;

    @Mock
    private RefundReason reasonModel;

    @Mock
    private ReturnStatus returnStatus;

    @InjectMocks
    private AdnocOrdermanagementReturnPopulator populator;

    @Mock
    private List<ReturnStatus> cancellableReturnStatusList;

    @Test
    public void testPopulateAllFields() {

        Date creationTime = new Date();
        String comment = "Damaged";
        String reasonCode = "DAMAGED_ITEM";
        String statusDisplay = "Approved";
        String reasonDisplay = "Damaged Item";

        Mockito.when(returnRequestModel.getCreationtime()).thenReturn(creationTime);
        Mockito.when(returnRequestModel.getComment()).thenReturn(comment);
        Mockito.when(returnRequestModel.getReason()).thenReturn(reasonModel);
        Mockito.when(reasonModel.getCode()).thenReturn(reasonCode);
        Mockito.when(returnRequestModel.getStatus()).thenReturn(returnStatus);
        Mockito.when(enumerationService.getEnumerationName(returnStatus)).thenReturn(statusDisplay);
        Mockito.when(enumerationService.getEnumerationName(reasonModel)).thenReturn(reasonDisplay);


        ReturnRequestData target = new ReturnRequestData();

        populator.setEnumerationService(enumerationService);
        populator.setCancellableReturnStatusList(cancellableReturnStatusList);
        populator.populate(returnRequestModel, target);

        // Verify
        assertEquals(creationTime, target.getCreationTime());
        assertEquals(comment, target.getComment());
        assertEquals(reasonModel, target.getRefundReason());
        assertEquals(reasonCode, target.getReason());
        assertEquals(reasonDisplay, target.getReasonDisplay());
        assertEquals(statusDisplay, target.getStatusDisplay());
    }
}
