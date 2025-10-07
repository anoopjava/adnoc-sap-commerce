package com.adnoc.service.contributor;

import com.adnoc.service.enums.AdnocPartnerRoles;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerRoles;
import de.hybris.platform.sap.orderexchange.constants.SaporderexchangeConstants;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdnocB2BQuotePartnerContributor
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BQuotePartnerContributor.class);

    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    public List<Map<String, Object>> createB2BRows(final QuoteModel quoteModel)
    {
        LOG.info("appEvent=AdnocCreateB2BRows, Creating B2B partner rows for quote: {}", quoteModel.getCode());
        final Map<String, Object> soldToRow = createPartnerRow(quoteModel, PartnerRoles.SOLD_TO, soldToFromOrder(quoteModel));
        final Map<String, Object> shipToRow = createAddressRow(quoteModel, PartnerRoles.SHIP_TO, SaporderexchangeConstants.ADDRESS_ONE);
        final Map<String, Object> payerRow = createAdnocPartnerRow(quoteModel, AdnocPartnerRoles.PAYER_TO, payerContactFromOrder(quoteModel));
        final List<Map<String, Object>> result = new ArrayList<>();
        if (!MapUtils.isEmpty(soldToRow))
        {
            result.add(soldToRow);
        }
        if (!MapUtils.isEmpty(shipToRow))
        {
            result.add(shipToRow);
        }
        if (MapUtils.isNotEmpty(payerRow))
        {
            result.add(payerRow);
        }
        return result;
    }

    private Map<String, Object> createAddressRow(final QuoteModel quoteModel, final PartnerRoles partnerRole, final String addressNumber)
    {
        final AddressModel address = addressForPartnerRole(quoteModel, partnerRole);
        Map<String, Object> row = null;
        if (address != null)
        {
            row = new HashMap<>();
            row.put(OrderCsvColumns.ORDER_ID, quoteModel.getCode());
            row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, partnerRole.getCode());
            final String sapCustomer = address.getSapCustomerID();
            if (sapCustomer == null || sapCustomer.isEmpty())
            {
                row.put(PartnerCsvColumns.PARTNER_CODE, soldToFromOrder(quoteModel));
                row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, addressNumber);
            }
            else
            {
                row.put(PartnerCsvColumns.PARTNER_CODE, sapCustomer);
                row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, "");
            }
        }
        return row;
    }

    protected AddressModel addressForPartnerRole(final QuoteModel quoteModel, final PartnerRoles partnerRole)
    {
        AddressModel result = null;
        if (partnerRole == PartnerRoles.SHIP_TO)
        {
            result = quoteModel.getDeliveryAddress();
        }
        return result;
    }

    private String payerContactFromOrder(final QuoteModel quoteModel)
    {
        final B2BUnitModel unit = quoteModel.getUnit();
        return unit.getUid();
    }

    private Map<String, Object> createAdnocPartnerRow(final QuoteModel quoteModel, final AdnocPartnerRoles adnocPartnerRoles, final String partnerId)
    {
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, quoteModel.getCode());
        row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, adnocPartnerRoles.getCode());
        row.put(PartnerCsvColumns.PARTNER_CODE, partnerId);
        row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, "");
        return row;
    }

    protected String soldToFromOrder(final QuoteModel quoteModel)
    {
        final CompanyModel rootUnit = getB2bUnitService().getRootUnit(quoteModel.getUnit());
        return rootUnit.getUid();
    }

    protected Map<String, Object> createPartnerRow(final QuoteModel quoteModel, final PartnerRoles partnerRole, final String partnerId)
    {
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, quoteModel.getCode());
        row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, partnerRole.getCode());
        row.put(PartnerCsvColumns.PARTNER_CODE, partnerId);
        row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, "");
        return row;
    }

    protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
    {
        return b2bUnitService;
    }

    public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
    {
        this.b2bUnitService = b2bUnitService;
    }
}
