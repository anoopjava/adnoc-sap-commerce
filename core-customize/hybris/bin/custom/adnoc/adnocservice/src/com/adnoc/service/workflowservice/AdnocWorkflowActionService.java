package com.adnoc.service.workflowservice;

import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;

import java.util.List;
import java.util.Set;

/**
 * AdnocWorkflowActionService interface provides methods to retrieve workflow action templates based on their status.
 * It is used to manage and interact with workflow actions in the Adnoc system.
 */
public interface AdnocWorkflowActionService
{
    /**
     * Retrieves a list of WorkflowActionTemplateModel based on the provided set of WorkflowActionStatus.
     *
     * @param status a set of WorkflowActionStatus to filter the templates
     * @return a list of WorkflowActionTemplateModel that match the given status
     */
    List<WorkflowActionTemplateModel> getTemplate(Set<WorkflowActionStatus> status);
}
