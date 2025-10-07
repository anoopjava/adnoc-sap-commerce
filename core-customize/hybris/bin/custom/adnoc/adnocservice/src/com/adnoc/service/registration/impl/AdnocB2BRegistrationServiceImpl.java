package com.adnoc.service.registration.impl;

import com.adnoc.service.duplicatecheckhelper.AdnocDuplicateCheckHelper;
import com.adnoc.service.model.AdnocRegistrationModel;
import com.adnoc.service.registration.AdnocB2BRegistrationService;
import com.adnoc.service.registration.dao.AdnocB2BRegistrationDao;
import de.hybris.platform.b2bacceleratorservices.registration.impl.DefaultB2BRegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class AdnocB2BRegistrationServiceImpl extends DefaultB2BRegistrationService implements AdnocB2BRegistrationService
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BRegistrationServiceImpl.class);
    private AdnocB2BRegistrationDao adnocRegistrationDao;
    private AdnocDuplicateCheckHelper adnocDuplicateCheckHelper;

    @Override
    public AdnocRegistrationModel getAdnocB2BRegistration(final Map<String, String> duplicateCheckParams, final String adnocRegistrationType)
    {
        LOG.info("appEvent=AdnocB2BRegistration, getAdnocB2BRegistration method called");
        getAdnocDuplicateCheckHelper().validateDuplicateInGigya(duplicateCheckParams);
        return getAdnocRegistrationDao().findAdnocB2BRegistration(duplicateCheckParams, adnocRegistrationType);
    }

    protected AdnocB2BRegistrationDao getAdnocRegistrationDao()
    {
        return adnocRegistrationDao;
    }

    public void setAdnocRegistrationDao(final AdnocB2BRegistrationDao adnocRegistrationDao)
    {
        this.adnocRegistrationDao = adnocRegistrationDao;
    }

    protected AdnocDuplicateCheckHelper getAdnocDuplicateCheckHelper()
    {
        return adnocDuplicateCheckHelper;
    }

    public void setAdnocDuplicateCheckHelper(final AdnocDuplicateCheckHelper adnocDuplicateCheckHelper)
    {
        this.adnocDuplicateCheckHelper = adnocDuplicateCheckHelper;
    }
}
