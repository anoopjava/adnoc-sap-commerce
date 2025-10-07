package com.adnoc.service.storeservice;

import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

/**
 * The interface Adnoc store dao.
 */
public interface AdnocStoreDao extends PointOfServiceDao
{
    /**
     * Find point of service by address point of service model.
     *
     * @param pk the pointofservice model
     * @return the point of service
     */
    PointOfServiceModel findPointOfServiceByPk(String pk);
}
