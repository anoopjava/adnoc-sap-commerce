package com.adnoc.validators.returnOrder;

import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputWsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class AdnocReturnRequestEntryInputListDTOValidator implements Validator
{
    private static final Logger LOG = LogManager.getLogger(AdnocReturnRequestEntryInputListDTOValidator.class);
    private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";

    @Override
    public boolean supports(final Class clazz)
    {
        return ReturnRequestEntryInputListWsDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors)
    {
        LOG.info("Started validation for target: {}", target);
        final ReturnRequestEntryInputListWsDTO returnRequestEntryInputList = (ReturnRequestEntryInputListWsDTO) target;

        if (StringUtils.isEmpty(returnRequestEntryInputList.getOrderCode()))
        {
            LOG.warn("Order code is empty or null.");
            errors.rejectValue("orderCode", FIELD_REQUIRED_MESSAGE_ID);
        }

        final List<ReturnRequestEntryInputWsDTO> returnRequestEntryInputs = returnRequestEntryInputList
                .getReturnRequestEntryInputs();

        if (CollectionUtils.isEmpty(returnRequestEntryInputs))
        {
            errors.rejectValue("returnRequestEntryInputs", FIELD_REQUIRED_MESSAGE_ID);
        }
        else
        {
            LOG.info("Validating returnRequestEntryInputs with size: {}", returnRequestEntryInputs.size());
            IntStream.range(0, returnRequestEntryInputs.size())
                    .filter(i -> Objects.isNull(returnRequestEntryInputs.get(i).getOrderEntryNumber())).forEach(i -> errors
                            .rejectValue(String.format("returnRequestEntryInputs[%d].orderEntryNumber", i), FIELD_REQUIRED_MESSAGE_ID));

            IntStream.range(0, returnRequestEntryInputs.size())
                    .filter(i -> Objects.isNull(returnRequestEntryInputs.get(i).getQuantity())).forEach(
                            i -> errors.rejectValue(String.format("returnRequestEntryInputs[%d].quantity", i), FIELD_REQUIRED_MESSAGE_ID));
        }

        if (Objects.isNull(returnRequestEntryInputList.getReturnReason()))
        {
            errors.rejectValue(("Return Reason cannot be empty for any order"), FIELD_REQUIRED_MESSAGE_ID);
        }
    }
}




