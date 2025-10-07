package com.adnoc.backoffice.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import de.hybris.platform.omsbackoffice.renderers.PriceRenderer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zul.Listcell;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;

public class AdnocPriceRenderer extends PriceRenderer
{
    private static final Logger LOG = LogManager.getLogger(AdnocPriceRenderer.class);

    private String priceRenderApplicableFor;

    @Override
    public void render(final Listcell listcell, final ListColumn columnConfiguration, final Object object, final DataType dataType, final WidgetInstanceManager widgetInstanceManager)
    {
        final String qualifier = columnConfiguration.getQualifier();

        try
        {

            final DataType objectDataType = getTypeFacade().load(getMyEntry());
            if (objectDataType != null && !getPermissionFacade().canReadProperty(objectDataType.getCode(), qualifier))
            {
                return;
            }

            final Object value = getPropertyValueService().readValue(object, qualifier);
            if (value == null)
            {
                return;
            }

            final BigDecimal amountValue;
            if (value instanceof Double)
            {
                amountValue = BigDecimal.valueOf((Double) value);
            }
            else
            {
                amountValue = (BigDecimal) value;
            }

            final BigDecimal entryAmount = amountValue.setScale(getDigitsNumber(object), 5);


            final String price = getLabelService().getObjectLabel(entryAmount);
            //Applying format
            String finalRefundEntryAmount = price;
            final boolean priceRenderApplicable = getPriceRenderApplicableFor().matches(".*\\b" + Pattern.quote(getMyEntry()) + "\\b.*");
            if (priceRenderApplicable)
            {
                final BigDecimal number = new BigDecimal(price.replace(",", ""));
                final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", new DecimalFormatSymbols()
                {{
                    setDecimalSeparator('.');
                    setGroupingSeparator(',');
                }});
                finalRefundEntryAmount = decimalFormat.format(number);
            }

            if (StringUtils.isBlank(finalRefundEntryAmount))
            {
                finalRefundEntryAmount = value.toString();
            }

            listcell.setLabel(finalRefundEntryAmount);
        }
        catch (final TypeNotFoundException e)
        {
            LOG.info("Could not render row.", e);
        }
    }

    protected String getPriceRenderApplicableFor()
    {
        return priceRenderApplicableFor;
    }

    public void setPriceRenderApplicableFor(final String priceRenderApplicableFor)
    {
        this.priceRenderApplicableFor = priceRenderApplicableFor;
    }
}