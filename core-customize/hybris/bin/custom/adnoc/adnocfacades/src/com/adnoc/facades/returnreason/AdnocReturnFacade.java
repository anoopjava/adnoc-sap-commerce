package com.adnoc.facades.returnreason;

import com.adnoc.facades.ordermanagement.data.ReturnReasonData;

import java.util.List;

/**
 * AdnocReturnFacade interface.
 * This interface provides methods to retrieve return reasons specific to ADNOC.
 */
public interface AdnocReturnFacade
{
    /**
     * Gets return reasons.
     *
     * @return the return reasons
     */
    List<ReturnReasonData> getReturnReasons();
}
