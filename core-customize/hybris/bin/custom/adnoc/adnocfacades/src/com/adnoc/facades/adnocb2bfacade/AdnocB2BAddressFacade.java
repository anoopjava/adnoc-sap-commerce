package com.adnoc.facades.adnocb2bfacade;

import de.hybris.platform.commercefacades.user.data.AddressData;

/**
 * Facade interface for handling B2B address operations in the ADNOC context.
 */
public interface AdnocB2BAddressFacade
{
    /**
     * Retrieves an address by its primary key.
     *
     * @param pk the primary key of the address
     * @return the AddressData object corresponding to the given primary key
     */
    AddressData getAddress(String pk);
}
