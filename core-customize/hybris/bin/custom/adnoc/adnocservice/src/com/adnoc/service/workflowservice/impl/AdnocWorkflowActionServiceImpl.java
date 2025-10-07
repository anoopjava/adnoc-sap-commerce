package com.adnoc.service.workflowservice.impl;

import com.adnoc.service.workflows.actions.AdnocWorkflowActionDao;
import com.adnoc.service.workflowservice.AdnocWorkflowActionService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class AdnocWorkflowActionServiceImpl implements AdnocWorkflowActionService
{
    private static final Logger LOG = LogManager.getLogger(AdnocWorkflowActionServiceImpl.class);

    private static final List<String> COMPLETED_STATUS = List.of("B2BRRegistrationApproved", "B2BRRegistrationRejected");
    private static final List<String> IN_PROGRESS_COMPLETED_STATUS = List.of("B2BRRegistrationApproved", "B2BRRegistrationRejected", "B2BRegistrationApproval");

    private AdnocWorkflowActionDao adnocWorkflowActionDao;

    @Override
    public List<WorkflowActionTemplateModel> getTemplate(@Nonnull final Set<WorkflowActionStatus> status)
    {
        LOG.debug("appEvent=AdnocWorkflowAction, workflow action template for status:{}", status);
        final List<String> templateCodes = CollectionUtils.containsAll(status, Set.of(WorkflowActionStatus.COMPLETED, WorkflowActionStatus.IN_PROGRESS)) ? IN_PROGRESS_COMPLETED_STATUS : COMPLETED_STATUS;
        return getAdnocWorkflowActionDao().getTemplate(templateCodes);
    }

    protected AdnocWorkflowActionDao getAdnocWorkflowActionDao()
    {
        return adnocWorkflowActionDao;
    }

    public void setAdnocWorkflowActionDao(final AdnocWorkflowActionDao adnocWorkflowActionDao)
    {
        this.adnocWorkflowActionDao = adnocWorkflowActionDao;
    }
}
