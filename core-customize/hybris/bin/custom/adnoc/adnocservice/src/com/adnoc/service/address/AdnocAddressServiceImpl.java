package com.adnoc.service.address;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.impl.DefaultAddressService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocAddressServiceImpl extends DefaultAddressService implements AdnocAddressService
{
    private static final Logger LOG = LogManager.getLogger(AdnocAddressServiceImpl.class);

    private transient AdnocAddressDao adnocAddressDao;

    @Override
    public AddressModel getAddress(final String pk)
    {
        LOG.info("appEvent=AdnocAddressFetch, address retrieval initiated for PK:{}", pk);
        return getAdnocAddressDao().getAddress(pk);
    }

    protected AdnocAddressDao getAdnocAddressDao()
    {
        return adnocAddressDao;
    }

    public void setAdnocAddressDao(final AdnocAddressDao adnocAddressDao)
    {
        this.adnocAddressDao = adnocAddressDao;
    }

}
