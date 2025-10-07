package com.adnoc.validators.registration;

import com.adnoc.b2bocc.unit.data.AdnocB2BRegistrationDataWsDTO;
import com.adnoc.validators.field.AbstractAdnocFieldValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.Errors;

public class AdnocOrgUserRegistrationValidator extends AbstractAdnocFieldValidator
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrgUserRegistrationValidator.class);
    private static final String B2B_REGISTRATION = "b2bregistration";

    @Override
    public boolean supports(final Class<?> clazz)
    {
        return AdnocB2BRegistrationDataWsDTO.class.equals(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors)
    {
        final AdnocB2BRegistrationDataWsDTO adnocB2BRegistrationDataWsDTO = (AdnocB2BRegistrationDataWsDTO) target;
        identificationNumberFormatValidation(adnocB2BRegistrationDataWsDTO.getIdentityType());
        phoneNumberFormatValidation(adnocB2BRegistrationDataWsDTO.getCountryOfOrigin(), "telephone");
        phoneNumberFormatValidation(adnocB2BRegistrationDataWsDTO.getCompanyAddressCountryIso(), "companyPhoneNumber");
        LOG.debug("appEvent=Starting of AdnocSoldToParty validation for target: {}.", target);
        super.validate(target, B2B_REGISTRATION, errors);
        fileSizeValidation(errors, B2B_REGISTRATION, "otherDocument", adnocB2BRegistrationDataWsDTO.getOtherDocument());
        fileSizeValidation(errors, B2B_REGISTRATION, "identificationNumberDocument", adnocB2BRegistrationDataWsDTO.getIdentificationNumberDocument());
        fileSizeValidation(errors, B2B_REGISTRATION, "tlnDocument", adnocB2BRegistrationDataWsDTO.getTlnDocument());
        fileSizeValidation(errors, B2B_REGISTRATION, "vatIdDocument", adnocB2BRegistrationDataWsDTO.getVatIdDocument());
    }
}
