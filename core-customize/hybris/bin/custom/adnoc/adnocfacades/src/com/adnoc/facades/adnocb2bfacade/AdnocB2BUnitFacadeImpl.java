package com.adnoc.facades.adnocb2bfacade;

import com.adnoc.facades.b2b.unit.data.AdnocB2BUnitRegistrationData;
import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.facades.registration.AdnocB2BRegistrationFacade;
import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.enums.*;
import com.adnoc.service.model.AdnocB2BUnitRegistrationModel;
import com.adnoc.service.model.AdnocPayerB2BUnitRegistrationModel;
import com.adnoc.service.model.AdnocRegistrationModel;
import com.adnoc.service.model.AdnocShipToB2BUnitRegistrationModel;
import com.adnoc.service.registration.AdnocB2BRegistrationService;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.jalo.B2BCustomer;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bacceleratorfacades.registration.B2BRegistrationWorkflowFacade;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.b2bcommercefacades.company.impl.DefaultB2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commercewebservices.core.user.data.AddressDataList;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ClassMismatchException;
import de.hybris.platform.servicelayer.exceptions.ConfigurationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.ticket.service.UnsupportedAttachmentException;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdnocB2BUnitFacadeImpl extends DefaultB2BUnitFacade implements AdnocB2BUnitFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BUnitFacadeImpl.class);

    private static final String C_STORE_DIVISION = "23";

    private Converter<AddressModel, AddressData> addressConverter;
    private AdnocB2BUnitService adnocB2BUnitService;
    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService;
    private CommonI18NService commonI18NService;
    private MediaService mediaService;
    private EnumerationService enumerationService;
    private B2BRegistrationWorkflowFacade b2bRegistrationWorkflowFacade;
    private WorkflowTemplateService workflowTemplateService;
    private AdnocB2BRegistrationService adnocB2BRegistrationService;
    private String allowedUploadedFormats;
    private AdnocB2BRegistrationFacade adnocB2BRegistrationFacade;
    private SearchRestrictionService searchRestrictionService;
    private Converter<IncoTerms, IncoTermsData> incoTermsTypeDataConverter;

    @Override
    public List<B2BUnitData> getB2BUnits(final PartnerFunction partnerFunction)
    {
        LOG.info("appEvent=AdnocB2BUnit, getB2BUnits method called with partnerFunction:{}", partnerFunction);
        final Set<B2BUnitModel> b2bUnitsForCustomer = getAdnocB2BUnitService().getB2BUnits(getB2BCustomerService().getCurrentB2BCustomer(), partnerFunction);
        return getB2BUnitConverter().convertAll(b2bUnitsForCustomer);
    }

    @Override
    public AddressDataList getShippingAddressList(final String division, final String incoTermsCode)
    {
        LOG.info("appEvent=AdnocB2BUnit, getShippingAddressList method called.");
        final AddressDataList addressDataList = new AddressDataList();
        final Set<B2BUnitModel> shipToB2BUnitsRelatedToDivision = getShipToB2BUnitsRelatedToDivision(division);
        final IncoTerms incoTerms = getEnumerationService().getEnumerationValue(IncoTerms._TYPECODE, incoTermsCode);
        final Set<B2BUnitModel> shipToB2BUnitsRelatedToIncoTerms = shipToB2BUnitsRelatedToDivision.stream().filter(b2BUnitModel -> Objects.equals(incoTerms, b2BUnitModel.getIncoTerms()))
                .collect(Collectors.toSet());
        final Set<AddressModel> shippingAddressess = shipToB2BUnitsRelatedToIncoTerms.stream()
                .flatMap(b2bUnit -> Objects.nonNull(b2bUnit.getShippingAddresses())
                        ? b2bUnit.getShippingAddresses().stream()
                        : Stream.empty())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        final List<AddressData> addressData = getAddressConverter().convertAll(shippingAddressess);
        addressDataList.setAddresses(addressData);

        LOG.info("getShippingAddressList method with {} addresses.", addressData != null ? addressData.size() : 0);
        return addressDataList;
    }

    private Set<B2BUnitModel> getShipToB2BUnitsRelatedToDivision(final String division)
    {
        final B2BUnitModel b2BUnitModel = getAdnocB2BUnitService().getCurrentB2BUnit();
        final Set<B2BUnitModel> shipToB2BUnits = getAdnocB2BUnitService().getChildB2BUnits(b2BUnitModel, PartnerFunction.SH);
        final Set<B2BUnitModel> shipToB2BUnitsRelatedToDivision = shipToB2BUnits.stream().filter(shipToB2BUnit -> Objects.nonNull(shipToB2BUnit.getSalesOrg()))
                .filter(shipToB2BUnit -> StringUtils.equals(division, shipToB2BUnit.getSalesOrg().getDivision())).collect(Collectors.toSet());
        LOG.info("appEvent=AdnocB2BUnit, Filtered {} ShipTo B2BUnits for Division={}", shipToB2BUnitsRelatedToDivision.size(), division);
        return shipToB2BUnitsRelatedToDivision;
    }


    @Override
    public List<IncoTermsData> getIncoTerms(final String division, final boolean pickup)
    {
        LOG.info("appEvent=B2BCustomer,getIncoTerms method called");
        Set<IncoTerms> incoTermsSet;
        if (StringUtils.equals(division, C_STORE_DIVISION) && pickup)
        {
            incoTermsSet = Set.of(IncoTerms.PICKUP);
            LOG.info("appEvent=AdnocB2BUnit,setting incoterm pickup for C-Store Division :{}", incoTermsSet);
        }
        else
        {
            LOG.info("appEvent=AdnocB2BUnit, retrieving ship-to B2B units for division: {}", division);
            final Set<B2BUnitModel> shipToB2BUnitsRelatedToDivision = getShipToB2BUnitsRelatedToDivision(division);
            LOG.info("appEvent=AdnocB2BUnit, found {} ship-to B2B units for division {}",
                    shipToB2BUnitsRelatedToDivision.size(), division);
            incoTermsSet = shipToB2BUnitsRelatedToDivision.stream().map(B2BUnitModel::getIncoTerms)
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            incoTermsSet = pickup ? incoTermsSet : incoTermsSet.stream().filter(incoTerms -> !Objects.equals(IncoTerms.PICKUP, incoTerms))
                    .collect(Collectors.toSet());
        }
        return getIncoTermsTypeDataConverter().convertAll(incoTermsSet);
    }

    @Override
    public List<IncoTermsData> getShipToApplicableIncoTerms()
    {
        LOG.info("appEvent=B2BCustomer,getIncoTerms method called");
        final List<String> shipToApplicableIncoTerms = List.of("DAP", "FOB");

        final List<IncoTerms> allIncoTerms = getEnumerationService().getEnumerationValues(IncoTerms._TYPECODE);
        final List<IncoTerms> filteredIncoTerms = allIncoTerms.stream()
                .filter(term -> shipToApplicableIncoTerms.contains(term.getCode())).toList();
        return Converters.convertAll(filteredIncoTerms, getIncoTermsTypeDataConverter());
    }

    @Override
    public List<B2BUnitData> getCurrentB2BUnits()
    {
        LOG.info("appEvent=AdnocB2BUnit, getCurrentB2BUnits method called.");
        final B2BCustomerModel b2bCustomer = getB2BCustomerService().getCurrentB2BCustomer();
        if (CollectionUtils.isEmpty(b2bCustomer.getGroups()))
        {
            throw new ConfigurationException(String.format("No Usergroup is configured with the customer '%s'", b2bCustomer.getUid()));
        }
        LOG.debug("appEvent=AdnocB2BUnit, filtering b2bUnitModels from user groups.. ");
        final Set<B2BUnitModel> b2bUnitModels = b2bCustomer.getGroups().stream()
                .filter(B2BUnitModel.class::isInstance).map(B2BUnitModel.class::cast).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(b2bUnitModels))
        {
            throw new ConfigurationException(String.format("No B2BUnit is configured with the customer '%s'", b2bCustomer.getUid()));
        }
        return getB2BUnitConverter().convertAll(b2bUnitModels);
    }

    @Override
    public void setCurrentB2BUnit(final String b2BUnitUid)
    {
        LOG.info("appEvent=AdnocB2BUnit, setCurrentB2BUnit method called with B2BUnit UID:{}", b2BUnitUid);
        final B2BUnitModel b2BUnitModel = getSessionService().executeInLocalView(new SessionExecutionBody()
        {
            @Override
            public Object execute()
            {
                getSearchRestrictionService().disableSearchRestrictions();
                return getAdnocB2BUnitService().getUnitForUid(b2BUnitUid);
            }
        });

        if (Objects.isNull(b2BUnitModel))
        {
            throw new UnknownIdentifierException(String.format("No B2BUnit found with uid='%s'.", b2BUnitUid));
        }
        if (Boolean.FALSE.equals(b2BUnitModel.getActive()))
        {
            final B2BCustomerModel b2BCustomerModel = getB2BCustomerService().getCurrentB2BCustomer();

            final boolean isAdmin = b2BCustomerModel.getAllGroups().stream().anyMatch(principalGroupModel -> StringUtils.equals(principalGroupModel.getUid(), B2BConstants.B2BADMINGROUP));
            final String contactPerson = isAdmin ? "ADNOC sales manager" : "your Admin";
            final String errorMessage = String.format("Your account is blocked, please contact %s", contactPerson);

            LOG.warn("appEvent=AdnocB2BUnit, B2BUnit '{}' is inactive. Blocking access for customer '{}'.", b2BUnitModel.getUid(), b2BCustomerModel.getUid());
            throw new InvalidGrantException(errorMessage);
        }

        LOG.info("appEvent=AdnocB2BUnit, successfully retrieved B2BUnit with UID:{}", b2BUnitUid);
        getAdnocB2BUnitService().setCurrentB2BUnit(b2BUnitModel);
    }

    @Override
    public String registerNewB2BUnit(final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData) throws CustomerAlreadyExistsException
    {
        LOG.info("appEvent=AdnocB2BUnit,registerNewB2BUnit method called for user:{}", adnocB2BUnitRegistrationData.getEmail());
        final Transaction tx = Transaction.current();
        tx.begin();
        boolean success = false;
        final AdnocB2BUnitRegistrationModel b2BUnitRegistrationModel;
        try
        {
            final String adnocRegistrationType = AdnocB2BUnitRegistrationModel._TYPECODE;
            final Map<String, String> duplicateCheckMap = new HashMap<>();
            duplicateCheckMap.put(AdnocB2BUnitRegistrationModel.EMAIL, adnocB2BUnitRegistrationData.getEmail());
            duplicateCheckMap.put(AdnocB2BUnitRegistrationModel.IDENTIFICATIONNUMBER, adnocB2BUnitRegistrationData.getIdentificationNumber());

            final AdnocRegistrationModel adnocB2BRegistrationModel = getAdnocB2BRegistrationService().getAdnocB2BRegistration(duplicateCheckMap, adnocRegistrationType);
            if (Objects.nonNull(adnocB2BRegistrationModel))
            {
                LOG.debug("appEvent=B2BRegistration,Already Registration request found with email={} ", adnocB2BUnitRegistrationData.getEmail());
                throw new CustomerAlreadyExistsException("Registration Request already exist with status = " + adnocB2BRegistrationModel.getStatus());
            }

            // Check if a user using the same email exist, if so we need to abort the current operation!
            final boolean userExists = getUserService().isUserExisting(StringUtils.lowerCase(adnocB2BUnitRegistrationData.getEmail()));
            if (userExists)
            {
                LOG.debug("appEvent=B2BRegistration, user with uid={} already exists!", adnocB2BUnitRegistrationData.getEmail());
                throw new CustomerAlreadyExistsException(String.format("Account %s already exist!", adnocB2BUnitRegistrationData.getEmail()));
            }

            b2BUnitRegistrationModel = toB2BUnitRegistrationModel(adnocB2BUnitRegistrationData);
            final CustomerModel customer = toCustomerModel(adnocB2BUnitRegistrationData);
            b2BUnitRegistrationModel.setCustomer(customer);

            getModelService().saveAll(customer, b2BUnitRegistrationModel);
            final WorkflowTemplateModel workflowTemplate = getWorkflowTemplateService().getWorkflowTemplateForCode(
                    B2BConstants.Workflows.REGISTRATION_WORKFLOW);

            LOG.info("appEvent=AdnocB2BUnit,Created Adnoc:{} Workflow", B2BConstants.Workflows.REGISTRATION_WORKFLOW);
            getB2bRegistrationWorkflowFacade().launchWorkflow(workflowTemplate, b2BUnitRegistrationModel);

            tx.commit();
            success = true;
            return b2BUnitRegistrationModel.getPk().getLongValueAsString();
        } finally
        {
            if (!success)
            {
                tx.rollback();
            }
        }
    }

    protected AdnocB2BUnitRegistrationModel toB2BUnitRegistrationModel(final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData)
    {
        final AdnocB2BUnitRegistrationModel b2BUnitRegistrationModel;
        if (Objects.equals(com.adnoc.facades.company.data.PartnerFunction.PY, adnocB2BUnitRegistrationData.getPartnerFunction()))
        {
            final AdnocPayerB2BUnitRegistrationModel adnocPayerB2BUnitRegistrationModel = getModelService().create(AdnocPayerB2BUnitRegistrationModel.class);
            populatePayerVatIdDocument(adnocPayerB2BUnitRegistrationModel, adnocB2BUnitRegistrationData);
            adnocPayerB2BUnitRegistrationModel.setVatId(adnocB2BUnitRegistrationData.getVatId());

            b2BUnitRegistrationModel = adnocPayerB2BUnitRegistrationModel;
        }
        else
        {
            final AdnocShipToB2BUnitRegistrationModel adnocShipToB2BUnitRegistrationModel = getModelService().create(AdnocShipToB2BUnitRegistrationModel.class);
            adnocShipToB2BUnitRegistrationModel.setLatitude(adnocB2BUnitRegistrationData.getLatitude());
            adnocShipToB2BUnitRegistrationModel.setLongitude(adnocB2BUnitRegistrationData.getLongitude());

            b2BUnitRegistrationModel = adnocShipToB2BUnitRegistrationModel;
        }
        LOG.info("appEvent=AdnocB2BUnit, created adnocB2BUnitRegistrationModel: {}", b2BUnitRegistrationModel);

        final B2BUnitModel b2BUnitModel = getAdnocB2BUnitService().getUnitForUid(adnocB2BUnitRegistrationData.getDefaultB2BUnit());
        if (Objects.isNull(b2BUnitModel))
        {
            throw new UnknownIdentifierException("No B2B Unit for given Uid" + adnocB2BUnitRegistrationData.getDefaultB2BUnit());
        }
        b2BUnitRegistrationModel.setDefaultB2BUnit(b2BUnitModel);
        b2BUnitRegistrationModel.setCustomerCategory(b2BUnitModel.getCustomerCategory());

        final B2BUnitModel soldToB2BUnitModel = getAdnocB2BUnitService().getCurrentSoldTo();
        if (Objects.nonNull(soldToB2BUnitModel))
        {
            b2BUnitRegistrationModel.setCustomerGroup(soldToB2BUnitModel.getCustomerGroup());
        }

        b2BUnitRegistrationModel.setSalesOffice(b2BUnitModel.getSalesOffice());
        b2BUnitRegistrationModel.setSalesGroup(b2BUnitModel.getSalesGroup());

        // Use reflection to copy most properties and ignore these since we want to manage them manually
        final String[] ignoreProperties = {"titleCode", "companyAddressCountryIso", "companyAddressRegion", "baseStore", "cmsSite", "currency", "language"};
        BeanUtils.copyProperties(adnocB2BUnitRegistrationData, b2BUnitRegistrationModel, ignoreProperties);
        LOG.info("appEvent=AdnocB2BUnit,Successfully copied properties to AdnocB2BUnitRegistrationModel");

        populateB2BUnitInfo(b2BUnitRegistrationModel, adnocB2BUnitRegistrationData);
        populateCustomerInfo(b2BUnitRegistrationModel, adnocB2BUnitRegistrationData);
        getAdnocB2BRegistrationFacade().populateStoreInfo(b2BUnitRegistrationModel);
        return b2BUnitRegistrationModel;
    }

    private void populateB2BUnitInfo(final AdnocB2BUnitRegistrationModel b2BUnitRegistrationModel, final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData)
    {
        LOG.info("appEvent=B2BRegistration, Populating B2BUnit information for registration model...");

        if (Objects.equals(adnocB2BUnitRegistrationData.getPartnerFunction().name(), PartnerFunction.SH.getCode()))
        {
            b2BUnitRegistrationModel.setIncoTerms(getEnumerationService().getEnumerationValue(IncoTerms._TYPECODE, adnocB2BUnitRegistrationData.getIncoTerms()));
            LOG.info("appEvent=B2BRegistration, Set Incoterms as partner function is SH.");
        }

        b2BUnitRegistrationModel.setCompanyName(adnocB2BUnitRegistrationData.getCompanyName());
        b2BUnitRegistrationModel.setCompanyName2(adnocB2BUnitRegistrationData.getCompanyName2());
        b2BUnitRegistrationModel.setCompanyAddressStreet(adnocB2BUnitRegistrationData.getCompanyAddressStreet());
        b2BUnitRegistrationModel.setCompanyAddressStreetLine2(adnocB2BUnitRegistrationData.getCompanyAddressStreetLine2());
        if (StringUtils.isNotBlank(adnocB2BUnitRegistrationData.getCompanyAddressCountryIso()))
        {
            final CountryModel countryModel = getCommonI18NService().getCountry(adnocB2BUnitRegistrationData.getCompanyAddressCountryIso());
            b2BUnitRegistrationModel.setCompanyAddressCountry(countryModel);
            LOG.info("appEvent=B2BRegistration, Set company address country: {}", adnocB2BUnitRegistrationData.getCompanyAddressCountryIso());

            if (StringUtils.isNotBlank(adnocB2BUnitRegistrationData.getCompanyAddressRegion()))
            {
                final RegionModel regionModel = getCommonI18NService().getRegion(countryModel, adnocB2BUnitRegistrationData.getCompanyAddressRegion());
                b2BUnitRegistrationModel.setCompanyAddressRegion(regionModel);
                LOG.info("appEvent=B2BRegistration, Set company address region: {}", adnocB2BUnitRegistrationData.getCompanyAddressRegion());
            }
        }
        b2BUnitRegistrationModel.setCompanyAddressPostalCode(adnocB2BUnitRegistrationData.getCompanyAddressPostalCode());
        b2BUnitRegistrationModel.setCompanyAddressCity(adnocB2BUnitRegistrationData.getCompanyAddressCity());
        b2BUnitRegistrationModel.setFaxNumber(adnocB2BUnitRegistrationData.getFaxNumber());
        b2BUnitRegistrationModel.setPoBox(adnocB2BUnitRegistrationData.getPoBox());
        b2BUnitRegistrationModel.setPartnerFunction(
                (PartnerFunction) getEnumerationValue(PartnerFunction.class, adnocB2BUnitRegistrationData.getPartnerFunction().toString())
        );
        populateSupportedDocument(b2BUnitRegistrationModel, adnocB2BUnitRegistrationData);
        LOG.info("appEvent=B2BRegistration, Populated supported documents for registration model.");
        LOG.info("appEvent=B2BRegistration, B2BUnit information population completed.");
    }


    private void populateCustomerInfo(final AdnocB2BUnitRegistrationModel b2BUnitRegistrationModel, final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData)
    {
        if (StringUtils.isNotBlank(adnocB2BUnitRegistrationData.getTitleCode()))
        {
            LOG.info("retrieving title code:{}", adnocB2BUnitRegistrationData.getTitleCode());
            final TitleModel titleModel = getUserService().getTitleForCode(adnocB2BUnitRegistrationData.getTitleCode());
            b2BUnitRegistrationModel.setTitle(titleModel);
        }
        b2BUnitRegistrationModel.setFirstName(adnocB2BUnitRegistrationData.getFirstName());
        b2BUnitRegistrationModel.setLastName(adnocB2BUnitRegistrationData.getLastName());
        b2BUnitRegistrationModel.setGender((Gender) getEnumerationValue(Gender.class, adnocB2BUnitRegistrationData.getGender()));
        b2BUnitRegistrationModel.setCountryOfOrigin((Nationality) getEnumerationValue(Nationality.class, adnocB2BUnitRegistrationData.getCountryOfOrigin()));
        b2BUnitRegistrationModel.setNationality((Nationality) getEnumerationValue(Nationality.class, adnocB2BUnitRegistrationData.getNationality()));
        b2BUnitRegistrationModel.setIdentityType((IdentityType) getEnumerationValue(IdentityType.class, adnocB2BUnitRegistrationData.getIdentityType()));
        b2BUnitRegistrationModel.setIdentificationNumber(adnocB2BUnitRegistrationData.getIdentificationNumber());
        b2BUnitRegistrationModel.setIdentificationValidFrom(adnocB2BUnitRegistrationData.getIdentificationValidFrom());
        b2BUnitRegistrationModel.setIdentificationValidTo(adnocB2BUnitRegistrationData.getIdentificationValidTo());
        b2BUnitRegistrationModel.setDesignation((Designation) getEnumerationValue(Designation.class, adnocB2BUnitRegistrationData.getDesignation()));
        LOG.info("appEvent=B2BRegistration, Populated customer info for B2BUnit registration (firstName: {}, lastName: {}, email: {}).",
                adnocB2BUnitRegistrationData.getFirstName(),
                adnocB2BUnitRegistrationData.getLastName(),
                adnocB2BUnitRegistrationData.getEmail());
    }

    private CustomerModel toCustomerModel(final B2BRegistrationData data)
    {
        LOG.info("appEvent=B2BRegistration, toCustomerModel method called...");
        final CustomerModel model = getModelService().create(CustomerModel.class);
        final String capitalizedName = WordUtils.capitalizeFully(data.getName());
        model.setName(capitalizedName);
        model.setUid(data.getEmail());
        model.setLoginDisabled(true);
        LOG.info("appEvent=B2BRegistration, Login disabled for customer.");
        final String customerId = UUID.randomUUID().toString();
        model.setCustomerID(customerId);
        LOG.info("appEvent=B2BRegistration, Set customerID: {}", customerId);
        model.setSessionLanguage(commonI18NService.getCurrentLanguage());
        LOG.info("appEvent=B2BRegistration, Set session language: {}", commonI18NService.getCurrentLanguage());
        model.setSessionCurrency(commonI18NService.getCurrentCurrency());
        LOG.info("appEvent=B2BRegistration, Set session currency: {}", commonI18NService.getCurrentCurrency());
        if (StringUtils.isNotBlank(data.getTitleCode()))
        {
            LOG.info("appEvent=B2BRegistration, Title code present: {}. Fetching and setting title...", data.getTitleCode());
            final TitleModel title = getUserService().getTitleForCode(data.getTitleCode());
            model.setTitle(title);
        }
        else
        {
            LOG.info("appEvent=B2BRegistration, Title code is blank. Skipping title assignment.");
        }
        model.setType(CustomerType.TEMPORARY);
        LOG.info("appEvent=B2BRegistration, Set customer type to TEMPORARY.");
        LOG.info("appEvent=B2BRegistration, CustomerModel created and populated successfully.");
        return model;
    }


    private void populateSupportedDocument(final AdnocB2BUnitRegistrationModel adnocB2BUnitRegistrationModel,
                                           final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData)
    {
        try
        {
            if (Objects.nonNull(adnocB2BUnitRegistrationData.getIdentificationNumberDocument()))
            {
                final MultipartFile identificationNumberDocument = adnocB2BUnitRegistrationData.getIdentificationNumberDocument();
                final DocumentMediaModel idnDocumentMediaModel = createAttachment(identificationNumberDocument.getOriginalFilename(), identificationNumberDocument.getContentType(),
                        identificationNumberDocument.getBytes());
                adnocB2BUnitRegistrationModel.setIdentificationNumberDocument(idnDocumentMediaModel);
                LOG.info("appEvent= B2BUnitRegistration, Successfully created identificationNumberDocument attachment for:{}", identificationNumberDocument.getOriginalFilename());
            }
            if (Objects.nonNull(adnocB2BUnitRegistrationData.getOtherDocument()))
            {
                final MultipartFile otherDocument = adnocB2BUnitRegistrationData.getOtherDocument();
                final DocumentMediaModel otherDocumentMediaModel = createAttachment(otherDocument.getOriginalFilename(), otherDocument.getContentType(),
                        otherDocument.getBytes());
                adnocB2BUnitRegistrationModel.setOtherDocument(otherDocumentMediaModel);
                LOG.info("appEvent= B2BUnitRegistration, Successfully created otherDocument attachment for:{}", otherDocument.getOriginalFilename());
            }
        }
        catch (final IOException ioException)
        {
            LOG.error(ioException.getMessage(), ioException);
            throw new RuntimeException("appEvent= B2BUnitRegistration, Failed to create attachment due to " + ExceptionUtils.getRootCauseMessage(ioException));
        }
    }

    private void populatePayerVatIdDocument(final AdnocPayerB2BUnitRegistrationModel adnocPayerB2BUnitRegistrationModel,
                                            final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData)
    {
        try
        {
            if (Objects.nonNull(adnocB2BUnitRegistrationData.getVatIdDocument()))
            {
                final MultipartFile otherDocument = adnocB2BUnitRegistrationData.getVatIdDocument();
                final DocumentMediaModel vatIdDocumentMediaModel = createAttachment(otherDocument.getOriginalFilename(), otherDocument.getContentType(),
                        otherDocument.getBytes());
                adnocPayerB2BUnitRegistrationModel.setVatIdDocument(vatIdDocumentMediaModel);
                LOG.info("appEvent= B2BUnitRegistration, Successfully created otherDocument attachment for:{}", otherDocument.getOriginalFilename());
            }
        }
        catch (final IOException ioException)
        {
            LOG.error(ioException.getMessage(), ioException);
            throw new RuntimeException("appEvent= B2BUnitRegistration, Failed to create attachment due to " + ExceptionUtils.getRootCauseMessage(ioException));
        }
    }


    private DocumentMediaModel createAttachment(final String fileName, final String contentType, final byte[] data)
    {
        checkFileExtension(fileName);

        LOG.info("creating documentMediaModel with fileName:{}", fileName);
        final DocumentMediaModel documentMediaModel = getModelService().create(DocumentMediaModel.class);
        documentMediaModel.setCode(UUID.randomUUID().toString());
        documentMediaModel.setMime(contentType);
        documentMediaModel.setRealFileName(fileName);
        getModelService().save(documentMediaModel);
        getMediaService().setDataForMedia(documentMediaModel, data);
        return documentMediaModel;
    }

    private void checkFileExtension(final String name)
    {
        if (!FilenameUtils.isExtension(name.toLowerCase(), getAllowedUploadedFormats().replaceAll("\\s", "").toLowerCase().split(",")))
        {
            throw new UnsupportedAttachmentException(String.format("File %s has unsupported extension. Only [%s] allowed.", name, getAllowedUploadedFormats()));
        }
    }

    private HybrisEnumValue getEnumerationValue(final Class enumClass, final String codeStr)
    {
        if (StringUtils.isEmpty(codeStr))
        {
            return null;
        }
        return getEnumerationService().getEnumerationValue(enumClass, StringUtils.upperCase(codeStr));
    }

    @Override
    public List<B2BUnitNodeData> getB2BUnitHierarchy()
    {
        final UserModel currentUser = getUserService().getCurrentUser();
        if (!(currentUser instanceof final B2BCustomerModel b2BCustomerModel))
        {
            throw new ClassMismatchException(B2BCustomer.class.getSimpleName(), currentUser.getClass().getSimpleName());
        }

        final Set<B2BUnitModel> salesDivisionSoldToB2BUnitModels = getSalesDivisionSoldToB2BUnitModels(b2BCustomerModel);
        final List<B2BUnitNodeData> soldToB2BUnitHierarchy = new ArrayList<>();
        for (final B2BUnitModel salesDivisionSoldToB2BUnitModel : salesDivisionSoldToB2BUnitModels)
        {
            final List<B2BUnitNodeData> payerB2BUnitHierarchy = getB2BUnitNodeData(salesDivisionSoldToB2BUnitModel, soldToB2BUnitHierarchy); //children of SoldTo B2B Unit
            final Set<B2BUnitModel> salesDivisionPayerB2BUnitModels = getChildB2BUnitModels(salesDivisionSoldToB2BUnitModel, PartnerFunction.PY);
            for (final B2BUnitModel salesDivisionPayerB2BUnitModel : salesDivisionPayerB2BUnitModels)
            {
                final List<B2BUnitNodeData> shipToB2BUnitHierarchy = getB2BUnitNodeData(salesDivisionPayerB2BUnitModel, payerB2BUnitHierarchy); //children of Payer B2B Unit
                final Set<B2BUnitModel> salesDivisionShipToB2BUnitModels = getChildB2BUnitModels(salesDivisionPayerB2BUnitModel, PartnerFunction.SH);
                for (final B2BUnitModel salesDivisionShipToB2BUnitModel : salesDivisionShipToB2BUnitModels)
                {
                    final B2BUnitNodeData salesDivisionShipToB2BUnitNodeData = getUnitTreeConverter().convert(salesDivisionShipToB2BUnitModel);
                    shipToB2BUnitHierarchy.add(salesDivisionShipToB2BUnitNodeData);
                }
            }
        }
        return soldToB2BUnitHierarchy;
    }

    private Set<B2BUnitModel> getSalesDivisionSoldToB2BUnitModels(final B2BCustomerModel b2BCustomerModel)
    {
        final Set<B2BUnitModel> allB2BUnitModels = getAdnocB2BUnitService().getAllUnitsOfOrganization(b2BCustomerModel);
        final Set<B2BUnitModel> soldToB2BUnitModels = allB2BUnitModels.stream().filter(b2BUnitModel -> Objects.equals(PartnerFunction.SP, b2BUnitModel.getPartnerFunction())).collect(Collectors.toSet());
        final Set<B2BUnitModel> salesDivisionSoldToB2BUnitModels = soldToB2BUnitModels.stream().filter(soldToB2BUnitModel -> Objects.nonNull(soldToB2BUnitModel.getSalesOrg())).collect(Collectors.toSet());
        LOG.info("appEvent=AdnocB2BUnit, Filtered {} SoldTo B2BUnits with non-null SalesOrg", salesDivisionSoldToB2BUnitModels.size());
        return salesDivisionSoldToB2BUnitModels;
    }

    private Set<B2BUnitModel> getChildB2BUnitModels(final B2BUnitModel b2BUnitModel, final PartnerFunction partnerFunction)
    {
        final B2BUnitModel parentB2BUnitModel = getAdnocB2BUnitService().getParent(b2BUnitModel);
        final Set<B2BUnitModel> childB2BUnitModels = getAdnocB2BUnitService().getChildB2BUnits(parentB2BUnitModel, partnerFunction);
        final Set<B2BUnitModel> salesDivisionChildB2BUnitModels = childB2BUnitModels.stream().filter(childB2BUnitModel ->
                Objects.equals(b2BUnitModel.getSalesOrg(), childB2BUnitModel.getSalesOrg())).collect(Collectors.toSet());
        LOG.info("appEvent=AdnocB2BUnit, Filtered {} child B2B units for SalesOrg={}", salesDivisionChildB2BUnitModels.size(), b2BUnitModel.getSalesOrg());
        return salesDivisionChildB2BUnitModels;
    }

    private List<B2BUnitNodeData> getB2BUnitNodeData(final B2BUnitModel b2BUnitModel, final List<B2BUnitNodeData> b2BUnitNodeDataList)
    {
        final B2BUnitNodeData salesDivisionB2BUnitNodeData = getUnitTreeConverter().convert(b2BUnitModel);
        final List<B2BUnitNodeData> b2bUnitHierarchy = new ArrayList<>();
        salesDivisionB2BUnitNodeData.setChildren(b2bUnitHierarchy);
        b2BUnitNodeDataList.add(salesDivisionB2BUnitNodeData);
        return b2bUnitHierarchy;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    protected MediaService getMediaService()
    {
        return mediaService;
    }

    public void setMediaService(final MediaService mediaService)
    {
        this.mediaService = mediaService;
    }

    protected String getAllowedUploadedFormats()
    {
        return allowedUploadedFormats;
    }

    public void setAllowedUploadedFormats(final String allowedUploadedFormats)
    {
        this.allowedUploadedFormats = allowedUploadedFormats;
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }

    protected Converter<AddressModel, AddressData> getAddressConverter()
    {
        return addressConverter;
    }

    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
    {
        this.addressConverter = addressConverter;
    }

    protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2BCustomerService()
    {
        return b2BCustomerService;
    }

    public void setB2BCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService)
    {
        this.b2BCustomerService = b2BCustomerService;
    }

    protected B2BRegistrationWorkflowFacade getB2bRegistrationWorkflowFacade()
    {
        return b2bRegistrationWorkflowFacade;
    }

    public void setB2bRegistrationWorkflowFacade(final B2BRegistrationWorkflowFacade b2bRegistrationWorkflowFacade)
    {
        this.b2bRegistrationWorkflowFacade = b2bRegistrationWorkflowFacade;
    }

    protected WorkflowTemplateService getWorkflowTemplateService()
    {
        return workflowTemplateService;
    }

    public void setWorkflowTemplateService(final WorkflowTemplateService workflowTemplateService)
    {
        this.workflowTemplateService = workflowTemplateService;
    }


    protected AdnocB2BRegistrationFacade getAdnocB2BRegistrationFacade()
    {
        return adnocB2BRegistrationFacade;
    }

    public void setAdnocB2BRegistrationFacade(final AdnocB2BRegistrationFacade adnocB2BRegistrationFacade)
    {
        this.adnocB2BRegistrationFacade = adnocB2BRegistrationFacade;
    }

    protected SearchRestrictionService getSearchRestrictionService()
    {
        return searchRestrictionService;
    }

    public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
    {
        this.searchRestrictionService = searchRestrictionService;
    }

    protected Converter<IncoTerms, IncoTermsData> getIncoTermsTypeDataConverter()
    {
        return incoTermsTypeDataConverter;
    }

    public void setIncoTermsTypeDataConverter(final Converter<IncoTerms, IncoTermsData> incoTermsTypeDataConverter)
    {
        this.incoTermsTypeDataConverter = incoTermsTypeDataConverter;
    }

    protected AdnocB2BRegistrationService getAdnocB2BRegistrationService()
    {
        return adnocB2BRegistrationService;
    }

    public void setAdnocB2BRegistrationService(final AdnocB2BRegistrationService adnocB2BRegistrationService)
    {
        this.adnocB2BRegistrationService = adnocB2BRegistrationService;
    }
}
