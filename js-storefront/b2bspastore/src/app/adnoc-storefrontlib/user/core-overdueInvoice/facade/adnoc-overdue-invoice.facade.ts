/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { facadeFactory } from '@spartacus/core';
import { Observable } from 'rxjs';
import {
  AdnocOverdueInvoicePayer,
  IAdnocCardConfigInfo,
  IAdnocFinalizeInvoicePaidResponse,
  IAdnocFinalizeInvoiceResultInfo,
  IAdnocInvoicePaymentType,
  IAdnocOverdueInvoice,
  IAdnocPayerPaidInvoiceList,
  IAdnocPayerPaidSessionResponse,
  IAdnocRetrieveInvoicePaidResponse,
  IAdnocRetrieveInvoiceResultInfo,
} from '../../profile/components/adnoc-payer-overdue-invoices/adnoc-payer-overdue-invoices.model';

@Injectable({
  providedIn: 'root',
  useFactory: () =>
    facadeFactory({
      facade: AdnocOverdueInvoiceFacade,
      feature: 'OVERDUE_BASE_CORE_FEATURE',
      methods: [
        'getPayerOverdueInvoiceList',
        'getInvoicePaymentType',
        'getInvoicePaymentSessionId',
        'getRetrieveOverdueInvoicePayment',
        'getFinalizeOverdueInvoicePayment',
        'getCardConfigUrlOverdueInvoicePayment',
      ],
      async: true,
    }),
})
export abstract class AdnocOverdueInvoiceFacade {
  abstract getPayerOverdueInvoiceList(
    payload: AdnocOverdueInvoicePayer
  ): Observable<IAdnocOverdueInvoice>;

  abstract getInvoicePaymentType(): Observable<IAdnocInvoicePaymentType>;
  abstract getInvoicePaymentSessionId(
    payload: IAdnocPayerPaidInvoiceList
  ): Observable<IAdnocPayerPaidSessionResponse>;

  abstract getRetrieveOverdueInvoicePayment(
    payload: IAdnocRetrieveInvoiceResultInfo
  ): Observable<IAdnocRetrieveInvoicePaidResponse>;

  abstract getFinalizeOverdueInvoicePayment(
    payload: IAdnocFinalizeInvoiceResultInfo
  ): Observable<IAdnocFinalizeInvoicePaidResponse>;

  abstract getCardConfigUrlOverdueInvoicePayment(): Observable<IAdnocCardConfigInfo>;
}
