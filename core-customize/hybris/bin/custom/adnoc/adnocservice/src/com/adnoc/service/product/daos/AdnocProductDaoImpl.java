package com.adnoc.service.product.daos;

import de.hybris.platform.product.daos.impl.DefaultProductDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocProductDaoImpl extends DefaultProductDao implements AdnocProductDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocProductDaoImpl.class);

    public AdnocProductDaoImpl(final String typecode)
    {
        super(typecode);
    }

}
