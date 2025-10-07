/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.workflows.actions;

import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import com.adnoc.service.enums.B2BRegistrationStatus;
import com.adnoc.service.model.AdnocRegistrationModel;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.b2bacceleratorservices.workflows.actions.RegistrationApprovedAutomatedWorkflowTemplateJob;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Action called when a registration request has been approved
 */
public class AdnocRegistrationApprovedAutomatedWorkflowTemplateJob extends RegistrationApprovedAutomatedWorkflowTemplateJob
{
    private static final Logger LOG = LogManager.getLogger(AdnocRegistrationApprovedAutomatedWorkflowTemplateJob.class);

    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.workflow.jobs.AutomatedWorkflowTemplateJob#perform(de.hybris.platform.workflow.model.
     * WorkflowActionModel)
     */
    @Override
    public WorkflowDecisionModel perform(final WorkflowActionModel workflowAction)
    {
        LOG.debug("appEvent=AdnocRegistrationApproved, starting {} workflow action", workflowAction.getWorkflow().getName());
        final B2BRegistrationModel registration = getRegistrationAttachment(workflowAction);
        final AdnocRegistrationModel adnocRegistrationModel = ((AdnocRegistrationModel) registration);

        LOG.info("appEvent=AdnocRegistrationApproved,approving registration:{} for customer:{}", adnocRegistrationModel.getPk().getLongValue(), adnocRegistrationModel.getCustomer().getName());
        adnocRegistrationModel.setStatus(B2BRegistrationStatus.APPROVED);
        getModelService().save(adnocRegistrationModel);

        LOG.info("appEvent=AdnocRegistrationApproved, Triggering outbound replication for Registered Customer {}", adnocRegistrationModel.getName());
        getAdnocOutboundReplicationDirector().scheduleOutboundTask(adnocRegistrationModel);

        final CustomerModel customer = getCustomer(registration);
        LOG.debug("appEvent=AdnocRegistrationApproved, deleting temporary customer:{}", customer.getName());
        //Delete temporary customer attached to workflow
        getModelService().remove(customer);

        LOG.info("appEvent=AdnocRegistrationApproved, Approval action completed for customer email :{}", adnocRegistrationModel.getEmail());
        return defaultDecision(workflowAction);
    }

    protected AdnocOutboundReplicationDirector getAdnocOutboundReplicationDirector()
    {
        return adnocOutboundReplicationDirector;
    }

    public void setAdnocOutboundReplicationDirector(final AdnocOutboundReplicationDirector adnocOutboundReplicationDirector)
    {
        this.adnocOutboundReplicationDirector = adnocOutboundReplicationDirector;
    }
}
