package com.adnoc.facades.storefinder.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SearchPagePointOfServiceDistancePopulatorTest {

    @Mock
    private Converter<PointOfServiceDistanceData, PointOfServiceData> converter;

    @InjectMocks
    private SearchPagePointOfServiceDistancePopulator<
            StoreFinderSearchPageData<PointOfServiceDistanceData>,
            StoreFinderSearchPageData<PointOfServiceData>> populator;

    @Test
    public void testPopulate() {
        // Prepare source
        StoreFinderSearchPageData<PointOfServiceDistanceData> source = new StoreFinderSearchPageData<>();
        PointOfServiceDistanceData posDistanceData = new PointOfServiceDistanceData();

        source.setResults(Collections.singletonList(posDistanceData));
        source.setLocationText("Abu Dhabi");
        source.setSourceLatitude(24.4539);
        source.setSourceLongitude(54.3773);

        // Mock converted POS data
        PointOfServiceData posData = new PointOfServiceData();
        posData.setName("TestStore");
        Mockito.when(converter.convert(any(PointOfServiceDistanceData.class))).thenReturn(posData);

        // Target
        StoreFinderSearchPageData<PointOfServiceData> target = new StoreFinderSearchPageData<>();

        // Act
        populator.populate(source, target);

        // Assert
        assertNotNull(target.getResults());
        assertEquals(1, target.getResults().size());

        PointOfServiceData result = target.getResults().get(0);
        assertEquals("TestStore", result.getName());
        assertNotNull(result.getUrl());
        assertTrue(result.getUrl().contains("/store/TestStore"));
        assertTrue(result.getUrl().contains("lat=24.4539"));
        assertTrue(result.getUrl().contains("long=54.3773"));

    }
}
