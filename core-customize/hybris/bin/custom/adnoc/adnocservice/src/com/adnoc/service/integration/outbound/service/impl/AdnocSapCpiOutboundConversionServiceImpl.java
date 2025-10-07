package com.adnoc.service.integration.outbound.service.impl;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.contributor.AdnocB2BQuotePartnerContributor;
import com.adnoc.service.enums.IncoTerms;
import com.adnoc.service.enums.PartnerFunction;
import com.adnoc.service.integration.outbound.service.AdnocSapCpiOutboundConversionService;
import com.adnoc.service.model.*;
import com.adnoc.service.ticket.AdnocTicketService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.comments.model.CommentAttachmentModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiConfig;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiTargetSystem;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderDestinationService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdnocSapCpiOutboundConversionServiceImpl implements AdnocSapCpiOutboundConversionService
{
    private static final Logger LOG = LogManager.getLogger(AdnocSapCpiOutboundConversionServiceImpl.class);
    public static final String CARD = "BANK CARD";
    public static final String BANK_TRANSFER = "BANK TSFR";
    public static final String ADNOC_CSTICKET_TRANSACTIONTYPE = "adnoc.csticket.transactiontype.code";
    public static final String ADNOC_CSTICKET_SALESORG = "adnoc.csticket.salesorg.code";
    public static final String ADNOC_REGISTRATION_DUNNINGPROCEDURE = "adnoc.registration.dunningprocedure";
    public static final String ADNOC_CSTICKET_DISTRIBUTION = "adnoc.csticket.distribution.code";
    public static final String ADNOC_CSTICKET_REFDOCCATEGORY = "adnoc.csticket.refdoccategory.code";
    public static final String ADNOC_QUOTE_TRANSACTIONTYPE = "adnoc.quote.transactiontype.code";
    public static final String ADNOC_QUOTE_SALESORG = "adnoc.quote.salesorg.code";
    public static final String ADNOC_QUOTE_DISTRIBUTION = "adnoc.quote.distribution.code";
    public static final String DOCUMENT_TYPE_IDENTIFICATION = "IdentificationNumberDocument";
    public static final String DOCUMENT_TYPE_OTHER = "OtherDocument";
    public static final String DOCUMENT_TYPE_SUPPORTED = "SupportedDocument";
    public static final int MONETARY_AMOUNT_SCALE = 2;

    private BaseSiteService baseSiteService;
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
    private ConfigurationService configurationService;
    private MediaService mediaService;
    private AdnocConfigService adnocConfigService;
    private SapCpiOrderDestinationService sapCpiOrderDestinationService;
    private AdnocTicketService adnocTicketService;
    private EnumerationService enumerationService;
    private AdnocB2BQuotePartnerContributor adnocB2BQuotePartnerContributor;
    private AdnocB2BUnitService adnocB2BUnitService;

    private void mapCommonAttributes(final AdnocRegistrationModel adnocRegistrationModel, final AdnocOutboundRegistrationModel adnocOutboundRegistrationModel)
    {
        adnocOutboundRegistrationModel.setCompanyAddressCountry(adnocRegistrationModel.getCompanyAddressCountry());
        adnocOutboundRegistrationModel.setCompanyAddressRegion(adnocRegistrationModel.getCompanyAddressRegion());
        adnocOutboundRegistrationModel.setCompanyAddressCity(adnocRegistrationModel.getCompanyAddressCity());
        adnocOutboundRegistrationModel.setCompanyAddressPostalCode(adnocRegistrationModel.getCompanyAddressPostalCode());
        adnocOutboundRegistrationModel.setCompanyAddressStreet(adnocRegistrationModel.getCompanyAddressStreet());
        adnocOutboundRegistrationModel.setCompanyAddressStreetLine2(adnocRegistrationModel.getCompanyAddressStreetLine2());
        adnocOutboundRegistrationModel.setCompanyName(adnocRegistrationModel.getCompanyName());
        adnocOutboundRegistrationModel.setCompanyName2(adnocRegistrationModel.getCompanyName2());
        adnocOutboundRegistrationModel.setCurrency(adnocRegistrationModel.getCurrency());
        adnocOutboundRegistrationModel.setPartnerFunction(adnocRegistrationModel.getPartnerFunction());
        adnocOutboundRegistrationModel.setFirstName(adnocRegistrationModel.getFirstName());
        adnocOutboundRegistrationModel.setLastName(adnocRegistrationModel.getLastName());
        adnocOutboundRegistrationModel.setDesignation(adnocRegistrationModel.getDesignation());
        adnocOutboundRegistrationModel.setTitle(adnocRegistrationModel.getTitle());
        adnocOutboundRegistrationModel.setEmail(adnocRegistrationModel.getEmail());
        adnocOutboundRegistrationModel.setTelephone(adnocRegistrationModel.getTelephone());
        adnocOutboundRegistrationModel.setIdentityType(adnocRegistrationModel.getIdentityType());
        adnocOutboundRegistrationModel.setIdentificationNumber(adnocRegistrationModel.getIdentificationNumber());
        adnocOutboundRegistrationModel.setIdentificationValidFrom(adnocRegistrationModel.getIdentificationValidFrom());
        adnocOutboundRegistrationModel.setIdentificationValidTo(adnocRegistrationModel.getIdentificationValidTo());
        adnocOutboundRegistrationModel.setMobileNumber(adnocRegistrationModel.getMobileNumber());
        adnocOutboundRegistrationModel.setCountryOfOrigin(adnocRegistrationModel.getCountryOfOrigin());
        adnocOutboundRegistrationModel.setNationality(adnocRegistrationModel.getNationality());
        adnocOutboundRegistrationModel.setName(adnocRegistrationModel.getName());
        adnocOutboundRegistrationModel.setGenderSapCode(getAdnocConfigService().getAdnocSapIntegrationCodeMap(IntegrationType.OUTBOUND, Gender.class, adnocRegistrationModel.getGender().getCode()));
        adnocOutboundRegistrationModel.setFaxNumber(adnocRegistrationModel.getFaxNumber());
        adnocOutboundRegistrationModel.setCustomerCategory(adnocRegistrationModel.getCustomerCategory());
        adnocOutboundRegistrationModel.setPoBox(adnocRegistrationModel.getPoBox());

        adnocOutboundRegistrationModel.setCustomerGroup(adnocRegistrationModel.getCustomerGroup());
        adnocOutboundRegistrationModel.setSalesOffice(adnocRegistrationModel.getSalesOffice());
        adnocOutboundRegistrationModel.setSalesGroup(adnocRegistrationModel.getSalesGroup());
        adnocOutboundRegistrationModel.setTaxClassification(adnocRegistrationModel.getTaxClassification());
        adnocOutboundRegistrationModel.setCurrency(adnocRegistrationModel.getCurrency());
        adnocOutboundRegistrationModel.setPlant(adnocRegistrationModel.getPlant());
        adnocOutboundRegistrationModel.setShippingCondition(adnocRegistrationModel.getShippingCondition());
        adnocOutboundRegistrationModel.setPaymentTerms(adnocRegistrationModel.getPaymentTerms());
        adnocOutboundRegistrationModel.setPriceGroup(adnocRegistrationModel.getPriceGroup());
        adnocOutboundRegistrationModel.setPriceListType(adnocRegistrationModel.getPriceListType());

    }

    private void createPopulateAdnocOutboundRegistrationDocument(final Collection<AdnocOutboundDocumentModel> adnocOutboundDocumentModels,
                                                                 final DocumentMediaModel documentMediaModel, final String supportedDocumentType)
    {
        if (Objects.isNull(documentMediaModel))
        {
            LOG.info("appEvent=AdnocOutboundRegistration, supportedDocumentType={} is null.", supportedDocumentType);
            return;
        }
        final AdnocOutboundDocumentModel adnocOutboundDocumentModel = getAdnocOutboundDocumentModel(documentMediaModel, supportedDocumentType);
        adnocOutboundDocumentModels.add(adnocOutboundDocumentModel);
    }

    private AdnocOutboundDocumentModel getAdnocOutboundDocumentModel(final DocumentMediaModel documentMediaModel, final String supportedDocumentType)
    {
        final AdnocOutboundDocumentModel adnocOutboundDocumentModel = new AdnocOutboundDocumentModel();
        adnocOutboundDocumentModel.setSupportedDocumentType(supportedDocumentType);
        adnocOutboundDocumentModel.setDocumentBase64(getMediaContentBase64Encoded(documentMediaModel));
        adnocOutboundDocumentModel.setDocumentName(getFileName(documentMediaModel));
        adnocOutboundDocumentModel.setDocumentType(getFileType(documentMediaModel));
        return adnocOutboundDocumentModel;
    }

    @Override
    public AdnocSoldToOutboundB2BRegistrationModel convertToSoldToOutboundModel(final AdnocSoldToB2BRegistrationModel adnocSoldToB2BRegistrationModel)
    {
        LOG.debug("appEvent=AdnocOutboundB2BRegistration, started converting adnocB2BRegistrationModel to adnocOutboundB2BRegistrationModel");
        final AdnocSoldToOutboundB2BRegistrationModel adnocSoldToOutboundB2BRegistration = new AdnocSoldToOutboundB2BRegistrationModel();

        mapCommonAttributes(adnocSoldToB2BRegistrationModel, adnocSoldToOutboundB2BRegistration);

        adnocSoldToOutboundB2BRegistration.setPrimaryProduct(adnocSoldToB2BRegistrationModel.getPrimaryProduct());
        adnocSoldToOutboundB2BRegistration.setDivision(adnocSoldToB2BRegistrationModel.getPrimaryProduct().getCode());
        adnocSoldToOutboundB2BRegistration.setCompanyPhoneNumber(adnocSoldToB2BRegistrationModel.getCompanyPhoneNumber());
        adnocSoldToOutboundB2BRegistration.setCompanyWebsite(adnocSoldToB2BRegistrationModel.getCompanyWebsite());
        adnocSoldToOutboundB2BRegistration.setPreferredCommunicationChannel(adnocSoldToB2BRegistrationModel.getPreferredCommunicationChannel());
        adnocSoldToOutboundB2BRegistration.setCompanyEmail(adnocSoldToB2BRegistrationModel.getCompanyEmail());
        adnocSoldToOutboundB2BRegistration.setCompanyMobileNumber(adnocSoldToB2BRegistrationModel.getCompanyMobileNumber());
        adnocSoldToOutboundB2BRegistration.setVatId(adnocSoldToB2BRegistrationModel.getVatId());
        adnocSoldToOutboundB2BRegistration.setTradeLicenseAuthority(adnocSoldToB2BRegistrationModel.getTradeLicenseAuthority());
        adnocSoldToOutboundB2BRegistration.setTradeLicenseNumber(adnocSoldToB2BRegistrationModel.getTradeLicenseNumber());
        adnocSoldToOutboundB2BRegistration.setTlnValidFrom(adnocSoldToB2BRegistrationModel.getTlnValidFrom());
        adnocSoldToOutboundB2BRegistration.setTlnValidTo(adnocSoldToB2BRegistrationModel.getTlnValidTo());
        adnocSoldToOutboundB2BRegistration.setReconciliationAccount(adnocSoldToB2BRegistrationModel.getReconciliationAccount());
        adnocSoldToOutboundB2BRegistration.setPreviousAccountNumber(adnocSoldToB2BRegistrationModel.getPreviousAccountNumber());
        adnocSoldToOutboundB2BRegistration.setSortKey(adnocSoldToB2BRegistrationModel.getSortKey());
        adnocSoldToOutboundB2BRegistration.setPlanningGroup(adnocSoldToB2BRegistrationModel.getPlanningGroup());
        adnocSoldToOutboundB2BRegistration.setPaymentMethods(adnocSoldToB2BRegistrationModel.getPaymentMethods());
        final Configuration configuration = getConfigurationService().getConfiguration();
        adnocSoldToOutboundB2BRegistration.setDunningProcedure(configuration.getString(ADNOC_REGISTRATION_DUNNINGPROCEDURE));
        adnocSoldToOutboundB2BRegistration.setDunningClerk(adnocSoldToB2BRegistrationModel.getDunningClerk());
        adnocSoldToOutboundB2BRegistration.setAccountingClerk(adnocSoldToB2BRegistrationModel.getAccountingClerk());
        adnocSoldToOutboundB2BRegistration.setTermsOfPayment(adnocSoldToB2BRegistrationModel.getTermsOfPayment());
        adnocSoldToOutboundB2BRegistration.setVirtualIBANRequired(adnocSoldToB2BRegistrationModel.getVirtualIBANRequired());
        adnocSoldToOutboundB2BRegistration.setVirtualIBANStatus(adnocSoldToB2BRegistrationModel.getVirtualIBANStatus());
        adnocSoldToOutboundB2BRegistration.setHouseBankID(adnocSoldToB2BRegistrationModel.getHouseBankID());
        adnocSoldToOutboundB2BRegistration.setAccountID(adnocSoldToB2BRegistrationModel.getAccountID());
        adnocSoldToOutboundB2BRegistration.setVirtualIBANNumber(adnocSoldToB2BRegistrationModel.getVirtualIBANNumber());
        adnocSoldToOutboundB2BRegistration.setCollectionIBANNumber(adnocSoldToB2BRegistrationModel.getCollectionIBANNumber());
        adnocSoldToOutboundB2BRegistration.setCollectionHouseBank(adnocSoldToB2BRegistrationModel.getCollectionHouseBank());
        adnocSoldToOutboundB2BRegistration.setIncoTerms(getEnumerationService().getEnumerationValue(IncoTerms._TYPECODE, "DAP"));

        final Collection<AdnocOutboundDocumentModel> adnocOutboundDocumentModels = new ArrayList<>();
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocSoldToB2BRegistrationModel.getIdentificationNumberDocument(), DOCUMENT_TYPE_IDENTIFICATION);
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocSoldToB2BRegistrationModel.getOtherDocument(), DOCUMENT_TYPE_OTHER);
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocSoldToB2BRegistrationModel.getSupportedDocument(), DOCUMENT_TYPE_SUPPORTED);
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocSoldToB2BRegistrationModel.getTlnDocument(), "TlnDocument");
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocSoldToB2BRegistrationModel.getVatIdDocument(), "VatIdDocument");
        adnocSoldToOutboundB2BRegistration.setAdnocOutboundDocument(adnocOutboundDocumentModels);

        LOG.debug("appEvent=AdnocOutboundB2BRegistration, conversion completed adnocB2BRegistrationModel to adnocSoldToOutboundB2BRegistration");
        return adnocSoldToOutboundB2BRegistration;
    }

    @Override
    public AdnocOutboundB2BUnitRegistrationModel convertToPayerOutboundB2BUnitModel(final AdnocPayerB2BUnitRegistrationModel adnocPayerB2BUnitRegistrationModel)
    {
        LOG.debug("appEvent=AdnocOutboundB2BRegistration, start conversion for B2BUnit registration: {}", adnocPayerB2BUnitRegistrationModel.getEmail());
        final AdnocPayerOutboundB2BUnitRegistrationModel adnocPayerOutboundB2BUnitRegistration = new AdnocPayerOutboundB2BUnitRegistrationModel();

        mapCommonAttributes(adnocPayerB2BUnitRegistrationModel, adnocPayerOutboundB2BUnitRegistration);

        adnocPayerOutboundB2BUnitRegistration.setDefaultB2BUnit(adnocPayerB2BUnitRegistrationModel.getDefaultB2BUnit());
        adnocPayerOutboundB2BUnitRegistration.setSalesManager(adnocPayerB2BUnitRegistrationModel.getSalesManager());
        adnocPayerOutboundB2BUnitRegistration.setCollector(adnocPayerB2BUnitRegistrationModel.getCollector());
        adnocPayerOutboundB2BUnitRegistration.setVatId(adnocPayerB2BUnitRegistrationModel.getVatId());
        adnocPayerOutboundB2BUnitRegistration.setInvoicingDate(adnocPayerB2BUnitRegistrationModel.getInvoicingDate());
        adnocPayerOutboundB2BUnitRegistration.setReconciliationAccount(adnocPayerB2BUnitRegistrationModel.getReconciliationAccount());
        adnocPayerOutboundB2BUnitRegistration.setPreviousAccountNumber(adnocPayerB2BUnitRegistrationModel.getPreviousAccountNumber());
        adnocPayerOutboundB2BUnitRegistration.setSortKey(adnocPayerB2BUnitRegistrationModel.getSortKey());
        adnocPayerOutboundB2BUnitRegistration.setPlanningGroup(adnocPayerB2BUnitRegistrationModel.getPlanningGroup());
        adnocPayerOutboundB2BUnitRegistration.setPaymentMethods(adnocPayerB2BUnitRegistrationModel.getPaymentMethods());
        final Configuration configuration = getConfigurationService().getConfiguration();
        adnocPayerOutboundB2BUnitRegistration.setDunningProcedure(configuration.getString(ADNOC_REGISTRATION_DUNNINGPROCEDURE));
        adnocPayerOutboundB2BUnitRegistration.setDunningClerk(adnocPayerB2BUnitRegistrationModel.getDunningClerk());
        adnocPayerOutboundB2BUnitRegistration.setAccountingClerk(adnocPayerB2BUnitRegistrationModel.getAccountingClerk());
        adnocPayerOutboundB2BUnitRegistration.setTermsOfPayment(adnocPayerB2BUnitRegistrationModel.getTermsOfPayment());
        adnocPayerOutboundB2BUnitRegistration.setVirtualIBANRequired(adnocPayerB2BUnitRegistrationModel.getVirtualIBANRequired());
        adnocPayerOutboundB2BUnitRegistration.setVirtualIBANStatus(adnocPayerB2BUnitRegistrationModel.getVirtualIBANStatus());
        adnocPayerOutboundB2BUnitRegistration.setHouseBankID(adnocPayerB2BUnitRegistrationModel.getHouseBankID());
        adnocPayerOutboundB2BUnitRegistration.setAccountID(adnocPayerB2BUnitRegistrationModel.getAccountID());
        adnocPayerOutboundB2BUnitRegistration.setVirtualIBANNumber(adnocPayerB2BUnitRegistrationModel.getVirtualIBANNumber());
        adnocPayerOutboundB2BUnitRegistration.setCollectionIBANNumber(adnocPayerB2BUnitRegistrationModel.getCollectionIBANNumber());
        adnocPayerOutboundB2BUnitRegistration.setCollectionHouseBank(adnocPayerB2BUnitRegistrationModel.getCollectionHouseBank());
        adnocPayerOutboundB2BUnitRegistration.setIncoTerms(getEnumerationService().getEnumerationValue(IncoTerms._TYPECODE, "DAP"));

        final Collection<AdnocOutboundDocumentModel> adnocOutboundDocumentModels = new ArrayList<>();
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocPayerB2BUnitRegistrationModel.getIdentificationNumberDocument(), DOCUMENT_TYPE_IDENTIFICATION);
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocPayerB2BUnitRegistrationModel.getOtherDocument(), DOCUMENT_TYPE_OTHER);
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocPayerB2BUnitRegistrationModel.getSupportedDocument(), DOCUMENT_TYPE_SUPPORTED);
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocPayerB2BUnitRegistrationModel.getVatIdDocument(), "VatIdDocument");
        adnocPayerOutboundB2BUnitRegistration.setAdnocOutboundDocument(adnocOutboundDocumentModels);

        LOG.debug("appEvent=AdnocOutboundB2BRegistration, B2B Unit Registration conversion complete for UID: {}", adnocPayerB2BUnitRegistrationModel.getEmail());
        return adnocPayerOutboundB2BUnitRegistration;
    }

    @Override
    public AdnocOutboundB2BUnitRegistrationModel convertToShipToOutboundB2BUnitModel(final AdnocShipToB2BUnitRegistrationModel adnocShipToB2BUnitRegistrationModel)
    {
        LOG.debug("appEvent=AdnocOutboundB2BRegistration, start conversion for B2BUnit registration: {}", adnocShipToB2BUnitRegistrationModel.getEmail());
        final AdnocShipToOutboundB2BUnitRegistrationModel adnocShipToOutboundB2BUnitRegistrationModel = new AdnocShipToOutboundB2BUnitRegistrationModel();

        mapCommonAttributes(adnocShipToB2BUnitRegistrationModel, adnocShipToOutboundB2BUnitRegistrationModel);

        adnocShipToOutboundB2BUnitRegistrationModel.setDefaultB2BUnit(adnocShipToB2BUnitRegistrationModel.getDefaultB2BUnit());
        adnocShipToOutboundB2BUnitRegistrationModel.setLatitude(adnocShipToB2BUnitRegistrationModel.getLatitude());
        adnocShipToOutboundB2BUnitRegistrationModel.setLongitude(adnocShipToB2BUnitRegistrationModel.getLongitude());
        adnocShipToOutboundB2BUnitRegistrationModel.setIncoTerms(adnocShipToB2BUnitRegistrationModel.getIncoTerms());
        final Collection<AdnocOutboundDocumentModel> adnocOutboundDocumentModels = new ArrayList<>();
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocShipToB2BUnitRegistrationModel.getIdentificationNumberDocument(), DOCUMENT_TYPE_IDENTIFICATION);
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocShipToB2BUnitRegistrationModel.getOtherDocument(), DOCUMENT_TYPE_OTHER);
        createPopulateAdnocOutboundRegistrationDocument(adnocOutboundDocumentModels, adnocShipToB2BUnitRegistrationModel.getSupportedDocument(), DOCUMENT_TYPE_SUPPORTED);
        adnocShipToOutboundB2BUnitRegistrationModel.setAdnocOutboundDocument(adnocOutboundDocumentModels);

        LOG.debug("appEvent=AdnocOutboundB2BRegistration, B2B Unit Registration conversion complete for UID: {}", adnocShipToB2BUnitRegistrationModel.getEmail());
        return adnocShipToOutboundB2BUnitRegistrationModel;
    }

    @Override
    public AdnocOutboundB2BCustomerModel convertToOutboundB2BCustomer(final B2BCustomerModel b2BCustomerModel)
    {
        LOG.info("appEvent=AdnocOutboundB2BRegistration,Converting B2B Customer Model: {}", b2BCustomerModel.getUid());
        final AdnocOutboundB2BCustomerModel adnocOutboundB2BCustomerModel = new AdnocOutboundB2BCustomerModel();
        adnocOutboundB2BCustomerModel.setUid(b2BCustomerModel.getUid());
        adnocOutboundB2BCustomerModel.setFirstName(b2BCustomerModel.getFirstName());
        adnocOutboundB2BCustomerModel.setDefaultB2BUnit(b2BCustomerModel.getDefaultB2BUnit());
        adnocOutboundB2BCustomerModel.setLastName(b2BCustomerModel.getLastName());
        adnocOutboundB2BCustomerModel.setTitle(b2BCustomerModel.getTitle());
        adnocOutboundB2BCustomerModel.setDescription(b2BCustomerModel.getDescription());
        adnocOutboundB2BCustomerModel.setCountryOfOrigin(b2BCustomerModel.getCountryOfOrigin());
        adnocOutboundB2BCustomerModel.setNationality(b2BCustomerModel.getNationality());
        adnocOutboundB2BCustomerModel.setGenderSapCode(getAdnocConfigService().getAdnocSapIntegrationCodeMap(IntegrationType.OUTBOUND, Gender.class, b2BCustomerModel.getGender().getCode()));
        adnocOutboundB2BCustomerModel.setEmail(b2BCustomerModel.getEmail());
        adnocOutboundB2BCustomerModel.setMobileNumber(b2BCustomerModel.getMobileNumber());
        adnocOutboundB2BCustomerModel.setIdentificationNumber(b2BCustomerModel.getIdentificationNumber());
        adnocOutboundB2BCustomerModel.setIdentificationValidFrom(b2BCustomerModel.getIdentificationValidFrom());
        adnocOutboundB2BCustomerModel.setIdentificationValidTo(b2BCustomerModel.getIdentificationValidTo());
        adnocOutboundB2BCustomerModel.setTelephone(b2BCustomerModel.getTelephone());
        adnocOutboundB2BCustomerModel.setIdentityType(b2BCustomerModel.getIdentityType());
        adnocOutboundB2BCustomerModel.setDesignation(b2BCustomerModel.getDesignation());
        adnocOutboundB2BCustomerModel.setPreferredCommunicationChannel(b2BCustomerModel.getPreferredCommunicationChannel());
        adnocOutboundB2BCustomerModel.setCompanyAddressStreet(b2BCustomerModel.getCompanyAddressStreet());
        adnocOutboundB2BCustomerModel.setCompanyAddressStreetLine2(b2BCustomerModel.getCompanyAddressStreetLine2());
        adnocOutboundB2BCustomerModel.setCompanyAddressCountry(b2BCustomerModel.getCompanyAddressCountry());
        adnocOutboundB2BCustomerModel.setCompanyAddressRegion(b2BCustomerModel.getCompanyAddressRegion());
        adnocOutboundB2BCustomerModel.setCompanyAddressCity(b2BCustomerModel.getCompanyAddressCity());
        final AdnocOutboundDocumentModel adnocOutboundDocumentModel = getAdnocOutboundDocumentModel(b2BCustomerModel.getIdentificationNumberDocument(), DOCUMENT_TYPE_IDENTIFICATION);
        adnocOutboundB2BCustomerModel.setIdentificationNumberDocument(adnocOutboundDocumentModel);
        return adnocOutboundB2BCustomerModel;
    }
    @Override
    public AdnocOutboundCrmCsTicketModel convertToOutboundCrmCsTicket(final CsTicketModel csTicketModel)
    {
        LOG.info("appEvent=AdnocOutboundCrmCsTicket,Converting csTicketModel: {}", csTicketModel.getTicketID());
        final AdnocOutboundCrmCsTicketModel adnocOutboundCrmCsTicket = new AdnocOutboundCrmCsTicketModel();
        adnocOutboundCrmCsTicket.setTicketID(csTicketModel.getTicketID());
        adnocOutboundCrmCsTicket.setCustomer(csTicketModel.getCustomer());
        adnocOutboundCrmCsTicket.setCategory(csTicketModel.getCategory());
        adnocOutboundCrmCsTicket.setRequestFor(csTicketModel.getRequestFor());
        adnocOutboundCrmCsTicket.setSubCategory(csTicketModel.getSubCategory());
        adnocOutboundCrmCsTicket.setTargetSystem(csTicketModel.getTargetSystem());
        adnocOutboundCrmCsTicket.setB2bUnit(csTicketModel.getB2bUnit());
        adnocOutboundCrmCsTicket.setExternalTicketId(csTicketModel.getExternalTicketId());
        if (csTicketModel.getCustomer() instanceof B2BCustomerModel)
        {
            LOG.info("appEvent=AdnocOutboundCrmCsTicket,Getting BP from Current customer model: {}", csTicketModel.getTicketID());
            final B2BCustomerModel customer = (B2BCustomerModel) csTicketModel.getCustomer();
            final B2BUnitModel b2BUnitModel = customer.getDefaultB2BUnit();
            final Set<B2BUnitModel> childB2BUnits = getAdnocB2BUnitService().getChildB2BUnits(b2BUnitModel, PartnerFunction.PY);
            final String division = childB2BUnits.stream().filter(unit -> Objects.nonNull(unit.getSalesOrg())).map(unit -> unit.getSalesOrg().getDivision())
                    .findFirst()
                    .get();
            adnocOutboundCrmCsTicket.setDivision(division);
            adnocOutboundCrmCsTicket.setSapBusinessPartnerId(customer.getSapBusinessPartnerID());
        }

        final Set<AdnocIntegrationCommentModel> integrationComments = getIntegrationComments(csTicketModel);
        adnocOutboundCrmCsTicket.setIntegrationComments(integrationComments);
        adnocOutboundCrmCsTicket.setPriority(csTicketModel.getPriority());
        adnocOutboundCrmCsTicket.setState(csTicketModel.getState());
        LOG.info("appEvent=AdnocOutboundCrmCsTicket,returning converted csTicketModel: {}", csTicketModel.getTicketID());
        return adnocOutboundCrmCsTicket;
    }

    @Override
    public AdnocReturnRequestOutboundModel convertToOutboundReturnRequest(final ReturnRequestModel returnRequestModel)
    {
        LOG.info("appEvent=AdnocOutboundCrmCsTicket,Converting returnRequestModel: {}", returnRequestModel.getCode());
        final AdnocReturnRequestOutboundModel adnocReturnRequestOutboundModel = new AdnocReturnRequestOutboundModel();

        adnocReturnRequestOutboundModel.setCode(returnRequestModel.getCode());
        adnocReturnRequestOutboundModel.setCurrency(returnRequestModel.getCurrency());
        adnocReturnRequestOutboundModel.setCreationtime(returnRequestModel.getCreationtime());
        adnocReturnRequestOutboundModel.setRMA(returnRequestModel.getRMA());
        adnocReturnRequestOutboundModel.setOrder(returnRequestModel.getOrder());
        adnocReturnRequestOutboundModel.setTotalTax(returnRequestModel.getTotalTax().setScale(MONETARY_AMOUNT_SCALE, BigDecimal.ROUND_DOWN));

        adnocReturnRequestOutboundModel.setStatus(returnRequestModel.getStatus());
        adnocReturnRequestOutboundModel.setRefundDeliveryCost(returnRequestModel.getRefundDeliveryCost());
        adnocReturnRequestOutboundModel.setSubtotal(returnRequestModel.getSubtotal().setScale(MONETARY_AMOUNT_SCALE, BigDecimal.ROUND_DOWN));
        adnocReturnRequestOutboundModel.setSapCpiConfig(mapCpiConfigInfo());
        adnocReturnRequestOutboundModel.setReturnEntries(returnRequestModel.getReturnEntries());
        adnocReturnRequestOutboundModel.setSapOrderCode(returnRequestModel.getOrder().getSapOrderCode());
        adnocReturnRequestOutboundModel.getReturnEntries().forEach(returnEntryModel -> returnEntryModel.setTax(returnEntryModel.getTax().setScale(MONETARY_AMOUNT_SCALE, BigDecimal.ROUND_DOWN)));
        Optional.ofNullable(returnRequestModel.getReturnRequestDocument()).
                ifPresent(documentMediaModel -> adnocReturnRequestOutboundModel.setReturnRequestOutboundDocument(convertToOutboundDocumentModel(documentMediaModel, "ReturnRequestDocument")));
        adnocReturnRequestOutboundModel.setReason(returnRequestModel.getReason());
        return adnocReturnRequestOutboundModel;
    }

    @Override
    public AdnocOutboundOverduePaymentTransactionModel convertToAdnocOutboundOverduePaymentTransaction(final AdnocOverduePaymentTransactionModel adnocOverduePaymentTransactionModel)
    {
        final AdnocOutboundOverduePaymentTransactionModel adnocOutboundOverduePaymentTransactionModel = new AdnocOutboundOverduePaymentTransactionModel();
        adnocOutboundOverduePaymentTransactionModel.setCustomer(adnocOverduePaymentTransactionModel.getPayerId());
        if (adnocOverduePaymentTransactionModel.getInfo() instanceof final CreditCardPaymentInfoModel creditCardPaymentInfoModel)
        {
            adnocOutboundOverduePaymentTransactionModel.setMop(CARD);
            adnocOutboundOverduePaymentTransactionModel.setRrnCode(creditCardPaymentInfoModel.getRrnNumber());
            adnocOutboundOverduePaymentTransactionModel.setAuthCode(creditCardPaymentInfoModel.getAuthorizationCode());
            adnocOutboundOverduePaymentTransactionModel.setCardNumber(creditCardPaymentInfoModel.getNumber());
            adnocOutboundOverduePaymentTransactionModel.setCardType(creditCardPaymentInfoModel.getType().getCode());
        }
        else if (adnocOverduePaymentTransactionModel.getInfo() instanceof final BankPaymentInfoModel bankPaymentInfoModel)
        {
            adnocOutboundOverduePaymentTransactionModel.setMop(BANK_TRANSFER);
            adnocOutboundOverduePaymentTransactionModel.setRrnCode(bankPaymentInfoModel.getRrnNumber());
            adnocOutboundOverduePaymentTransactionModel.setAuthCode(bankPaymentInfoModel.getAuthorizationCode());
            adnocOutboundOverduePaymentTransactionModel.setCardNumber(bankPaymentInfoModel.getAccount());
            adnocOutboundOverduePaymentTransactionModel.setCardType(bankPaymentInfoModel.getCardType());
        }
        final BigDecimal plannedAmountScaled = adnocOverduePaymentTransactionModel.getPlannedAmount().setScale(MONETARY_AMOUNT_SCALE, BigDecimal.ROUND_DOWN);
        adnocOutboundOverduePaymentTransactionModel.setTotalAmount(plannedAmountScaled.doubleValue());
        adnocOutboundOverduePaymentTransactionModel.setUtrn(adnocOverduePaymentTransactionModel.getRequestId());
        adnocOutboundOverduePaymentTransactionModel.setInvoiceDetails(adnocOverduePaymentTransactionModel.getInvoiceDetails());
        adnocOutboundOverduePaymentTransactionModel.setCurrency(adnocOverduePaymentTransactionModel.getCurrency().getIsocode());

        return adnocOutboundOverduePaymentTransactionModel;
    }

    @Override
    public AdnocOutboundQuoteModel convertToOutboundQuote(final QuoteModel quoteModel)
    {
        final AdnocOutboundQuoteModel adnocOutboundQuoteModel = new AdnocOutboundQuoteModel();
        final Configuration configuration = getConfigurationService().getConfiguration();
        adnocOutboundQuoteModel.setTransactionType(configuration.getString(ADNOC_QUOTE_TRANSACTIONTYPE));
        adnocOutboundQuoteModel.setSalesOrg(configuration.getString(ADNOC_QUOTE_SALESORG));
        adnocOutboundQuoteModel.setDistribution(configuration.getString(ADNOC_QUOTE_DISTRIBUTION));
        adnocOutboundQuoteModel.setDivision(quoteModel.getEntries().get(0).getDivision());
        adnocOutboundQuoteModel.setQuoteNumber(quoteModel.getCode());
        adnocOutboundQuoteModel.setValidFrom(quoteModel.getCreationtime());
        adnocOutboundQuoteModel.setValidTo(quoteModel.getExpirationTime());

        final List<CommentModel> comments = quoteModel.getComments();
        final StringBuilder combinedComments = new StringBuilder();
        for (final CommentModel comment : comments)
        {
            if (Objects.nonNull(comment) && StringUtils.isNotBlank(comment.getText()))
            {
                combinedComments.append(comment.getText()).append("\n");
            }
        }
        adnocOutboundQuoteModel.setHeaderComments(combinedComments.toString().trim());

        final Set<AdnocOutboundQuoteItemModel> quoteItems = mapOrderItems(quoteModel);
        adnocOutboundQuoteModel.setAdnocOutboundQuoteItem(quoteItems);
        final Set<AdnocQuoteOutboundPartnerRoleModel> adnocQuoteOutboundPartnerRoleModels = mapQuotePartners(quoteModel);
        adnocOutboundQuoteModel.setAdnocQuoteOutboundPartnerRole(adnocQuoteOutboundPartnerRoleModels);
        return adnocOutboundQuoteModel;
    }

    protected Set<AdnocQuoteOutboundPartnerRoleModel> mapQuotePartners(final QuoteModel quoteModel)
    {

        final Set<AdnocQuoteOutboundPartnerRoleModel> adnocQuoteOutboundPartnerRoleModels = new HashSet<>();

        getAdnocB2BQuotePartnerContributor().createB2BRows(quoteModel).forEach(row -> {
            final AdnocQuoteOutboundPartnerRoleModel adnocQuoteOutboundPartnerRoleModel = new AdnocQuoteOutboundPartnerRoleModel();
            adnocQuoteOutboundPartnerRoleModel.setQuoteCode(mapAttribute(OrderCsvColumns.ORDER_ID, row));
            adnocQuoteOutboundPartnerRoleModel.setDocumentAddressId(mapAttribute(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, row));
            adnocQuoteOutboundPartnerRoleModel.setPartnerId(mapAttribute(PartnerCsvColumns.PARTNER_CODE, row));
            adnocQuoteOutboundPartnerRoleModel.setPartnerRoleCode(mapAttribute(PartnerCsvColumns.PARTNER_ROLE_CODE, row));
            adnocQuoteOutboundPartnerRoleModels.add(adnocQuoteOutboundPartnerRoleModel);
        });
        return adnocQuoteOutboundPartnerRoleModels;
    }

    protected String mapAttribute(final String attribute, final Map<String, Object> row)
    {
        return row.get(attribute) != null ? row.get(attribute).toString() : null;
    }

    private Set<AdnocOutboundQuoteItemModel> mapOrderItems(final QuoteModel quoteModel)
    {
        final Set<AdnocOutboundQuoteItemModel> adnocOutboundQuoteItems = new HashSet<>();
        LOG.info("appEvent=AdnocSapCpiOmmOrder, mapping order items for Quote:{}", quoteModel.getCode());

        quoteModel.getEntries().forEach(orderEntryModel -> {

            final AdnocOutboundQuoteItemModel adnocOutboundQuoteItem = new AdnocOutboundQuoteItemModel();
            adnocOutboundQuoteItem.setEntryNumber(String.valueOf((orderEntryModel.getEntryNumber() + 1) * 10));
            adnocOutboundQuoteItem.setQuantity(orderEntryModel.getQuantity().toString());
            adnocOutboundQuoteItem.setProductCode(orderEntryModel.getProduct().getCode());
            adnocOutboundQuoteItem.setUnit(orderEntryModel.getUnit().getCode());

            final List<CommentModel> itemComments = orderEntryModel.getComments();
            final StringBuilder itemText = new StringBuilder();
            for (final CommentModel comment : itemComments)
            {
                if (Objects.nonNull(comment) && StringUtils.isNotBlank(comment.getText()))
                {
                    itemText.append(comment.getText()).append("\n");
                }
            }
            adnocOutboundQuoteItem.setItemText(itemText.toString().trim());

            if (Objects.nonNull(orderEntryModel.getNamedDeliveryDate()))
            {
                final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                final LocalDate namedDeliveryLocalDate = orderEntryModel.getNamedDeliveryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                adnocOutboundQuoteItem.setDeliveryDate(namedDeliveryLocalDate.format(dateTimeFormatter));
            }
            adnocOutboundQuoteItems.add(adnocOutboundQuoteItem);
            LOG.debug("appEvent=AdnocSapCpiOmmOrder, added order item:{}", orderEntryModel.getEntryNumber());
        });
        return adnocOutboundQuoteItems;
    }

    /**
     * @param documentMediaModel
     * @param supportedDocumentType
     * @return
     */
    private AdnocOutboundDocumentModel convertToOutboundDocumentModel(final DocumentMediaModel documentMediaModel, final String supportedDocumentType)
    {
        final AdnocOutboundDocumentModel adnocOutboundDocument = new AdnocOutboundDocumentModel();
        adnocOutboundDocument.setDocumentBase64(getMediaContentBase64Encoded(documentMediaModel));
        adnocOutboundDocument.setDocumentName(getFileName(documentMediaModel));
        adnocOutboundDocument.setDocumentType(getFileType(documentMediaModel));
        adnocOutboundDocument.setSupportedDocumentType(supportedDocumentType);
        return adnocOutboundDocument;
    }


    protected SAPCpiOutboundConfigModel mapCpiConfigInfo()
    {
        LOG.info("appEvent=AdnocOutboundB2BRegistration,mapping Cpi config info");
        final SapCpiTargetSystem sapCpiTargetSystem = new SapCpiTargetSystem();

        final SAPLogicalSystemModel sapLogicalSystem = getSapCpiOrderDestinationService().readSapLogicalSystem();
        sapCpiTargetSystem.setSenderName(sapLogicalSystem.getSenderName());
        sapCpiTargetSystem.setSenderPort(sapLogicalSystem.getSenderPort());
        sapCpiTargetSystem.setReceiverName(sapLogicalSystem.getSapLogicalSystemName());
        sapCpiTargetSystem.setReceiverPort(sapLogicalSystem.getSapLogicalSystemName());
        Objects.requireNonNull(sapLogicalSystem.getSapHTTPDestination(), "No HTTP destination is maintained in back-office for order replication to SCPI!");
        sapCpiTargetSystem.setUsername(sapLogicalSystem.getSapHTTPDestination().getUserid());
        sapCpiTargetSystem.setUrl(getSapCpiOrderDestinationService().determineUrlDestination(sapLogicalSystem));
        sapCpiTargetSystem.setClient(getSapCpiOrderDestinationService().extractSapClient(sapLogicalSystem));
        final SapCpiConfig sapCpiConfig = new SapCpiConfig();
        sapCpiConfig.setSapCpiTargetSystem(sapCpiTargetSystem);

        final SAPCpiOutboundConfigModel sapCpiOutboundConfig = new SAPCpiOutboundConfigModel();
        sapCpiOutboundConfig.setUrl(sapCpiTargetSystem.getUrl());
        sapCpiOutboundConfig.setUsername(sapCpiTargetSystem.getUsername());
        sapCpiOutboundConfig.setClient(sapCpiTargetSystem.getClient());
        sapCpiOutboundConfig.setSenderName(sapCpiTargetSystem.getSenderName());
        sapCpiOutboundConfig.setSenderPort(sapCpiTargetSystem.getSenderPort());
        sapCpiOutboundConfig.setReceiverName(sapCpiTargetSystem.getReceiverName());
        sapCpiOutboundConfig.setReceiverPort(sapCpiTargetSystem.getReceiverPort());

        return sapCpiOutboundConfig;
    }

    private Set<AdnocIntegrationCommentModel> getIntegrationComments(final CsTicketModel csTicketModel)
    {
        final List<CsTicketEventModel> csTicketEventModels = StringUtils.isNotBlank(csTicketModel.getExternalTicketId()) ?
                getAdnocTicketService().getUpdateEventsForTicket(csTicketModel) : getAdnocTicketService().getEventsForTicket(csTicketModel);
        if (CollectionUtils.isEmpty(csTicketEventModels))
        {
            LOG.info("appEvent=AdnocOutboundCsTicket,No ticket events found for ticket:{}", csTicketModel.getTicketID());
            return Collections.emptySet();
        }
        final Set<AdnocIntegrationCommentModel> integrationComments = new HashSet<>();
        for (final CommentModel commentModel : csTicketEventModels)
        {
            final AdnocIntegrationCommentModel adnocIntegrationCommentModel = new AdnocIntegrationCommentModel();
            adnocIntegrationCommentModel.setCode(commentModel.getCode());
            adnocIntegrationCommentModel.setText(commentModel.getText());
            final Set<AdnocAttachmentModel> attachments = getAttachments(commentModel);
            adnocIntegrationCommentModel.setAttachments(attachments);
            integrationComments.add(adnocIntegrationCommentModel);
        }
        LOG.info("appEvent=AdnocOutboundCsTicket,At End of Integration comments for ticket:{}", csTicketModel.getTicketID());
        return integrationComments;
    }

    private Set<AdnocAttachmentModel> getAttachments(final CommentModel commentModel)
    {
        if (CollectionUtils.isEmpty(commentModel.getAttachments()))
        {
            LOG.info("appEvent=AdnocOutboundCsTicket,No attachments found for comment: {}", commentModel.getCode());
            return Collections.emptySet();
        }
        final Set<AdnocAttachmentModel> attachments = new HashSet<>();
        for (final CommentAttachmentModel commentAttachmentModel : commentModel.getAttachments())
        {
            if (commentAttachmentModel.getItem() instanceof final MediaModel mediaModel)
            {
                final AdnocAttachmentModel adnocAttachment = new AdnocAttachmentModel();
                adnocAttachment.setAttachmentBase64(getMediaContentBase64Encoded(mediaModel));
                adnocAttachment.setRealFileName(mediaModel.getRealFileName());
                attachments.add(adnocAttachment);
            }
        }
        LOG.info("appEvent=AdnocOutboundCsTicket,Adding and returning attachment for comment: {}", commentModel.getCode());
        return attachments;
    }

    private String getMediaContentBase64Encoded(final MediaModel mediaModel)
    {
        LOG.info("appEvent=AdnocOutboundB2BRegistration,encoding media content to Base64 for media with code: {}", mediaModel.getCode());
        final byte[] mediaByteArray = getMediaService().getDataFromMedia(mediaModel);
        return Base64.getEncoder().encodeToString(mediaByteArray);
    }

    private String getFileName(final MediaModel mediaModel)
    {
        return mediaModel.getRealFileName();
    }

    private String getFileType(final MediaModel mediaModel)
    {
        return mediaModel.getMime();
    }

    protected BaseSiteService getBaseSiteService()
    {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService)
    {
        this.baseSiteService = baseSiteService;
    }

    protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
    {
        return siteBaseUrlResolutionService;
    }

    public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
    {
        this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    protected MediaService getMediaService()
    {
        return mediaService;
    }

    public void setMediaService(final MediaService mediaService)
    {
        this.mediaService = mediaService;
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    protected SapCpiOrderDestinationService getSapCpiOrderDestinationService()
    {
        return sapCpiOrderDestinationService;
    }

    public void setSapCpiOrderDestinationService(final SapCpiOrderDestinationService sapCpiOrderDestinationService)
    {
        this.sapCpiOrderDestinationService = sapCpiOrderDestinationService;
    }

    protected AdnocTicketService getAdnocTicketService()
    {
        return adnocTicketService;
    }

    public void setAdnocTicketService(final AdnocTicketService adnocTicketService)
    {
        this.adnocTicketService = adnocTicketService;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    protected AdnocB2BQuotePartnerContributor getAdnocB2BQuotePartnerContributor()
    {
        return adnocB2BQuotePartnerContributor;
    }

    public void setAdnocB2BQuotePartnerContributor(final AdnocB2BQuotePartnerContributor adnocB2BQuotePartnerContributor)
    {
        this.adnocB2BQuotePartnerContributor = adnocB2BQuotePartnerContributor;
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