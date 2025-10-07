package com.adnoc.facades.b2bcustomer.populators;

import com.adnoc.facades.b2b.unit.data.AdnocB2BUnitRegistrationData;
import com.adnoc.service.enums.Designation;
import com.adnoc.service.enums.IdentityType;
import com.adnoc.service.enums.Nationality;
import com.adnoc.service.enums.PreferredCommunicationChannel;
import com.adnoc.service.model.AdnocB2BUnitRegistrationModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BCustomerReversePopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.service.UnsupportedAttachmentException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class AdnocB2BCustomerReversePopulator extends B2BCustomerReversePopulator
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BCustomerReversePopulator.class);

    private EnumerationService enumerationService;
    private ModelService modelService;
    private MediaService mediaService;
    private String allowedUploadedFormats;
    private CommonI18NService commonI18NService;

    @Override
    public void populate(final CustomerData customerData, final B2BCustomerModel b2bCustomerModel) throws ConversionException
    {
        LOG.debug("appEvent=B2BCustomer, identificationNumber={} assigned to sub user for creation of customer.", customerData.getIdentificationNumber());
        super.populate(customerData, b2bCustomerModel);

        b2bCustomerModel.setPreferredCommunicationChannel((PreferredCommunicationChannel) getEnumerationValue(PreferredCommunicationChannel.class, customerData.getPreferredCommunicationChannel()));
        b2bCustomerModel.setFirstName(customerData.getFirstName());
        b2bCustomerModel.setLastName(customerData.getLastName());
        b2bCustomerModel.setGender((Gender) getEnumerationValue(Gender.class, customerData.getGender()));
        b2bCustomerModel.setCountryOfOrigin((Nationality) getEnumerationValue(Nationality.class, customerData.getCountryOfOrigin()));
        b2bCustomerModel.setNationality((Nationality) getEnumerationValue(Nationality.class, customerData.getNationality()));
        b2bCustomerModel.setIdentityType((IdentityType) getEnumerationValue(IdentityType.class, customerData.getIdentityType()));
        b2bCustomerModel.setIdentificationNumber(customerData.getIdentificationNumber());
        b2bCustomerModel.setIdentificationValidFrom(customerData.getIdentificationValidFrom());
        b2bCustomerModel.setIdentificationValidTo(customerData.getIdentificationValidTo());
        b2bCustomerModel.setDesignation((Designation) getEnumerationValue(Designation.class, customerData.getDesignation()));
        b2bCustomerModel.setMobileNumber(customerData.getMobileNumber());
        b2bCustomerModel.setTelephone(customerData.getTelephone());
        b2bCustomerModel.setCompanyAddressStreet(customerData.getCompanyAddressStreet());
        b2bCustomerModel.setCompanyAddressStreetLine2(customerData.getCompanyAddressStreetLine2());
        if (StringUtils.isNotBlank(customerData.getCompanyAddressCountryIso()))
        {
            final CountryModel countryModel = getCommonI18NService().getCountry(customerData.getCompanyAddressCountryIso());
            b2bCustomerModel.setCompanyAddressCountry(countryModel);
            if (StringUtils.isNotBlank(customerData.getCompanyAddressRegion()))
            {
                final RegionModel regionModel = getCommonI18NService().getRegion(countryModel, customerData.getCompanyAddressRegion());
                b2bCustomerModel.setCompanyAddressRegion(regionModel);
            }
        }
        b2bCustomerModel.setCompanyAddressCity(customerData.getCompanyAddressCity());
        populateSupportedDocument(b2bCustomerModel,customerData);
        if (StringUtils.isEmpty(customerData.getUid()))
        {
            b2bCustomerModel.setActive(false);
            b2bCustomerModel.setLoginDisabled(true);
        }
    }

    private void populateSupportedDocument(final B2BCustomerModel b2bCustomerModel,
                                           final CustomerData customerData)
    {
        try
        {
            if (Objects.nonNull(customerData.getIdentificationNumberDocument()))
            {
                final MultipartFile identificationNumberDocument = customerData.getIdentificationNumberDocument();
                final DocumentMediaModel idnDocumentMediaModel = createAttachment(identificationNumberDocument.getOriginalFilename(), identificationNumberDocument.getContentType(),
                        identificationNumberDocument.getBytes());
                b2bCustomerModel.setIdentificationNumberDocument(idnDocumentMediaModel);
                LOG.info("appEvent= B2BUnitRegistration, Successfully created identificationNumberDocument attachment for:{}", identificationNumberDocument.getOriginalFilename());
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

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected MediaService getMediaService()
    {
        return mediaService;
    }

    public void setMediaService(MediaService mediaService)
    {
        this.mediaService = mediaService;
    }

    protected String getAllowedUploadedFormats()
    {
        return allowedUploadedFormats;
    }

    public void setAllowedUploadedFormats(String allowedUploadedFormats)
    {
        this.allowedUploadedFormats = allowedUploadedFormats;
    }
}
