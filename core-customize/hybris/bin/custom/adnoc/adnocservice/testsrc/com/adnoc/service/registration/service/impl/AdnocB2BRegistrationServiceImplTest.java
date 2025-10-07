package com.adnoc.service.registration.service.impl;

import com.adnoc.service.model.AdnocSoldToB2BRegistrationModel;
import com.adnoc.service.registration.dao.AdnocB2BRegistrationDao;
import com.adnoc.service.registration.impl.AdnocB2BRegistrationServiceImpl;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocB2BRegistrationServiceImplTest
{
    @InjectMocks
    private AdnocB2BRegistrationServiceImpl registrationService = new AdnocB2BRegistrationServiceImpl();
    @Mock
    private AdnocB2BRegistrationDao registrationDao = Mockito.mock(AdnocB2BRegistrationDao.class);
    @Mock
    private AdnocSoldToB2BRegistrationModel registrationModel = Mockito.mock(AdnocSoldToB2BRegistrationModel.class);

    @Test
    void testGetAdnocB2BRegistration()
    {
        registrationService.setAdnocRegistrationDao(registrationDao);
        final Map<String, String> duplicateCheckParams = new HashMap<>();
        duplicateCheckParams.put("email", "test@example.com");
        final String adnocRegistrationType = AdnocSoldToB2BRegistrationModel._TYPECODE;
        Mockito.when(registrationDao.findAdnocB2BRegistration(duplicateCheckParams, adnocRegistrationType)).thenReturn(registrationModel);
        final AdnocSoldToB2BRegistrationModel result = (AdnocSoldToB2BRegistrationModel) registrationService.getAdnocB2BRegistration(duplicateCheckParams, adnocRegistrationType);
        Assertions.assertThat(result).isNotNull();
    }
}
