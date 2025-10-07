package com.adnoc.mapping.convertor;

import com.adnoc.b2bocc.unit.data.AdnocRegistrationDataWsDTO;
import com.adnoc.facades.b2b.data.AdnocRegistrationData;
import com.adnoc.facades.company.data.PartnerFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocRegistrationDataConverter
{
    private static final Logger LOG = LogManager.getLogger(AdnocRegistrationDataConverter.class);

    protected void populateCompanyInfo(final AdnocRegistrationDataWsDTO adnocRegistrationDataWsDTO, final AdnocRegistrationData adnocRegistrationData)
    {
        LOG.info("appEvent=AdnocRegistration, populating company info");
        adnocRegistrationData.setCompanyName(adnocRegistrationDataWsDTO.getCompanyName());
        adnocRegistrationData.setCompanyAddressStreet(adnocRegistrationDataWsDTO.getCompanyAddressStreet());
        adnocRegistrationData.setCompanyAddressStreetLine2(adnocRegistrationDataWsDTO.getCompanyAddressStreetLine2());
        adnocRegistrationData.setCompanyAddressCity(adnocRegistrationDataWsDTO.getCompanyAddressCity());
        adnocRegistrationData.setCompanyAddressCountryIso(adnocRegistrationDataWsDTO.getCompanyAddressCountryIso());
        adnocRegistrationData.setCompanyAddressRegion(adnocRegistrationDataWsDTO.getCompanyAddressRegion());
        adnocRegistrationData.setCompanyAddressPostalCode(adnocRegistrationDataWsDTO.getCompanyAddressPostalCode());
        adnocRegistrationData.setFaxNumber(adnocRegistrationDataWsDTO.getFaxNumber());
        adnocRegistrationData.setPoBox(adnocRegistrationDataWsDTO.getPoBox());
        adnocRegistrationData.setCompanyName2(adnocRegistrationDataWsDTO.getCompanyName2());
        LOG.info("appEvent=AdnocRegistration, populated company info successfully.");
    }

    protected void populateCustomerInfo(final AdnocRegistrationDataWsDTO adnocRegistrationDataWsDTO, final AdnocRegistrationData adnocRegistrationData)
    {
        LOG.info("appEvent=AdnocRegistration, populating customer info");
        adnocRegistrationData.setTitleCode(adnocRegistrationDataWsDTO.getTitleCode());
        adnocRegistrationData.setFirstName(adnocRegistrationDataWsDTO.getFirstName());
        adnocRegistrationData.setName(adnocRegistrationDataWsDTO.getFirstName() + StringUtils.SPACE + adnocRegistrationDataWsDTO.getLastName());
        adnocRegistrationData.setLastName(adnocRegistrationDataWsDTO.getLastName());
        adnocRegistrationData.setGender(adnocRegistrationDataWsDTO.getGender());
        adnocRegistrationData.setEmail(adnocRegistrationDataWsDTO.getEmail());
        adnocRegistrationData.setPosition(String.valueOf(adnocRegistrationDataWsDTO.getDesignation()));
        adnocRegistrationData.setIdentityType(adnocRegistrationDataWsDTO.getIdentityType());
        adnocRegistrationData.setIdentificationNumber(adnocRegistrationDataWsDTO.getIdentificationNumber());
        adnocRegistrationData.setCountryOfOrigin(adnocRegistrationDataWsDTO.getCountryOfOrigin());
        adnocRegistrationData.setNationality(adnocRegistrationDataWsDTO.getNationality());
        adnocRegistrationData.setDesignation(adnocRegistrationDataWsDTO.getDesignation());
        adnocRegistrationData.setTelephone(adnocRegistrationDataWsDTO.getTelephone());
        adnocRegistrationData.setMobileNumber(adnocRegistrationDataWsDTO.getMobileNumber());
        if (StringUtils.isNotBlank(adnocRegistrationDataWsDTO.getPartnerFunction()))
        {
            adnocRegistrationData.setPartnerFunction(PartnerFunction.valueOf(adnocRegistrationDataWsDTO.getPartnerFunction()));
        }
        adnocRegistrationData.setIdentificationValidFrom(adnocRegistrationDataWsDTO.getIdentificationValidFrom());
        adnocRegistrationData.setIdentificationValidTo(adnocRegistrationDataWsDTO.getIdentificationValidTo());
        LOG.info("appEvent=AdnocRegistration, populated customer info successfully");
    }
}
