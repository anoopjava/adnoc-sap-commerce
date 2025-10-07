/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * Velocity context for a quote notification email.
 */
public class QuoteNotificationEmailContext extends AbstractEmailContext<QuoteProcessModel>
{
    private static final Logger LOG = LogManager.getLogger(QuoteNotificationEmailContext.class);
    private static final String ADNOC_CSA_EMPLOYEE_UID = "adnoc.registration.workflow.assigned.employee.uid";

    private transient Converter<QuoteModel, QuoteData> quoteConverter;
    private transient QuoteService quoteService;
    private transient UserService userService;

    private QuoteData quoteData;
    private String backOfficeLink;
    private String csaEmployeeId;
    private String csaEmployeeName;
    private String frontendUrl;

    @Override
    public void init(final QuoteProcessModel quoteProcessModel, final EmailPageModel emailPageModel)
    {
        super.init(quoteProcessModel, emailPageModel);
        quoteData = getQuoteConverter().convert(getQuote(quoteProcessModel));
        UserModel assignedEmployee;
        try
        {
            assignedEmployee = getUserService().getUserForUID(csaEmployeeId);
        }
        catch (final UnknownIdentifierException unknownIdentifierException)
        {
            LOG.error("User with UID '{}' not found. Falling back to default CSA employee UID '{}'. Error: {}",
                    csaEmployeeId, ADNOC_CSA_EMPLOYEE_UID, unknownIdentifierException.getMessage());
            assignedEmployee = getUserService().getUserForUID(ADNOC_CSA_EMPLOYEE_UID);
        }
        csaEmployeeName = assignedEmployee.getName();
    }

    public QuoteData getQuote()
    {
        return quoteData;
    }

    @Override
    protected BaseSiteModel getSite(final QuoteProcessModel quoteProcessModel)
    {
        return getQuote(quoteProcessModel).getSite();
    }

    @Override
    protected CustomerModel getCustomer(final QuoteProcessModel quoteProcessModel)
    {
        return (CustomerModel) getQuote(quoteProcessModel).getUser();
    }

    @Override
    protected LanguageModel getEmailLanguage(final QuoteProcessModel quoteProcessModel)
    {
        return getSite(quoteProcessModel).getDefaultLanguage();
    }

    protected QuoteModel getQuote(final QuoteProcessModel quoteProcessModel)
    {
        return Optional.of(quoteProcessModel)
                .map(QuoteProcessModel::getQuoteCode)
                .map(getQuoteService()::getCurrentQuoteForCode).orElseThrow();
    }

    protected Converter<QuoteModel, QuoteData> getQuoteConverter()
    {
        return quoteConverter;
    }

    public void setQuoteConverter(final Converter<QuoteModel, QuoteData> quoteConverter)
    {
        this.quoteConverter = quoteConverter;
    }

    protected QuoteService getQuoteService()
    {
        return quoteService;
    }

    public void setQuoteService(final QuoteService quoteService)
    {
        this.quoteService = quoteService;
    }

    protected UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    public QuoteData getQuoteData()
    {
        return quoteData;
    }

    public String getBackOfficeLink()
    {
        return backOfficeLink;
    }

    public void setBackOfficeLink(final String backOfficeLink)
    {
        this.backOfficeLink = backOfficeLink;
    }

    public String getCsaEmployeeId()
    {
        return csaEmployeeId;
    }

    public void setCsaEmployeeId(final String csaEmployeeId)
    {
        this.csaEmployeeId = csaEmployeeId;
    }

    public String getCsaEmployeeName()
    {
        return csaEmployeeName;
    }

    public void setCsaEmployeeName(final String csaEmployeeName)
    {
        this.csaEmployeeName = csaEmployeeName;
    }

    public String getFrontendUrl()
    {
        return frontendUrl;
    }

    public void setFrontendUrl(String frontendUrl)
    {
        this.frontendUrl = frontendUrl;
    }
}
