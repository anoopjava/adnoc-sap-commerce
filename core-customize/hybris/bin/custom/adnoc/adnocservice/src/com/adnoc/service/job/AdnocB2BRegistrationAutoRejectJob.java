package com.adnoc.service.job;

import com.adnoc.service.config.AdnocConfigService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class AdnocB2BRegistrationAutoRejectJob extends AbstractJobPerformable<CronJobModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BRegistrationAutoRejectJob.class);

    public static final String B2B_REGISTRATION_REJECTED = "B2BRRegistrationRejected";
    public static final String B2B_USER_REGISTRATION = "B2BUserRegistration";
    public static final String ADNOC_ADMINUSER = "ecommmercespelicalistapprover";
    public static final String REGISTRATION_AUTO_REJECTION_DAYS = "registrationAutoRejectionDays";
    public static final int DEFAULT_REGISTRATION_AUTO_REJECTION_DAYS = 15;

    private WorkflowTemplateService workflowTemplateService;
    private UserService userService;
    private WorkflowProcessingService workflowProcessingService;
    private AdnocConfigService adnocConfigService;
    private WorkflowService workflowService;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel)
    {
        LOG.info("appEvent=AdnocB2BRegistrationAutoReject,perform method called");
        final WorkflowTemplateModel workflowTemplate = getWorkflowTemplateService().getWorkflowTemplateForCode(
                B2B_USER_REGISTRATION);
        final UserModel userModel = getUserService().getUserForUID(ADNOC_ADMINUSER);
        LOG.info("appEvent=AdnocB2BRegistrationAutoReject,fetch adnoc config for:{}", ADNOC_ADMINUSER);

        final int expiryDays = getAdnocConfigService().getAdnocConfigValue(REGISTRATION_AUTO_REJECTION_DAYS, DEFAULT_REGISTRATION_AUTO_REJECTION_DAYS);
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -expiryDays);
        final Date time = calendar.getTime();
        final List<WorkflowModel> workflowsForTemplateAndUser = getWorkflowService().getWorkflowsForTemplateAndUser(workflowTemplate, userModel);
        final List<WorkflowModel> eligibleWorkflows = workflowsForTemplateAndUser.stream()
                .filter(workflow -> !Objects.equals(workflow.getStatus(), CronJobStatus.FINISHED))
                .filter(workflow -> workflow.getCreationtime().getDate() == time.getDate()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(eligibleWorkflows))
        {
            eligibleWorkflows.forEach(workflow -> {
                LOG.info("Rejecting workflow for code: {}", workflow.getCode());
                final List<WorkflowActionModel> workflowActionModels = workflow.getActions();
                for (final WorkflowActionModel workflowActionModel : workflowActionModels)
                {
                    findDecision(workflowActionModel, B2B_REGISTRATION_REJECTED)
                            .ifPresent(decision -> {
                                LOG.info("Decision [{}] found for workflow action [{}]. Proceeding to reject.",
                                        decision.getCode(), workflowActionModel.getCode());
                                getWorkflowProcessingService().decideAction(workflowActionModel, decision);
                            });
                    LOG.info("Decision with code B2BRRegistrationRejected not found for workflow: {}", workflow.getCode());
                }
            });
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    /**
     * @param workflowActionModel
     * @param decisionCode
     * @return
     */
    private Optional<WorkflowDecisionModel> findDecision(final WorkflowActionModel workflowActionModel, final String decisionCode)
    {
        LOG.info("appEvent=AdnocB2BRegistrationAutoReject,finding decision with code {} workflow action{}", decisionCode, workflowActionModel.getCode());
        return workflowActionModel.getDecisions().stream()
                .filter(decision -> decision.getCode().equals(decisionCode))
                .findFirst();
    }


    protected WorkflowTemplateService getWorkflowTemplateService()
    {
        return workflowTemplateService;
    }

    public void setWorkflowTemplateService(final WorkflowTemplateService workflowTemplateService)
    {
        this.workflowTemplateService = workflowTemplateService;
    }

    protected UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    protected WorkflowProcessingService getWorkflowProcessingService()
    {
        return workflowProcessingService;
    }

    public void setWorkflowProcessingService(final WorkflowProcessingService workflowProcessingService)
    {
        this.workflowProcessingService = workflowProcessingService;
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }

    protected WorkflowService getWorkflowService()
    {
        return workflowService;
    }

    public void setWorkflowService(final WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }

    @Override
    public boolean isAbortable()
    {
        return true;
    }

}
