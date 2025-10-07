package com.adnoc.facades.b2bcustomer.populators;

import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BCustomerPopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.hybris.platform.b2b.constants.B2BConstants.B2BADMINGROUP;
import static de.hybris.platform.b2b.constants.B2BConstants.B2BCUSTOMERGROUP;

public class AdnocB2BCustomerPopulator extends B2BCustomerPopulator
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BCustomerPopulator.class);

    @Override
    public void populate(final CustomerModel source, final CustomerData target) throws ConversionException
    {
        super.populate(source, target);
        target.setCurrency(getCurrencyConverter().convert(source.getSessionCurrency()));
        target.setUserRole(getUserRoles(target));
    }

    private String getUserRoles(final CustomerData target)
    {
        if (CollectionUtils.isNotEmpty(target.getRoles()))
        {
            LOG.info("appevent=customerPopulator, customer roles is not empty");
            if (target.getRoles().contains(B2BADMINGROUP))
            {
                return "Super Admin";
            }
            else if (target.getRoles().contains(B2BCUSTOMERGROUP))
            {
                return "Payer User";
            }
        }
        return "No Role Assigned";
    }
}
