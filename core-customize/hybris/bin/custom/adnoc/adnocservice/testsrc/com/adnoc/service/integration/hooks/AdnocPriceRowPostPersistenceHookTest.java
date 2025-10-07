package com.adnoc.service.integration.hooks;

import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocPriceRowPostPersistenceHookTest {

    @Test
    public void executeTrue_shouldCallGetDeletionIndicator() {
        AdnocPriceRowPostPersistenceHook hook = new AdnocPriceRowPostPersistenceHook();
        PriceRowModel priceRowModel = mock(PriceRowModel.class);
        PersistenceContext context = mock(PersistenceContext.class);
        AdnocOutboundReplicationDirector director = mock(AdnocOutboundReplicationDirector.class);
        when(priceRowModel.getDeletionIndicator()).thenReturn(Boolean.TRUE);
        doNothing().when(director).scheduleOutboundTask(any());
        hook.setAdnocOutboundReplicationDirector(director);
        hook.execute(priceRowModel, context);

        verify(priceRowModel, times(1)).getDeletionIndicator();
    }

    @Test
    public void executeFalse_shouldCallGetDeletionIndicator() {
        AdnocPriceRowPostPersistenceHook hook = new AdnocPriceRowPostPersistenceHook();
        PriceRowModel priceRowModel = mock(PriceRowModel.class);
        PersistenceContext context = mock(PersistenceContext.class);
        AdnocOutboundReplicationDirector director = mock(AdnocOutboundReplicationDirector.class);
        when(priceRowModel.getDeletionIndicator()).thenReturn(Boolean.FALSE);
        hook.setAdnocOutboundReplicationDirector(director);
        hook.execute(priceRowModel, context);
        verify(priceRowModel, times(1)).getDeletionIndicator();
    }

    @Test
    public void executeWithNonPriceRowItem_shouldNotCallGetDeletionIndicator() {
        AdnocPriceRowPostPersistenceHook hook = new AdnocPriceRowPostPersistenceHook();
        ItemModel itemModel = mock(ItemModel.class);
        PersistenceContext context = mock(PersistenceContext.class);
        AdnocOutboundReplicationDirector director = mock(AdnocOutboundReplicationDirector.class);
        hook.setAdnocOutboundReplicationDirector(director);
        hook.execute(itemModel, context);
        verifyNoInteractions(director);
    }
}
