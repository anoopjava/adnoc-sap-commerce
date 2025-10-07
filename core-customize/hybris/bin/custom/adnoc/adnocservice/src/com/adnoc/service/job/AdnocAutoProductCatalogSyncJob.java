package com.adnoc.service.job;

import com.adnoc.service.category.AdnocCategoryService;
import com.adnoc.service.config.AdnocConfigService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.adnoc.service.constants.AdnocserviceConstants.*;

public class AdnocAutoProductCatalogSyncJob extends AbstractJobPerformable<CronJobModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocAutoProductCatalogSyncJob.class);

    public static final String ADNOC_AUTO_PRODUCT_CATALOGSYNC_DIVISION = "adnocAutoProductCatalogSyncDivision";
    public static final String ADNOC_AUTO_PRODUCT_CATALOGSYNC_DIVISION_DEFAULT_VALUE = "21,31";

    private CatalogSynchronizationService catalogSynchronizationService;
    private CatalogVersionService catalogVersionService;
    private SyncConfig syncConfig;
    private AdnocConfigService adnocConfigService;
    private AdnocCategoryService adnocCategoryService;
    private ProductService productService;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel)
    {
        LOG.info("appEvent=AdnocAutoProductCatalogSyncJob, Started.");
        final CatalogVersionModel sourceCatalogVersion = getCatalogVersionService().getCatalogVersion(ADNOC_PRODUCT_CATALOG, STAGED);
        final CatalogVersionModel targetCatalogVersion = getCatalogVersionService().getCatalogVersion(ADNOC_PRODUCT_CATALOG, ONLINE);
        final SyncItemJobModel syncItemJobModel = getCatalogSynchronizationService().getSyncJob(sourceCatalogVersion, targetCatalogVersion, null);

        final String productCatalogsyncDivisionStr = getAdnocConfigService().getAdnocConfigValue(ADNOC_AUTO_PRODUCT_CATALOGSYNC_DIVISION, ADNOC_AUTO_PRODUCT_CATALOGSYNC_DIVISION_DEFAULT_VALUE);
        LOG.debug("appEvent=AdnocAutoProductCatalogSyncJob, found configured division={} config for Product Catalog Sync.", productCatalogsyncDivisionStr);
        final Set<String> productCatalogsyncDivisions = Arrays.stream(productCatalogsyncDivisionStr.split(",")).map(String::trim).collect(Collectors.toSet());
        final Set<CategoryModel> stagedCategoryModels = productCatalogsyncDivisions.stream()
                .map(division -> getAdnocCategoryService().getCategoryForDivision(division))
                .filter(categoryModel -> Objects.nonNull(categoryModel)
                        && Objects.equals(sourceCatalogVersion, categoryModel.getCatalogVersion())).collect(Collectors.toSet());
       LOG.debug("appEvent=AdnocAutoProductCatalogSyncJob, found {} categories for Product Catalog Sync.", CollectionUtils.size(stagedCategoryModels));
                final List<ItemModel> productModels = CollectionUtils.isEmpty(stagedCategoryModels) ? List.of()
                        : stagedCategoryModels.stream()
                                .flatMap(categoryModel -> getProductService().getProductsForCategory(categoryModel).stream())
                                .collect(Collectors.toList());
        LOG.debug("appEvent=AdnocAutoProductCatalogSyncJob, found {} productModels for Product Catalog Sync.", CollectionUtils.size(productModels));

        LOG.info("appEvent=AdnocAutoProductCatalogSyncJob, Product Catalog Sync Started for {}.",
                        Stream.ofNullable(productModels).filter(Objects::nonNull).map(itemModel -> ((ProductModel) itemModel).getCode()).toList());

        getCatalogSynchronizationService().performSynchronization(productModels, syncItemJobModel, syncConfig);
        LOG.info("appEvent=AdnocAutoProductCatalogSyncJob, End.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    @Override
    public boolean isAbortable()
    {
        LOG.info("appEvent=AdnocB2BUnitRelationMappingJob, Aborted.");
        return true;
    }

    protected CatalogSynchronizationService getCatalogSynchronizationService()
    {
        return catalogSynchronizationService;
    }

    public void setCatalogSynchronizationService(final CatalogSynchronizationService catalogSynchronizationService)
    {
        this.catalogSynchronizationService = catalogSynchronizationService;
    }

    protected CatalogVersionService getCatalogVersionService()
    {
        return catalogVersionService;
    }

    public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
    {
        this.catalogVersionService = catalogVersionService;
    }

    protected SyncConfig getSyncConfig()
    {
        return syncConfig;
    }

    public void setSyncConfig(final SyncConfig syncConfig)
    {
        this.syncConfig = syncConfig;
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    protected AdnocCategoryService getAdnocCategoryService()
    {
        return adnocCategoryService;
    }

    public void setAdnocCategoryService(final AdnocCategoryService adnocCategoryService)
    {
        this.adnocCategoryService = adnocCategoryService;
    }

    protected ProductService getProductService()
    {
        return productService;
    }

    public void setProductService(final ProductService productService)
    {
        this.productService = productService;
    }
}
