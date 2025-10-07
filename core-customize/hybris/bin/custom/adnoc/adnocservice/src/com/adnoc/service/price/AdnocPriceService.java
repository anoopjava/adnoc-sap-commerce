package com.adnoc.service.price;

import com.adnoc.service.order.strategies.calculation.pdt.impl.converter.impl.AdnocPriceValueInfoCriteria;
import com.adnoc.service.price.data.AdnocPriceInfoResponseData;

/**
 * Adnoc Price Service interface.
 * This service provides methods to retrieve price information from SAP.
 */
public interface AdnocPriceService
{
    /**
     * Gets sap price information.
     *
     * @param adnocPriceValueInfoCriteria the price value info criteria
     * @return the sap price information
     */
    AdnocPriceInfoResponseData getSapPriceInformation(AdnocPriceValueInfoCriteria adnocPriceValueInfoCriteria);
}
