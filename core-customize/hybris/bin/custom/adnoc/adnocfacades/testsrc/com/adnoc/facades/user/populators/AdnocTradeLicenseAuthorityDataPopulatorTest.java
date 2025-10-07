package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.AdnocTradeLicenseAuthorityData;
import com.adnoc.service.enums.TradeLicenseAuthority;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocTradeLicenseAuthorityDataPopulatorTest {

    @Mock
    private TypeService typeService;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private TradeLicenseAuthority tradeLicenseAuthority;

    @Mock
    private TradeLicenseAuthority defaultTradeLicenseAuthority;

    @Mock
    private EnumerationValueModel enumValueModel;

    @InjectMocks
    private AdnocTradeLicenseAuthorityDataPopulator populator;

    @Test
    public void testPopulateWithValidLicenseAuthority() {

        Mockito.when(tradeLicenseAuthority.getCode()).thenReturn("AUTH_CODE");
        Mockito.when(typeService.getEnumerationValue(tradeLicenseAuthority)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Authority Name");

        AdnocTradeLicenseAuthorityData target = new AdnocTradeLicenseAuthorityData();

        populator.populate(tradeLicenseAuthority, target);

        assertEquals("AUTH_CODE", target.getCode());
        assertEquals("Authority Name", target.getName());
    }

    @Test
    public void testPopulateWithNullLicenseAuthority() {

        Mockito.when(enumerationService.getEnumerationValue(TradeLicenseAuthority.class, ""))
                .thenReturn(defaultTradeLicenseAuthority);
        Mockito.when(defaultTradeLicenseAuthority.getCode()).thenReturn("DEFAULT_AUTH_CODE");
        Mockito.when(typeService.getEnumerationValue(defaultTradeLicenseAuthority)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Default Authority Name");

        AdnocTradeLicenseAuthorityData target = new AdnocTradeLicenseAuthorityData();


        populator.populate(null, target);

        assertEquals("DEFAULT_AUTH_CODE", target.getCode());
        assertEquals("Default Authority Name", target.getName());
    }
}
