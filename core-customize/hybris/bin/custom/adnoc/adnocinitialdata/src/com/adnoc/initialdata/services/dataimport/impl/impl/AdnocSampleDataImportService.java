/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.initialdata.services.dataimport.impl.impl;

import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.core.initialization.SystemSetupContext;


/**
 * Implementation to handle specific Sample Data Import services to Adnoc.
 */
public class AdnocSampleDataImportService extends SampleDataImportService
{

    /**
     * Imports the data related to Commerce Org.
     *
     * @param context the context used.
     */
    public void importCommerceOrgData(final SystemSetupContext context)
    {
        final String extensionName = context.getExtensionName();

        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/common-addon-extra.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/cms-content.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/solr.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/commerceorg/user-groups.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/commerceorg/adnoc-user-groups.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/essentialdata_documenttype.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/accountsummary/documents.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/backoffice/customersupport/customersupport-access-rights.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/cronjobs.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/quote-buyer-process.impex", extensionName), false);
    }

    /**
     * Imports the data related to Inbound Integration.
     *
     * @param context the context used.
     */
    public void importInboundIntegrationData(final SystemSetupContext context)
    {
        final String extensionName = context.getExtensionName();
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/InboundB2BUnit.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/InboundB2BCustomer.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/InboundOAuth2Cred.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/adnocconfig.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/InboundProduct.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/InboundPriceRow.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/inbound/adnoc_inbound_csticket.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/removalBookingLineEntry.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/InboundB2BDocument.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/InboundOMMOrder.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/AdnocStockInbound.impex", extensionName), false);
    }

    /**
     * Imports the data related to Outbound Integration.
     *
     * @param context the context used.
     */
    public void importOutboundIntegrationData(final SystemSetupContext context)
    {
        final String extensionName = context.getExtensionName();
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/outboundDestination.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/OutboundOMMOrder.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/AdnocSoldToOutboundB2BRegistration.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/AdnocPayerOutboundB2BUnitRegistration.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/AdnocShipToOutboundB2BUnitRegistration.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/adnocOutboundB2BCustomer.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/outbound/adnoc_outbound_csticket.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/outbound/adnoc_outbound_csticket_tos4.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/OutboundCancelOMMOrder.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/AdnocOutboundReturnOrderIntegrationObject.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/AdnocOutboundOverduePaymentTransaction.impex", extensionName), false);
        getSetupImpexService().importImpexFile(String.format("/%s/import/coredata/common/AdnocOutboundQuote.impex", extensionName), false);
    }

    /**
     * Imports the data related to Dynamic Process Definition.
     *
     * @param context the context used.
     */
    public void importOtherData(final SystemSetupContext context)
    {
        final String extensionName = context.getExtensionName();
        getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/dynamicprocessdefinition/dynamicprocess-registrationprocess.impex", extensionName), false);
    }
}

