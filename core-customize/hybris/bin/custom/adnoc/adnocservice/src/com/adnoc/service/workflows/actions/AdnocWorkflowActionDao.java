package com.adnoc.service.workflows.actions;

import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;

import java.util.List;

/**
 * The interface Adnoc workflow action dao.
 */
public interface AdnocWorkflowActionDao
{
    /**
     * Gets template.
     *
     * @param codes the codes
     * @return the template
     */
    List<WorkflowActionTemplateModel> getTemplate(List<String> codes);
}
