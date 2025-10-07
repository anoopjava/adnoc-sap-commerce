/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * The files in this addon are licensed under the Apache Software License, v. 2
 * except as noted otherwise in the LICENSE file.
 */
package com.adnoc.initialdata.services.dataimport.impl.impl;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.util.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * This class extends {@link AbstractSystemSetup} and specifies how to import sample data spartacus
 */
public class AdnocSpaSampleDataImportService extends AbstractSystemSetup
{
    private static final String STORES_URL = "/stores/";
    private static final String INITIALIZING_JOB_MSG = "initializing job";
    private static final String SYNCHRONIZING_MSG = "synchronizing";
    private static final String PRODUCT_CATALOGS_URL = "/productCatalogs/";
    private static final String CONTENT_CATALOGS_URL = "/contentCatalogs/";
    private static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";

    private ConfigurationService configurationService;
    private Map<String, String> additionalSampleDataImports;

    public void adnocSpaSampleDataImport(final SystemSetupContext context, final String importRoot, final String catalogName)
    {
        importSpaCatalog(context, importRoot, catalogName);
        final List<String> contentCatalogs = Collections.singletonList(catalogName);
        importStoreInitialData(context, importRoot, contentCatalogs, catalogName, contentCatalogs, true);
    }

    protected void importSpaCatalog(final SystemSetupContext context, final String importRoot, final String catalogName)
    {
        // 1- create new catalog
        importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/catalog.impex", false);
        // 2- perform some cleaning
        importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/cleaning.impex", false);
        // 3- import responsive content catalog from impex
        importContentCatalog(context, importRoot, catalogName);
        // 4- import additional sample data
        importAdditionalContentData(context, catalogName, importRoot);
        // 5- synchronize spaContentCatalog:staged->online
        synchronizeContentCatalog(context, catalogName, true);
        // 6- give permission to cmsmanager to do the sync
        importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/sync.impex", false);
        // 7- import email data
        importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/email-content.impex", false);
        // 8- solr ammendments
        importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/solr.impex", false);
    }

    protected void importContentCatalog(final SystemSetupContext context, final String importRoot, final String catalogName)
    {
        logInfo(context, "Begin importing Content Catalog [" + catalogName + "]");

        final String responsiveContentFile =
                importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/cms-responsive-content.impex";
        try (final InputStream resourceAsStream = getClass().getResourceAsStream(responsiveContentFile))
        {
            if (resourceAsStream != null)
            {
                importImpexFile(context, responsiveContentFile, false);
            } else
            {
                importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/cms-content.impex",
                        false);
            }
        } catch (final IOException e)
        {
            logError(context, "importing Content Catalog Failed. Catalog: [" + catalogName + "]", e);
        }

        // support a general script for extra uncategorized script
        importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/cms-addon-extra.impex", false);

        logInfo(context, "Done importing Content Catalog [" + catalogName + "]");
    }

    protected boolean synchronizeContentCatalog(final SystemSetupContext context, final String catalogName, final boolean sync)
    {
        logInfo(context,
                "Begin synchronizing Content Catalog [" + catalogName + "] - " + (sync ? SYNCHRONIZING_MSG : INITIALIZING_JOB_MSG));

        createContentCatalogSyncJob(context, catalogName + "ContentCatalog");

        boolean result = true;

        if (sync)
        {
            final PerformResult syncCronJobResult = executeCatalogSyncJob(context, catalogName + "ContentCatalog");
            if (isSyncRerunNeeded(syncCronJobResult))
            {
                logInfo(context, "Catalog catalog [" + catalogName + "] sync has issues.");
                result = false;
            }
        }

        logInfo(context, "Done " + (sync ? SYNCHRONIZING_MSG : INITIALIZING_JOB_MSG) + " Content Catalog [" + catalogName + "]");
        return result;
    }


    /**
     * This methods imports the additional impex files that are required by various
     * different modules. If those extension are loaded in the commerce
     * installation, then the impex file is imported <br/>
     * To enable the import the following needs to be done: <br/>
     * 1. Define an entry in the map additionalSampleDataImports via spring config
     * where the key is the extension name, and value is the impex file name <br/>
     * 2. Include the impex file which contains the required data changes within the
     * ContentCatalog folder
     *
     * @param context
     * @param catalogName
     * @param importRoot
     */
    protected void importAdditionalContentData(final SystemSetupContext context, final String catalogName,
                                               final String importRoot)
    {
        getAdditionalSampleDataImports().entrySet().forEach(extensionName -> {
            if (Utilities.getExtensionNames().contains(extensionName.getKey())) {
                importImpexFile(context,
                        importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/" + extensionName.getValue(), false);
            }
        });
    }

    protected void importStoreInitialData(final SystemSetupContext context, final String importRoot, final List<String> storeNames,
                                          final String productCatalog, final List<String> contentCatalogs, final boolean solrReindex)
    {
        synchronizeContent(context, productCatalog, contentCatalogs, true);
        processStoreNames(context, importRoot, storeNames, productCatalog, solrReindex);
    }

    protected void synchronizeContent(final SystemSetupContext context, final String productCatalog,
                                      final List<String> contentCatalogs, final boolean productSyncSuccess)
    {
        // perform content sync jobs
        for (final String contentCatalog : contentCatalogs)
        {
            synchronizeContentCatalog(context, contentCatalog, true);
        }
    }

    protected void processStoreNames(final SystemSetupContext context, final String importRoot, final List<String> storeNames,
                                     final String productCatalog, final boolean solrReindex)
    {
        for (final String storeName : storeNames)
        {
            // Load site
            importImpexFile(context, importRoot + STORES_URL + storeName + "/site.impex", false);
            // Load consents
            importImpexFile(context, importRoot + STORES_URL + storeName + "/consents.impex", false);

            if (solrReindex)
            {
                // Index product data
                logInfo(context, "Begin SOLR re-index [" + storeName + "]");
                executeSolrIndexerCronJob(storeName + "Index", true);
                logInfo(context, "Done SOLR re-index [" + storeName + "]");
            }

            if (getBooleanSystemSetupParameter(context, ACTIVATE_SOLR_CRON_JOBS))
            {
                logInfo(context, "Activating SOLR index job for [" + productCatalog + "]");
                activateSolrIndexerCronJobs(productCatalog + "Index");
            }
        }
    }

    @Override
    public List<SystemSetupParameter> getInitializationOptions()
    {
        return List.of();
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    public Map<String, String> getAdditionalSampleDataImports()
    {
        return additionalSampleDataImports;
    }

    public void setAdditionalSampleDataImports(final Map<String, String> additionalSampleDataImports)
    {
        this.additionalSampleDataImports = additionalSampleDataImports;
    }
}
