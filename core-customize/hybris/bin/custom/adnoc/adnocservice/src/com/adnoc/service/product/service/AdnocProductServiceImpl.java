package com.adnoc.service.product.service;

import com.adnoc.service.product.daos.AdnocProductDao;
import de.hybris.platform.product.impl.DefaultProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocProductServiceImpl extends DefaultProductService implements AdnocProductService
{
    private static final Logger LOG = LogManager.getLogger(AdnocProductServiceImpl.class);

    private AdnocProductDao adnocProductDao;

    protected AdnocProductDao getAdnocProductDao()
    {
        return adnocProductDao;
    }

    public void setAdnocProductDao(final AdnocProductDao adnocProductDao)
    {
        this.adnocProductDao = adnocProductDao;
    }
}
