/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.integration.hooks;

import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocPriceRowPostPersistenceHook implements PostPersistHook
{
    private static final Logger LOG = LogManager.getLogger(AdnocPriceRowPostPersistenceHook.class);

    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;

    @Override
    public void execute(final ItemModel item, final PersistenceContext context)
    {
        if (item instanceof final PriceRowModel priceRowModel)
        {
            LOG.debug("appEvent=AdnocPriceRowInbound,AdnocPriceRowPostPersistenceHook is called for PriceRow with code:{} ", priceRowModel.getProduct());

            if (BooleanUtils.isTrue(priceRowModel.getDeletionIndicator()))
            {
                getAdnocOutboundReplicationDirector().scheduleOutboundTask(priceRowModel);
            }
        }

    }

    protected AdnocOutboundReplicationDirector getAdnocOutboundReplicationDirector()
    {
        return adnocOutboundReplicationDirector;
    }

    public void setAdnocOutboundReplicationDirector(final AdnocOutboundReplicationDirector adnocOutboundReplicationDirector)
    {
        this.adnocOutboundReplicationDirector = adnocOutboundReplicationDirector;
    }
}