package com.adnoc.service.actions.quote;

import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * This class is responsible for notifying B2B Customer Admins when a new customer created.
 * The notification will be sent to CS Admin (CS Cockpit) email addresses, which belong to the
 * B2BADMINGROUP user group.
 */
public class AdnocShipToCreatedB2BCustomerEmailAction extends AbstractAdnocB2BAdminEmailAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocShipToCreatedB2BCustomerEmailAction.class);

    @Override
    public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException
    {
        final Transition transition = super.executeAction(businessProcessModel);
        LOG.info("appEvent=AdnocShipToEmailCreation, transitionStatus={}", transition);
        return transition;
    }
}
