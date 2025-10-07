package com.adnoc.service.contributor;

import com.adnoc.service.enums.AdnocPartnerRoles;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AdnocB2BPartnerContributorTest
{
    @Spy
    private AdnocB2BPartnerContributor adnocB2BPartnerContributor;
    @Mock
    private B2BUnitService b2bUnitService;

    @Mock
    private OrderModel order;

    @Mock
    private B2BUnitModel unit;
    @Mock
    private CompanyModel rootUnit;
    @Mock
    private B2BCustomerModel b2bCustomer;

    @Before
    public void setup()
    {
        MockitoAnnotations.openMocks(this);
        Mockito.when(adnocB2BPartnerContributor.getB2bUnitService()).thenReturn(b2bUnitService);
        Mockito.when(b2bUnitService.getRootUnit(Mockito.any())).thenReturn(rootUnit);
        Mockito.when(rootUnit.getUid()).thenReturn("root123");
        Mockito.when(order.getUnit()).thenReturn(unit);
        Mockito.when(unit.getUid()).thenReturn("payer123");
        Mockito.when(order.getCode()).thenReturn("ORDER001");
        Mockito.when(order.getUser()).thenReturn(b2bCustomer);
        Mockito.when(b2bCustomer.getCustomerID()).thenReturn("customer123");

    }

    @Test
    public void testCreateB2BRows()
    {
        final List<Map<String, Object>> result = adnocB2BPartnerContributor.createB2BRows(order);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        final Map<String, Object> payerRow = result.get(result.size() - 1);
        assertEquals("ORDER001", payerRow.get(OrderCsvColumns.ORDER_ID));
        assertEquals("payer123", payerRow.get(PartnerCsvColumns.PARTNER_CODE));
        assertEquals(AdnocPartnerRoles.PAYER_TO.getCode(), payerRow.get(PartnerCsvColumns.PARTNER_ROLE_CODE));
    }
}
