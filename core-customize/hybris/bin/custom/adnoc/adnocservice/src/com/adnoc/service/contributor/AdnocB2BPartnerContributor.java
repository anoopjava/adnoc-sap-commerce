package com.adnoc.service.contributor;


import com.adnoc.service.enums.AdnocPartnerRoles;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchangeb2b.outbound.impl.DefaultB2BPartnerContributor;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdnocB2BPartnerContributor extends DefaultB2BPartnerContributor
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BPartnerContributor.class);

    @Override
    protected List<Map<String, Object>> createB2BRows(final OrderModel order)
    {
        LOG.info("appEvent=AdnocCreateB2BRows, Creating B2B partner rows for order: {}", order.getCode());
        final List<Map<String, Object>> result = super.createB2BRows(order);
        final Map<String, Object> payerRow = createAdnocPartnerRow(order, AdnocPartnerRoles.PAYER_TO, payerContactFromOrder(order));
        if (MapUtils.isNotEmpty(payerRow))
        {
            result.add(payerRow);
        }
        return result;
    }

    private String payerContactFromOrder(final OrderModel order)
    {
        final B2BUnitModel unit = order.getUnit();
        return unit.getUid();
    }

    private Map<String, Object> createAdnocPartnerRow(final OrderModel order, final AdnocPartnerRoles adnocPartnerRoles, final String partnerId)
    {
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, order.getCode());
        row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, adnocPartnerRoles.getCode());
        row.put(PartnerCsvColumns.PARTNER_CODE, partnerId);
        row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, "");
        return row;
    }
}
