package com.adnoc.service.integration.hooks;

import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class AdnocB2BDocumentPrePersist implements PrePersistHook
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BDocumentPrePersist.class);

    @Override
    public Optional<ItemModel> execute(final ItemModel item, final PersistenceContext context)
    {
        if (item instanceof B2BDocumentModel)
        {
            LOG.info("appEvent=AdnocB2BDocumentInbound, AdnocB2BDocumentInboundPrePersist start...");
            return Optional.empty();
        }
        return Optional.of(item);
    }
}
