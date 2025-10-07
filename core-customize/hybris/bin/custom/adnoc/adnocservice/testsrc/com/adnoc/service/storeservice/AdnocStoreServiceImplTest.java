package com.adnoc.service.storeservice;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.apache.commons.collections.list.PredicatedList;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocStoreServiceImplTest
{
    @InjectMocks
    private AdnocStoreServiceImpl adnocStoreService=new AdnocStoreServiceImpl();

    @Mock
    private AdnocStoreDao adnocStoreDao= Mockito.mock(AdnocStoreDaoImpl.class);

    @Mock
    private BaseStoreService baseStoreService=Mockito.mock(BaseStoreService.class);

    @Mock
    private BaseStoreModel baseStore=Mockito.mock(BaseStoreModel.class);

    @Test
    void testGetPointOfServicePk()
    {
        adnocStoreService.setAdnocStoreDao(adnocStoreDao);
        Mockito.when(adnocStoreDao.findPointOfServiceByPk("test")).thenReturn(new PointOfServiceModel());
        PointOfServiceModel pointOfServiceModel=adnocStoreService.getPointOfServicePk("test");
        Assertions.assertThat(pointOfServiceModel).isNotNull();
    }

    @Test
    void testGetPickupWarehouses()
    {
        adnocStoreService.setAdnocStoreDao(adnocStoreDao);
        adnocStoreService.setBaseStoreService(baseStoreService);
        Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
        List<PointOfServiceModel> allPOS=Mockito.mock(PredicatedList.class);
        Mockito.when(baseStore.getPointsOfService()).thenReturn(allPOS)
        ;
        Mockito.when(adnocStoreDao.findPointOfServiceByPk("test")).thenReturn(new PointOfServiceModel());
        adnocStoreService.getEligiblePickupPOSForBaseStore("test");
      }
}