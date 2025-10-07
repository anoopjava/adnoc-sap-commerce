package com.adnoc.validators.field;

import com.adnoc.facades.user.data.validation.FieldValidationData;
import com.adnoc.service.config.AdnocConfigService;
import com.adnoc.service.model.AdnocConfigModel;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractAdnocFieldValidator implements Validator
{
    private static final Logger LOG = LogManager.getLogger(AbstractAdnocFieldValidator.class);
    private static final String FIELD_MANDATORY_ERROR_CODE = "adnoc.%s.%s.mandatory.error.message";
    private static final String FIELD_LENGTH_ERROR_CODE = "adnoc.%s.%s.length.error.message";
    private static final String FIELD_PATTERN_ERROR_CODE = "adnoc.%s.%s.pattern.error.message";
    private static final String UPLOAD_FILE_SIZE_LIMIT_IN_MB = "uploadFileSizeLimitInMB";
    public static final String AE = "AE";
    public static final String AE_PHONENMUBER_FORMAT = "^[0-9]{9}";
    public static final String OTHER_PHONENMUBER_FORMAT = "^[0-9]{10}";

    private Map<String, FieldValidationData> fieldValueValidatorMap;
    private AdnocConfigService adnocConfigService;

    public void validate(final Object target, final String validationType, final Errors errors)
    {
        Assert.notNull(errors, "Errors object must not be null");

        LOG.info("appEvent=AdnocFieldValidator, validation started for ValidationType:{}", validationType);

        for (final Map.Entry<String, FieldValidationData> entry : getFieldValueValidatorMap().entrySet())
        {
            final String fieldName = entry.getKey();
            final FieldValidationData fieldValidationData = entry.getValue();

            LOG.debug("appEvent=AdnocFieldValidator, Validating field:{}", fieldName);
            final Object fieldValue = errors.getFieldValue(fieldName);

            if (BooleanUtils.isTrue(fieldValidationData.getMandatory()))
            {
                LOG.debug("appEvent=AdnocFieldValidator,Field: {} is marked as mandatory, performing mandatory check.", fieldName);
                mandatoryCheckValidation(errors, validationType, fieldName, fieldValue, fieldValidationData);
            }
            if (fieldValue instanceof final String fieldValueStr && StringUtils.isNotBlank(fieldValueStr))
            {
                LOG.debug("appEvent=AdnocFieldValidator,Field: {} is a non-blank string, performing length and pattern validation.", fieldName);
                stringLengthPatternValidation(errors, validationType, fieldName, fieldValueStr, fieldValidationData);
            }
        }
    }

    protected void mandatoryCheckValidation(final Errors errors, final String validationType, final String fieldName, final Object fieldValue, final FieldValidationData fieldValidationData)
    {
        if (Objects.isNull(fieldValue) || (fieldValue instanceof final String fieldValueStr && (StringUtils.isBlank(fieldValueStr))))
        {
            LOG.debug("appEvent=validationType={}, fieldName={} is mandatory for registration.", validationType, fieldName);
            errors.rejectValue(fieldName, String.format(FIELD_MANDATORY_ERROR_CODE, validationType, fieldName));
        }
    }

    private void stringLengthPatternValidation(final Errors errors, final String validationType, final String fieldName, final String fieldValue, final FieldValidationData fieldValidationData)
    {
        final int length = fieldValue.length();
        if (Objects.nonNull(fieldValidationData.getMinLength()) && length < fieldValidationData.getMinLength())
        {
            LOG.debug("appEvent=validationType={}, minLength={} is required for field={}.", validationType, fieldValidationData.getMinLength(), fieldName);
            errors.rejectValue(fieldName, String.format(FIELD_LENGTH_ERROR_CODE, validationType, fieldName));
            return;
        }
        if (Objects.nonNull(fieldValidationData.getMaxLength()) && length > fieldValidationData.getMaxLength())
        {
            LOG.debug("appEvent=validationType={}, maxLength={} is required for field={}.", validationType, fieldValidationData.getMaxLength(), fieldName);
            errors.rejectValue(fieldName, String.format(FIELD_LENGTH_ERROR_CODE, validationType, fieldName));
            return;
        }
        if (StringUtils.isNotBlank(fieldValidationData.getPattern()) && !(fieldValue.matches(fieldValidationData.getPattern())))
        {
            LOG.debug("appEvent=validationType={}, pattern={} must match for field={}.", validationType, fieldValidationData.getPattern(), fieldName);
            errors.rejectValue(fieldName, String.format(FIELD_PATTERN_ERROR_CODE, validationType, fieldName));
        }
    }

    protected void fileSizeValidation(final Errors errors, final String validationType, final String fieldName, final MultipartFile file)
    {
        if (Objects.isNull(file))
        {
            return;
        }
        // Validate file extension
        final String fileName = StringUtils.toRootLowerCase(file.getOriginalFilename());
        if (!fileName.endsWith(".pdf"))
        {
            errors.rejectValue(fieldName, String.format(FIELD_PATTERN_ERROR_CODE, validationType, fieldName));
            return;
        }
        // Validate file size
        final long fileSize = file.getSize();
        final AdnocConfigModel adnocConfigModel = getAdnocConfigService().getAdnocConfig(UPLOAD_FILE_SIZE_LIMIT_IN_MB);
        final long uploadFileSizeLimitInMB = (Objects.nonNull(adnocConfigModel) ?
                NumberUtils.toLong(adnocConfigModel.getConfigValue(), 1) : 1) * (1024 * 1024);
        if (fileSize > uploadFileSizeLimitInMB)
        {
            errors.rejectValue(fieldName, String.format(FIELD_LENGTH_ERROR_CODE, validationType, fieldName));
        }
    }

    protected void phoneNumberFormatValidation(final String country, final String phoneNumberFieldName)
    {
        Assert.notNull(country, "Country must not be null");
        Assert.notNull(phoneNumberFieldName, "phoneNumberFieldName must not be null");
        final FieldValidationData telephoneData = getFieldValueValidatorMap().get(phoneNumberFieldName);

        final String telephoneFormat = StringUtils.equals(country, AE) ? AE_PHONENMUBER_FORMAT : OTHER_PHONENMUBER_FORMAT;
        telephoneData.setPattern(telephoneFormat);
    }

    protected void companyPhoneNumberFormatValidation(String companyAddressCountryIso)
    {
        if (StringUtils.equals(companyAddressCountryIso, "AE"))
        {
            String companyPhoneNumberFormat = "^[0-9]{9}";
            FieldValidationData companyPhoneNumberData = getFieldValueValidatorMap().get("companyPhoneNumber");
            companyPhoneNumberData.setPattern(companyPhoneNumberFormat);
        }
        else
        {
            String companyPhoneNumberFormat = "^[0-9]{10}";
            FieldValidationData companyPhoneNumberData = getFieldValueValidatorMap().get("companyPhoneNumber");
            companyPhoneNumberData.setPattern(companyPhoneNumberFormat);
        }
    }

    protected void identificationNumberFormatValidation(String identityType)
    {
        if (StringUtils.equals(identityType, "FS0001"))
        {
            String identificationNumberFormat = "^\\d{3}-\\d{4}-\\d{7}-\\d{1}$";
            FieldValidationData identificationNumberData = getFieldValueValidatorMap().get("identificationNumber");
            identificationNumberData.setPattern(identificationNumberFormat);
        }
        else if (StringUtils.equals(identityType, "FS0002"))
        {
            String identificationNumberFormat = "^[a-zA-Z0-9 ]{1,20}$";
            FieldValidationData identificationNumberData = getFieldValueValidatorMap().get("identificationNumber");
            identificationNumberData.setPattern(identificationNumberFormat);
        }
    }

    protected Map<String, FieldValidationData> getFieldValueValidatorMap()
    {
        return fieldValueValidatorMap;
    }

    public void setFieldValueValidatorMap(final Map<String, FieldValidationData> fieldValueValidatorMap)
    {
        this.fieldValueValidatorMap = fieldValueValidatorMap;
    }

    protected AdnocConfigService getAdnocConfigService()
    {
        return adnocConfigService;
    }

    public void setAdnocConfigService(final AdnocConfigService adnocConfigService)
    {
        this.adnocConfigService = adnocConfigService;
    }
}
