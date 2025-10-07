package com.adnoc.mapping.convertor;

import com.adnoc.b2bocc.unit.data.AdnocB2BUnitRegistrationDataWsDTO;
import com.adnoc.facades.b2b.unit.data.AdnocB2BUnitRegistrationData;
import com.adnoc.service.enums.IncoTerms;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WsDTOMapping
public class AdnocB2BUnitRegistrationDataConverter extends CustomConverter<AdnocB2BUnitRegistrationDataWsDTO, AdnocB2BUnitRegistrationData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BUnitRegistrationDataConverter.class);

    private AdnocRegistrationDataConverter adnocRegistrationDataConverter;

    private EnumerationService enumerationService;

    @Override
    public AdnocB2BUnitRegistrationData convert(final AdnocB2BUnitRegistrationDataWsDTO adnocB2BUnitRegistrationDataWsDTO,
                                                final Type<? extends AdnocB2BUnitRegistrationData> type, final MappingContext mappingContext)
    {
        LOG.info("appEvent=AdnocB2BUnitRegistration, start converting with wsDTO={}", adnocB2BUnitRegistrationDataWsDTO);
        final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData = new AdnocB2BUnitRegistrationData();
        getAdnocRegistrationDataConverter().populateCompanyInfo(adnocB2BUnitRegistrationDataWsDTO, adnocB2BUnitRegistrationData);
        getAdnocRegistrationDataConverter().populateCustomerInfo(adnocB2BUnitRegistrationDataWsDTO, adnocB2BUnitRegistrationData);
        populateSupportDocument(adnocB2BUnitRegistrationDataWsDTO, adnocB2BUnitRegistrationData);
        populateB2BUnitInfo(adnocB2BUnitRegistrationDataWsDTO, adnocB2BUnitRegistrationData);
        populateCustomerInfo(adnocB2BUnitRegistrationDataWsDTO, adnocB2BUnitRegistrationData);
        LOG.info("appEvent=AdnocB2BUnitRegistration, conversion completed with DTO={}", adnocB2BUnitRegistrationData);
        return adnocB2BUnitRegistrationData;
    }

    private void populateB2BUnitInfo(final AdnocB2BUnitRegistrationDataWsDTO adnocB2BUnitRegistrationDataWsDTO, final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData)
    {
        LOG.info("appEvent=AdnocB2BUnitRegistration, Setting default B2B unit with UID: {}", adnocB2BUnitRegistrationDataWsDTO.getParentB2BUnitUid());
        adnocB2BUnitRegistrationData.setDefaultB2BUnit(adnocB2BUnitRegistrationDataWsDTO.getParentB2BUnitUid());
    }

    private void populateCustomerInfo(final AdnocB2BUnitRegistrationDataWsDTO adnocB2BUnitRegistrationDataWsDTO, final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData)
    {
        LOG.info("appEvent=AdnocB2BUnitRegistration, Populating customer info.");
        adnocB2BUnitRegistrationData.setTitleCode(adnocB2BUnitRegistrationDataWsDTO.getTitleCode());
        adnocB2BUnitRegistrationData.setFirstName(adnocB2BUnitRegistrationDataWsDTO.getFirstName());
        adnocB2BUnitRegistrationData.setLastName(adnocB2BUnitRegistrationDataWsDTO.getLastName());
        adnocB2BUnitRegistrationData.setName(adnocB2BUnitRegistrationDataWsDTO.getFirstName() + StringUtils.SPACE + adnocB2BUnitRegistrationDataWsDTO.getLastName());
        adnocB2BUnitRegistrationData.setEmail(adnocB2BUnitRegistrationDataWsDTO.getEmail());
        adnocB2BUnitRegistrationData.setIncoTerms(adnocB2BUnitRegistrationDataWsDTO.getIncoTerms());
        adnocB2BUnitRegistrationData.setLatitude(adnocB2BUnitRegistrationDataWsDTO.getLatitude());
        adnocB2BUnitRegistrationData.setLongitude(adnocB2BUnitRegistrationDataWsDTO.getLongitude());
        adnocB2BUnitRegistrationData.setVatId(adnocB2BUnitRegistrationDataWsDTO.getVatId());
    }

    private void populateSupportDocument(final AdnocB2BUnitRegistrationDataWsDTO adnocB2BUnitRegistrationDataWsDTO, final AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData)
    {
        LOG.debug("appEvent=AdnocB2BUnitRegistration, populating IdentificationNumberDocument with:{}", adnocB2BUnitRegistrationDataWsDTO.getIdentificationNumberDocument());
        adnocB2BUnitRegistrationData.setIdentificationNumberDocument(adnocB2BUnitRegistrationDataWsDTO.getIdentificationNumberDocument());

        LOG.debug("appEvent=AdnocB2BUnitRegistration, populating OtherDocument with:{}", adnocB2BUnitRegistrationDataWsDTO.getOtherDocument());
        adnocB2BUnitRegistrationData.setOtherDocument(adnocB2BUnitRegistrationDataWsDTO.getOtherDocument());
        adnocB2BUnitRegistrationData.setVatIdDocument(adnocB2BUnitRegistrationDataWsDTO.getVatIdDocument());
    }

    protected AdnocRegistrationDataConverter getAdnocRegistrationDataConverter()
    {
        return adnocRegistrationDataConverter;
    }

    public void setAdnocRegistrationDataConverter(final AdnocRegistrationDataConverter adnocRegistrationDataConverter)
    {
        this.adnocRegistrationDataConverter = adnocRegistrationDataConverter;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }
}
