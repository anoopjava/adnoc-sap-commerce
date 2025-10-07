package com.adnoc.facades.customerticketingfacades.strategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocTicketNAAssociationStrategyTest {

    @Mock
    private UserModel userModel;

    @Test
    public void testGetObjects_ReturnsNAEntry() {
        Mockito.when(userModel.getUid()).thenReturn("test-user");

        AdnocTicketNAAssociationStrategy strategy = new AdnocTicketNAAssociationStrategy();
        Map<String, List<TicketAssociatedData>> result = strategy.getObjects(userModel);

        assertNotNull(result);
        assertTrue(result.containsKey("NA"));

        List<TicketAssociatedData> associatedDataList = result.get("NA");
        assertEquals(1, associatedDataList.size());

        TicketAssociatedData data = associatedDataList.get(0);
        assertEquals("NA", data.getCode());
        assertEquals("NA", data.getType());
    }
}
