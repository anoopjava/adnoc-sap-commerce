package com.adnoc.validators.helper;

import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.b2bocc.v2.helper.OrgUnitsHelper;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitNodeListWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitNodeWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdnocOrgUnitsHelper extends OrgUnitsHelper
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrgUnitsHelper.class);

    @Resource(name = "b2bUnitFacade")
    protected B2BUnitFacade b2bUnitFacade;

    @Override
    public B2BUnitNodeListWsDTO getAvailableOrgUnitNodes(final String fields)
    {
        final List<B2BUnitNodeData> branchNodes = b2bUnitFacade.getBranchNodes();
        final List<B2BUnitNodeData> parentNodes = branchNodes.stream().filter(node -> StringUtils.isBlank(node.getLob())).collect(Collectors.toList());
        for (final B2BUnitNodeData parentNode : parentNodes)
        {
            final List<B2BUnitNodeData> childNodes = parentNode.getChildren().stream().filter(node -> StringUtils.isNotBlank(node.getLob())).collect(Collectors.toList());
            final List<String> childLobs = childNodes.stream().map(B2BUnitNodeData::getLob).collect(Collectors.toList());
            LOG.info("appEvent=B2BUnit, setting child LOB's:{},for parent:{}", childLobs, parentNode.getId());
            parentNode.setLob(String.join("/ ", childLobs));
        }
        final B2BUnitNodeListWsDTO b2BUnitNodeListWsDTO = new B2BUnitNodeListWsDTO();
        b2BUnitNodeListWsDTO.setUnitNodes(getDataMapper().mapAsList(parentNodes, B2BUnitNodeWsDTO.class, fields));
        return b2BUnitNodeListWsDTO;
    }
}
