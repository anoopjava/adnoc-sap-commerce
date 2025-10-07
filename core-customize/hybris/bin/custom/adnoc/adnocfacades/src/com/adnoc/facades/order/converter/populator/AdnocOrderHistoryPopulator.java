package com.adnoc.facades.order.converter.populator;

import com.adnoc.service.category.AdnocCategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.order.converters.populator.OrderHistoryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Objects;

public class AdnocOrderHistoryPopulator extends OrderHistoryPopulator
{
    private AdnocCategoryService adnocCategoryService;
    private Converter<CategoryModel, CategoryData> categoryConverter;

    @Override
    public void populate(final OrderModel orderModel, final OrderHistoryData orderHistoryData)
    {
        super.populate(orderModel, orderHistoryData);

        if (CollectionUtils.isNotEmpty(orderModel.getEntries()))
        {
            final AbstractOrderEntryModel abstractOrderEntryModel = orderModel.getEntries().get(0);
            if (Objects.nonNull(abstractOrderEntryModel.getProduct()))
            {
                String division = abstractOrderEntryModel.getProduct().getDivision();
                CategoryModel categoryModel = getAdnocCategoryService().getCategoryForDivision(division);
                if(Objects.nonNull(categoryModel))
                {
                    orderHistoryData.setCategory(getCategoryConverter().convert(categoryModel));
                }
                AddressData addressData = new AddressData();
                addressData.setSapCustomerID(orderModel.getDeliveryAddress().getSapCustomerID());
                orderHistoryData.setDeliveryAddress(addressData);
            }
        }
        if (Objects.nonNull(orderModel.getTotalPrice()))
        {
            BigDecimal totalPrice = BigDecimal.valueOf(orderModel.getTotalPrice().doubleValue());
            orderHistoryData.setTotal(getPriceDataFactory().create(PriceDataType.BUY, totalPrice, orderModel.getCurrency()));
        }
    }

    protected AdnocCategoryService getAdnocCategoryService()
    {
        return adnocCategoryService;
    }

    public void setAdnocCategoryService(AdnocCategoryService adnocCategoryService)
    {
        this.adnocCategoryService = adnocCategoryService;
    }

    protected Converter<CategoryModel, CategoryData> getCategoryConverter()
    {
        return categoryConverter;
    }

    public void setCategoryConverter(final Converter<CategoryModel, CategoryData> categoryConverter)
    {
        this.categoryConverter = categoryConverter;
    }
}
