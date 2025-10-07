package com.adnoc.backoffice.workflow;

import com.google.common.collect.Lists;
import com.hybris.backoffice.workflow.WorkflowActionDecisionActionRenderer;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionListener;
import com.hybris.cockpitng.actions.CockpitAction;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;

public class AdnocWorkflowActionDecisionActionRenderer extends WorkflowActionDecisionActionRenderer
{
    private static final String CONFIRM_LABEL = "adnocworkflowactiondecisionaction.confirm";
    private static final String CHOOSE_ACTION_LABEL = "adnocworkflowactiondecisionaction.chooseaction";

    @Override
    protected Button createConfirmDecisionButton(final ActionContext<WorkflowActionModel> context, final CockpitAction<WorkflowActionModel, WorkflowDecisionModel> action, final ActionListener<WorkflowDecisionModel> listener)
    {
        final Button confirmDecisionButton = new Button(context.getLabel("adnocworkflowactiondecisionaction.confirm"));
        confirmDecisionButton.addEventListener("onClick", (event) -> {
            confirmDecisionButton.setDisabled(true);
            perform(action, context, wrapActionListener(listener, confirmDecisionButton));
        });
        return confirmDecisionButton;
    }

    @Override
    protected Combobox createDecisionCombobox(final ActionContext<WorkflowActionModel> context, final Button confirmButton)
    {
        final Combobox combobox = new Combobox();
        combobox.setItemRenderer((comboitem, decision, index) -> {
            renderSingleComboitem(comboitem, decision);
        });
        combobox.setSclass("yw-workflowaction-selected-decision");
        combobox.setReadonly(true);
        combobox.setPlaceholder(context.getLabel("adnocworkflowactiondecisionaction.chooseaction"));
        final WorkflowActionModel workflowAction = (WorkflowActionModel) context.getData();
        final ListModelList<WorkflowDecisionModel> model = new ListModelList(Lists.newArrayList(getPermittedDecisions(workflowAction)));
        combobox.setModel(model);
        setInitialComboboxValue(combobox, context);
        combobox.addEventListener("onChange", (event) -> {
            final Object selectedValue = combobox.getSelectedItem().getValue();
            setValue(context, "selectedDecision", selectedValue);
            confirmButton.setDisabled(shouldDisableConfirmDecisionButton(combobox, workflowAction.getStatus()));
        });
        confirmButton.setDisabled(shouldDisableConfirmDecisionButton(combobox, workflowAction.getStatus()));
        return combobox;
    }
}
