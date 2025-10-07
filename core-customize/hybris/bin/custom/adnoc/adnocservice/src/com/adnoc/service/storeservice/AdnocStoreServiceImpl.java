package com.adnoc.service.storeservice;

import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.commerceservices.storefinder.impl.DefaultStoreFinderService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AdnocStoreServiceImpl extends DefaultStoreFinderService implements AdnocStoreService
{
    private static final Logger LOG = LogManager.getLogger(AdnocStoreServiceImpl.class);
    private AdnocStoreDao adnocStoreDao;
    private BaseStoreService baseStoreService;
    private StockService stockService;
    private ProductService productService;

    @Override
    public PointOfServiceModel getPointOfServicePk(final String pk)
    {
        return getAdnocStoreDao().findPointOfServiceByPk(pk);
    }

    @Override
    public List<PointOfServiceModel> getEligiblePickupPOSForBaseStore(final String productCode)
    {
        LOG.info("appEvent=AdnocStoreService, pickup pos for basestore method called..");
        final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
        final List<PointOfServiceModel> allPOS = baseStore.getPointsOfService();
        LOG.info("appEvent=AdnocStoreService,Fetching all POS for the BaseStore:{} ", allPOS);
        final Predicate<PointOfServiceModel> pointOfServiceModelPredicate = pos -> Objects.equals(PointOfServiceTypeEnum.STORE, pos.getType());
        final Predicate<PointOfServiceModel> pointOfServiceModelPredicateWarehouse = pos -> {
            final Collection<WarehouseModel> pickupWarehouses = getPickupWarehouses(pos);
            return pickupWarehouses.stream().anyMatch(warehouse -> {
                final StockLevelModel stockLevel = getStockService().getStockLevel(getProductService().getProductForCode(productCode), warehouse);
                return Objects.nonNull(stockLevel) && stockLevel.getAvailable() > 0;
            });
        };
        return allPOS.stream()
                .filter(pointOfServiceModelPredicate.and(pointOfServiceModelPredicateWarehouse))
                .collect(Collectors.toList());
    }


    private Collection<WarehouseModel> getPickupWarehouses(final PointOfServiceModel pos)
    {
        if (CollectionUtils.isEmpty(pos.getWarehouses()))
        {
            LOG.info("appEvent=AdnocStoreService, No warehouses linked to POS: {}", pos.getName());
            return Collections.emptyList();
        }
        return pos.getWarehouses().stream().filter(warehouseModel -> warehouseModel.getDeliveryModes().stream().
                anyMatch(deliveryModeModel -> (deliveryModeModel instanceof PickUpDeliveryModeModel)
                        || StringUtils.equalsIgnoreCase("pick", deliveryModeModel.getCode()))).collect(Collectors.toSet());
    }


    protected StockService getStockService()
    {
        return stockService;
    }

    public void setStockService(final StockService stockService)
    {
        this.stockService = stockService;
    }

    protected BaseStoreService getBaseStoreService()
    {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService)
    {
        this.baseStoreService = baseStoreService;
    }

    protected AdnocStoreDao getAdnocStoreDao()
    {
        return adnocStoreDao;
    }

    public void setAdnocStoreDao(final AdnocStoreDao adnocStoreDao)
    {
        this.adnocStoreDao = adnocStoreDao;
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
