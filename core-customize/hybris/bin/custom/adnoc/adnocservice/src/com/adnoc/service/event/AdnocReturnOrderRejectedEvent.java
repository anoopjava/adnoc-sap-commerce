package com.adnoc.service.event;

import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.io.Serializable;

public class AdnocReturnOrderRejectedEvent extends AbstractEvent
{

    private ReturnRequestModel returnRequest;

    public AdnocReturnOrderRejectedEvent(final ReturnRequestModel returnRequest)
    {
        this.returnRequest = returnRequest;
    }

    public AdnocReturnOrderRejectedEvent(final Serializable source, final ReturnRequestModel returnRequest)
    {
        super(source);
        this.returnRequest = returnRequest;
    }

    public AdnocReturnOrderRejectedEvent()
    {
        super();
    }

    public void setReturnRequest(final ReturnRequestModel returnRequest)
    {
        this.returnRequest = returnRequest;
    }

    public ReturnRequestModel getReturnRequest()
    {
        return returnRequest;
    }

}
