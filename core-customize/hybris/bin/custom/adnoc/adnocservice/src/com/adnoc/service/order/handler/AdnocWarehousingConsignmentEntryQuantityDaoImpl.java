package com.adnoc.service.order.handler;

import de.hybris.platform.basecommerce.constants.GeneratedBasecommerceConstants;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.warehousing.daos.impl.DefaultWarehousingConsignmentEntryQuantityDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AdnocWarehousingConsignmentEntryQuantityDaoImpl extends DefaultWarehousingConsignmentEntryQuantityDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocWarehousingConsignmentEntryQuantityDaoImpl.class);
    private static final StringBuilder shippedQuery = new StringBuilder()
            .append("SELECT SUM({consignmentEntry:shippedQuantity}) FROM {")
            .append(GeneratedBasecommerceConstants.TC.CONSIGNMENTENTRY)
            .append(" as consignmentEntry JOIN ")
            .append(GeneratedBasecommerceConstants.TC.CONSIGNMENT)
            .append(" as consignment ON {consignmentEntry:consignment}={consignment:pk}} WHERE {consignmentEntry.pk}=?consignmentEntry");

    @Override
    public Long getQuantityShipped(final ConsignmentEntryModel consignmentEntry)
    {
        LOG.debug("AppEvent=AdnocWarehousingConsignmentEntryQuantity, Getting quantity shipped for consignment entry {}", consignmentEntry.getPk());
        final Map<String, Object> params = new HashMap();
        params.put("consignmentEntry", consignmentEntry);
        return processRequestWithParams(shippedQuery.toString(), params);
    }
}