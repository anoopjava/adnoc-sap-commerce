package com.adnoc.service.integration.hooks;

import com.adnoc.service.enums.PartnerFunction;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class AdnocB2BCustomerPrePersist implements PrePersistHook
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BCustomerPrePersist.class);

    private UserService userService;
    private CommonI18NService commonI18NService;

    @Override
    public Optional<ItemModel> execute(final ItemModel item, final PersistenceContext context)
    {
        if (item instanceof final B2BCustomerModel b2BCustomerModel)
        {
            LOG.info("appEvent=AdnocB2BCustomerInbound, AdnocB2BCustomerPrePersist Started for customer: {}", b2BCustomerModel.getUid());

            if (Objects.isNull(b2BCustomerModel.getPk()))
            {
                LOG.debug("appEvent=AdnocB2BCustomerInbound, Inbound B2BCustomer={} is new.", b2BCustomerModel.getUid());
                final Set<PrincipalGroupModel> customerGroups = new HashSet<>(b2BCustomerModel.getGroups());
                final boolean isSoldToCustomer = customerGroups.stream().filter(B2BUnitModel.class::isInstance).map(B2BUnitModel.class::cast)
                        .anyMatch(b2BUnitModel -> Objects.equals(PartnerFunction.SP, b2BUnitModel.getPartnerFunction()));
                LOG.debug("appEvent=AdnocB2BCustomerInbound, isSoldToCustomer={} for customer: {}.", isSoldToCustomer, b2BCustomerModel.getUid());
                //For new inbound sold to level b2b customer, assigning b2b admin group and removing b2b customer group
                if (isSoldToCustomer)
                {
                    final UserGroupModel adminGroup = userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP);
                    customerGroups.add(adminGroup);
                    LOG.debug("appEvent=AdnocB2BCustomerInbound, adding {} for customer: {}", B2BConstants.B2BADMINGROUP, b2BCustomerModel.getUid());
                    final UserGroupModel customerGroup = userService.getUserGroupForUID(B2BConstants.B2BCUSTOMERGROUP);
                    customerGroups.remove(customerGroup);
                    LOG.debug("appEvent=AdnocB2BCustomerInbound, removing {} for customer: {}", B2BConstants.B2BCUSTOMERGROUP, b2BCustomerModel.getUid());
                }
                b2BCustomerModel.setGroups(customerGroups);
                if (Objects.isNull(b2BCustomerModel.getSessionCurrency()))
                {
                    b2BCustomerModel.setSessionCurrency(Objects.nonNull(getUserService().getCurrentUser().getSessionCurrency())
                            ? getUserService().getCurrentUser().getSessionCurrency()
                            : getCommonI18NService().getCurrency("AED"));
                }
            }
        }
        return Optional.of(item);
    }

    protected UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }
}
