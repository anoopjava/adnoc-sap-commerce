package com.adnoc.facades.adnocb2bfacade;

import com.adnoc.facades.b2b.unit.data.AdnocB2BUnitRegistrationData;
import com.adnoc.facades.company.data.IncoTermsData;
import com.adnoc.service.enums.PartnerFunction;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.commercewebservices.core.user.data.AddressDataList;

import java.util.List;

/**
 * Facade interface for ADNOC B2B Unit operations. Extends the standard B2BUnitFacade to provide
 * additional functionality specific to ADNOC business requirements, including management of B2B units,
 * shipping addresses, and incoterms for divisions and ship-to units.
 */
public interface AdnocB2BUnitFacade extends B2BUnitFacade
{
    /**
     * Gets List Of B2BUnits based on PartnerFunction.
     *
     * @param partnerFunction the partner function
     * @return the List of B2BUnits based on PartnerFunction.
     */
    List<B2BUnitData> getB2BUnits(PartnerFunction partnerFunction);

    /**
     * Gets List Of Shipping Addresses based on Division and IncoTerms.
     *
     * @param division  the division
     * @param incoTerms the inco terms
     * @return the List of Shipping Addresses based on Division and IncoTerms.
     */
    AddressDataList getShippingAddressList(String division, String incoTerms);

    /**
     * Gets current B2BUnits.
     *
     * @return the current B2BUnits
     */
    List<B2BUnitData> getCurrentB2BUnits();

    /**
     * Sets current b2bUnit Uid.
     *
     * @param b2BUnitUid the b2bUnit Uid
     */
    void setCurrentB2BUnit(String b2BUnitUid);

    /**
     * Register new b2b unit.
     *
     * @param adnocB2BUnitRegistrationData the adnoc b2b unit registration data
     * @return the string
     * @throws CustomerAlreadyExistsException the customer already exists exception
     */
    String registerNewB2BUnit(AdnocB2BUnitRegistrationData adnocB2BUnitRegistrationData) throws CustomerAlreadyExistsException;

    /**
     * Gets List Of IncoTerms
     *
     * @param division the division
     * @param pickup   the pickup
     * @return the List if IncoTerms based on Division and ShipTO B2BUnits.
     */
    List<IncoTermsData> getIncoTerms(String division, boolean pickup);

    /**
     * Gets List Of ShipTo IncoTerms
     *
     * @return the List if IncoTerms based on ShipTO B2BUnits.
     */
    List<IncoTermsData> getShipToApplicableIncoTerms();

    /**
     * Gets b2b unit hierarchy.
     *
     * @return the b2b unit hierarchy
     */
    List<B2BUnitNodeData> getB2BUnitHierarchy();
}