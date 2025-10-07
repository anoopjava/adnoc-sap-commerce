package com.adnoc.validators.checkout;

import com.adnoc.service.validators.AdnocB2BCartValidator;
import de.hybris.platform.b2bocc.validators.B2BPlaceOrderCartValidator;
import de.hybris.platform.commercefacades.order.data.CartData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.Errors;

public class AdnocB2BPlaceOrderCartValidator extends B2BPlaceOrderCartValidator
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BPlaceOrderCartValidator.class);

    private AdnocB2BCartValidator adnocB2BCartValidator;

    @Override
    public void validate(final Object target, final Errors errors)
    {
        final CartData cart = (CartData) target;
        if (!cart.isCalculated())
        {
            errors.reject("cart.notCalculated");
        }
        LOG.debug("Validation starts for AdnocB2BPlaceOrderCartValidator:");
        getAdnocB2BCartValidator().validate(cart, errors);
    }

    protected AdnocB2BCartValidator getAdnocB2BCartValidator()
    {
        return adnocB2BCartValidator;
    }

    public void setAdnocB2BCartValidator(final AdnocB2BCartValidator adnocB2BCartValidator)
    {
        this.adnocB2BCartValidator = adnocB2BCartValidator;
    }

}

