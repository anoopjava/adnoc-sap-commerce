package com.adnoc.service.registration.dao;

import com.adnoc.service.model.AdnocRegistrationModel;
import com.adnoc.service.model.AdnocSoldToB2BRegistrationModel;

import java.util.Map;

public interface AdnocB2BRegistrationDao
{
    /**
     * Find adnoc b2b registration adnoc b 2 b registration model.
     *
     * @param duplicateCheckParams  passes Map with four parameters
     * @param adnocRegistrationType the adnoc registration type
     * @return the adnoc b2b registration model
     */
    AdnocRegistrationModel findAdnocB2BRegistration(Map<String, String> duplicateCheckParams,String adnocRegistrationType);
}
