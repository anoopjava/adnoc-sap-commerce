package com.adnoc.integration.controllers;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping(value = "/payment")
@Tag(name = "AdnocIntegrationController")
public class AdnocIntegrationController
{
    private static final Logger LOG = LogManager.getLogger(AdnocIntegrationController.class);

    private static final String ADNOC_PAYMENT_BANK_REDIRECT_PATH = "adnoc.%s.review.order.url";
    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @PostMapping(value = "/{paymentFlow}/adnoc-payment-bank-return", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(operationId = "adnoc-payment-bank-return", summary = "Adnoc Payment Bank Return")
    @ApiBaseSiteIdParam
    public void adnocPaymentBankRedirect(@PathVariable("paymentFlow") final String paymentFlow,
                                         @Parameter(description = "Transaction ID, which is generated from Bank Transfer Registration", required = true)
                                         @RequestParam(name = "TransactionID") final String transactionID, final HttpServletResponse httpServletResponse) throws IOException
    {
        LOG.info("appEvent=AdnocBankPayment,getBankRegistrationApiResponse() for type={} with Transaction Id={}", paymentFlow, transactionID);

        final String baseUrlConfig = String.format(ADNOC_PAYMENT_BANK_REDIRECT_PATH, paymentFlow);
        final String baseUrl = configurationService.getConfiguration().getString(baseUrlConfig);
        final String finalUrl = String.format("%s?transactionID=%s", baseUrl, transactionID);
        httpServletResponse.sendRedirect(finalUrl);
    }
}
