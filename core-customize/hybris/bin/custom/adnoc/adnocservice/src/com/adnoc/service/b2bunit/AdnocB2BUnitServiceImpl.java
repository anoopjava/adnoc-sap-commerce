package com.adnoc.service.b2bunit;

import com.adnoc.service.b2bunit.dao.AdnocB2BUnitDao;
import com.adnoc.service.enums.PartnerFunction;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.impl.DefaultB2BUnitService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class AdnocB2BUnitServiceImpl extends DefaultB2BUnitService implements AdnocB2BUnitService
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BUnitServiceImpl.class);

    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService;
    private AdnocB2BUnitDao adnocB2BUnitDao;

    @Override
    public Set<B2BUnitModel> getB2BUnits(final B2BCustomerModel b2BCustomerModel, final PartnerFunction partnerFunction)
    {
        if (Objects.isNull(b2BCustomerModel))
        {
            LOG.debug("appEvent=B2BUnits, b2BCustomerModel is null.");
            return Collections.emptySet();
        }
        LOG.debug("appEvent=B2BUnits, getting B2B Units for uid={} and partnerFunction={}", b2BCustomerModel.getUid(), partnerFunction);
        B2BUnitModel parentB2BUnit = getParent(b2BCustomerModel);
        if (Objects.equals(partnerFunction, PartnerFunction.SP))
        {
            parentB2BUnit = getParent(parentB2BUnit);
        }
        return getChildB2BUnits(parentB2BUnit, partnerFunction);
    }

    @Override
    public Set<B2BUnitModel> getChildB2BUnits(final B2BUnitModel b2BUnitModel, final PartnerFunction partnerFunction)
    {
        LOG.info("appEvent=B2BUnits, getting list of Child B2B Units for partnerFunction={}, b2BUnitModel={}", partnerFunction, b2BUnitModel);
        final Set<B2BUnitModel> childB2BUnits = getB2BUnits(b2BUnitModel);
        final Set<B2BUnitModel> allChildB2BUnits = childB2BUnits.stream().flatMap(childB2BUnit -> getChildB2BUnits(childB2BUnit, partnerFunction).stream()).collect(Collectors.toSet());
        final Set<B2BUnitModel> childPartnerB2BUnits = childB2BUnits.stream().filter(childB2BUnit ->
                Objects.equals(partnerFunction, childB2BUnit.getPartnerFunction())).collect(Collectors.toSet());
        allChildB2BUnits.addAll(childPartnerB2BUnits);
        LOG.debug("appEvent=B2BUnits,Added {} child B2B units to partner units for partner function {}", allChildB2BUnits.size(), partnerFunction);
        return allChildB2BUnits;
    }

    @Override
    public B2BUnitModel getCurrentB2BUnit()
    {
        final B2BCustomerModel currentB2BCustomer = getB2BCustomerService().getCurrentB2BCustomer();
        return getParent(currentB2BCustomer);
    }

    @Override
    public void setCurrentB2BUnit(final B2BUnitModel b2BUnitModel)
    {
        setCurrentUnit(getB2BCustomerService().getCurrentB2BCustomer(), b2BUnitModel);
    }

    @Override
    public Collection<B2BUnitModel> getB2BUnitsForChildMapping()
    {
        return getAdnocB2BUnitDao().fetchB2BUnitsForChildMapping();
    }

    @Override
    public B2BUnitModel getCurrentSoldTo()
    {
        final B2BUnitModel currentB2BUnit = getCurrentB2BUnit();
        return getSoldToB2BUnit(currentB2BUnit);
    }

    @Override
    public B2BUnitModel getSoldToB2BUnit(final B2BUnitModel b2BUnitModel)
    {
        validateParameterNotNullStandardMessage("b2bUnit", b2BUnitModel);
        if (Objects.equals(PartnerFunction.SP, b2BUnitModel.getPartnerFunction()))
        {
            return b2BUnitModel;
        }
        final Set<PrincipalGroupModel> allGroups = b2BUnitModel.getAllGroups();
        final B2BUnitModel soldToB2BUnitModel = allGroups.stream()
                .filter(B2BUnitModel.class::isInstance).map(B2BUnitModel.class::cast)
                .filter(parentB2BUnitModel -> Objects.equals(PartnerFunction.SP, parentB2BUnitModel.getPartnerFunction()))
                .findAny().orElse(null);
        if (Objects.isNull(soldToB2BUnitModel))
        {
            LOG.debug("appEvent=SoldToB2BUnit, No sold to B2B unit found for b2BUnitModel={}", b2BUnitModel);
        }
        return soldToB2BUnitModel;
    }

    @Override
    public B2BUnitModel getSoldToB2BUnit(final B2BCustomerModel b2BCustomerModel)
    {
        validateParameterNotNullStandardMessage("b2bCustomer", b2BCustomerModel);
        final B2BUnitModel currentB2BUnit = getParent(b2BCustomerModel);
        return getSoldToB2BUnit(currentB2BUnit);
    }

    protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2BCustomerService()
    {
        return b2BCustomerService;
    }

    public void setB2BCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService)
    {
        this.b2BCustomerService = b2BCustomerService;
    }

    protected AdnocB2BUnitDao getAdnocB2BUnitDao()
    {
        return adnocB2BUnitDao;
    }

    public void setAdnocB2BUnitDao(final AdnocB2BUnitDao adnocB2BUnitDao)
    {
        this.adnocB2BUnitDao = adnocB2BUnitDao;
    }
}
