package com.adnoc.service.storeservice;

import de.hybris.platform.commerceservices.storefinder.StoreFinderService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.List;

/**
 * The interface Adnoc store service.
 */
public interface AdnocStoreService extends StoreFinderService
{
    /**
     * Gets point of service by address.
     *
     * @param pk the pointofservice model
     * @return the point of service
     */
    PointOfServiceModel getPointOfServicePk(String pk);

    /**
     * Gets eligible pickup pos for base store.
     *
     * @param productCode of the product
     * @return the eligible pickup pos for base store
     */
    List<PointOfServiceModel> getEligiblePickupPOSForBaseStore(String productCode);
}
