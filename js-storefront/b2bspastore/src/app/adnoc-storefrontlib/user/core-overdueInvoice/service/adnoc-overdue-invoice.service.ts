import { Injectable } from '@angular/core';
import { AdnocOverdueInvoiceFacade } from '../facade/adnoc-overdue-invoice.facade';
import { HttpClient } from '@angular/common/http';
import { OccEndpointsService } from '@spartacus/core';
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
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdnocOverdueInvoiceService implements AdnocOverdueInvoiceFacade {
  constructor(
    protected OccEndpointsService: OccEndpointsService,
    protected http: HttpClient
  ) {}

  getPayerOverdueInvoiceList(
    payload: AdnocOverdueInvoicePayer
  ): Observable<IAdnocOverdueInvoice> {
    let url = this.OccEndpointsService.buildUrl('getPayerOverdueInvoiceList');
    return this.http.post<IAdnocOverdueInvoice>(url, payload);
  }

  getInvoicePaymentType(): Observable<IAdnocInvoicePaymentType> {
    let url = this.OccEndpointsService.buildUrl('getInvoicePaymentType');
    return this.http.get<IAdnocInvoicePaymentType>(url);
  }

  getInvoicePaymentSessionId(
    payload: IAdnocPayerPaidInvoiceList
  ): Observable<IAdnocPayerPaidSessionResponse> {
    let url = this.OccEndpointsService.buildUrl('getInvoicePaymentSessionId');
    return this.http.post<IAdnocPayerPaidSessionResponse>(url, payload);
  }

  getRetrieveOverdueInvoicePayment(
    payload: IAdnocRetrieveInvoiceResultInfo
  ): Observable<IAdnocRetrieveInvoicePaidResponse> {
    let url = this.OccEndpointsService.buildUrl(
      'getRetrieveOverdueInvoicePayment',
      {
        urlParams: {
          resultIndicator: payload.resultIndicator,
        },
      }
    );
    return this.http.get<IAdnocRetrieveInvoicePaidResponse>(url);
  }

  getFinalizeOverdueInvoicePayment(
    payload: IAdnocFinalizeInvoiceResultInfo
  ): Observable<IAdnocFinalizeInvoicePaidResponse> {
    let url = this.OccEndpointsService.buildUrl(
      'getFinalizeOverdueInvoicePayment',
      {
        urlParams: {
          transactionID: payload.transactionID,
        },
      }
    );
    return this.http.post<IAdnocFinalizeInvoicePaidResponse>(url, payload);
  }

  getCardConfigUrlOverdueInvoicePayment(
  ): Observable<IAdnocCardConfigInfo> {
    let url = this.OccEndpointsService.buildUrl('adnocConfig', {
      urlParams: {
        configKey: 'adnocPaymentCardUrl',
      },
    });
    return this.http.get<IAdnocCardConfigInfo>(url);
  }
}
