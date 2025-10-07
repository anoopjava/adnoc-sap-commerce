package com.adnoc.facades.ticket.populators;

import com.adnoc.facades.ticket.data.AdnocCsTicketCategoryMapData;
import com.adnoc.facades.ticket.data.CsTicketCategoryData;
import com.adnoc.facades.ticket.data.CsTicketRequestForCategoryData;
import com.adnoc.facades.ticket.data.CsTicketRequestForSubCategoryData;
import com.adnoc.service.model.AdnocCsTicketCategoryMapModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocCsTicketCategoryMapDataPopulator implements Populator<AdnocCsTicketCategoryMapModel, AdnocCsTicketCategoryMapData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocCsTicketCategoryMapDataPopulator.class);

    private TypeService typeService;

    @Override
    public void populate(final AdnocCsTicketCategoryMapModel adnocCsTicketCategoryMapModel,
                         final AdnocCsTicketCategoryMapData adnocCsTicketCategoryMapData) throws ConversionException
    {
        adnocCsTicketCategoryMapData.setCsTicketCategoryMapId(adnocCsTicketCategoryMapModel.getCsTicketCategoryMapId());
        LOG.info("Set CsTicketCategoryMapId:{}", adnocCsTicketCategoryMapModel.getCsTicketCategoryMapId());

        final CsTicketCategoryData csTicketCategoryData = new CsTicketCategoryData();
        csTicketCategoryData.setCode(StringUtils.upperCase(adnocCsTicketCategoryMapModel.getRequestType().getCode()));
        csTicketCategoryData.setName(getTypeService().getEnumerationValue(adnocCsTicketCategoryMapModel.getRequestType()).getName());
        adnocCsTicketCategoryMapData.setRequestType(csTicketCategoryData);

        final CsTicketRequestForCategoryData csTicketRequestForCategoryData = new CsTicketRequestForCategoryData();
        csTicketRequestForCategoryData.setCode(adnocCsTicketCategoryMapModel.getRequestFor().getCode());
        csTicketRequestForCategoryData.setName(getTypeService().getEnumerationValue(adnocCsTicketCategoryMapModel.getRequestFor()).getName());
        adnocCsTicketCategoryMapData.setRequestFor(csTicketRequestForCategoryData);

        final CsTicketRequestForSubCategoryData csTicketRequestForSubCategoryData = new CsTicketRequestForSubCategoryData();
        csTicketRequestForSubCategoryData.setCode(adnocCsTicketCategoryMapModel.getSubCategory().getCode());
        csTicketRequestForSubCategoryData.setName(getTypeService().getEnumerationValue(adnocCsTicketCategoryMapModel.getSubCategory()).getName());
        adnocCsTicketCategoryMapData.setSubCategory(csTicketRequestForSubCategoryData);
        LOG.info("Set subCategory: {}", csTicketRequestForSubCategoryData);
    }

    protected TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(final TypeService typeService)
    {
        this.typeService = typeService;
    }
}
