package com.adnoc.validators.customer;

import com.adnoc.b2bocc.user.data.AdnocB2BUserRegistrationDataWsDTO;
import com.adnoc.facades.user.data.validation.FieldValidationData;
import com.adnoc.validators.field.AbstractAdnocFieldValidator;
import com.oracle.truffle.js.nodes.cast.JSToIndexNodeGen;
import de.hybris.platform.b2bwebservicescommons.dto.company.OrgCustomerCreationWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.Errors;

public class AdnocOrgCustomerCreationWsDTOValidator extends AbstractAdnocFieldValidator
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrgCustomerCreationWsDTOValidator.class);
    private static final String B2B_REGISTRATION = "b2bregistration";

    @Override
    public void validate(Object target, Errors errors)
    {
        final AdnocB2BUserRegistrationDataWsDTO adnocB2BUserRegistrationDataWsDTO = (AdnocB2BUserRegistrationDataWsDTO) target;
        phoneNumberFormatValidation(adnocB2BUserRegistrationDataWsDTO.getCountryOfOrigin(), "telephone");
        identificationNumberFormatValidation(adnocB2BUserRegistrationDataWsDTO.getIdentityType());
        LOG.debug("appEvent=Starting of AdnocSubUser validation for target: {}.", target);
        super.validate(target, "b2bcustomer", errors);
    }

    @Override
    public boolean supports(final Class<?> clazz)
    {
        return OrgCustomerCreationWsDTO.class.equals(clazz);
    }

}
