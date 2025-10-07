package com.adnoc.service.price.impl;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.exception.AdnocS4HanaException;
import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import com.adnoc.service.order.strategies.calculation.pdt.impl.converter.impl.AdnocPriceValueInfoCriteria;
import com.adnoc.service.price.AdnocPriceService;
import com.adnoc.service.price.data.AdnocPriceInfoLineItemRequestData;
import com.adnoc.service.price.data.AdnocPriceInfoRequestData;
import com.adnoc.service.price.data.AdnocPriceInfoResponseData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Objects;

public class AdnocPriceServiceImpl implements AdnocPriceService
{
    private static final Logger LOG = LogManager.getLogger(AdnocPriceServiceImpl.class);

    public static final String ADNOC_SAPPRICEINFO_GATEWAY_DESTINATION = "adnocSapPriceInfoDestination";
    public static final String ADNOC_SAPPRICEINFO_DESTINATION_TARGET = "adnoc-sappriceinfo-destination-target";
    public static final int DEFAULT_LINE_ITEM_ENTRY_NUMBER = 10;

    private AdnocRestIntegrationService adnocRestIntegrationService;
    private AdnocConfigService adnocConfigService;
    private Map<String, String> divisionOrderTypeMap;
    private AdnocB2BUnitService adnocB2BUnitService;

    @Override
    public AdnocPriceInfoResponseData getSapPriceInformation(final AdnocPriceValueInfoCriteria adnocPriceValueInfoCriteria)
    {
        final ProductModel productModel = adnocPriceValueInfoCriteria.getProduct();
        LOG.info("appEvent=AdnocSapPricing, get Sap Price Information for: {}", productModel.getCode());

        final AdnocPriceInfoRequestData adnocPriceInfoRequestData = createAdnocPriceInfoRequestData(adnocPriceValueInfoCriteria);
        final AdnocPriceInfoResponseData adnocPriceInfoResponseData;
        try
        {
            adnocPriceInfoResponseData = getAdnocRestIntegrationService().restIntegration(
                    ADNOC_SAPPRICEINFO_GATEWAY_DESTINATION, ADNOC_SAPPRICEINFO_DESTINATION_TARGET, adnocPriceInfoRequestData,
                    AdnocPriceInfoResponseData.class);
            LOG.info("appEvent=AdnocSapPricing, got Sap Price Information for: {} with status={} and errorMessage={}.",
                    productModel.getCode(), adnocPriceInfoResponseData.getStatus(), adnocPriceInfoResponseData.getErrorMessage());
        }
        catch (final Exception exception)
        {
            LOG.error("appEvent=AdnocSapPricing, An exception={} occurred while getting Sap Price Information for: {}",
                    ExceptionUtils.getRootCauseMessage(exception), productModel.getCode());
            throw new AdnocS4HanaException(String.format("AdnocSapPricing, An exception=%s occurred while getting Sap Price Information for: %s",
                    ExceptionUtils.getRootCauseMessage(exception), productModel.getCode()));
        }
        return adnocPriceInfoResponseData;
    }

