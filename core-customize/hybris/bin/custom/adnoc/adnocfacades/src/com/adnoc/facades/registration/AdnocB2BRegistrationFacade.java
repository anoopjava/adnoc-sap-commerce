package com.adnoc.facades.registration;


import com.adnoc.facades.user.data.PrimaryProductData;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bacceleratorfacades.exception.RegistrationNotEnabledException;
import de.hybris.platform.b2bacceleratorfacades.registration.B2BRegistrationFacade;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;

import java.util.List;
/**
 * Adnoc B2B Registration Facade interface.
 * This interface extends the B2BRegistrationFacade to provide additional functionality specific to ADNOC B2B registrations.
 */
public interface AdnocB2BRegistrationFacade extends B2BRegistrationFacade
{
    /**
     * Gets primary products.
     *
     * @return the primary products
     */
    List<PrimaryProductData> getPrimaryProducts();

    /**
     * Populate store info.
     *
     * @param b2BRegistrationModel the b 2 b registration model
     */
    void populateStoreInfo(B2BRegistrationModel b2BRegistrationModel);

    /**
     * Register sold to string.
     *
     * @param b2BRegistrationData the b 2 b registration data
     * @return the string
     * @throws CustomerAlreadyExistsException  the customer already exists exception
     * @throws RegistrationNotEnabledException the registration not enabled exception
     */
    String registerSoldTo(B2BRegistrationData b2BRegistrationData) throws CustomerAlreadyExistsException, RegistrationNotEnabledException;

}
