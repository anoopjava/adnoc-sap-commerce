package com.adnoc.controllers;

import com.adnoc.facades.store.AdnocStoreFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceDataList;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceListWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/{baseSiteId}")
@ApiVersion("v2")
@Tag(name = "Adnoc Stores")
public class AdnocStoresController extends AdnocBaseController
{
    private static final Logger LOG = LogManager.getLogger(AdnocStoresController.class);
    @Resource(name = "dataMapper")
    protected DataMapper dataMapper;
    @Resource(name = "adnocStoreFacade")
    private AdnocStoreFacade adnocStoreFacade;

    @GetMapping(value = "/pickup-store/{productCode}")
    @ApiBaseSiteIdParam
    @Operation(summary = "Get pickup points for a product")
    public PointOfServiceListWsDTO getPickupPointsForProduct(
            @Parameter(description = "Product identifier.", required = true) @PathVariable final String productCode)
    {
        LOG.info("processing pickup points for productCode={}", productCode);
        final List<PointOfServiceData> posData = adnocStoreFacade.getEligiblePickupPOSForBaseStore(productCode);
        final PointOfServiceDataList pointOfServiceDataList = new PointOfServiceDataList();
        pointOfServiceDataList.setPointOfServices(posData);
        return dataMapper.map(pointOfServiceDataList, PointOfServiceListWsDTO.class, AdnocBaseController.DEFAULT_FIELD_SET);
    }

}
