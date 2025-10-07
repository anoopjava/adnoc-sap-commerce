package com.adnoc.service.event;

import de.hybris.platform.returns.model.ReturnRequestModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AdnocReturnOrderRejectedEventTest
{

    @Test
    public void testEventInitialization()
    {
        final ReturnRequestModel returnRequestModel = new ReturnRequestModel();
        final AdnocReturnOrderRejectedEvent adnocReturnOrderRejectedEvent = new AdnocReturnOrderRejectedEvent(returnRequestModel);
        assertEquals(returnRequestModel, adnocReturnOrderRejectedEvent.getReturnRequest());
    }

    @Test
    public void testEventWithSourceAndReturnRequest()
    {
        ReturnRequestModel mockReturnRequest = new ReturnRequestModel();
        Serializable serializable = "TestSource";
        AdnocReturnOrderRejectedEvent event = new AdnocReturnOrderRejectedEvent(serializable, mockReturnRequest);
        assertEquals(mockReturnRequest, event.getReturnRequest());
        assertEquals(serializable, event.getSource());
    }
}
