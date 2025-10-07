package com.adnoc.service.address;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.AddressService;

/**
 * Adnoc specific AddressService interface that extends the default AddressService.
 * This interface can be used to define additional methods specific to Adnoc's address handling.
 */
public interface AdnocAddressService extends AddressService
{
    /**
     * Retrieves an address by its primary key.
     *
     * @param pk the primary key of the address
     * @return the AddressModel corresponding to the given primary key
     */
    AddressModel getAddress(String pk);
}
