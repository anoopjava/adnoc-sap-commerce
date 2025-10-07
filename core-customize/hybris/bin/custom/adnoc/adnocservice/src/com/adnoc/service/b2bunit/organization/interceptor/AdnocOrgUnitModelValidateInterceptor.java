package com.adnoc.service.b2bunit.organization.interceptor;

import de.hybris.platform.commerceservices.organization.interceptor.OrgUnitModelValidateInterceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocOrgUnitModelValidateInterceptor extends OrgUnitModelValidateInterceptor
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrgUnitModelValidateInterceptor.class);

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
    {
        //keeping it intentionally empty
        LOG.info("appEvent=OrgUnitModelValidateInterceptor, Inside OrgUnitModelValidateInterceptor.");
    }
}
