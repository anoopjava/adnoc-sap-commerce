package com.adnoc.service.workflows.actions.impl;

import com.adnoc.service.workflows.actions.AdnocWorkflowActionDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AdnocWorkflowActionDaoImpl implements AdnocWorkflowActionDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocWorkflowActionDaoImpl.class);

    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<WorkflowActionTemplateModel> getTemplate(final List<String> codes)
    {
        LOG.debug("appEvent=AdnocWorkflowAction, fetching workflow action templates for codes: {}", codes);

        final String query = "SELECT {" + WorkflowActionTemplateModel.PK + "} " + "FROM {" + WorkflowActionTemplateModel._TYPECODE + "} " + "WHERE {" + WorkflowActionTemplateModel.CODE + "} IN (?codes)";

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        searchQuery.addQueryParameter("codes", codes);
        final SearchResult<WorkflowActionTemplateModel> searchResult = getFlexibleSearchService().search(searchQuery);

        LOG.info("appEvent=AdnocWorkflowAction, Retrieved:{} templates for codes:{}", searchResult.getResult().size(), codes);
        return searchResult.getResult();
    }

    protected FlexibleSearchService getFlexibleSearchService()
    {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
    {
        this.flexibleSearchService = flexibleSearchService;
    }
}
