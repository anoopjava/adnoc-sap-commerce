/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.initialdata.setup;

import com.adnoc.initialdata.constants.AdnocInitialDataConstants;
import com.adnoc.initialdata.services.dataimport.impl.impl.AdnocSampleDataImportService;
import com.adnoc.initialdata.services.dataimport.impl.impl.AdnocSpaSampleDataImportService;
import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.setup.events.CoreDataImportedEvent;
import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.util.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@SystemSetup(extension = AdnocInitialDataConstants.EXTENSIONNAME)
public class AdnocStoreSystemSetup extends AbstractSystemSetup
{
    private static final Logger LOG = LogManager.getLogger(AdnocStoreSystemSetup.class);

    public static final String ADNOC_PRODUCT_CATALOG = "adnoc";
    public static final String ADNOC_CONTENT_CATALOG = "adnoc";
    public static final String ADNOC_STORE = "adnoc";
    public static final String ADNOC_SITE = "adnoc";

    private static final String IMPORT_CORE_DATA = "importCoreData";
    private static final String IMPORT_SAMPLE_DATA = "importSampleData";
    private static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";
    private static final String ADNOC_PATCH_RELEASE_BUILD_NAME = "adnoc.patch.release.build.name";
    private static final String PATCH_RELEASE_PATH = "/adnocinitialdata/import/patch/release/";
    private static final String BUILD_NAME = Config.getString(ADNOC_PATCH_RELEASE_BUILD_NAME, StringUtils.EMPTY);

    private CoreDataImportService coreDataImportService;
    private AdnocSampleDataImportService adnocSampleDataImportService;
    private AdnocSpaSampleDataImportService adnocSpaSampleDataImportService;

    @SystemSetupParameterMethod
    @Override
    public List<SystemSetupParameter> getInitializationOptions()
    {
        final List<SystemSetupParameter> params = new ArrayList<>();

        params.add(createBooleanSystemSetupParameter(AdnocStoreSystemSetup.IMPORT_CORE_DATA, "Import Core Data", true));
        params.add(createBooleanSystemSetupParameter(AdnocStoreSystemSetup.IMPORT_SAMPLE_DATA, "Import Sample Data", true));
        params.add(createBooleanSystemSetupParameter(AdnocStoreSystemSetup.ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs", true));

        return params;
    }

    /**
     * This method will be called during the system initialization.
     *
     * @param context the context provides the selected parameters and values
     */
    @SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.INIT)
    public void createProjectDataInit(final SystemSetupContext context)
    {
        LOG.info("Inside createProjectData()::AdnocStoreSystemSetup");
        final List<ImportData> importData = new ArrayList<>();

        final ImportData adnocImportData = new ImportData();

        adnocImportData.setProductCatalogName(AdnocStoreSystemSetup.ADNOC_PRODUCT_CATALOG);
        adnocImportData.setContentCatalogNames(List.of(AdnocStoreSystemSetup.ADNOC_CONTENT_CATALOG));
        adnocImportData.setStoreNames(List.of(AdnocStoreSystemSetup.ADNOC_STORE));
        adnocImportData.setSiteNames(List.of(AdnocStoreSystemSetup.ADNOC_SITE));
        importData.add(adnocImportData);

        getCoreDataImportService().execute(this, context, importData);
        getEventService().publishEvent(new CoreDataImportedEvent(context, importData));

        getAdnocSampleDataImportService().execute(this, context, importData);
        getAdnocSampleDataImportService().importCommerceOrgData(context);
        getAdnocSampleDataImportService().importInboundIntegrationData(context);
        getAdnocSampleDataImportService().importOutboundIntegrationData(context);
        getAdnocSampleDataImportService().importOtherData(context);
        getEventService().publishEvent(new SampleDataImportedEvent(context, importData));
        //Spartacus sampledata import
        final String importRoot = "/spartacussampledata/import";
        getAdnocSpaSampleDataImportService().adnocSpaSampleDataImport(context, importRoot, ADNOC_CONTENT_CATALOG);
    }


    /**
     * This method will be called during the system update.
     *
     * @param context the context provides the selected parameters and values
     */
    @SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.UPDATE)
    public void createProjectDataUpdate(final SystemSetupContext context)
    {
        LOG.info("appEvent=AdnocSystemSetupUpdated, START importing essential data files");
        importImpexFile(context, "/adnocinitialdata/import/coredata/common/essential-data.impex");
        LOG.info("appEvent=AdnocSystemSetupUpdated, END importing essential data files");

        LOG.info("appEvent=AdnocSystemSetupUpdated, START the delta impex execution for build {}.", BUILD_NAME);
        final String patchFullPath = PATCH_RELEASE_PATH + BUILD_NAME;
        LOG.info("appEvent=AdnocSystemSetupUpdated, loading impexes from {} to execute for build {}.", patchFullPath, BUILD_NAME);
        final URL resource = getClass().getResource(patchFullPath);
        try (Stream<Path> fileStream = Files.list(Paths.get(Objects.requireNonNull(resource).toURI())))
        {
            fileStream
                    .filter(file -> file.getFileName().toString().endsWith(".impex"))
                    .sorted()
                    .forEach(file -> importImpexFile(context, patchFullPath + File.separator + file.getFileName().toString()));
        }
        catch (final URISyntaxException | IOException exception)
        {
            LOG.error("Failed to import due to {}", exception.getMessage());
        }
        catch (final Exception exception)
        {
            LOG.error("Unexpected error importing {}", exception.getMessage());
        }
        LOG.info("appEvent=AdnocSystemSetupUpdated, END the delta impex execution for build {}.", BUILD_NAME);
    }

    protected CoreDataImportService getCoreDataImportService()
    {
        return coreDataImportService;
    }

    public void setCoreDataImportService(final CoreDataImportService coreDataImportService)
    {
        this.coreDataImportService = coreDataImportService;
    }

    protected AdnocSampleDataImportService getAdnocSampleDataImportService()
    {
        return adnocSampleDataImportService;
    }

    public void setAdnocSampleDataImportService(final AdnocSampleDataImportService adnocSampleDataImportService)
    {
        this.adnocSampleDataImportService = adnocSampleDataImportService;
    }

    protected AdnocSpaSampleDataImportService getAdnocSpaSampleDataImportService()
    {
        return adnocSpaSampleDataImportService;
    }

    public void setAdnocSpaSampleDataImportService(final AdnocSpaSampleDataImportService adnocSpaSampleDataImportService)
    {
        this.adnocSpaSampleDataImportService = adnocSpaSampleDataImportService;
    }
}
