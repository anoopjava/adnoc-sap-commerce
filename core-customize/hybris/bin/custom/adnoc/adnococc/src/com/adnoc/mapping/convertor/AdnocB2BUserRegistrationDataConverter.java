package com.adnoc.mapping.convertor;

import com.adnoc.b2bocc.user.data.AdnocB2BUserRegistrationDataWsDTO;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WsDTOMapping
public class AdnocB2BUserRegistrationDataConverter extends CustomConverter<AdnocB2BUserRegistrationDataWsDTO, CustomerData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BUserRegistrationDataConverter.class);

    @Override
    public CustomerData convert(final AdnocB2BUserRegistrationDataWsDTO adnocB2BUserRegistrationDataWsDTO,
                                final Type<? extends CustomerData> type, final MappingContext mappingContext)
    {
        final CustomerData customerData = new CustomerData();

        customerData.setPreferredCommunicationChannel(adnocB2BUserRegistrationDataWsDTO.getPreferredCommunicationChannel());
        customerData.setGender(adnocB2BUserRegistrationDataWsDTO.getGender());
        customerData.setCountryOfOrigin(adnocB2BUserRegistrationDataWsDTO.getCountryOfOrigin());
        customerData.setNationality(adnocB2BUserRegistrationDataWsDTO.getNationality());
        customerData.setIdentityType(adnocB2BUserRegistrationDataWsDTO.getIdentityType());
        customerData.setIdentificationNumber(adnocB2BUserRegistrationDataWsDTO.getIdentificationNumber());
        customerData.setIdentificationValidFrom(adnocB2BUserRegistrationDataWsDTO.getIdentificationValidFrom());
        customerData.setIdentificationValidTo(adnocB2BUserRegistrationDataWsDTO.getIdentificationValidTo());
        customerData.setDesignation(adnocB2BUserRegistrationDataWsDTO.getDesignation());
        customerData.setTelephone(adnocB2BUserRegistrationDataWsDTO.getTelephone());
        customerData.setMobileNumber(adnocB2BUserRegistrationDataWsDTO.getMobileNumber());
        customerData.setTitleCode(adnocB2BUserRegistrationDataWsDTO.getTitleCode());
        customerData.setFirstName(adnocB2BUserRegistrationDataWsDTO.getFirstName());
        customerData.setName(adnocB2BUserRegistrationDataWsDTO.getFirstName() + StringUtils.SPACE + adnocB2BUserRegistrationDataWsDTO.getLastName());
        customerData.setLastName(adnocB2BUserRegistrationDataWsDTO.getLastName());
        customerData.setEmail(adnocB2BUserRegistrationDataWsDTO.getEmail());
        customerData.setDisplayUid(adnocB2BUserRegistrationDataWsDTO.getEmail());
        customerData.setRoles(adnocB2BUserRegistrationDataWsDTO.getRoles());
        customerData.setCustomerId(adnocB2BUserRegistrationDataWsDTO.getCustomerId());
        customerData.setCompanyAddressStreet(adnocB2BUserRegistrationDataWsDTO.getCompanyAddressStreet());
        customerData.setCompanyAddressStreetLine2(adnocB2BUserRegistrationDataWsDTO.getCompanyAddressStreetLine2());
        customerData.setCompanyAddressCountryIso(adnocB2BUserRegistrationDataWsDTO.getCompanyAddressCountryIso());
        customerData.setCompanyAddressRegion(adnocB2BUserRegistrationDataWsDTO.getCompanyAddressRegion());
        customerData.setCompanyAddressCity(adnocB2BUserRegistrationDataWsDTO.getCompanyAddressCity());

        populateSupportDocument(adnocB2BUserRegistrationDataWsDTO, customerData);
        LOG.info("appEvent=customerData, conversion completed with DTO={}", customerData);
        return customerData;
    }

    private void populateSupportDocument(final AdnocB2BUserRegistrationDataWsDTO adnocB2BUserRegistrationDataWsDTO, final CustomerData customerData)
    {
        LOG.debug("appEvent=customerData, populating IdentificationNumberDocument with:{}", adnocB2BUserRegistrationDataWsDTO.getIdentificationNumberDocument());
        customerData.setIdentificationNumberDocument(adnocB2BUserRegistrationDataWsDTO.getIdentificationNumberDocument());
    }
}
