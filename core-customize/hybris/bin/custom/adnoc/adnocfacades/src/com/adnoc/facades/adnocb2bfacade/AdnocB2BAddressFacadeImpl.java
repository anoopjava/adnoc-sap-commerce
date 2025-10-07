package com.adnoc.facades.adnocb2bfacade;

import com.adnoc.service.address.AdnocAddressService;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocB2BAddressFacadeImpl implements AdnocB2BAddressFacade
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BAddressFacadeImpl.class);

    private AdnocAddressService adnocAddressService;
    private Converter<AddressModel, AddressData> addressConverter;

    @Override
    public AddressData getAddress(final String pk)
    {
        LOG.info("appEvent=AdnocB2BAddress, getAddress method called with pk:{}", pk);
        final AddressData convertedAddressData = getAddressConverter().convert(getAdnocAddressService().getAddress(pk));
        LOG.debug("appEvent=AdnocB2BAddress, Converted address for pk={} -> {}", pk, convertedAddressData);
        return convertedAddressData;
    }

    protected AdnocAddressService getAdnocAddressService()
    {
        return adnocAddressService;
    }

    public void setAdnocAddressService(final AdnocAddressService adnocAddressService)
    {
        this.adnocAddressService = adnocAddressService;
    }

    protected Converter<AddressModel, AddressData> getAddressConverter()
    {
        return addressConverter;
    }

    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
    {
        this.addressConverter = addressConverter;
    }

}
