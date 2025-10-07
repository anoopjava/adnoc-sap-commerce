package com.adnoc.facades.company.converters.populators;

import com.adnoc.service.category.AdnocCategoryService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class AdnocB2BUnitNodePopulatorTest {

    @Test
    public void testPopulateWithSalesOrgAndValidCategory() {
        B2BUnitModel source = mock(B2BUnitModel.class);
        B2BUnitNodeData target = new B2BUnitNodeData();
        SAPSalesOrganizationModel salesOrg = mock(SAPSalesOrganizationModel.class);
        CategoryModel categoryModel = mock(CategoryModel.class);
        AdnocCategoryService adnocCategoryService = mock(AdnocCategoryService.class);

        // Mock required for parent behavior
        @SuppressWarnings("unchecked")
        B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService = mock(B2BUnitService.class);

        // Define behavior
        Mockito.when(source.getSalesOrg()).thenReturn(salesOrg);
        Mockito.when(salesOrg.getDivision()).thenReturn("DIV001");
        Mockito.when(adnocCategoryService.getCategoryForDivision("DIV001")).thenReturn(categoryModel);
        Mockito.when(categoryModel.getName()).thenReturn("Industrial Lubricants");

        // Create instance and inject mock
        AdnocB2BUnitNodePopulator populator = new AdnocB2BUnitNodePopulator();
        populator.setAdnocCategoryService(adnocCategoryService);
        populator.setB2BUnitService(b2BUnitService); // necessary to avoid NPE from parent


        populator.populate(source, target);

        assertEquals("Industrial Lubricants", target.getLob());
        verify(adnocCategoryService).getCategoryForDivision("DIV001");
        verify(categoryModel).getName();
    }


    @Test
    public void testPopulateWithNullSalesOrg() {
        B2BUnitModel source = mock(B2BUnitModel.class);
        B2BUnitNodeData target = new B2BUnitNodeData();
        AdnocCategoryService adnocCategoryService = mock(AdnocCategoryService.class);

        Mockito.when(source.getSalesOrg()).thenReturn(null);

        // Mock required for parent behavior
        @SuppressWarnings("unchecked")
        B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService = mock(B2BUnitService.class);

        AdnocB2BUnitNodePopulator populator = new AdnocB2BUnitNodePopulator();
        populator.setAdnocCategoryService(adnocCategoryService);
        populator.setB2BUnitService(b2BUnitService);

        populator.populate(source, target);

        assertNull(target.getLob(), "LOB should not be set when SalesOrg is null.");
    }

    @Test
    public void testPopulateWithNullCategory() {
        B2BUnitModel source = mock(B2BUnitModel.class);
        B2BUnitNodeData target = new B2BUnitNodeData();
        SAPSalesOrganizationModel salesOrg = mock(SAPSalesOrganizationModel.class);
        AdnocCategoryService adnocCategoryService = mock(AdnocCategoryService.class);

        Mockito.when(source.getSalesOrg()).thenReturn(salesOrg);
        Mockito.when(salesOrg.getDivision()).thenReturn("DIV002");
        @SuppressWarnings("unchecked")
        B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService = mock(B2BUnitService.class);


        Mockito.when(adnocCategoryService.getCategoryForDivision("DIV002")).thenReturn(null);

        AdnocB2BUnitNodePopulator populator = new AdnocB2BUnitNodePopulator();

        populator.setAdnocCategoryService(adnocCategoryService);
        populator.setB2BUnitService(b2BUnitService);

        populator.populate(source, target);

        assertNull(target.getLob(), "LOB should not be set when CategoryModel is null.");
    }
}
