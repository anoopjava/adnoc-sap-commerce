package com.adnoc.service.b2bunit;

import com.adnoc.service.enums.PartnerFunction;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;

import java.util.Collection;
import java.util.Set;

/**
 * Service interface for B2B Unit operations in ADNOC.
 * Extends the core B2BUnitService to provide additional functionality for managing B2B units,
 * including partner function-based unit filtering, session management, and child unit mapping.
 */
public interface AdnocB2BUnitService extends B2BUnitService<B2BUnitModel, B2BCustomerModel>
{
    /**
     * Gets b2b units.
     *
     * @param b2BCustomerModel the b2bCustomerModel
     * @param partnerFunction  the partner function
     * @return the b2b units
     */
    Set<B2BUnitModel> getB2BUnits(B2BCustomerModel b2BCustomerModel, PartnerFunction partnerFunction);

    /**
     * Gets child b2b units.
     *
     * @param b2BUnitModel    the b2b unit model
     * @param partnerFunction the partner function
     * @return the child b2b units
     */
    Set<B2BUnitModel> getChildB2BUnits(B2BUnitModel b2BUnitModel, PartnerFunction partnerFunction);

    /**
     * Get current payer from session
     *
     * @return
     */
    B2BUnitModel getCurrentB2BUnit();

    /**
     * Set b2BUnit in session
     *
     * @param b2BUnitModel
     */
    void setCurrentB2BUnit(B2BUnitModel b2BUnitModel);

    /**
     * Gets b2b units for child mapping.
     *
     * @return the b2b units for child mapping
     */
    Collection<B2BUnitModel> getB2BUnitsForChildMapping();

    /**
     * Gets current sold to.
     *
     * @return the current sold to
     */
    B2BUnitModel getCurrentSoldTo();

    /**
     * Gets sold to B2B unit for the given customer.
     *
     * @param b2BCustomerModel the b2b customer
     * @return the sold to B2B unit
     */
    B2BUnitModel getSoldToB2BUnit(B2BCustomerModel b2BCustomerModel);

    /**
     * Gets sold to B2B unit for the given B2B unit model.
     *
     * @param b2BUnitModel the b2b unit model
     * @return the sold to B2B unit
     */
    B2BUnitModel getSoldToB2BUnit(B2BUnitModel b2BUnitModel);
}
