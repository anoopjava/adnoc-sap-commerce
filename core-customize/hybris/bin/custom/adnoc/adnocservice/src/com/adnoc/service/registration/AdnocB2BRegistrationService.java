package com.adnoc.service.registration;

import com.adnoc.service.model.AdnocRegistrationModel;
import de.hybris.platform.b2bacceleratorservices.registration.B2BRegistrationService;

import java.util.Map;

/**
 * Adnoc B2B Registration Service interface.
 * This service extends the B2BRegistrationService to provide additional functionality specific to ADNOC B2B registrations.
 */
public interface AdnocB2BRegistrationService extends B2BRegistrationService
{
    /**
     * Gets adnoc b2b registration.
     *
     * @param duplicateCheckParams  the duplicate check params
     * @param adnocRegistrationType the adnoc registration type
     * @return the adnoc b2b registration
     */
    AdnocRegistrationModel getAdnocB2BRegistration(Map<String, String> duplicateCheckParams, String adnocRegistrationType);

}
