package com.adnoc.service.config.dao;

import com.adnoc.service.model.AdnocBackofficeApprovalRegistrationEnumMappingModel;
import com.adnoc.service.model.AdnocConfigModel;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import com.adnoc.service.model.AdnocSapIntegrationCodeMapModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;

import java.util.List;

public interface AdnocConfigDao
{
    /**
     * Find adnoc config string.
     *
     * @param configKeys the config key
     * @return the AdnocConfigModel
     */
    List<AdnocConfigModel> findAdnocConfig(String... configKeys);

    /**
     * Find adnoc sap integration code map adnoc sap integration code map model.
     *
     * @param integrationType the integration type
     * @param objType         the obj type
     * @param enumCode        the enum code
     * @return the adnoc sap integration code map model
     */
    AdnocSapIntegrationCodeMapModel findAdnocSapIntegrationCodeMap(IntegrationType integrationType, Class objType, String enumCode);

    /**
     * Find adnoc cs ticket category map list.
     *
     * @return the list
     */
    List<AdnocCsTicketCategoryMapModel> findAdnocCsTicketCategoryMap();

    /**
     * Find adnoc cs ticket category map adnoc cs ticket category map.
     *
     * @param csTicketCategoryMapId the cs ticket category map id
     * @return the adnoc cs ticket category map
     */
    AdnocCsTicketCategoryMapModel findAdnocCsTicketCategoryMap(String csTicketCategoryMapId);

    SAPSalesOrganizationModel getSalesOrgbyDivision(String division);

    /**
     * Get target enum values based on source enum type and value.
     *
     * @param sourceEnumType  the source enum type
     * @param sourceEnumValue the source enum value
     * @param targetEnumType  the target enum type
     * @return the list of AdnocBackofficeApprovalRegistrationEnumMappingModel
     */
    List<AdnocBackofficeApprovalRegistrationEnumMappingModel> getTargetEnumValues(Class sourceEnumType, String sourceEnumValue, Class targetEnumType);
}
