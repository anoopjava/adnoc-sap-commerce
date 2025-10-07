package com.adnoc.facades.order.converter.populator;

import com.adnoc.facades.company.data.DeliveryTypeData;
import com.adnoc.facades.company.data.ShippingConditionData;
import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.enums.ShippingCondition;
import com.adnoc.service.model.CreditLimitPaymentInfoModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionData;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdnocOrderPopulator implements Populator<OrderModel, OrderData>
{
    private static final Logger LOG = LogManager.getLogger(AdnocOrderPopulator.class);

    private AdnocB2BUnitService adnocB2BUnitService;
    private EnumerationService enumerationService;
    private Converter<PaymentTransactionModel, PaymentTransactionData> adnocPaymentTransactionConverter;

    @Override
    public void populate(final OrderModel source, final OrderData target)
    {
        LOG.info("appEvent=AdnocOrder, populated source={} to target={}", source, target);
        final String sapCustomerID = source.getDeliveryAddress().getSapCustomerID();
        if (StringUtils.isNotBlank(sapCustomerID))
        {
            final B2BUnitModel shipToB2BUnitModel = getAdnocB2BUnitService().getUnitForUid(sapCustomerID);
            if (Objects.nonNull(shipToB2BUnitModel))
            {
                populateShipToB2BUnitDetails(target, shipToB2BUnitModel);
            }
        }
        if (StringUtils.isNotBlank(source.getSapOrderCode()))
        {
            target.setSapOrderCode(source.getSapOrderCode());
        }
        if (StringUtils.isNotBlank(source.getSapErrorMessage()))
        {
            target.setSapErrorMessage(source.getSapErrorMessage());
        }
        if (CollectionUtils.isNotEmpty(source.getPaymentTransactions()))
        {
            List<PaymentTransactionModel> paymentTransactions = source.getPaymentTransactions();
            if (CollectionUtils.isNotEmpty(paymentTransactions))
            {
                List<PaymentTransactionData> convertedTransactions = paymentTransactions.stream()
                        .map(getAdnocPaymentTransactionConverter()::convert)
                        .collect(Collectors.toList());
                target.setPaymentTransactions(convertedTransactions);
            }
        }
    }

    private void populateShipToB2BUnitDetails(final OrderData target, final B2BUnitModel shipToB2BUnitModel)
    {
        if (CollectionUtils.isNotEmpty(shipToB2BUnitModel.getDeliveryTypes()))
        {
            LOG.debug("appEvent=AdnocOrder, DeliveryType is not empty");
            final DeliveryTypeData deliveryTypeData = new DeliveryTypeData();
            shipToB2BUnitModel.getDeliveryTypes().stream().findFirst()
                    .ifPresent(deliveryType -> {
                        deliveryTypeData.setCode(deliveryType.getCode());
                        deliveryTypeData.setName(getEnumerationService().getEnumerationName(deliveryType));
                    });
            target.setDeliveryType(deliveryTypeData);
        }
        final ShippingCondition shippingCondition = shipToB2BUnitModel.getShippingCondition();
        if (Objects.nonNull(shippingCondition))
        {
            final ShippingConditionData shippingConditionData = new ShippingConditionData();
            shippingConditionData.setCode(shippingCondition.getCode());
            shippingConditionData.setName(getEnumerationService().getEnumerationName(shippingCondition));
            target.setShippingCondition(shippingConditionData);
        }
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }

    protected Converter<PaymentTransactionModel, PaymentTransactionData> getAdnocPaymentTransactionConverter()
    {
        return adnocPaymentTransactionConverter;
    }

    public void setAdnocPaymentTransactionConverter(Converter<PaymentTransactionModel, PaymentTransactionData> adnocPaymentTransactionConverter)
    {
        this.adnocPaymentTransactionConverter = adnocPaymentTransactionConverter;
    }
}
