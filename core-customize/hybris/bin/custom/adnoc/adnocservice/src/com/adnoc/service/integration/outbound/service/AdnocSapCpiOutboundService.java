package com.adnoc.service.integration.outbound.service;

import com.adnoc.service.model.*;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.Map;

/**
 * Interface for handling outbound communications with SAP CPI (Cloud Platform Integration) for ADNOC services.
 * Extends the base SAP CPI outbound service to provide ADNOC-specific integration capabilities for B2B registration,
 * customer management, ticket handling, return orders, payment transactions, and quote processing.
 */
public interface AdnocSapCpiOutboundService extends SapCpiOutboundService
{
    /**
     * Send adnoc b 2 b registration observable.
     *
     * @param adnocOutboundB2BRegistrationModel the adnoc outbound b 2 b registration model
     * @return the observable
     */
    Observable<ResponseEntity<Map>> sendAdnocB2BRegistration(Object adnocOutboundB2BRegistrationModel);

    /**
     * Send adnoc b 2 b customer observable.
     *
     * @param adnocOutboundB2BCustomerModel the adnoc outbound b 2 b customer model
     * @return the observable
     */
    Observable<ResponseEntity<Map>> sendAdnocB2BCustomer(AdnocOutboundB2BCustomerModel adnocOutboundB2BCustomerModel);

    /**
     * Send crm cs ticket observable.
     *
     * @param adnocOutboundCrmCsTicketModel the adnoc outbound crm cs ticket model
     * @return the observable
     */
    Observable<ResponseEntity<Map>> sendCrmCsTicket(AdnocOutboundCrmCsTicketModel adnocOutboundCrmCsTicketModel);

    /**
     * Send return order observable.
     *
     * @param returnRequestOutboundModel the return request outbound model
     * @return the observable
     */
    Observable<ResponseEntity<Map>> sendReturnOrder(AdnocReturnRequestOutboundModel returnRequestOutboundModel);

    /**
     * Send adnoc outbound overdue payment transaction observable.
     *
     * @param adnocOutboundOverduePaymentTransactionModel the adnoc outbound overdue payment transaction model
     * @return the observable
     */
    Observable<ResponseEntity<Map>> sendAdnocOutboundOverduePaymentTransaction(AdnocOutboundOverduePaymentTransactionModel adnocOutboundOverduePaymentTransactionModel);

    /**
     * Send adnoc quote observable.
     *
     * @param adnocOutboundQuoteModel the adnoc outbound quote model
     * @return the observable
     */
    Observable<ResponseEntity<Map>> sendAdnocQuote(AdnocOutboundQuoteModel adnocOutboundQuoteModel);
}