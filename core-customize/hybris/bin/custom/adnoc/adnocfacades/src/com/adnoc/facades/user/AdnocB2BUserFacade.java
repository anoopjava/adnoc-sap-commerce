package com.adnoc.facades.user;

import com.adnoc.facades.data.AdnocOrderSummaryData;
import com.adnoc.facades.product.data.GenderData;
import com.adnoc.facades.user.data.*;
import de.hybris.platform.b2bcommercefacades.company.B2BUserFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;

import java.util.List;

/**
 * Adnoc B2B User Facade interface.
 * This interface extends the B2BUserFacade to provide additional functionality specific to ADNOC B2B users.
 */
public interface AdnocB2BUserFacade extends B2BUserFacade
{
    /**
     * Get list of genders
     *
     * @return List of Genders
     */
    List<GenderData> getGenders();

    /**
     * Get list of Nationalities
     *
     * @return List of nationalities
     */

    List<NationalityData> getNationalities();

    /**
     * Get list Preferred Communication channel
     *
     * @return list of Preferred Communication channels
     */

    List<PreferredCommunicationChannelData> getPreferredCommunicationChannels();

    /**
     * Get list of identity types
     *
     * @return list of Identity types
     */

    List<IdentityTypeData> getIdentityTypes();

    /**
     * Get list of Designation Types
     *
     * @return list of designation types
     */

    List<AdnocDesignationData> getDesignationTypes();

    /**
     * Get list trade license authority types
     *
     * @return list of trade license authority types
     */

    List<AdnocTradeLicenseAuthorityData> getTradeLicenseAuthorityTypes();

    /**
     * Gets order summary.
     *
     * @param userId the user id
     * @return the order summary
     */
    AdnocOrderSummaryData getOrderSummary(String userId);

    /**
     * Is user existing boolean.
     *
     * @param orgCustomerData the org customer data
     * @return the boolean
     */
    boolean isUserExisting(CustomerData orgCustomerData);
}
