package com.adnoc.facades.store;

import com.adnoc.service.storeservice.AdnocStoreService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocStoreFacadeImplTest
{
    @InjectMocks
    private AdnocStoreFacadeImpl adnocStoreFacadeImpl;
    @Mock
    private AdnocStoreService adnocStoreService;
    @Mock
    private PointOfServiceModel pointOfServiceModel;
    @Mock
    private PointOfServiceData pointOfServiceData;
    @Mock
    private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;

    @Test
    public void testGetEligiblePickupPOSForBaseStore()
    {
        final String productCode = "12345";
        final List<PointOfServiceModel> posModels = Collections.singletonList(pointOfServiceModel);
        Mockito.when(adnocStoreService.getEligiblePickupPOSForBaseStore(productCode)).thenReturn(posModels);
        Mockito.when(pointOfServiceConverter.convertAll(posModels)).thenReturn(Collections.singletonList(pointOfServiceData));
        final List<PointOfServiceData> result = adnocStoreFacadeImpl.getEligiblePickupPOSForBaseStore(productCode);
        verify(adnocStoreService).getEligiblePickupPOSForBaseStore(productCode);
        verify(pointOfServiceConverter).convertAll(posModels);
        assertNotNull(result);
    }
}
