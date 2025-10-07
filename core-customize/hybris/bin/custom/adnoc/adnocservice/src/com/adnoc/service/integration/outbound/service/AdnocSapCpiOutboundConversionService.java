package com.adnoc.service.integration.outbound.service;

import com.adnoc.service.model.*;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.ticket.model.CsTicketModel;

/**
 * Interface for converting various ADNOC business models to their corresponding outbound SAP CPI format.
 * This service handles the transformation of B2B registrations, customer data, tickets, quotes,
 * return requests, and payment transactions for integration with SAP CPI system.
 */
public interface AdnocSapCpiOutboundConversionService
{

    /**
     * Convert to sold to outbound model adnoc sold to outbound b 2 b registration model.
     *
     * @param adnocSoldToB2BRegistrationModel the adnoc sold to b 2 b registration model
     * @return the adnoc sold to outbound b 2 b registration model
     */
    AdnocSoldToOutboundB2BRegistrationModel convertToSoldToOutboundModel(AdnocSoldToB2BRegistrationModel adnocSoldToB2BRegistrationModel);

    /**
     * Convert to payer outbound b 2 b unit model adnoc outbound b 2 b unit registration model.
     *
     * @param adnocPayerB2BUnitRegistrationModel the adnoc payer b 2 b unit registration model
     * @return the adnoc outbound b 2 b unit registration model
     */
    AdnocOutboundB2BUnitRegistrationModel convertToPayerOutboundB2BUnitModel(AdnocPayerB2BUnitRegistrationModel adnocPayerB2BUnitRegistrationModel);

    /**
     * Convert to ship to outbound b 2 b unit model adnoc outbound b 2 b unit registration model.
     *
     * @param adnocShipToB2BUnitRegistrationModel the adnoc ship to b 2 b unit registration model
     * @return the adnoc outbound b 2 b unit registration model
     */
    AdnocOutboundB2BUnitRegistrationModel convertToShipToOutboundB2BUnitModel(AdnocShipToB2BUnitRegistrationModel adnocShipToB2BUnitRegistrationModel);

    /**
     * Convert to outbound b 2 b customer adnoc outbound b 2 b customer model.
     *
     * @param b2BCustomerModel the b 2 b customer model
     * @return the adnoc outbound b 2 b customer model
     */
    AdnocOutboundB2BCustomerModel convertToOutboundB2BCustomer(B2BCustomerModel b2BCustomerModel);

    /**
     * Convert to outbound crm cs ticket adnoc outbound crm cs ticket model.
     *
     * @param csTicketModel the cs ticket model
     * @return the adnoc outbound crm cs ticket model
     */
    AdnocOutboundCrmCsTicketModel convertToOutboundCrmCsTicket(CsTicketModel csTicketModel);

    /**
     * Convert to outbound return request adnoc return request outbound model.
     *
     * @param returnRequestModel the return request model
     * @return the adnoc return request outbound model
     */
    AdnocReturnRequestOutboundModel convertToOutboundReturnRequest(ReturnRequestModel returnRequestModel);

    /**
     * Convert to adnoc outbound overdue payment transaction adnoc outbound overdue payment transaction model.
     *
     * @param adnocOverduePaymentTransactionModel the adnoc overdue payment transaction model
     * @return the adnoc outbound overdue payment transaction model
     */
    AdnocOutboundOverduePaymentTransactionModel convertToAdnocOutboundOverduePaymentTransaction(AdnocOverduePaymentTransactionModel adnocOverduePaymentTransactionModel);

    /**
     * Convert to outbound quote adnoc outbound quote model.
     *
     * @param quoteModel the quote model
     * @return the adnoc outbound quote model
     */
    AdnocOutboundQuoteModel convertToOutboundQuote(QuoteModel quoteModel);

}