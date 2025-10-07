package com.adnoc.facades.order;

import com.adnoc.facades.ordermanagement.data.CancelReasonData;
import de.hybris.platform.ordermanagementfacades.order.OmsOrderFacade;

import java.util.List;

/**
 * AdnocCancelReasonFacade interface.
 * This interface extends the OmsOrderFacade to provide additional functionality for managing cancel reasons.
 */
public interface AdnocCancelReasonFacade
{
    /**
     * Gets cancel reasons.
     *
     * @return the cancel reasons
     */
    List<CancelReasonData> getCanceReasons();
}
