/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.workflows.actions;

import com.adnoc.service.enums.B2BRegistrationStatus;
import com.adnoc.service.model.AdnocRegistrationModel;
import de.hybris.platform.b2bacceleratorservices.workflows.actions.RegistrationRejectedAutomatedWorkflowTemplateJob;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Action called when a registration request has been rejected
 */
public class AdnocRegistrationRejectedAutomatedWorkflowTemplateJob extends RegistrationRejectedAutomatedWorkflowTemplateJob
{
    private static final Logger LOG = LogManager.getLogger(AdnocRegistrationRejectedAutomatedWorkflowTemplateJob.class);

    /*
     * In this workflow step, we do nothing to reject registration, the registered customer will be removed in the next
     * send email workflow step
     *
     * @see de.hybris.platform.workflow.jobs.AutomatedWorkflowTemplateJob#perform(de.hybris.platform.workflow.model.
     * WorkflowActionModel)
     */
    @Override
    public WorkflowDecisionModel perform(final WorkflowActionModel workflowAction)
    {
        LOG.debug("appEvent=AdnocRegistrationRejected, rejecting:{} workflow", workflowAction.getCode());
        final AdnocRegistrationModel registration = (AdnocRegistrationModel) getRegistrationAttachment(workflowAction);
        LOG.info("appEvent=AdnocRegistrationRejected, Registration:{} Rejected for customer ID:{}", registration.getPk().getLongValue(), registration.getCustomer().getUid());
        registration.setStatus(B2BRegistrationStatus.REJECTED);
        getModelService().save(registration);
        return super.perform(workflowAction);
    }
}
