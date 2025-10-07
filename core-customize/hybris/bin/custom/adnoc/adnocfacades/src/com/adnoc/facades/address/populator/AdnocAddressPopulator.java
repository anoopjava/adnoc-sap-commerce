package com.adnoc.facades.address.populator;

import de.hybris.platform.commercefacades.user.converters.populator.AddressPopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocAddressPopulator extends AddressPopulator
{
    private static final Logger LOG = LogManager.getLogger(AdnocAddressPopulator.class);

    @Override
    public void populate(final AddressModel source, final AddressData target)
    {
        LOG.info("appEvent=AdnocAddress, populate method called with source={},target={}", source, target);

        super.populate(source, target);
        if (StringUtils.isNotBlank(source.getSapCustomerID()))
        {
            LOG.debug("appEvent=AdnocAddress, found SapCustomerID ={}", source.getSapCustomerID());
            target.setSapCustomerID(source.getSapCustomerID());
        }
    }
}
