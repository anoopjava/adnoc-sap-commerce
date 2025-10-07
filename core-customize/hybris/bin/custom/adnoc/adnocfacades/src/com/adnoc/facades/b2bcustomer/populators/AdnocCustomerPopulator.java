package com.adnoc.facades.b2bcustomer.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.user.converters.populator.CustomerPopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocCustomerPopulator extends CustomerPopulator
{
    private static final Logger LOG = LogManager.getLogger(AdnocCustomerPopulator.class);

    private EnumerationService enumerationService;

    @Override
    public void populate(final CustomerModel customerModel, final CustomerData customerData)
    {
        LOG.info("appEvent=B2BCustomer,populate customerModel to customerData");

        super.populate(customerModel, customerData);
        if (customerModel instanceof final B2BCustomerModel b2BCustomerModel)
        {
            customerData.setPreferredCommunicationChannel(String.valueOf(b2BCustomerModel.getPreferredCommunicationChannel()));
            customerData.setGender(String.valueOf(b2BCustomerModel.getGender()));
            customerData.setNationality(String.valueOf(b2BCustomerModel.getNationality()));
            customerData.setIdentityType(String.valueOf(b2BCustomerModel.getIdentityType()));
            customerData.setIdentificationNumber(b2BCustomerModel.getIdentificationNumber());
            customerData.setIdentificationValidFrom(b2BCustomerModel.getIdentificationValidFrom());
            customerData.setIdentificationValidTo(b2BCustomerModel.getIdentificationValidTo());
            if (Objects.nonNull(b2BCustomerModel.getDesignation())) {
                String designation = getEnumerationService().getEnumerationName(b2BCustomerModel.getDesignation());
                customerData.setDesignation(designation);
            }
            customerData.setMobileNumber(b2BCustomerModel.getMobileNumber());
            customerData.setTelephone(b2BCustomerModel.getTelephone());
            customerData.setLastlogin(b2BCustomerModel.getLastLogin());
            customerData.setCompanyAddressStreet(b2BCustomerModel.getCompanyAddressStreet());
            customerData.setCompanyAddressStreetLine2(b2BCustomerModel.getCompanyAddressStreetLine2());
            customerData.setCompanyAddressCountryIso(String.valueOf(b2BCustomerModel.getCompanyAddressCountry()));
            customerData.setCompanyAddressRegion(String.valueOf(b2BCustomerModel.getCompanyAddressRegion()));
            customerData.setCompanyAddressCity(String.valueOf(b2BCustomerModel.getCompanyAddressCity()));
            customerData.setSapBusinessPartnerID(b2BCustomerModel.getSapBusinessPartnerID());

            final String finalStatus = BooleanUtils.isNotTrue(b2BCustomerModel.getSapIsReplicated()) ? "In-Progress" :
                    BooleanUtils.isTrue(b2BCustomerModel.isLoginDisabled()) ? "Not for Login" :
                            BooleanUtils.isFalse(b2BCustomerModel.getActive()) ? "InActive" : "Active";

            LOG.info("appEvent=B2BCustomer, UserId: {}, Customer Status: {}", b2BCustomerModel.getUid(), finalStatus);
            customerData.setStatus(finalStatus);
        }
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }
}
