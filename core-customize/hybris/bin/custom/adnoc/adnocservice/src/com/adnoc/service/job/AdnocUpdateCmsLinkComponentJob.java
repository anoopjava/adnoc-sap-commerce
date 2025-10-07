package com.adnoc.service.job;

import com.adnoc.service.component.AdnocLinkComponentDao;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;
import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.adnoc.service.constants.AdnocserviceConstants.*;

public class AdnocUpdateCmsLinkComponentJob extends AbstractJobPerformable<CronJobModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocUpdateCmsLinkComponentJob.class);
    public static final String PDF_FILE_PATH = "adnoc.customer.guide";
    public static final String CUSTOMERGUIDE_UID = "adnoc.customer.guide.uid";

    private CatalogVersionService catalogVersionService;
    private MediaService mediaService;
    private CatalogSynchronizationService catalogSynchronizationService;
    private AdnocLinkComponentDao adnocLinkComponentDao;
    private SyncConfig syncConfig;
    private ConfigurationService configurationService;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel)
    {
        final Configuration configuration = getConfigurationService().getConfiguration();
        LOG.info("appEvent=AdnocUpdateCmsLinkComponentJob, Started.");
        if (cronJobModel.getStatus().equals(CronJobStatus.FINISHED))
        {
            LOG.info("appEvent=AdnocUpdateCmsLinkComponentJob, Job already executed once...");
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        }
        final CatalogVersionModel stagedVersion = getCatalogVersionService().getCatalogVersion(ADNOC_CONTENT_CATALOG, STAGED);
        final CatalogVersionModel onlineVersion = getCatalogVersionService().getCatalogVersion(ADNOC_CONTENT_CATALOG, ONLINE);
        final MediaModel media = new MediaModel();
        media.setCode(String.valueOf(UUID.randomUUID()));
        media.setCatalogVersion(stagedVersion);
        media.setRealFileName("CustomerGuide.pdf");
        media.setMime("application/pdf");
        modelService.save(media);
        try (final InputStream is = getClass().getResourceAsStream(configuration.getString(PDF_FILE_PATH)))
        {
            mediaService.setStreamForMedia(media, is);
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Error setting stream for media", e);
        } finally
        {
            LOG.info("Finished attempting to set stream for media: " + media.getCode());
        }
        final CMSLinkComponentModel cmsLinkComponent = getAdnocLinkComponentDao()
                .findCMSLinkComponentByUidAndCatalogVersion(configuration.getString(CUSTOMERGUIDE_UID), stagedVersion);
        final String mediaUrl = media.getURL();
        cmsLinkComponent.setUrl(mediaUrl);
        modelService.save(cmsLinkComponent);

        LOG.info("CMSLinkComponent URL updated successfully to {}", mediaUrl);
        final List<ItemModel> itemsToSync = Arrays.asList(media, cmsLinkComponent);
        final SyncItemJobModel syncJob = getCatalogSynchronizationService().getSyncJob(stagedVersion, onlineVersion, null);
        getCatalogSynchronizationService().performSynchronization(itemsToSync, syncJob, syncConfig);
        LOG.info("Synchronized Media and CMSLinkComponent to Online catalog version");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    @Override
    public boolean isAbortable()
    {
        return true;
    }

    protected CatalogVersionService getCatalogVersionService()
    {
        return catalogVersionService;
    }

    public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
    {
        this.catalogVersionService = catalogVersionService;
    }


    protected MediaService getMediaService()
    {
        return mediaService;
    }

    public void setMediaService(final MediaService mediaService)
    {
        this.mediaService = mediaService;
    }

    protected CatalogSynchronizationService getCatalogSynchronizationService()
    {
        return catalogSynchronizationService;
    }

    public void setCatalogSynchronizationService(final CatalogSynchronizationService catalogSynchronizationService)
    {
        this.catalogSynchronizationService = catalogSynchronizationService;
    }

    protected SyncConfig getSyncConfig()
    {
        return syncConfig;
    }

    public void setSyncConfig(final SyncConfig syncConfig)
    {
        this.syncConfig = syncConfig;
    }

    protected AdnocLinkComponentDao getAdnocLinkComponentDao()
    {
        return adnocLinkComponentDao;
    }

    public void setAdnocLinkComponentDao(final AdnocLinkComponentDao adnocLinkComponentDao)
    {
        this.adnocLinkComponentDao = adnocLinkComponentDao;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }
}
