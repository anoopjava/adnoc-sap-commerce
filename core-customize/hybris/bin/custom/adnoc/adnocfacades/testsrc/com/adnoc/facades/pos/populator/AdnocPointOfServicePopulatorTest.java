package com.adnoc.facades.pos.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.core.PK;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocPointOfServicePopulatorTest {

    @Mock
    private PointOfServiceModel source;

    @Mock
    private PK pk;

    @Test
    public void populateSetIdFromPk() {

        Mockito.when(source.getPk()).thenReturn(pk);
        Mockito.when(pk.toString()).thenReturn("12345");

        PointOfServiceData target = new PointOfServiceData();

        AdnocPointOfServicePopulator populator = new AdnocPointOfServicePopulator();

        populator.populate(source, target);

        assertEquals("12345", target.getId());
    }
}
