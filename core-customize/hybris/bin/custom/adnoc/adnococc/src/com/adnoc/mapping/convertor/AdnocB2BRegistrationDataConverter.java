/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.mapping.convertor;

import com.adnoc.b2bocc.unit.data.AdnocB2BRegistrationDataWsDTO;
import com.adnoc.facades.b2b.data.AdnocB2BRegistrationData;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WsDTOMapping
public class AdnocB2BRegistrationDataConverter extends CustomConverter<AdnocB2BRegistrationDataWsDTO, AdnocB2BRegistrationData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BRegistrationDataConverter.class);

    private AdnocRegistrationDataConverter adnocRegistrationDataConverter;

    @Override
    public AdnocB2BRegistrationData convert(final AdnocB2BRegistrationDataWsDTO adnocB2BRegistrationDataWsDTO,
                                            final Type<? extends AdnocB2BRegistrationData> type, final MappingContext mappingContext)
    {
        LOG.debug("appEvent=AdnocB2BRegistration, start converting with wsDTO={}", adnocB2BRegistrationDataWsDTO);

        final AdnocB2BRegistrationData adnocB2BRegistrationData = new AdnocB2BRegistrationData();
        getAdnocRegistrationDataConverter().populateCompanyInfo(adnocB2BRegistrationDataWsDTO, adnocB2BRegistrationData);
        getAdnocRegistrationDataConverter().populateCustomerInfo(adnocB2BRegistrationDataWsDTO, adnocB2BRegistrationData);
        populateCompanyInfo(adnocB2BRegistrationDataWsDTO, adnocB2BRegistrationData);
        populateSupportDocument(adnocB2BRegistrationDataWsDTO, adnocB2BRegistrationData);
        adnocB2BRegistrationData.setPrimaryProduct(adnocB2BRegistrationDataWsDTO.getPrimaryProduct());
        adnocB2BRegistrationData.setPreferredCommunicationChannel(adnocB2BRegistrationDataWsDTO.getPreferredCommunicationChannel());

        LOG.info("appEvent=AdnocB2BRegistration, conversion completed with DTO={}", adnocB2BRegistrationData);
        return adnocB2BRegistrationData;
    }

    private void populateCompanyInfo(final AdnocB2BRegistrationDataWsDTO adnocB2BRegistrationDataWsDTO, final AdnocB2BRegistrationData adnocB2BRegistrationData)
    {
        LOG.info("appEvent=AdnocB2BRegistration, Populating company info fields");
        adnocB2BRegistrationData.setCompanyName(adnocB2BRegistrationDataWsDTO.getCompanyName());
        adnocB2BRegistrationData.setCompanyName2(adnocB2BRegistrationDataWsDTO.getCompanyName2());
        adnocB2BRegistrationData.setCompanyEmail(adnocB2BRegistrationDataWsDTO.getCompanyEmail());
        adnocB2BRegistrationData.setCompanyWebsite(adnocB2BRegistrationDataWsDTO.getCompanyWebsite());
        adnocB2BRegistrationData.setCompanyMobileNumber(adnocB2BRegistrationDataWsDTO.getCompanyMobileNumber());
        adnocB2BRegistrationData.setCompanyPhoneNumber(adnocB2BRegistrationDataWsDTO.getCompanyPhoneNumber());
        adnocB2BRegistrationData.setTradeLicenseAuthority(adnocB2BRegistrationDataWsDTO.getTradeLicenseAuthority());
        adnocB2BRegistrationData.setTradeLicenseNumber(adnocB2BRegistrationDataWsDTO.getTradeLicenseNumber());
        adnocB2BRegistrationData.setTlnValidFrom(adnocB2BRegistrationDataWsDTO.getTlnValidFrom());
        adnocB2BRegistrationData.setTlnValidTo(adnocB2BRegistrationDataWsDTO.getTlnValidTo());
        adnocB2BRegistrationData.setVatId(adnocB2BRegistrationDataWsDTO.getVatId());
        adnocB2BRegistrationData.setPoBox(adnocB2BRegistrationDataWsDTO.getPoBox());
        LOG.info("appEvent=AdnocB2BRegistration, Company info fields populated successfully");
    }

    private void populateSupportDocument(final AdnocB2BRegistrationDataWsDTO adnocB2BRegistrationDataWsDTO, final AdnocB2BRegistrationData adnocB2BRegistrationData)
    {
        LOG.debug("appEvent=AdnocB2BRegistration, populating IdentificationNumberDocument with:{}", adnocB2BRegistrationDataWsDTO.getIdentificationNumberDocument());
        adnocB2BRegistrationData.setIdentificationNumberDocument(adnocB2BRegistrationDataWsDTO.getIdentificationNumberDocument());

        LOG.debug("appEvent=AdnocB2BRegistration, populating OtherDocument with:{}", adnocB2BRegistrationDataWsDTO.getOtherDocument());
        adnocB2BRegistrationData.setOtherDocument(adnocB2BRegistrationDataWsDTO.getOtherDocument());

        LOG.debug("appEvent=AdnocB2BRegistration, populating TLNDocument with:{}", adnocB2BRegistrationDataWsDTO.getTlnDocument());
        adnocB2BRegistrationData.setTlnDocument(adnocB2BRegistrationDataWsDTO.getTlnDocument());

        LOG.debug("appEvent=AdnocB2BRegistration, populating VatIdDocument with:{}", adnocB2BRegistrationDataWsDTO.getVatIdDocument());
        adnocB2BRegistrationData.setVatIdDocument(adnocB2BRegistrationDataWsDTO.getVatIdDocument());
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode()
    {
        return getClass().hashCode();
    }

    protected AdnocRegistrationDataConverter getAdnocRegistrationDataConverter()
    {
        return adnocRegistrationDataConverter;
    }

    public void setAdnocRegistrationDataConverter(final AdnocRegistrationDataConverter adnocRegistrationDataConverter)
    {
        this.adnocRegistrationDataConverter = adnocRegistrationDataConverter;
    }
}
