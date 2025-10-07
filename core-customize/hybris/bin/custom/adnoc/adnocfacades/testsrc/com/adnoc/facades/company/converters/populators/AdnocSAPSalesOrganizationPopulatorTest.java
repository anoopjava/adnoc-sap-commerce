package com.adnoc.facades.company.converters.populators;

import com.adnoc.facades.company.data.SAPSalesOrganizationData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class AdnocSAPSalesOrganizationPopulatorTest {

    @Test
    public void testPopulate() {
        // Arrange
        SAPSalesOrganizationModel source = mock(SAPSalesOrganizationModel.class);
        SAPSalesOrganizationData target = new SAPSalesOrganizationData();

        Mockito.when(source.getSalesOrgId()).thenReturn("SO123");
        Mockito.when(source.getSalesOrganization()).thenReturn("ADNOC SALES");
        Mockito.when(source.getDistributionChannel()).thenReturn("DC01");
        Mockito.when(source.getDivision()).thenReturn("DIV01");

        AdnocSAPSalesOrganizationPopulator populator = new AdnocSAPSalesOrganizationPopulator();

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals("SO123", target.getSalesOrgId());
        assertEquals("ADNOC SALES", target.getSalesOrganization());
        assertEquals("DC01", target.getDistributionChannel());
        assertEquals("DIV01", target.getDivision());

        verify(source).getSalesOrgId();
        verify(source).getSalesOrganization();
        verify(source).getDistributionChannel();
        verify(source).getDivision();
    }
}
