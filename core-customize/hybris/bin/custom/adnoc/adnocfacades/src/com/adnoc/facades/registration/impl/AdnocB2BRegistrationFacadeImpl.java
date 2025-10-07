/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.facades.registration.impl;

import com.adnoc.facades.b2b.data.AdnocB2BRegistrationData;
import com.adnoc.facades.registration.AdnocB2BRegistrationFacade;
import com.adnoc.facades.user.data.PrimaryProductData;
import com.adnoc.service.enums.*;
import com.adnoc.service.model.AdnocRegistrationModel;
import com.adnoc.service.model.AdnocSoldToB2BRegistrationModel;
import com.adnoc.service.registration.AdnocB2BRegistrationService;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bacceleratorfacades.exception.RegistrationNotEnabledException;
import de.hybris.platform.b2bacceleratorfacades.registration.B2BRegistrationWorkflowFacade;
import de.hybris.platform.b2bacceleratorfacades.registration.impl.DefaultB2BRegistrationFacade;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.ticket.service.UnsupportedAttachmentException;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


/**
 * AdnocB2BRegistrationFacadeImpl
 */
public class AdnocB2BRegistrationFacadeImpl extends DefaultB2BRegistrationFacade implements AdnocB2BRegistrationFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BRegistrationFacadeImpl.class);

    private BaseSiteService baseSiteService;
    private CommonI18NService commonI18NService;
    private ModelService modelService;
    private BaseStoreService baseStoreService;
    private UserService userService;
    private EnumerationService enumerationService;
    private MediaService mediaService;
    private String allowedUploadedFormats;
    private B2BRegistrationWorkflowFacade b2bRegistrationWorkflowFacade;
    private WorkflowTemplateService workflowTemplateService;
    private AdnocB2BRegistrationService adnocB2BRegistrationService;
    private Converter<PrimaryProduct, PrimaryProductData> primaryProductDataConverter;

    @Override
    public String registerSoldTo(final B2BRegistrationData b2BRegistrationData) throws CustomerAlreadyExistsException, RegistrationNotEnabledException
    {
        final AdnocB2BRegistrationData adnocB2BRegistrationData = (AdnocB2BRegistrationData) b2BRegistrationData;
        final boolean isRegistrationEnabled = getBaseSiteService().getCurrentBaseSite().isEnableRegistration();
        if (!isRegistrationEnabled)
        {
            LOG.debug("appEvent=B2BRegistration, Registration is not enabled!");
            throw new RegistrationNotEnabledException("Registration is not enabled!");
        }

        final Transaction tx = Transaction.current();
        tx.begin();
        boolean success = false;
        final B2BRegistrationModel registration;
        try
        {
            LOG.info("appEvent=B2BRegistration, Register soldTo with CompanyEmail: {},Trade License Number: {}, Vat ID: {}, Email: {},IdentificationNumber: {}",
                    adnocB2BRegistrationData.getCompanyEmail(), adnocB2BRegistrationData.getTradeLicenseNumber(), adnocB2BRegistrationData.getVatId(), adnocB2BRegistrationData.getEmail(), adnocB2BRegistrationData.getIdentificationNumber());
            final String adnocRegistrationType = AdnocSoldToB2BRegistrationModel._TYPECODE;
            final Map<String, String> duplicateCheckMap = new HashMap<>();
            duplicateCheckMap.put(AdnocSoldToB2BRegistrationModel.COMPANYEMAIL, adnocB2BRegistrationData.getCompanyEmail());
            duplicateCheckMap.put(AdnocSoldToB2BRegistrationModel.TRADELICENSENUMBER, adnocB2BRegistrationData.getTradeLicenseNumber());
            duplicateCheckMap.put(AdnocSoldToB2BRegistrationModel.VATID, adnocB2BRegistrationData.getVatId());
            duplicateCheckMap.put(AdnocSoldToB2BRegistrationModel.EMAIL, adnocB2BRegistrationData.getEmail());
            duplicateCheckMap.put(AdnocSoldToB2BRegistrationModel.IDENTIFICATIONNUMBER, adnocB2BRegistrationData.getIdentificationNumber());

            final AdnocRegistrationModel adnocB2BRegistrationModel = getAdnocB2BRegistrationService().getAdnocB2BRegistration(duplicateCheckMap, adnocRegistrationType);
            if (Objects.nonNull(adnocB2BRegistrationModel))
            {
                LOG.debug("appEvent=B2BRegistration,Already Registration request found with email={} ", adnocB2BRegistrationData.getEmail());
                throw new CustomerAlreadyExistsException("Registration Request already exist with status = " + adnocB2BRegistrationModel.getStatus());
            }

            // Check if a user using the same email exist, if so we need to abort the current operation!
            final boolean userExists = getUserService().isUserExisting(StringUtils.lowerCase(adnocB2BRegistrationData.getEmail()));
            if (userExists)
            {
                LOG.debug("appEvent=B2BRegistration, user with uid={} already exists!", adnocB2BRegistrationData.getEmail());
                throw new CustomerAlreadyExistsException(String.format("Account already exist with email %s!", adnocB2BRegistrationData.getEmail()));
            }


            final CustomerModel customer = toCustomerModel(adnocB2BRegistrationData);
            registration = toRegistrationModel(adnocB2BRegistrationData);

            registration.setCustomer(customer);
            modelService.saveAll(customer, registration);
            LOG.info("appEvent=B2BRegistration, Customer and registration details saved successfully.");
            final WorkflowTemplateModel workflowTemplate = workflowTemplateService.getWorkflowTemplateForCode(
                    B2BConstants.Workflows.REGISTRATION_WORKFLOW);

            LOG.debug("Created WorkflowTemplateModel using name '{}'", B2BConstants.Workflows.REGISTRATION_WORKFLOW);
            b2bRegistrationWorkflowFacade.launchWorkflow(workflowTemplate, registration);

            tx.commit();
            success = true;
            return registration.getPk().getLongValueAsString();
        } finally
        {
            if (!success)
            {
                tx.rollback();
            }
        }
    }

    @Override
    protected B2BRegistrationModel toRegistrationModel(final B2BRegistrationData b2bRegistrationData)
    {
        LOG.info("appEvent=B2BRegistration, toRegistrationModel method called...");
        final AdnocSoldToB2BRegistrationModel adnocSoldToB2BRegistrationModel = modelService.create(AdnocSoldToB2BRegistrationModel.class);
        final AdnocB2BRegistrationData adnocB2BRegistrationData = (AdnocB2BRegistrationData) b2bRegistrationData;

        // Use reflection to copy most properties and ignore these since we want to manage them manually
        final String[] ignoreProperties = {"titleCode", "companyAddressCountryIso", "companyAddressRegion", "baseStore", "cmsSite", "currency", "language"};
        BeanUtils.copyProperties(adnocB2BRegistrationData, adnocSoldToB2BRegistrationModel, ignoreProperties);

        adnocSoldToB2BRegistrationModel.setPrimaryProduct((PrimaryProduct) getEnumerationValue(PrimaryProduct.class, adnocB2BRegistrationData.getPrimaryProduct()));
        populateCompanyInfo(adnocSoldToB2BRegistrationModel, adnocB2BRegistrationData);
        populateCustomerInfo(adnocSoldToB2BRegistrationModel, adnocB2BRegistrationData);
        populateSupportedDocument(adnocSoldToB2BRegistrationModel, adnocB2BRegistrationData);
        populateStoreInfo(adnocSoldToB2BRegistrationModel);

        return adnocSoldToB2BRegistrationModel;
    }

    private void populateCompanyInfo(final AdnocSoldToB2BRegistrationModel adnocB2BRegistrationModel, final AdnocB2BRegistrationData adnocB2BRegistrationData)
    {

        if (StringUtils.isNotBlank(adnocB2BRegistrationData.getCompanyAddressCountryIso()))
        {
            LOG.debug("appEvent=B2BRegistration, Company Address Country Iso: {}", adnocB2BRegistrationData.getCompanyAddressCountryIso());
            final CountryModel countryModel = getCommonI18NService().getCountry(adnocB2BRegistrationData.getCompanyAddressCountryIso());
            adnocB2BRegistrationModel.setCompanyAddressCountry(countryModel);

            if (StringUtils.isNotBlank(adnocB2BRegistrationData.getCompanyAddressRegion()))
            {
                LOG.debug("appEvent=B2BRegistration, Company Address Region: {}", adnocB2BRegistrationData.getCompanyAddressRegion());
                final RegionModel regionModel = getCommonI18NService().getRegion(countryModel, adnocB2BRegistrationData.getCompanyAddressRegion());
                adnocB2BRegistrationModel.setCompanyAddressRegion(regionModel);
            }
            adnocB2BRegistrationModel.setCompanyAddressCity(adnocB2BRegistrationData.getCompanyAddressCity());
            LOG.debug("appEvent=B2BRegistration,setting vatId:{},TlnValidFrom :{},TlnValidTo :{}", adnocB2BRegistrationData.getVatId(), adnocB2BRegistrationData.getTlnValidFrom(), adnocB2BRegistrationData.getTlnValidTo());
            adnocB2BRegistrationModel.setPartnerFunction((PartnerFunction) getEnumerationValue(PartnerFunction.class, adnocB2BRegistrationData.getPartnerFunction().toString()));
            adnocB2BRegistrationModel.setVatId(adnocB2BRegistrationData.getVatId());
            adnocB2BRegistrationModel.setTradeLicenseNumber(adnocB2BRegistrationData.getTradeLicenseNumber());
            adnocB2BRegistrationModel.setTradeLicenseAuthority((TradeLicenseAuthority) getEnumerationValue(TradeLicenseAuthority.class, adnocB2BRegistrationData.getTradeLicenseAuthority()));
            adnocB2BRegistrationModel.setTlnValidFrom(adnocB2BRegistrationData.getTlnValidFrom());
            adnocB2BRegistrationModel.setTlnValidTo(adnocB2BRegistrationData.getTlnValidTo());
            adnocB2BRegistrationModel.setCompanyName2(adnocB2BRegistrationData.getCompanyName2());
        }
    }

    private void populateCustomerInfo(final AdnocSoldToB2BRegistrationModel adnocB2BRegistrationModel, final AdnocB2BRegistrationData adnocB2BRegistrationData)
    {
        LOG.info("appEvent=B2BRegistration, Populating preferred communication channel...");
        adnocB2BRegistrationModel.setPreferredCommunicationChannel((PreferredCommunicationChannel) getEnumerationValue(PreferredCommunicationChannel.class, adnocB2BRegistrationData.getPreferredCommunicationChannel()));
        if (StringUtils.isNotBlank(adnocB2BRegistrationData.getTitleCode()))
        {
            LOG.info("appEvent=B2BRegistration, title is mandatory");
            final TitleModel titleModel = getUserService().getTitleForCode(adnocB2BRegistrationData.getTitleCode());
            adnocB2BRegistrationModel.setTitle(titleModel);
        }
        LOG.info("appEvent=B2BRegistration, Setting gender...");
        adnocB2BRegistrationModel.setGender((Gender) getEnumerationValue(Gender.class, adnocB2BRegistrationData.getGender()));
        LOG.info("appEvent=B2BRegistration, Setting designation...");
        adnocB2BRegistrationModel.setDesignation((Designation) getEnumerationValue(Designation.class, adnocB2BRegistrationData.getDesignation()));
        LOG.info("appEvent=B2BRegistration, Setting country of origin...");
        adnocB2BRegistrationModel.setCountryOfOrigin((Nationality) getEnumerationValue(Nationality.class, adnocB2BRegistrationData.getCountryOfOrigin()));
        LOG.info("appEvent=B2BRegistration, Setting nationality...");
        adnocB2BRegistrationModel.setNationality((Nationality) getEnumerationValue(Nationality.class, adnocB2BRegistrationData.getNationality()));
        LOG.info("appEvent=B2BRegistration, Setting identity type...");
        adnocB2BRegistrationModel.setIdentityType((IdentityType) getEnumerationValue(IdentityType.class, adnocB2BRegistrationData.getIdentityType()));
    }

    @Override
    public void populateStoreInfo(final B2BRegistrationModel b2BRegistrationModel)
    {
        LOG.info("appEvent=B2BRegistration, Setting base store...");
        b2BRegistrationModel.setBaseStore(getBaseStoreService().getCurrentBaseStore());
        LOG.info("appEvent=B2BRegistration, Setting base site...");
        b2BRegistrationModel.setBaseSite(getBaseSiteService().getCurrentBaseSite());
        LOG.info("appEvent=B2BRegistration, Setting currency...");
        b2BRegistrationModel.setCurrency(getCommonI18NService().getCurrentCurrency());
        LOG.info("appEvent=B2BRegistration, Setting language...");
        b2BRegistrationModel.setLanguage(getCommonI18NService().getCurrentLanguage());
        LOG.info("appEvent=B2BRegistration, Store information set successfully in B2BRegistrationModel.");
    }


    private void populateSupportedDocument(final AdnocSoldToB2BRegistrationModel adnocSoldToB2BRegistrationModel, final AdnocB2BRegistrationData adnocB2BRegistrationData)
    {
        try
        {
            if (Objects.nonNull(adnocB2BRegistrationData.getIdentificationNumberDocument()))
            {
                LOG.info("appEvent=B2BRegistration, Setting identificationNumber  Document...");
                final MultipartFile identificationNumberDocument = adnocB2BRegistrationData.getIdentificationNumberDocument();
                final DocumentMediaModel idnDocumentMediaModel = createAttachment(identificationNumberDocument.getOriginalFilename(), identificationNumberDocument.getContentType(),
                        identificationNumberDocument.getBytes());
                adnocSoldToB2BRegistrationModel.setIdentificationNumberDocument(idnDocumentMediaModel);
            }

            if (Objects.nonNull(adnocB2BRegistrationData.getTlnDocument()))
            {
                LOG.info("appEvent=B2BRegistration, Setting Tln document...");
                final MultipartFile tlnDocument = adnocB2BRegistrationData.getTlnDocument();
                final DocumentMediaModel tlnDocumentMediaModel = createAttachment(tlnDocument.getOriginalFilename(), tlnDocument.getContentType(),
                        tlnDocument.getBytes());
                adnocSoldToB2BRegistrationModel.setTlnDocument(tlnDocumentMediaModel);
            }

            if (Objects.nonNull(adnocB2BRegistrationData.getVatIdDocument()))
            {
                LOG.info("appEvent=B2BRegistration, Setting vatId document...");
                final MultipartFile vatIdDocument = adnocB2BRegistrationData.getVatIdDocument();
                final DocumentMediaModel vatIdDocumentMediaModel = createAttachment(vatIdDocument.getOriginalFilename(), vatIdDocument.getContentType(),
                        vatIdDocument.getBytes());
                adnocSoldToB2BRegistrationModel.setVatIdDocument(vatIdDocumentMediaModel);
            }

            if (Objects.nonNull(adnocB2BRegistrationData.getOtherDocument()))
            {
                LOG.info("appEvent=B2BRegistration, Setting other document...");
                final MultipartFile otherDocument = adnocB2BRegistrationData.getOtherDocument();
                final DocumentMediaModel otherDocumentMediaModel = createAttachment(otherDocument.getOriginalFilename(), otherDocument.getContentType(),
                        otherDocument.getBytes());
                adnocSoldToB2BRegistrationModel.setOtherDocument(otherDocumentMediaModel);
            }
            LOG.info("appEvent= B2BRegistration, setting supported documents to adnocB2BRegistrationModel");
        }
        catch (final IOException ioException)
        {
            LOG.error(ioException.getMessage(), ioException);
            throw new RuntimeException("Failed to create attachment for the ticket.");
        }
    }

    private DocumentMediaModel createAttachment(final String fileName, final String contentType, final byte[] data)
    {
        LOG.info("appEvent=B2BRegistration, createAttachment method start");
        checkFileExtension(fileName);
        final DocumentMediaModel documentMediaModel = getModelService().create(DocumentMediaModel.class);
        documentMediaModel.setCode(UUID.randomUUID().toString());
        documentMediaModel.setMime(contentType);
        documentMediaModel.setRealFileName(fileName);
        getModelService().save(documentMediaModel);
        getMediaService().setDataForMedia(documentMediaModel, data);
        LOG.info("appEvent=B2BRegistration, createAttachment end");
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
    public List<PrimaryProductData> getPrimaryProducts()
    {
        final List<PrimaryProduct> primaryProducts = getEnumerationService().getEnumerationValues(PrimaryProduct._TYPECODE);
        return Converters.convertAll(primaryProducts, getPrimaryProductDataConverter());
    }


    protected BaseSiteService getBaseSiteService()
    {
        return baseSiteService;
    }

    @Override
    public void setBaseSiteService(final BaseSiteService baseSiteService)
    {
        super.setBaseSiteService(baseSiteService);
        this.baseSiteService = baseSiteService;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    @Override
    public void setCommonI18NService(final CommonI18NService commonI18NService)
    {
        super.setCommonI18NService(commonI18NService);
        this.commonI18NService = commonI18NService;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService)
    {
        super.setModelService(modelService);
        this.modelService = modelService;
    }

    protected BaseStoreService getBaseStoreService()
    {
        return baseStoreService;
    }

    @Override
    public void setBaseStoreService(final BaseStoreService baseStoreService)
    {
        super.setBaseStoreService(baseStoreService);
        this.baseStoreService = baseStoreService;
    }

    protected UserService getUserService()
    {
        return userService;
    }

    @Override
    public void setUserService(final UserService userService)
    {
        super.setUserService(userService);
        this.userService = userService;
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

    protected B2BRegistrationWorkflowFacade getB2bRegistrationWorkflowFacade()
    {
        return b2bRegistrationWorkflowFacade;
    }

    @Override
    public void setB2bRegistrationWorkflowFacade(final B2BRegistrationWorkflowFacade b2bRegistrationWorkflowFacade)
    {
        super.setB2bRegistrationWorkflowFacade(b2bRegistrationWorkflowFacade);
        this.b2bRegistrationWorkflowFacade = b2bRegistrationWorkflowFacade;
    }

    protected WorkflowTemplateService getWorkflowTemplateService()
    {
        return workflowTemplateService;
    }

    @Override
    public void setWorkflowTemplateService(final WorkflowTemplateService workflowTemplateService)
    {
        super.setWorkflowTemplateService(workflowTemplateService);
        this.workflowTemplateService = workflowTemplateService;
    }

    protected AdnocB2BRegistrationService getAdnocB2BRegistrationService()
    {
        return adnocB2BRegistrationService;
    }

    public void setAdnocB2BRegistrationService(final AdnocB2BRegistrationService adnocB2BRegistrationService)
    {
        this.adnocB2BRegistrationService = adnocB2BRegistrationService;
    }

    protected Converter<PrimaryProduct, PrimaryProductData> getPrimaryProductDataConverter()
    {
        return primaryProductDataConverter;
    }

    public void setPrimaryProductDataConverter(final Converter<PrimaryProduct, PrimaryProductData> primaryProductDataConverter)
    {
        this.primaryProductDataConverter = primaryProductDataConverter;
    }

}
