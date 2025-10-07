package com.adnoc.service.order.strategies.calculation.pdt.impl;

import com.adnoc.service.exception.AdnocS4HanaException;
import com.adnoc.service.order.strategies.calculation.pdt.impl.converter.impl.AdnocPriceValueInfoCriteria;
import com.adnoc.service.price.AdnocPriceService;
import com.adnoc.service.price.data.AdnocPriceInfoLineItemResponseData;
import com.adnoc.service.price.data.AdnocPriceInfoResponseData;
import com.adnoc.service.price.data.AdnocSapPricingConditionResponseData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.pdt.converter.PDTConverter;
import de.hybris.platform.order.strategies.calculation.pdt.criteria.PriceValueInfoCriteria;
import de.hybris.platform.order.strategies.calculation.pdt.impl.DefaultFindPriceValueInfoStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.localization.Localization;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.SUCCESS;

public class AdnocFindPriceValueInfoStrategy extends DefaultFindPriceValueInfoStrategy
{
    private static final Logger LOG = LogManager.getLogger(AdnocFindPriceValueInfoStrategy.class);

    public static final String ADNOC_SAP_PRICE_INFO_FETCH_ENABLED = "adnoc.sap.price.info.fetch.enabled";
    public static final String ADNOC_SAP_PRICE_INFO_MOCK_ENABLED = "adnoc.sap.price.info.mock.enabled";

    private ConfigurationService configurationService;
    private AdnocPriceService adnocPriceService;
    private AdnocPriceService adnocMockedPriceService;
    private PDTConverter<AdnocPriceInfoLineItemResponseData, PriceValue, PriceValueInfoCriteria> adnocPriceValueConverter;
    private PDTConverter<AdnocPriceInfoLineItemResponseData, PriceInformation, PriceValueInfoCriteria> adnocPriceInfoConverter;

    @Override
    public List<PriceValue> getPDTValues(final PriceValueInfoCriteria priceValueInfoCriteria) throws CalculationException
    {
        final ProductModel productModel = priceValueInfoCriteria.getProduct();
        LOG.info("appEvent=AdnocSapPricing, finding PriceValue for product={}.", productModel.getCode());
        final boolean sapPriceInfoFechEnabled = getConfigurationService().getConfiguration().getBoolean(ADNOC_SAP_PRICE_INFO_FETCH_ENABLED, false);
        LOG.debug("appEvent=AdnocSapPricing, sapPriceInfoFechEnabled={}.", sapPriceInfoFechEnabled);
        if (sapPriceInfoFechEnabled && (priceValueInfoCriteria.getUser() instanceof B2BCustomerModel))
        {
            try
            {
                final AdnocPriceInfoResponseData adnocPriceInfoResponseData = getAdnocPriceInfoResponseData(priceValueInfoCriteria);
                return getAdnocPriceValueConverter().convertAll(List.of(adnocPriceInfoResponseData.getLineItem()), priceValueInfoCriteria);
            }
            catch (final CalculationException | AdnocS4HanaException pricingException)
            {
                LOG.error("appEvent=AdnocSapPricing, An exception={} occurred while getting Sap Price Information for: {}",
                        ExceptionUtils.getRootCauseMessage(pricingException), productModel.getCode());
                LOG.info("appEvent=AdnocSapPricing, adnocPriceInfoResponseData is not valid or having error, so falling back to default price strategy.");
            }
        }
        return super.getPDTValues(priceValueInfoCriteria);
    }

