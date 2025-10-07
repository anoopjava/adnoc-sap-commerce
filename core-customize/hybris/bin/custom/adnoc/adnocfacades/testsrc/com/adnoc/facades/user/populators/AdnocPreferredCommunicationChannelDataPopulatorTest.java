package com.adnoc.facades.user.populators;

import com.adnoc.facades.user.data.PreferredCommunicationChannelData;
import com.adnoc.service.enums.PreferredCommunicationChannel;
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
public class AdnocPreferredCommunicationChannelDataPopulatorTest {

    @Mock
    private TypeService typeService;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private PreferredCommunicationChannel channel;

    @Mock
    private PreferredCommunicationChannel defaultChannel;

    @Mock
    private EnumerationValueModel enumValueModel;

    @InjectMocks
    private AdnocPreferredCommunicationChannelDataPopulator populator;

    @Test
    public void testPopulateWithValidChannel() {

        Mockito.when(channel.getCode()).thenReturn("EMAIL");
        Mockito.when(typeService.getEnumerationValue(channel)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Email");

        PreferredCommunicationChannelData target = new PreferredCommunicationChannelData();

        populator.populate(channel, target);

        assertEquals("EMAIL", target.getCode());
        assertEquals("Email", target.getName());
    }

    @Test
    public void testPopulateWithNullChannel() {

        Mockito.when(enumerationService.getEnumerationValue(PreferredCommunicationChannel.class, "INT"))
                .thenReturn(defaultChannel);
        Mockito.when(defaultChannel.getCode()).thenReturn("INT");
        Mockito.when(typeService.getEnumerationValue(defaultChannel)).thenReturn(enumValueModel);
        Mockito.when(enumValueModel.getName()).thenReturn("Internal");


        PreferredCommunicationChannelData target = new PreferredCommunicationChannelData();

        populator.populate(null, target);

        assertEquals("INT", target.getCode());
        assertEquals("Internal", target.getName());
    }
}
