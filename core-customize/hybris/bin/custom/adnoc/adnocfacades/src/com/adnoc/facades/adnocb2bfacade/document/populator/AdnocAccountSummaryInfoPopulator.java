package com.adnoc.facades.adnocb2bfacade.document.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.data.AccountSummaryInfoData;
import de.hybris.platform.b2bacceleratorfacades.document.populators.AccountSummaryInfoPopulator;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class AdnocAccountSummaryInfoPopulator extends AccountSummaryInfoPopulator
{
    @Override
    protected void setAccountManagerDetails(final B2BUnitModel b2bUnitModel, final AccountSummaryInfoData target)
    {
        String accountManagerName = StringUtils.EMPTY;
        final StringBuilder emailStringBuilder = new StringBuilder();
        final EmployeeModel employeeModel = b2bUnitModel.getCollector();

        if (employeeModel != null)
        {
            final UserModel accountManager = userService.getUserForUID(employeeModel.getUid());

            if (accountManager != null)
            {
                populateEmail(emailStringBuilder, accountManager);
                accountManagerName = accountManager.getDisplayName();
            }
        }

        target.setAccountManagerName(accountManagerName);
        target.setAccountManagerEmail(emailStringBuilder.toString());
    }

    @Override
    protected AddressData getDefaultAddress(final B2BUnitData b2bUnitData)
    {
        if (CollectionUtils.isNotEmpty(b2bUnitData.getAddresses()))
        {
            return b2bUnitData.getAddresses().get(0);
        }
        return null;
    }
}
