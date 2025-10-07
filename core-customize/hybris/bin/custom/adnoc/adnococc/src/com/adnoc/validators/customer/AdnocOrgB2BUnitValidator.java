package com.adnoc.validators.customer;

import com.adnoc.b2bocc.unit.data.AdnocB2BUnitRegistrationDataWsDTO;
import com.adnoc.service.enums.PartnerFunction;
import com.adnoc.validators.field.AbstractAdnocFieldValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.Errors;

import java.util.Objects;

public class AdnocOrgB2BUnitValidator extends AbstractAdnocFieldValidator
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrgB2BUnitValidator.class);
    private static final String B2B_REGISTRATION = "b2bregistration";

    @Override
    public boolean supports(final Class<?> clazz)
    {
        return AdnocB2BUnitRegistrationDataWsDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors)
    {
        final AdnocB2BUnitRegistrationDataWsDTO adnocB2BUnitRegistrationDataWsDTO = (AdnocB2BUnitRegistrationDataWsDTO) target;
        LOG.debug("Starting of AdnocB2BUnit validation for target: {}", target);
        identificationNumberFormatValidation(adnocB2BUnitRegistrationDataWsDTO.getIdentityType());
        phoneNumberFormatValidation(adnocB2BUnitRegistrationDataWsDTO.getCountryOfOrigin(), "telephone");
        super.validate(target, B2B_REGISTRATION, errors);
        if (StringUtils.equals(adnocB2BUnitRegistrationDataWsDTO.getPartnerFunction(), PartnerFunction.SH.getCode()))
        {
            mandatoryCheckValidation(errors, B2B_REGISTRATION, "incoTerms", adnocB2BUnitRegistrationDataWsDTO.getIncoTerms(), null);
            mandatoryCheckValidation(errors, B2B_REGISTRATION, "latitude", adnocB2BUnitRegistrationDataWsDTO.getLatitude(), null);
            mandatoryCheckValidation(errors, B2B_REGISTRATION, "longitude", adnocB2BUnitRegistrationDataWsDTO.getLongitude(), null);
        }
        else if (StringUtils.equals(adnocB2BUnitRegistrationDataWsDTO.getPartnerFunction(), PartnerFunction.PY.getCode()))
        {
            mandatoryCheckValidation(errors, B2B_REGISTRATION, "vatId", adnocB2BUnitRegistrationDataWsDTO.getVatId(), null);
        }
        fileSizeValidation(errors, B2B_REGISTRATION, "otherDocument", adnocB2BUnitRegistrationDataWsDTO.getOtherDocument());
        fileSizeValidation(errors, B2B_REGISTRATION, "identificationNumberDocument", adnocB2BUnitRegistrationDataWsDTO.getIdentificationNumberDocument());
    }
}