    private AdnocPriceInfoRequestData createAdnocPriceInfoRequestData(final AdnocPriceValueInfoCriteria adnocPriceValueInfoCriteria)
    {
        LOG.info("appEvent=AdnocSapPricing, create AdnocPriceInfoRequestData for: {}.", adnocPriceValueInfoCriteria.getProduct().getCode());
        final ProductModel productModel = adnocPriceValueInfoCriteria.getProduct();

        final AdnocPriceInfoRequestData adnocPriceInfoRequestData = new AdnocPriceInfoRequestData();
        adnocPriceInfoRequestData.setDate(adnocPriceValueInfoCriteria.getDate());
        final String division = productModel.getDivision();
        if (StringUtils.isBlank(division))
        {
            throw new IllegalArgumentException(String.format("Product's division is not configured for product %s.", productModel.getCode()));
        }
        final String orderType = getDivisionOrderTypeMap().get(division);
        if (StringUtils.isBlank(orderType))
        {
            throw new IllegalArgumentException(String.format("OrderType is not configured for division %s.", division));
        }
        adnocPriceInfoRequestData.setOrderType(orderType);
        final UserModel userModel = adnocPriceValueInfoCriteria.getUser();
        B2BUnitModel soldToB2BUnitModel = null;
        if (userModel instanceof final B2BCustomerModel b2BCustomerModel)
        {
            soldToB2BUnitModel = getAdnocB2BUnitService().getSoldToB2BUnit(b2BCustomerModel);
        }
        if (Objects.isNull(soldToB2BUnitModel))
        {
            throw new IllegalArgumentException(String.format("SoldToB2BUnit is not mapped for customer %s.", userModel.getUid()));
        }
        adnocPriceInfoRequestData.setB2bUnit(soldToB2BUnitModel.getUid());

        final SAPSalesOrganizationModel salesOrgbyDivision = adnocConfigService.getSalesOrgbyDivision(division);
        if (Objects.isNull(salesOrgbyDivision))
        {
            throw new IllegalArgumentException(String.format("SAPSalesOrganization is not configured for division %s.", division));
        }
        adnocPriceInfoRequestData.setSalesOrg(salesOrgbyDivision.getSalesOrganization());
        adnocPriceInfoRequestData.setDistribution(salesOrgbyDivision.getDistributionChannel());
        adnocPriceInfoRequestData.setDivision(salesOrgbyDivision.getDivision());
        adnocPriceInfoRequestData.setCurrency(adnocPriceValueInfoCriteria.getCurrency().getIsocode());

        final AdnocPriceInfoLineItemRequestData adnocPriceInfoLineItemRequestData = new AdnocPriceInfoLineItemRequestData();
        if (Objects.nonNull(adnocPriceValueInfoCriteria.getShippingAddress()))
        {
            final String shipToSapCustomerID = adnocPriceValueInfoCriteria.getShippingAddress().getSapCustomerID();
            adnocPriceInfoRequestData.setShipTo(shipToSapCustomerID);
            final B2BUnitModel shipToB2BUnitModel = getAdnocB2BUnitService().getUnitForUid(shipToSapCustomerID);
            if (Objects.nonNull(shipToB2BUnitModel) && Objects.nonNull(shipToB2BUnitModel.getPlant()))
            {
                adnocPriceInfoLineItemRequestData.setPlant(shipToB2BUnitModel.getPlant().getCode());
            }
        }

        adnocPriceInfoLineItemRequestData.setEntryNumber(DEFAULT_LINE_ITEM_ENTRY_NUMBER);
        adnocPriceInfoLineItemRequestData.setProductCode(productModel.getCode());
        adnocPriceInfoLineItemRequestData.setQuantity(adnocPriceValueInfoCriteria.getQuantity());
        adnocPriceInfoLineItemRequestData.setUnit(adnocPriceValueInfoCriteria.getProduct().getUnit().getCode());
        adnocPriceInfoLineItemRequestData.setIncoTerms(adnocPriceValueInfoCriteria.getIncoTerms());
        adnocPriceInfoRequestData.setLineItem(adnocPriceInfoLineItemRequestData);
        return adnocPriceInfoRequestData;
    }

    protected AdnocRestIntegrationService getAdnocRestIntegrationService()
    {
        return adnocRestIntegrationService;
    }

    public void setAdnocRestIntegrationService(final AdnocRestIntegrationService adnocRestIntegrationService)
    {
        this.adnocRestIntegrationService = adnocRestIntegrationService;
    }

    protected Map<String, String> getDivisionOrderTypeMap()
    {
        return divisionOrderTypeMap;
    }

    public void setDivisionOrderTypeMap(final Map<String, String> divisionOrderTypeMap)
    {
        this.divisionOrderTypeMap = divisionOrderTypeMap;
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }
}
