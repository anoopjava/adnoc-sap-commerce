package com.adnoc.service.b2bunit;

import com.adnoc.service.b2bunit.dao.AdnocB2BUnitDao;
import com.adnoc.service.enums.PartnerFunction;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocB2BUnitServiceImplTest
{
    @InjectMocks
    private AdnocB2BUnitServiceImpl adnocB2BUnitService= Mockito.mock(AdnocB2BUnitServiceImpl.class);
    @Mock
    private B2BCustomerModel b2BCustomerModel=Mockito.mock(B2BCustomerModel.class);
    @Mock
    private PartnerFunction partnerFunction=Mockito.mock(PartnerFunction.class);
    @Mock
    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService=Mockito.mock(B2BCustomerService.class);
    @Mock
    private AdnocB2BUnitDao adnocB2BUnitDao=Mockito.mock(AdnocB2BUnitDao.class);
    @Mock
    private B2BUnitModel b2BUnitModel=Mockito.mock(B2BUnitModel.class);

    @Test
    void testGetB2BUnits()
    {

        Set<B2BUnitModel> b2BUnitModels=adnocB2BUnitService.getB2BUnits(b2BCustomerModel,partnerFunction);
        Assertions.assertThat(b2BUnitModels).isNotNull();
    }

    @Test
    void testGetChildB2BUnits()
    {

        Set<B2BUnitModel> b2BUnitModels=adnocB2BUnitService.getChildB2BUnits(b2BUnitModel,partnerFunction);
        Assertions.assertThat(b2BUnitModels).isNotNull();
    }

    @Test
    void testGetCurrentB2BUnit()
    {
        b2BCustomerModel.setDefaultB2BUnit(b2BUnitModel);
        adnocB2BUnitService.setB2BCustomerService(b2BCustomerService);
        Mockito.when(b2BCustomerService.getCurrentB2BCustomer()).thenReturn(b2BCustomerModel);
        Mockito.when(b2BCustomerModel.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        B2BUnitModel bUnitModel=adnocB2BUnitService.getCurrentB2BUnit();
        Assertions.assertThat(bUnitModel).isNotNull();
    }

    @Test
    void testSetCurrentB2BUnit()
    {
        adnocB2BUnitService.setCurrentB2BUnit(b2BUnitModel);
    }

    @Test
    void testGetB2BUnitsForChildMapping()
    {
        adnocB2BUnitService.setAdnocB2BUnitDao(adnocB2BUnitDao);
        Collection<B2BUnitModel> b2BUnitModels=adnocB2BUnitService.getB2BUnitsForChildMapping();
        Assertions.assertThat(b2BUnitModels).isNotNull();
    }
}