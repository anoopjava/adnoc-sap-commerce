package com.adnoc.service.actions.quote;

import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocAdminCreatedEmailAction extends AbstractAdnocB2BAdminEmailAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocAdminCreatedEmailAction.class);

    @Override
    public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException
    {
        final Transition transition = super.executeAction(businessProcessModel);
        LOG.info("appEvent=AdnocAdminEmailCreation, transitionStatus={}", transition);
        return transition;
    }
}

