package com.adnoc.service.config.dao.impl;

import com.adnoc.service.config.dao.AdnocConfigDao;
import com.adnoc.service.model.AdnocBackofficeApprovalRegistrationEnumMappingModel;
import com.adnoc.service.model.AdnocConfigModel;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import com.adnoc.service.model.AdnocSapIntegrationCodeMapModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class AdnocConfigDaoImpl implements AdnocConfigDao
{
    private static final Logger LOG = LogManager.getLogger(AdnocConfigDaoImpl.class);

    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<AdnocConfigModel> findAdnocConfig(final String... configKeys)
    {
        LOG.info(" appEvent=AdnocConfig ,findAdnocConfig called with configKeys: {}", (Object[]) configKeys);
        final String query = "SELECT {" + AdnocConfigModel.PK + "} FROM {" + AdnocConfigModel._TYPECODE
                + "} WHERE {" + AdnocConfigModel.CONFIGKEY + "} IN (?" + AdnocConfigModel.CONFIGKEY + ")";
        final Map<String, Object> params = new HashMap<>();
        params.put(AdnocConfigModel.CONFIGKEY, Arrays.asList(configKeys));

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, params);
        final SearchResult<AdnocConfigModel> result = getFlexibleSearchService().search(searchQuery);

        LOG.info("appEvent=AdnocConfig, findAdnocConfig completed, returning {} results for the provided keys.", result.getResult().size());
        return result.getResult();
    }

    @Override
    public AdnocSapIntegrationCodeMapModel findAdnocSapIntegrationCodeMap(final IntegrationType integrationType,
                                                                          final Class objType, final String enumCode)
    {
        LOG.info(" appEvent=AdnocSapIntegrationCodeMap ,findAdnocSapIntegrationCodeMap called with integrationType: {},objType: {},enumCode: {}", integrationType, objType, enumCode);

        String query = "SELECT {" + AdnocSapIntegrationCodeMapModel.PK + "} FROM {" + AdnocSapIntegrationCodeMapModel._TYPECODE
                + "} WHERE {" + AdnocSapIntegrationCodeMapModel.INTEGRATIONTYPE + "}=?" + AdnocSapIntegrationCodeMapModel.INTEGRATIONTYPE + " AND {"
                + AdnocSapIntegrationCodeMapModel.ENUMTYPE + "}=?" + AdnocSapIntegrationCodeMapModel.ENUMTYPE;

        final Map<String, Object> params = new HashMap<>();
        params.put(AdnocSapIntegrationCodeMapModel.INTEGRATIONTYPE, integrationType);
        params.put(AdnocSapIntegrationCodeMapModel.ENUMTYPE, objType.getName());
        if (Objects.equals(IntegrationType.OUTBOUND, integrationType))
        {
            query = query + " AND {" + AdnocSapIntegrationCodeMapModel.ENUMCODE + "}=?" + AdnocSapIntegrationCodeMapModel.ENUMCODE;
            params.put(AdnocSapIntegrationCodeMapModel.ENUMCODE, enumCode);
        }
        else
        {
            query = query + " AND {" + AdnocSapIntegrationCodeMapModel.SAPCODE + "}=?" + AdnocSapIntegrationCodeMapModel.SAPCODE;
            params.put(AdnocSapIntegrationCodeMapModel.SAPCODE, enumCode);
        }

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, params);
        final SearchResult<AdnocSapIntegrationCodeMapModel> result = getFlexibleSearchService().search(searchQuery);

        LOG.info("appEvent=AdnocSapIntegrationCodeMap,findAdnocSapIntegrationCodeMap method completed,returning {} results", result.getResult());
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult().get(0) : null;
    }

    @Override
    public List<AdnocCsTicketCategoryMapModel> findAdnocCsTicketCategoryMap()
    {
        LOG.debug(" appEvent=AdnocCsTicketCategoryMap ,findAdnocCsTicketCategoryMap is being called");

        final String query = "SELECT {" + AdnocCsTicketCategoryMapModel.PK + "} FROM {" + AdnocCsTicketCategoryMapModel._TYPECODE + "}";

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        final SearchResult<AdnocCsTicketCategoryMapModel> result = getFlexibleSearchService().search(searchQuery);

        LOG.debug("appEvent=AdnocCsTicketCategoryMap,findAdnocCsTicketCategoryMap method completed, returning {} results", result.getResult());
        return result.getResult();
    }

    @Override
    public AdnocCsTicketCategoryMapModel findAdnocCsTicketCategoryMap(final String csTicketCategoryMapId)
    {
        LOG.info("appEvent=AdnocCsTicketCategoryMap,findAdnocCsTicketCategoryMap start with csTicketCategoryMapId: {}", csTicketCategoryMapId);
        final String query = "SELECT {" + AdnocCsTicketCategoryMapModel.PK + "} FROM {" + AdnocCsTicketCategoryMapModel._TYPECODE
                + "} WHERE {" + AdnocCsTicketCategoryMapModel.CSTICKETCATEGORYMAPID + "}=?" + AdnocCsTicketCategoryMapModel.CSTICKETCATEGORYMAPID;

        final Map<String, Object> params = new HashMap<>();
        params.put(AdnocCsTicketCategoryMapModel.CSTICKETCATEGORYMAPID, csTicketCategoryMapId);

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, params);
        final SearchResult<AdnocCsTicketCategoryMapModel> result = getFlexibleSearchService().search(searchQuery);

        LOG.info("appEvent=AdnocCsTicketCategoryMap, findAdnocCsTicketCategoryMap method END and returning result");
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult().get(0) : null;
    }

    @Override
    public SAPSalesOrganizationModel getSalesOrgbyDivision(final String division)
    {

        LOG.info("appEvent=SAPSalesOrganization,getSalesOrgbyDivision called with division: {}", division);

        final String query = "SELECT {" + SAPSalesOrganizationModel.PK + "} FROM {" + SAPSalesOrganizationModel._TYPECODE
                + "} WHERE {" + SAPSalesOrganizationModel.DIVISION + "}=?" + SAPSalesOrganizationModel.DIVISION;

        final Map<String, Object> params = new HashMap<>();
        params.put(SAPSalesOrganizationModel.DIVISION, division);

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, params);
        final SearchResult<SAPSalesOrganizationModel> result = getFlexibleSearchService().search(searchQuery);

        LOG.info("appEvent=SAPSalesOrganization, getSalesOrgbyDivision method END and returning result");
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult().get(0) : null;
    }

    @Override
    public List<AdnocBackofficeApprovalRegistrationEnumMappingModel> getTargetEnumValues(Class sourceEnumType, String sourceEnumValue, Class targetEnumType)
    {
        LOG.info("appEvent=AdnocBackofficeApprovalRegistration, Resolving mapping for sourceEnumType: {}, sourceEnumValue: {}, targetEnumType: {}",
                sourceEnumType.getName(), sourceEnumValue, targetEnumType.getName());

        final String query = "SELECT {" + AdnocBackofficeApprovalRegistrationEnumMappingModel.PK + "} FROM {"
                + AdnocBackofficeApprovalRegistrationEnumMappingModel._TYPECODE + "} WHERE {"
                + AdnocBackofficeApprovalRegistrationEnumMappingModel.SOURCEENUMTYPE + "} = ?sourceEnumType AND {"
                + AdnocBackofficeApprovalRegistrationEnumMappingModel.SOURCEENUMVALUE + "} = ?sourceEnumValue AND {"
                + AdnocBackofficeApprovalRegistrationEnumMappingModel.TARGETENUMTYPE + "} = ?targetEnumType";

        final Map<String, Object> params = Map.of("sourceEnumType", sourceEnumType.getName(), "sourceEnumValue", sourceEnumValue, "targetEnumType", targetEnumType.getName());

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, params);
        final SearchResult<AdnocBackofficeApprovalRegistrationEnumMappingModel> result = getFlexibleSearchService().search(searchQuery);

        List<AdnocBackofficeApprovalRegistrationEnumMappingModel> enumMappingModels = result.getResult();

        LOG.info("appEvent=AdnocBackofficeApprovalRegistration, Mapping result: {}", enumMappingModels);
        return enumMappingModels;
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
