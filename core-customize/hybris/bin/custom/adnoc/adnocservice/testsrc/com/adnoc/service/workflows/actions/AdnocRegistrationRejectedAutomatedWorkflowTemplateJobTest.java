package com.adnoc.service.workflows.actions;

import com.adnoc.service.enums.B2BRegistrationStatus;
import com.adnoc.service.model.AdnocRegistrationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocRegistrationRejectedAutomatedWorkflowTemplateJobTest
{
    @InjectMocks
    private AdnocRegistrationRejectedAutomatedWorkflowTemplateJob adnocRegistrationRejectedAutomatedWorkflowTemplateJob=new AdnocRegistrationRejectedAutomatedWorkflowTemplateJob();


    private WorkflowActionModel workflowAction = Mockito.mock(WorkflowActionModel.class);

    private AdnocRegistrationModel adnocRegistrationModel = Mockito.mock(AdnocRegistrationModel.class);

    private WorkflowAttachmentService workflowAttachmentService = Mockito.mock(WorkflowAttachmentService.class);
    private CustomerModel customer = Mockito.mock(CustomerModel.class);
    private ModelService modelService = Mockito.mock(ModelService.class);

    @Mock
    private WorkflowDecisionModel workflowDecisionModel= Mockito.mock(WorkflowDecisionModel.class);

    @Test
    void testPerform()
    {
        adnocRegistrationModel.setCustomer(customer);
        List<ItemModel> itemModels = new ArrayList<>();
        itemModels.add(adnocRegistrationModel);
        Mockito.when(workflowAttachmentService.getAttachmentsForAction(any(), anyString())).thenReturn(itemModels);
        Mockito.when(adnocRegistrationModel.getCustomer()).thenReturn(customer);

        adnocRegistrationRejectedAutomatedWorkflowTemplateJob.setWorkflowAttachmentService(workflowAttachmentService);
        adnocRegistrationRejectedAutomatedWorkflowTemplateJob.setModelService(modelService);

        List<WorkflowDecisionModel> workflowDecisionModels=new ArrayList<>();
        workflowDecisionModels.add(workflowDecisionModel);
        Mockito.when(workflowAction.getDecisions()).thenReturn(workflowDecisionModels);

        WorkflowDecisionModel workflowDecisionModel1= adnocRegistrationRejectedAutomatedWorkflowTemplateJob.perform(workflowAction);
        Assertions.assertThat(workflowDecisionModel1).isNotNull();
    }


}