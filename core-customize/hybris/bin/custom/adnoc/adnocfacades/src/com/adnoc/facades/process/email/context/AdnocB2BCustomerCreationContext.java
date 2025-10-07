package com.adnoc.facades.process.email.context;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.model.AdnocB2BCustomerCreationProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AdnocB2BCustomerCreationContext extends CustomerEmailContext
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BCustomerCreationContext.class);
    private static final String ADNOC_B2BCUSTOMER_PORTAL_LOGIN_LINK = "adnoc.b2bcustomer.portal.login.link";

    private String portalLink;
    private String password;
    private String adnocB2BAdminName;
    private String adnocB2BCustomer;
    private AdnocB2BUnitService adnocB2BUnitService;

    @Override
    public void init(final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel, final EmailPageModel emailPageModel)
    {
        LOG.info("appEvent=AdnocB2BCustomer, inti method start... ");
        super.init(storeFrontCustomerProcessModel, emailPageModel);
        if (storeFrontCustomerProcessModel instanceof final AdnocB2BCustomerCreationProcessModel adnocB2BCustomerCreationProcessModel)
        {
            setPassword(((AdnocB2BCustomerCreationProcessModel) storeFrontCustomerProcessModel).getPassword());
            setPortalLink(getConfigurationService().getConfiguration().getString(ADNOC_B2BCUSTOMER_PORTAL_LOGIN_LINK));
            put("sapBusinessPartnerID", adnocB2BCustomerCreationProcessModel.getSapBusinessPartnerID());
            LOG.info("appEvent=AdnocB2BCustomer, portal link set to={}", getPortalLink());
            adnocB2BCustomer = storeFrontCustomerProcessModel.getCustomer().getName();
            LOG.info("customer Name:{}", adnocB2BCustomer);

            final B2BCustomerModel b2BCustomerModel = (B2BCustomerModel) adnocB2BCustomerCreationProcessModel.getCustomer();
            final B2BUnitModel soldToB2BUnitModel = getAdnocB2BUnitService().getSoldToB2BUnit(b2BCustomerModel);
            final Set<B2BCustomerModel> soldToB2BCustomerModels = getAdnocB2BUnitService().getB2BCustomers(soldToB2BUnitModel);
            final Set<B2BCustomerModel> soldToB2BAdminCustomers = soldToB2BCustomerModels.stream().filter(soldToB2BCustomerModel -> soldToB2BCustomerModel.getGroups().stream()
                    .anyMatch(principalGroupModel -> StringUtils.equals(principalGroupModel.getUid(), B2BConstants.B2BADMINGROUP))).collect(Collectors.toSet());

            final List<String> employeeNames = soldToB2BAdminCustomers.stream()
                    .filter(soldToB2BCustomerModel -> soldToB2BCustomerModel.getGroups().stream()
                            .anyMatch(principalGroupModel -> StringUtils.equals(principalGroupModel.getUid(), B2BConstants.B2BADMINGROUP)))
                    .map(B2BCustomerModel::getDisplayName)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            adnocB2BAdminName = String.join(", ", employeeNames);
            LOG.info("appEvent=AdnocB2BCustomer, Employee display name: {}", adnocB2BAdminName);
        }
    }

    public String getPortalLink()
    {
        return portalLink;
    }

    public void setPortalLink(final String portalLink)
    {
        this.portalLink = portalLink;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    public String getAdnocB2BAdminName()
    {
        return adnocB2BAdminName;
    }

    public void setAdnocB2BAdminName(final String adnocB2BAdminName)
    {
        this.adnocB2BAdminName = adnocB2BAdminName;
    }

    public String getAdnocB2BCustomer()
    {
        return adnocB2BCustomer;
    }

    public void setAdnocB2BCustomer(final String adnocB2BCustomer)
    {
        this.adnocB2BCustomer = adnocB2BCustomer;
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }
}