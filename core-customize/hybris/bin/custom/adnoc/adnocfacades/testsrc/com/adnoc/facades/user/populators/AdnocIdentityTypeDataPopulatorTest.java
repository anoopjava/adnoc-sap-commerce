package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.IdentityTypeData;
import com.adnoc.service.enums.IdentityType;
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
public class AdnocIdentityTypeDataPopulatorTest {

    @Mock
    private TypeService typeService;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private IdentityType identityType;

    @Mock
    private EnumerationValueModel enumValueModel;

    @InjectMocks
    private AdnocIdentityTypeDataPopulator populator;

    @Test
    public void testWithValidIdentityType() {

        Mockito.when(identityType.getCode()).thenReturn("FS0001");
        Mockito.when(typeService.getEnumerationValue(identityType)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Federal Service");

        IdentityTypeData target = new IdentityTypeData();

        populator.populate(identityType, target);

        assertEquals("FS0001", target.getCode());
        assertEquals("Federal Service", target.getName());
    }

    @Test
    public void testWithNullIdentityType() {
        IdentityType defaultIdentityType = Mockito.mock(IdentityType.class);

        Mockito.when(enumerationService.getEnumerationValue(IdentityType.class, "FS0001"))
                .thenReturn(defaultIdentityType);
        Mockito.when(defaultIdentityType.getCode()).thenReturn("FS0001");
        Mockito.when(typeService.getEnumerationValue(defaultIdentityType)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("National ID");

        IdentityTypeData target = new IdentityTypeData();
        populator.populate(null, target);

        assertEquals("FS0001", target.getCode());
        assertEquals("National ID", target.getName());
    }
}
