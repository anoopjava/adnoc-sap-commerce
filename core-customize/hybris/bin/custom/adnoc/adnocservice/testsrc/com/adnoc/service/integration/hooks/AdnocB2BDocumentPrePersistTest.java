package com.adnoc.service.integration.hooks;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BDocumentPrePersistTest
{
    @InjectMocks
    private AdnocB2BDocumentPrePersist adnocB2BDocumentPrePersist;

    @Mock
    private B2BDocumentModel item;
    @Mock
    private ItemModel itemModel;
    @Mock
    private PersistenceContext context;

    @Test
    public void execute()
    {
        adnocB2BDocumentPrePersist.execute(item, context);
        assertFalse(adnocB2BDocumentPrePersist.execute(item, context).isPresent());
    }

    @Test
    public void executeNonEmpty()
    {
        adnocB2BDocumentPrePersist.execute(itemModel, context);
        assertTrue(adnocB2BDocumentPrePersist.execute(itemModel, context).isPresent());
    }
}