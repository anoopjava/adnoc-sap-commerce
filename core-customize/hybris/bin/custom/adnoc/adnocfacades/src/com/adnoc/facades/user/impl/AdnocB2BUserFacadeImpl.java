package com.adnoc.facades.user.impl;

import com.adnoc.facades.data.AdnocOrderSummaryData;
import com.adnoc.facades.product.data.GenderData;
import com.adnoc.facades.user.AdnocB2BUserFacade;
import com.adnoc.facades.user.data.*;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.customer.AdnocCustomerAccountService;
import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import com.adnoc.service.enums.*;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.company.impl.DefaultB2BUserFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import java.util.*;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class AdnocB2BUserFacadeImpl extends DefaultB2BUserFacade implements AdnocB2BUserFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BUserFacadeImpl.class);

    public static final String ORDER_PROCESSING_DAYS = "orderProcessingDays";

    private EnumerationService enumerationService;
    private Converter<Gender, GenderData> genderDataConverter;
    private Converter<Nationality, NationalityData> nationalityDataConverter;
    private Converter<PreferredCommunicationChannel, PreferredCommunicationChannelData> preferredCommunicationChannelDataConverter;
    private Converter<IdentityType, IdentityTypeData> identityTypeDataConverter;
    private Converter<Designation, AdnocDesignationData> designationDataConverter;
    private Converter<TradeLicenseAuthority, AdnocTradeLicenseAuthorityData> tradeLicenseAuthorityDataConverter;
    private AdnocCustomerAccountService adnocCustomerAccountService;
    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;
    private AdnocConfigService adnocConfigService;

    @Override
    public List<GenderData> getGenders()
    {
        LOG.info("appEvent=B2BCustomer,getGenders method called");
        final List<Gender> genders = getEnumerationService().getEnumerationValues(Gender._TYPECODE);
        return Converters.convertAll(genders, getGenderDataConverter());
    }

    @Override
    public List<NationalityData> getNationalities()
    {
        LOG.info("appEvent=B2BCustomer,getNationalities method called");
        final List<Nationality> nationalities = getEnumerationService().getEnumerationValues(Nationality._TYPECODE);
        final List<NationalityData> nationalityDataList = Converters.convertAll(nationalities, getNationalityDataConverter());
        return nationalityDataList.stream().sorted(Comparator.comparing(nationalityData -> nationalityData.getName())).toList();
    }

    @Override
    public List<PreferredCommunicationChannelData> getPreferredCommunicationChannels()
    {
        LOG.info("appEvent=B2BCustomer,getPreferredCommunicationChannels method called");
        final List<PreferredCommunicationChannel> preferredCommunicationChannels = getEnumerationService().getEnumerationValues(PreferredCommunicationChannel._TYPECODE);
        return Converters.convertAll(preferredCommunicationChannels, getPreferredCommunicationChannelDataConverter());
    }

    @Override
    public List<IdentityTypeData> getIdentityTypes()
    {
        LOG.info("appEvent=B2BCustomer,getIdentityTypes method called");
        final List<IdentityType> identityTypes = getEnumerationService().getEnumerationValues(IdentityType._TYPECODE);
        return Converters.convertAll(identityTypes, getIdentityTypeDataConverter());
    }

    @Override
    public List<AdnocDesignationData> getDesignationTypes()
    {
        LOG.info("appEvent=B2BCustomer,getDesignationTypes method called");
        final List<Designation> designationTypes = getEnumerationService().getEnumerationValues(Designation._TYPECODE);
        return Converters.convertAll(designationTypes, getDesignationDataConverter());
    }

    @Override
    public List<AdnocTradeLicenseAuthorityData> getTradeLicenseAuthorityTypes()
    {
        LOG.info("appEvent=B2BCustomer,getTradeLicenseAuthorityTypes method called");
        final List<TradeLicenseAuthority> tLATypes = getEnumerationService().getEnumerationValues(TradeLicenseAuthority._TYPECODE);
        final List<AdnocTradeLicenseAuthorityData> tradeLicenseAuthorityListData = Converters.convertAll(tLATypes, getTradeLicenseAuthorityDataConverter());
        tradeLicenseAuthorityListData.sort(Comparator.comparing(AdnocTradeLicenseAuthorityData::getName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return tradeLicenseAuthorityListData;
    }

    @Override
    public void updateCustomer(final CustomerData customerData)
    {
        validateParameterNotNullStandardMessage("customerData", customerData);
        Assert.hasText(customerData.getFirstName(), "The field [FirstName] cannot be empty");
        Assert.hasText(customerData.getLastName(), "The field [LastName] cannot be empty");
        final B2BCustomerModel b2BCustomerModel;
        if (StringUtils.isEmpty(customerData.getUid()))
        {
            LOG.info("appEvent=B2BCustomer, customerData UID is empty!");
            b2BCustomerModel = getModelService().create(B2BCustomerModel.class);
            b2BCustomerModel.setCustomerID(UUID.randomUUID().toString());
            final CurrencyModel currencyModel = getUserService().getCurrentUser().getSessionCurrency();
            b2BCustomerModel.setSessionCurrency(currencyModel);
            getModelService().save(getB2BCustomerReverseConverter().convert(customerData, b2BCustomerModel));
            getAdnocOutboundReplicationDirector().scheduleOutboundTask(b2BCustomerModel);
        }
        else
        {
            LOG.info("appEvent=B2BCustomer, updating existing B2BCustomerModel with UID:{}", customerData.getUid());
            b2BCustomerModel = getUserService().getUserForUID(customerData.getUid(), B2BCustomerModel.class);
            getModelService().save(getB2BCustomerReverseConverter().convert(customerData, b2BCustomerModel));
        }
    }

    @Override
    public AdnocOrderSummaryData getOrderSummary(final String userId)
    {
        final List<OrderModel> orders = getAdnocCustomerAccountService().getOrderSummary(userId);
        LOG.info("appEvent=OrderSummary, found {}", CollectionUtils.size(orders));
        final int totalOrdersCount = CollectionUtils.size(orders);
        double totalPrice = 0.0;
        if (CollectionUtils.isNotEmpty(orders))
        {
            totalPrice = orders.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(order -> order.getTotalPrice() != null ? order.getTotalPrice() : 0.0)
                    .sum();
        }
        final AdnocOrderSummaryData adnocOrderSummaryData = new AdnocOrderSummaryData();
        adnocOrderSummaryData.setOrdersPlacedCount(totalOrdersCount);
        LOG.info("appEvent=OrderSummary,getting number of order:{} ", adnocOrderSummaryData.getOrdersPlacedCount());
        adnocOrderSummaryData.setTotalOrdersValue(totalPrice);
        LOG.info("appEvent= orderSummary, total orders count:{}", totalPrice);

        final List<ReturnRequestModel> returnOrders = getAdnocCustomerAccountService().getReturnRequests(userId);
        LOG.info("appEvent=OrderSummary, found {}", CollectionUtils.size(returnOrders));
        final int totalReturnOrdersCount = CollectionUtils.size(returnOrders);
        adnocOrderSummaryData.setReturnOrdersCount(totalReturnOrdersCount);
        LOG.info("appEvent=OrderSummary, getting number of return orders: {}", totalReturnOrdersCount);

        final int orderProcessingDays = getAdnocConfigService().getAdnocConfigValue(ORDER_PROCESSING_DAYS, 30);
        adnocOrderSummaryData.setConfigValue(orderProcessingDays);

        return adnocOrderSummaryData;
    }

    @Override
    public boolean isUserExisting(final CustomerData orgCustomerData)
    {
        LOG.info("appEvent=B2BCustomer, Check if user exists with Email: {}, Identification Number: {}", orgCustomerData.getEmail(), orgCustomerData.getIdentificationNumber());
        final Map<String, String> duplicateCheckMap = new HashMap<>();
        duplicateCheckMap.put(B2BCustomerModel.EMAIL, orgCustomerData.getEmail());
        duplicateCheckMap.put(B2BCustomerModel.IDENTIFICATIONNUMBER, orgCustomerData.getIdentificationNumber());

        final B2BCustomerModel b2BCustomerModel = getAdnocCustomerAccountService().getB2BCustomer(duplicateCheckMap);
        return Objects.nonNull(b2BCustomerModel);
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    protected Converter<Gender, GenderData> getGenderDataConverter()
    {
        return genderDataConverter;
    }

    public void setGenderDataConverter(final Converter<Gender, GenderData> genderDataConverter)
    {
        this.genderDataConverter = genderDataConverter;
    }

    protected Converter<Nationality, NationalityData> getNationalityDataConverter()
    {
        return nationalityDataConverter;
    }

    public void setNationalityDataConverter(final Converter<Nationality, NationalityData> nationalityDataConverter)
    {
        this.nationalityDataConverter = nationalityDataConverter;
    }

    protected Converter<PreferredCommunicationChannel, PreferredCommunicationChannelData> getPreferredCommunicationChannelDataConverter()
    {
        return preferredCommunicationChannelDataConverter;
    }


    public void setPreferredCommunicationChannelDataConverter(final Converter<PreferredCommunicationChannel, PreferredCommunicationChannelData> preferredCommunicationChannelDataConverter)
    {
        this.preferredCommunicationChannelDataConverter = preferredCommunicationChannelDataConverter;
    }

    protected Converter<IdentityType, IdentityTypeData> getIdentityTypeDataConverter()
    {
        return identityTypeDataConverter;
    }

    public void setIdentityTypeDataConverter(final Converter<IdentityType, IdentityTypeData> identityTypeDataConverter)
    {
        this.identityTypeDataConverter = identityTypeDataConverter;
    }

    protected Converter<Designation, AdnocDesignationData> getDesignationDataConverter()
    {
        return designationDataConverter;
    }

    public void setDesignationDataConverter(final Converter<Designation, AdnocDesignationData> designationDataConverter)
    {
        this.designationDataConverter = designationDataConverter;
    }

    protected Converter<TradeLicenseAuthority, AdnocTradeLicenseAuthorityData> getTradeLicenseAuthorityDataConverter()
    {
        return tradeLicenseAuthorityDataConverter;
    }

    public void setTradeLicenseAuthorityDataConverter(final Converter<TradeLicenseAuthority, AdnocTradeLicenseAuthorityData> tradeLicenseAuthorityDataConverter)
    {
        this.tradeLicenseAuthorityDataConverter = tradeLicenseAuthorityDataConverter;
    }

    protected AdnocCustomerAccountService getAdnocCustomerAccountService()
    {
        return adnocCustomerAccountService;
    }

    public void setAdnocCustomerAccountService(final AdnocCustomerAccountService adnocCustomerAccountService)
    {
        this.adnocCustomerAccountService = adnocCustomerAccountService;
    }

    protected AdnocOutboundReplicationDirector getAdnocOutboundReplicationDirector()
    {
        return adnocOutboundReplicationDirector;
    }

    public void setAdnocOutboundReplicationDirector(final AdnocOutboundReplicationDirector adnocOutboundReplicationDirector)
    {
        this.adnocOutboundReplicationDirector = adnocOutboundReplicationDirector;
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }
}