    @Override
    public List<PriceInformation> getPDTInformation(final PriceValueInfoCriteria priceValueInfoCriteria) throws CalculationException
    {
        final ProductModel productModel = priceValueInfoCriteria.getProduct();
        LOG.info("appEvent=AdnocSapPricing, finding PriceInformation for product={}.", productModel.getCode());
        final boolean sapPriceInfoFechEnabled = getConfigurationService().getConfiguration().getBoolean(ADNOC_SAP_PRICE_INFO_FETCH_ENABLED, false);
        LOG.info("appEvent=AdnocSapPricing, sapPriceInfoFechEnabled={}.", sapPriceInfoFechEnabled);
        if (sapPriceInfoFechEnabled && (priceValueInfoCriteria.getUser() instanceof B2BCustomerModel))
        {
            try
            {
                final AdnocPriceInfoResponseData adnocPriceInfoResponseData = getAdnocPriceInfoResponseData(priceValueInfoCriteria);
                return getAdnocPriceInfoConverter().convertAll(List.of(adnocPriceInfoResponseData.getLineItem()), priceValueInfoCriteria);
            }
            catch (final CalculationException | AdnocS4HanaException pricingException)
            {
                LOG.error("appEvent=AdnocSapPricing, An exception={} occurred while getting Sap Price Information for: {}",
                        ExceptionUtils.getRootCauseMessage(pricingException), productModel.getCode());
                LOG.info("appEvent=AdnocSapPricing, adnocPriceInfoResponseData is not valid or having error, so falling back to default price strategy.");
            }
        }
        return super.getPDTInformation(priceValueInfoCriteria);
    }

    private AdnocPriceInfoResponseData getAdnocPriceInfoResponseData(final PriceValueInfoCriteria priceValueInfoCriteria) throws CalculationException
    {
        if (!validatePriceValueInfoCriteria(priceValueInfoCriteria))
        {
            throw new IllegalArgumentException("Invalid PriceValueInfoCriteria.");
        }

        final AdnocPriceValueInfoCriteria adnocPriceValueInfoCriteria = (AdnocPriceValueInfoCriteria) priceValueInfoCriteria;
        final boolean sapPriceInfoMockEnabled = getConfigurationService().getConfiguration().getBoolean(ADNOC_SAP_PRICE_INFO_MOCK_ENABLED, false);
        LOG.info("appEvent=AdnocSapPricing, sapPriceInfoMockEnabled={}.", sapPriceInfoMockEnabled);
        final AdnocPriceService priceService = sapPriceInfoMockEnabled ? getAdnocMockedPriceService() : getAdnocPriceService();
        final AdnocPriceInfoResponseData adnocPriceInfoResponseData = priceService.getSapPriceInformation(adnocPriceValueInfoCriteria);
        if (!validateAdnocPriceInfoResponse(adnocPriceInfoResponseData))
        {
            LOG.info("appEvent=AdnocSapPricing, adnocPriceInfoResponseData is not valid.");
            throw new CalculationException(StringUtils.isNotBlank(adnocPriceInfoResponseData.getErrorMessage()) ?
                    adnocPriceInfoResponseData.getErrorMessage() : getExceptionMessage(adnocPriceValueInfoCriteria));
        }
        return adnocPriceInfoResponseData;
    }

    private boolean validatePriceValueInfoCriteria(final PriceValueInfoCriteria priceValueInfoCriteria)
    {
        LOG.debug("appEvent=AdnocSapPricing, validating PriceValueInfoCriteria.");
        return (priceValueInfoCriteria instanceof final AdnocPriceValueInfoCriteria adnocPriceValueInfoCriteria)
                && Objects.nonNull(adnocPriceValueInfoCriteria)
                && Objects.nonNull(adnocPriceValueInfoCriteria.getProduct())
                && Objects.nonNull(adnocPriceValueInfoCriteria.getCurrency())
                && Objects.nonNull(adnocPriceValueInfoCriteria.getUser())
                && Objects.nonNull(adnocPriceValueInfoCriteria.getDate())
                && Objects.nonNull(adnocPriceValueInfoCriteria.getQuantity());
    }

    private boolean validateAdnocPriceInfoResponse(final AdnocPriceInfoResponseData adnocPriceInfoResponseData)
    {
        LOG.debug("appEvent=AdnocSapPricing, validating AdnocPriceInfoResponseData.");
        return StringUtils.equalsIgnoreCase(SUCCESS, adnocPriceInfoResponseData.getStatus())
                && StringUtils.isNotBlank(adnocPriceInfoResponseData.getB2bUnit())
                && StringUtils.isNotBlank(adnocPriceInfoResponseData.getCurrency())
                && validateAdnocPriceInfoLineItemResponse(adnocPriceInfoResponseData.getLineItem());
    }

