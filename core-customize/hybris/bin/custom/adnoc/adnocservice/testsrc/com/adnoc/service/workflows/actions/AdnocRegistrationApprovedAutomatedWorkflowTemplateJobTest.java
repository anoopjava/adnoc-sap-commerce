package com.adnoc.service.workflows.actions;

import com.adnoc.service.director.AdnocOutboundReplicationDirector;
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
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocRegistrationApprovedAutomatedWorkflowTemplateJobTest
{
    @InjectMocks
    AdnocRegistrationApprovedAutomatedWorkflowTemplateJob adnocRegistrationApprovedAutomatedWorkflowTemplateJob = new AdnocRegistrationApprovedAutomatedWorkflowTemplateJob();

    private WorkflowActionModel workflowAction = Mockito.mock(WorkflowActionModel.class);

    private WorkflowModel workflowModel = Mockito.mock(WorkflowModel.class);

    private AdnocRegistrationModel adnocRegistrationModel = Mockito.mock(AdnocRegistrationModel.class);

    private CustomerModel customer = Mockito.mock(CustomerModel.class);
    private WorkflowAttachmentService workflowAttachmentService = Mockito.mock(WorkflowAttachmentService.class);
    private ModelService modelService = Mockito.mock(ModelService.class);
    private AdnocOutboundReplicationDirector adnocOutboundReplicationDirector = Mockito.mock(AdnocOutboundReplicationDirector.class);
    private WorkflowDecisionModel workflowDecisionModel= Mockito.mock(WorkflowDecisionModel.class);


    @Test
    void testPerform()
    {
        adnocRegistrationModel.setCustomer(customer);
        List<ItemModel> itemModels = new ArrayList<>();
        itemModels.add(adnocRegistrationModel);
        Mockito.when(workflowAttachmentService.getAttachmentsForAction(any(), anyString())).thenReturn(itemModels);
        Mockito.when(adnocRegistrationModel.getCustomer()).thenReturn(customer);
        Mockito.when(workflowAction.getWorkflow()).thenReturn(workflowModel);
        List<WorkflowDecisionModel> workflowDecisionModels=new ArrayList<>();
        workflowDecisionModels.add(workflowDecisionModel);
        Mockito.when(workflowAction.getDecisions()).thenReturn(workflowDecisionModels);

        adnocRegistrationApprovedAutomatedWorkflowTemplateJob.setWorkflowAttachmentService(workflowAttachmentService);
        adnocRegistrationApprovedAutomatedWorkflowTemplateJob.setModelService(modelService);
        adnocRegistrationApprovedAutomatedWorkflowTemplateJob.setAdnocOutboundReplicationDirector(adnocOutboundReplicationDirector);

        WorkflowDecisionModel decision = adnocRegistrationApprovedAutomatedWorkflowTemplateJob.perform(workflowAction);
        Assertions.assertThat(decision).isNotNull();
    }

}