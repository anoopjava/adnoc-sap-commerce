package com.adnoc.service.b2bunit.dao;

import de.hybris.platform.b2b.dao.B2BUnitDao;
import de.hybris.platform.b2b.model.B2BUnitModel;

import java.util.Collection;

public interface AdnocB2BUnitDao extends B2BUnitDao
{
    /**
     * Fetch b2b units for child mapping set.
     *
     * @return the collection of b2bunits
     */
    Collection<B2BUnitModel> fetchB2BUnitsForChildMapping();

}