    private boolean validateAdnocPriceInfoLineItemResponse(final AdnocPriceInfoLineItemResponseData adnocPriceInfoLineItemResponseData)
    {
        LOG.debug("appEvent=AdnocSapPricing, validating AdnocPriceInfoLineItemResponseData.");
        return Objects.nonNull(adnocPriceInfoLineItemResponseData)
                && Objects.nonNull(adnocPriceInfoLineItemResponseData.getEntryNumber()) &&
                Objects.nonNull(adnocPriceInfoLineItemResponseData.getQuantity()) &&
                StringUtils.isNotBlank(adnocPriceInfoLineItemResponseData.getProductCode()) &&
                StringUtils.isNotBlank(adnocPriceInfoLineItemResponseData.getUnit()) &&
                Objects.nonNull(adnocPriceInfoLineItemResponseData.getBasePrice()) &&
                Objects.nonNull(adnocPriceInfoLineItemResponseData.getGrossTotalPrice()) &&
                Objects.nonNull(adnocPriceInfoLineItemResponseData.getTotalDiscount()) &&
                Objects.nonNull(adnocPriceInfoLineItemResponseData.getNetTotalPrice()) &&
                Objects.nonNull(adnocPriceInfoLineItemResponseData.getTotalTaxAmount()) &&
                Objects.nonNull(adnocPriceInfoLineItemResponseData.getGrandTotalPrice()) &&
                validateSapPricingConditions(adnocPriceInfoLineItemResponseData.getSapPricingConditions());
    }

    private boolean validateSapPricingConditions(final List<AdnocSapPricingConditionResponseData> sapPricingConditions)
    {
        LOG.debug("appEvent=AdnocSapPricing, validating sapPricingConditions.");
        return CollectionUtils.isNotEmpty(sapPricingConditions) && sapPricingConditions.stream().allMatch(sapPricingConditionResponseData ->
                StringUtils.isNotBlank(sapPricingConditionResponseData.getConditionType()) &&
                        Objects.nonNull(sapPricingConditionResponseData.getConditionValue()) &&
                        Objects.nonNull(sapPricingConditionResponseData.getTotalConditionValue()));
    }


    private static String getExceptionMessage(final PriceValueInfoCriteria criteria)
    {
        return Localization.getLocalizedString("exception.europe1pricefactory.getbaseprice.jalopricefactoryexception1",
                new Object[]{
                        criteria.getProduct(), criteria.getProductGroup(), criteria.getUser(), criteria.getUserGroup(),
                        Long.toString(criteria.getQuantity()), criteria.getProduct().getUnit(), criteria.getCurrency(), criteria.getDate(),
                        Boolean.toString(criteria.isNet())
                });
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    protected AdnocPriceService getAdnocPriceService()
    {
        return adnocPriceService;
    }

    public void setAdnocPriceService(final AdnocPriceService adnocPriceService)
    {
        this.adnocPriceService = adnocPriceService;
    }

    protected AdnocPriceService getAdnocMockedPriceService()
    {
        return adnocMockedPriceService;
    }

    public void setAdnocMockedPriceService(final AdnocPriceService adnocMockedPriceService)
    {
        this.adnocMockedPriceService = adnocMockedPriceService;
    }

    protected PDTConverter<AdnocPriceInfoLineItemResponseData, PriceValue, PriceValueInfoCriteria> getAdnocPriceValueConverter()
    {
        return adnocPriceValueConverter;
    }

    public void setAdnocPriceValueConverter(final PDTConverter<AdnocPriceInfoLineItemResponseData, PriceValue, PriceValueInfoCriteria> adnocPriceValueConverter)
    {
        this.adnocPriceValueConverter = adnocPriceValueConverter;
    }

    protected PDTConverter<AdnocPriceInfoLineItemResponseData, PriceInformation, PriceValueInfoCriteria> getAdnocPriceInfoConverter()
    {
        return adnocPriceInfoConverter;
    }

    public void setAdnocPriceInfoConverter(final PDTConverter<AdnocPriceInfoLineItemResponseData, PriceInformation, PriceValueInfoCriteria> adnocPriceInfoConverter)
    {
        this.adnocPriceInfoConverter = adnocPriceInfoConverter;
    }
}
