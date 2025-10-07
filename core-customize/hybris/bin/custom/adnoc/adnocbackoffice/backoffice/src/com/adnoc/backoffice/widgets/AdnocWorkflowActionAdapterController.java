package com.adnoc.backoffice.widgets;

import com.adnoc.service.address.AdnocAddressDao;
import com.adnoc.service.workflowservice.AdnocWorkflowActionService;
import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.backoffice.widgets.workflowactionadapter.WorkflowActionAdapterController;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.core.user.CockpitUserService;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AdnocWorkflowActionAdapterController extends WorkflowActionAdapterController
{
    protected static final String BACKOFFICE_WFL_INBOX_ID = "hmc_backoffice-workflow-inbox";
    /**
     * @deprecated
     */
    @Deprecated(
            since = "6.5",
            forRemoval = true
    )
    protected static final String WORKFLOW_ACTION_TYPE_CODE = "WorkflowAction";
    protected static final String WORKFLOW_ACTION_PRINCIPAL_ASSIGNED_ATTR = "principalAssigned";
    protected static final String SOCKET_INPUT_STATUSES = "statuses";
    protected static final String SOCKET_OUTPUT_CONTEXT = "context";
    protected static final String MODEL_SELECTED_STATUSES = "selectedStatuses";
    protected static final String MODEL_NAVIGATION_NODE = "navigationNode";
    protected transient CockpitUserService cockpitUserService;
    protected transient UserService userService;
    protected transient AdnocWorkflowActionService adnocWorkflowActionService;

    @SocketEvent(
            socketId = "nodeSelected"
    )
    public void createAdvancedSearchInitContext(NavigationNode navigationNode)
    {
        this.setValue("navigationNode", navigationNode);
        super.createAdvancedSearchInitContext(navigationNode);
    }

    @SocketEvent(
            socketId = "statuses"
    )
    public void setSelectedStatuses(Set<Object> statuses)
    {
        this.setValue("selectedStatuses", statuses);
        NavigationNode navigationNode = (NavigationNode) this.getValue("navigationNode", NavigationNode.class);
        if (navigationNode != null)
        {
            super.createAdvancedSearchInitContext(navigationNode);
        }

    }

    public void addSearchDataConditions(AdvancedSearchData searchData, Optional<NavigationNode> optional)
    {
        this.addUserCondition(searchData);
        this.addStatusCondition(searchData);
        this.addTemplateCondition(searchData);
    }

    private void addTemplateCondition(AdvancedSearchData searchData)
    {
        this.clearConditionsForAttribute(searchData, "template");
        Set<?> statuses = (Set) this.getValue("selectedStatuses", Set.class);
        List<SearchConditionData> conditions = new ArrayList();
        List<WorkflowActionTemplateModel> templates = getAdnocWorkflowActionService().getTemplate((Set<WorkflowActionStatus>) statuses);
        if (CollectionUtils.containsAny(statuses, WorkflowActionStatus.COMPLETED) && CollectionUtils.isNotEmpty(templates))
        {
            FieldType templaFieldType = this.createFieldType("template");
            for (WorkflowActionTemplateModel workflowActionTemplateModel : templates)
            {
                conditions.add(new SearchConditionData(templaFieldType, workflowActionTemplateModel, ValueComparisonOperator.EQUALS));
            }
            searchData.addFilterQueryRawConditionsList(ValueComparisonOperator.OR, conditions);
        }
    }

    protected void addUserCondition(AdvancedSearchData searchData)
    {
        this.clearConditionsForAttribute(searchData, "principalAssigned");
        UserModel currentUser = this.getCurrentUser();
        FieldType fieldType = this.createFieldType("principalAssigned");
        List<SearchConditionData> conditions = new ArrayList();
        SearchConditionData currentUserCondition = new SearchConditionData(fieldType, currentUser, ValueComparisonOperator.EQUALS);
        conditions.add(currentUserCondition);

        for (UserGroupModel currentGroup : this.getUserService().getAllUserGroupsForUser(currentUser))
        {
            SearchConditionData userGroupCondition = new SearchConditionData(fieldType, currentGroup, ValueComparisonOperator.EQUALS);
            conditions.add(userGroupCondition);
        }

        searchData.addFilterQueryRawConditionsList(ValueComparisonOperator.OR, conditions);
    }

    protected void clearConditionsForAttribute(AdvancedSearchData searchData, String attribute)
    {
        List<SearchConditionData> conditions = searchData.getConditions(attribute);
        if (CollectionUtils.isNotEmpty(conditions))
        {
            conditions.clear();
        }

    }

    protected FieldType createFieldType(String fieldName)
    {
        FieldType fieldType = new FieldType();
        fieldType.setDisabled(Boolean.TRUE);
        fieldType.setSelected(Boolean.TRUE);
        fieldType.setName(fieldName);
        return fieldType;
    }

    protected void addStatusCondition(AdvancedSearchData searchData)
    {
        this.clearConditionsForAttribute(searchData, "status");
        Set<?> statuses = (Set) this.getValue("selectedStatuses", Set.class);
        if (CollectionUtils.isNotEmpty(statuses))
        {
            FieldType fieldType = this.createFieldType("status");
            List<SearchConditionData> conditions = new ArrayList();

            for (Object status : statuses)
            {
                conditions.add(new SearchConditionData(fieldType, status, ValueComparisonOperator.EQUALS));
            }

            searchData.addFilterQueryRawConditionsList(ValueComparisonOperator.OR, conditions);
        }

    }

    protected UserModel getCurrentUser()
    {
        UserModel userModel = null;
        String userId = this.getCockpitUserService().getCurrentUser();
        if (StringUtils.isNotBlank(userId))
        {
            userModel = this.getUserService().getUserForUID(userId);
        }

        return userModel;
    }

    public String getTypeCode()
    {
        return "WorkflowAction";
    }

    public String getNavigationNodeId()
    {
        return "hmc_backoffice-workflow-inbox";
    }

    protected String getOutputSocketName()
    {
        return "context";
    }

    protected CockpitUserService getCockpitUserService()
    {
        return this.cockpitUserService;
    }

    @WireVariable
    public void setCockpitUserService(CockpitUserService cockpitUserService)
    {
        this.cockpitUserService = cockpitUserService;
    }

    protected UserService getUserService()
    {
        return this.userService;
    }

    @WireVariable
    public void setUserService(UserService userService)
    {
        this.userService = userService;
    }

    protected AdnocWorkflowActionService getAdnocWorkflowActionService()
    {
        return this.adnocWorkflowActionService;
    }

    @WireVariable
    public void setAdnocWorkflowActionService(AdnocWorkflowActionService adnocWorkflowActionService)
    {
        this.adnocWorkflowActionService = adnocWorkflowActionService;
    }
}
