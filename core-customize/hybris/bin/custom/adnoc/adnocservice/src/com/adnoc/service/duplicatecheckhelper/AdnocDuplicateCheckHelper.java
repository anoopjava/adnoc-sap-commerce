package com.adnoc.service.duplicatecheckhelper;

import com.adnoc.facades.gigya.data.AdnocGigyaAccountSearchResponseData;
import com.adnoc.facades.gigya.data.AdnocGigyaCompanyData;
import com.adnoc.facades.gigya.data.AdnocGigyaProfileData;
import com.adnoc.facades.gigya.data.AdnocGigyaResultData;
import com.adnoc.service.exception.AdnocRegistrationException;
import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AdnocDuplicateCheckHelper
{
    private static final Logger LOG = LogManager.getLogger(AdnocDuplicateCheckHelper.class);
    private static final String ADNOC_SQL_QUERY = "adnoc.gigya.account.search.sql.query";
    private static final String ADNOC_SQL_CONDITIONS = "adnoc.gigya.account.search.sql.conditions";
    private static final String ADNOC_SQL_FIELD_MAPPING = "adnoc.gigya.account.search.sql.field.mapping.";
    public static final String ADNOC_DUPLICATE_CHECK_DESTINATION = "adnocGigyaDuplicateCheckEndPointDestination";
    public static final String ADNOC_DUPLICATE_CHECK_DESTINATION_TARGET = "adnoc-gigya-duplicatecheck-destination-target";
    public static final String EMAIL = "email";
    private AdnocRestIntegrationService adnocRestIntegrationService;
    private ConfigurationService configurationService;

    public void validateDuplicateInGigya(final Map<String, String> duplicateCheckParams)
    {
        final String sqlQuery = constructSqlQuery(duplicateCheckParams);
        LOG.info("appEvent=AdnocDuplicateCheck, Duplicate Check SQL Query = {} constructed.", sqlQuery);
        final AdnocGigyaAccountSearchResponseData responseData = getAdnocRestIntegrationService().restIntegration(
                ADNOC_DUPLICATE_CHECK_DESTINATION,
                ADNOC_DUPLICATE_CHECK_DESTINATION_TARGET,
                sqlQuery,
                AdnocGigyaAccountSearchResponseData.class
        );
        validateGigyaAccountSearchResponseData(responseData);
        checkForDuplicityInGigyaAccountSearch(duplicateCheckParams, responseData);
    }

    private String constructSqlQuery(final Map<String, String> duplicateCheckParams)
    {
        final Configuration configuration = getConfigurationService().getConfiguration();
        final String sqlQuery = configuration.getString(ADNOC_SQL_QUERY);
        final String sqlCondition = configuration.getString(ADNOC_SQL_CONDITIONS);
        final Set<String> conditions = duplicateCheckParams.entrySet().stream()
                .filter(entry -> sqlCondition.contains(entry.getKey()))
                .map(entry -> mapToSqlCondition(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
        return String.format(sqlQuery, String.join(" OR ", conditions));
    }

    private String mapToSqlCondition(final String key, String value)
    {
        final String fieldMapping = getConfigurationService().getConfiguration().getString(ADNOC_SQL_FIELD_MAPPING + key, key);

        if (StringUtils.equalsIgnoreCase(EMAIL, key))
        {
            try
            {
                value = URLEncoder.encode(value, "UTF-8");
            }
            catch (final UnsupportedEncodingException e)
            {
                LOG.error("appEvent=AdnocDuplicateCheck, Exception occurred while encoding email value{}", value, e);
            }
        }
        final String condition = StringUtils.isNotBlank(fieldMapping) ? String.format("%s=\"%s\"", fieldMapping, value) : "";
        LOG.info("appEvent=AdnocDuplicateCheck, Map to SQL Condition, key={}, mappedField={}, condition={}", key, fieldMapping, condition);
        return condition;
    }

    private void validateGigyaAccountSearchResponseData(final AdnocGigyaAccountSearchResponseData adnocGigyaAccountSearchResponseData)
    {
        if (adnocGigyaAccountSearchResponseData.getStatusCode() != 200 || adnocGigyaAccountSearchResponseData.getErrorCode() != 0)
        {
            LOG.error("appEvent=AdnocDuplicateCheck, Duplicate Check Failed: statusCode={}, errorCode={}, reason={}",
                    adnocGigyaAccountSearchResponseData.getStatusCode(),
                    adnocGigyaAccountSearchResponseData.getErrorCode(),
                    adnocGigyaAccountSearchResponseData.getStatusReason());
            throw new AdnocRegistrationException("appEvent=AdnocDuplicateCheck, Duplicate check failed with status: " + adnocGigyaAccountSearchResponseData.getStatusCode()
                    + "reason: " + adnocGigyaAccountSearchResponseData.getStatusReason());
        }
    }

    private void checkForDuplicityInGigyaAccountSearch(final Map<String, String> duplicateCheckParams, final AdnocGigyaAccountSearchResponseData adnocGigyaAccountSearchResponseData)
    {
        if (CollectionUtils.isNotEmpty(adnocGigyaAccountSearchResponseData.getResults()))
        {
            final AdnocGigyaResultData result = adnocGigyaAccountSearchResponseData.getResults().get(0);
            final AdnocGigyaCompanyData data = result.getData();
            final AdnocGigyaProfileData profile = result.getProfile();
            if (Objects.nonNull(data))
            {
                checkMatch(duplicateCheckParams.get("identificationNumber"), data.getIdentificationNumber(), "identificationNumber");
                checkMatch(duplicateCheckParams.get("companyEmail"), data.getCompanyEmail(), "companyEmail");
                checkMatch(duplicateCheckParams.get("vatId"), data.getTaxRegistrationNumber(), "taxRegistrationNumber");
                checkMatch(duplicateCheckParams.get("tradeLicenseNumber"), data.getTradeLicenseNumber(), "tradeLicenseNumber");
            }
            if (Objects.nonNull(profile))
            {
                checkMatch(duplicateCheckParams.get(EMAIL), profile.getAssociatedEmail(), EMAIL);
            }
        }
        else
        {
            LOG.info("appEvent=AdnocDuplicateCheck, Checked For DuplicityInGigyaAccountSearch, No results found.");
        }
    }

    private void checkMatch(final String inputValue, final String existingValue, final String fieldName)
    {
        if (StringUtils.isNotBlank(inputValue) && StringUtils.equals(inputValue, existingValue))
        {
            throw new AdnocRegistrationException("appEvent=AdnocDuplicateCheck, Customer already exists with " + fieldName + ": " + existingValue);
        }
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    protected AdnocRestIntegrationService getAdnocRestIntegrationService()
    {
        return adnocRestIntegrationService;
    }

    public void setAdnocRestIntegrationService(final AdnocRestIntegrationService adnocRestIntegrationService)
    {
        this.adnocRestIntegrationService = adnocRestIntegrationService;
    }

}
