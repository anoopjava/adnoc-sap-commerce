package com.adnoc.facades.product.converters.populator;

import com.adnoc.facade.product.data.UnitData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocProductPopulator implements Populator<ProductModel, ProductData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocProductPopulator.class);

    private Converter<UnitModel, UnitData> adnocUnitConverter;

    @Override
    public void populate(final ProductModel productModel, final ProductData productData)
    {
        LOG.info("appEvent=AdnocProduct, populate method called with source:{} and target:{} ", productModel, productData);
        productData.setDivision(productModel.getDivision());
        if (Objects.nonNull(productModel.getUnit()))
        {
            LOG.debug("appEvent=AdnocProduct, unit is not null");
            productData.setUnit(getAdnocUnitConverter().convert(productModel.getUnit()));
        }
        productData.setMinOrderQuantity(productModel.getMinOrderQuantity());
        productData.setMaxOrderQuantity(productModel.getMaxOrderQuantity());
        if(Objects.nonNull(productModel.getProductDocument())){
            productData.setDocumentMediaCode(productModel.getProductDocument().getCode());
        }

    }

    protected Converter<UnitModel, UnitData> getAdnocUnitConverter()
    {
        return adnocUnitConverter;
    }

    public void setAdnocUnitConverter(final Converter<UnitModel, UnitData> adnocUnitConverter)
    {
        this.adnocUnitConverter = adnocUnitConverter;
    }
}
