package com.adnoc.service.overdueinvoices.impl;

import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceData;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceRequestData;
import com.adnoc.facades.overdue.inoice.data.AdnocOverdueInvoiceResponseData;
import com.adnoc.service.enums.AdnocOverDueStatus;
import com.adnoc.service.enums.PrimaryProduct;
import com.adnoc.service.integration.rest.AdnocRestIntegrationService;
import com.adnoc.service.model.AdnocOverdueInvoiceDetailsModel;
import com.adnoc.service.model.AdnocOverduePaymentTransactionModel;
import com.adnoc.service.overdueinvoices.AdnocOverdueInvoicesService;
import com.adnoc.service.overdueinvoices.dao.AdnocOverdueInvoicesDao;
import de.hybris.platform.enumeration.EnumerationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class AdnocOverdueInvoicesServiceImpl implements AdnocOverdueInvoicesService
{
    private static final Logger LOG = LogManager.getLogger(AdnocOverdueInvoicesServiceImpl.class);

    public static final String ADNOC_PAYMENT_CARD_GATEWAY_DESTINATION = "adnocPaymentCardDestination";
    public static final String ADNOC_PAYMENT_CARD_GATEWAY_DESTINATION_TARGET = "adnoc-payment-card-destination-target";

    public static final String OPEN = "Open";
    public static final String OVERDUE = "Overdue";
    public static final String ADNOC_OVERDUE_INVOICES_DESTINATION = "adnocOverdueInvoicesDestination";
    public static final String ADNOC_OVERDUEINVOICES_DESTINATION_TARGET = "adnoc-overdueinvoices-destination-target";
    public static final String NA = "N/A";

    private AdnocRestIntegrationService adnocRestIntegrationService;
    private AdnocOverdueInvoicesDao adnocOverdueInvoicesDao;
    private EnumerationService enumerationService;

    @Override
    public AdnocOverdueInvoiceResponseData getOverdueInvoicesResponse(final AdnocOverdueInvoiceRequestData adnocOverdueInvoiceRequestData)
    {
        LOG.info("appEvent=AdnocOverdueInvoices, received overdue invoices request for: {}", adnocOverdueInvoiceRequestData.getPayer());
        final AdnocOverdueInvoiceResponseData adnocOverdueInvoiceResponseData = getAdnocRestIntegrationService().restIntegration(ADNOC_OVERDUE_INVOICES_DESTINATION, ADNOC_OVERDUEINVOICES_DESTINATION_TARGET, adnocOverdueInvoiceRequestData, AdnocOverdueInvoiceResponseData.class);
        final List<AdnocOverdueInvoiceData> adnocOverdueInvoiceDataList = adnocOverdueInvoiceResponseData.getInvoice();
        if (CollectionUtils.isNotEmpty(adnocOverdueInvoiceDataList))
        {
            LOG.info("appEvent=AdnocOverdueInvoices, found {} invoices for {}.", CollectionUtils.size(adnocOverdueInvoiceDataList), adnocOverdueInvoiceRequestData.getPayer());
            adnocOverdueInvoiceDataList.forEach(adnocOverdueInvoiceData -> {
                final List<AdnocOverdueInvoiceDetailsModel> adnocOverdueInvoiceDetailsModels = getAdnocOverdueInvoicesDao().fetchPaymentInProgessAdnocOverdueInvoiceDetails();
                final Set<String> adnocInvoiceNumbers = adnocOverdueInvoiceDetailsModels.stream().map(AdnocOverdueInvoiceDetailsModel::getInvoiceNumber).collect(Collectors.toSet());
                final String status = adnocInvoiceNumbers.contains(adnocOverdueInvoiceData.getDocumentNumber()) ? getEnumerationService().getEnumerationName(AdnocOverDueStatus.PAYMENT_IN_PROGRESS)
                        : ((Objects.isNull(adnocOverdueInvoiceData.getNetDueDate()) || adnocOverdueInvoiceData.getNetDueDate().after(new Date())) || adnocOverdueInvoiceData.getDueAmount() <= 0) ? OPEN : OVERDUE;
                adnocOverdueInvoiceData.setStatus(status);

                if (StringUtils.isNotEmpty(adnocOverdueInvoiceData.getDivision()))
                {
                    final PrimaryProduct primaryProduct = getEnumerationService().getEnumerationValue(PrimaryProduct.class, adnocOverdueInvoiceData.getDivision());
                    if (Objects.nonNull(primaryProduct))
                    {
                        adnocOverdueInvoiceData.setDivision(getEnumerationService().getEnumerationName(primaryProduct));
                    }
                }
                else
                {
                    adnocOverdueInvoiceData.setDivision(NA);
                }
            });
            final List<AdnocOverdueInvoiceData> adnocOverdueInvoiceDataSortedList = adnocOverdueInvoiceDataList.stream()
                    .sorted(Comparator.comparing(AdnocOverdueInvoiceData::getStatus, Comparator.reverseOrder())
                            .thenComparing(AdnocOverdueInvoiceData::getNetDueDate)
                            .thenComparing(AdnocOverdueInvoiceData::getFiscalYear))
                    .collect(Collectors.toList());
            adnocOverdueInvoiceResponseData.setInvoice(adnocOverdueInvoiceDataSortedList);
        }
        else
        {
            LOG.info("appEvent=AdnocOverdueInvoices, found no invoices for {}.", adnocOverdueInvoiceRequestData.getPayer());
            adnocOverdueInvoiceResponseData.setInvoice(Collections.emptyList());
        }
        return adnocOverdueInvoiceResponseData;
    }

    @Override
    public AdnocOverduePaymentTransactionModel getAdnocOverduePaymentTransaction(final String resultIndicator)
    {
        return getAdnocOverdueInvoicesDao().findAdnocOverduePaymentTransaction(resultIndicator);
    }


    protected AdnocRestIntegrationService getAdnocRestIntegrationService()
    {
        return adnocRestIntegrationService;
    }

    public void setAdnocRestIntegrationService(final AdnocRestIntegrationService adnocRestIntegrationService)
    {
        this.adnocRestIntegrationService = adnocRestIntegrationService;
    }

    protected AdnocOverdueInvoicesDao getAdnocOverdueInvoicesDao()
    {
        return adnocOverdueInvoicesDao;
    }

    public void setAdnocOverdueInvoicesDao(final AdnocOverdueInvoicesDao adnocOverdueInvoicesDao)
    {
        this.adnocOverdueInvoicesDao = adnocOverdueInvoicesDao;
    }

    protected EnumerationService getEnumerationService()
    {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService)
    {
        this.enumerationService = enumerationService;
    }
}
