package com.adnoc.service.address;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.daos.AddressDao;

/**
 * AdnocAddressDao interface extends the AddressDao to provide methods specific to Adnoc's address handling.
 * It includes a method to retrieve an AddressModel by its primary key.
 */
public interface AdnocAddressDao extends AddressDao
{
    /**
     * Retrieves an AddressModel by its primary key.
     *
     * @param pk the primary key of the address
     * @return the AddressModel associated with the given primary key, or null if not found
     */
    AddressModel getAddress(String pk);
}
