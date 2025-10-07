package com.adnoc.validators.cancelOrder;

import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputWsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class AdnocCancellationRequestEntryInputListDTOValidator implements Validator
{
    private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";

    @Override
    public boolean supports(final Class clazz)
    {
        return CancellationRequestEntryInputListWsDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors)
    {
        final CancellationRequestEntryInputListWsDTO cancellationRequestEntryInputList = (CancellationRequestEntryInputListWsDTO) target;

        final List<CancellationRequestEntryInputWsDTO> cancellationRequestEntryInputs = cancellationRequestEntryInputList
                .getCancellationRequestEntryInputs();
        if (CollectionUtils.isEmpty(cancellationRequestEntryInputs))
        {
            errors.rejectValue("cancellationRequestEntryInputs", FIELD_REQUIRED_MESSAGE_ID);
        }
        else
        {
            IntStream.range(0, cancellationRequestEntryInputs.size())
                    .forEach(i -> {
                        final CancellationRequestEntryInputWsDTO entry = cancellationRequestEntryInputs.get(i);

                        if (Objects.isNull(entry.getOrderEntryNumber()))
                        {
                            errors.rejectValue(String.format("cancellationRequestEntryInputs[%d].orderEntryNumber", i), FIELD_REQUIRED_MESSAGE_ID);
                        }

                        if (Objects.isNull(entry.getQuantity()))
                        {
                            errors.rejectValue(String.format("cancellationRequestEntryInputs[%d].quantity", i), FIELD_REQUIRED_MESSAGE_ID);
                        }

                        if (Objects.isNull(entry.getCancelReason()))
                        {
                            errors.rejectValue(String.format("cancellationRequestEntryInputs[%d].cancelReason", i), FIELD_REQUIRED_MESSAGE_ID);
                        }
                    });
        }
    }
}