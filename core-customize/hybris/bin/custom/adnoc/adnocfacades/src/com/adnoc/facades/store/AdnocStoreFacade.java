package com.adnoc.facades.store;

import de.hybris.platform.commercefacades.storefinder.StoreFinderFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;

import java.util.List;

/**
 * The interface Adnoc store facade.
 */
public interface AdnocStoreFacade extends StoreFinderFacade
{
    /**
     * Gets eligible pickup pos for base store.
     *
     * @param productCode the product code
     * @return the eligible pickup pos for base store
     */
    List<PointOfServiceData> getEligiblePickupPOSForBaseStore(String productCode);
}
