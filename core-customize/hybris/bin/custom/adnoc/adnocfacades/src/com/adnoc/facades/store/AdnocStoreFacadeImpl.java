package com.adnoc.facades.store;

import com.adnoc.service.storeservice.AdnocStoreService;
import de.hybris.platform.commercefacades.storefinder.impl.DefaultStoreFinderFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AdnocStoreFacadeImpl extends DefaultStoreFinderFacade implements AdnocStoreFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocStoreFacadeImpl.class);
    private AdnocStoreService adnocStoreService;

    @Override
    public List<PointOfServiceData> getEligiblePickupPOSForBaseStore(final String productCode)
    {
        LOG.info("appEvent=AdnocStoreFacade, get pickup POS for base store method called..");
        final List<PointOfServiceModel> posModels = getAdnocStoreService().getEligiblePickupPOSForBaseStore(productCode);
        LOG.info("appEvent=EligiblePickupPOSForBaseStore, getting posmodels with value={}.", posModels);
        return getPointOfServiceConverter().convertAll(posModels);
    }

    protected AdnocStoreService getAdnocStoreService()
    {
        return adnocStoreService;
    }

    public void setAdnocStoreService(final AdnocStoreService adnocStoreService)
    {
        this.adnocStoreService = adnocStoreService;
    }
}
