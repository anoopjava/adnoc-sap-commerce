/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { PaymentType } from '@spartacus/cart/base/root';
import { CHECKOUT_CORE_FEATURE } from '@spartacus/checkout/base/root';
import { facadeFactory, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import {
  IadnocConfigs,
  IBankFinalize,
  IBankPaymentParams,
  ICartwsDTO,
  ICreditLimit,
  ICurrentUser,
  IOrderStatusParams,
  IOverdue,
  IPaymentCheckoutParams,
  IRetriveStatus,
  ISessionData,
  IUrlParams,
} from '../../assets/checkout/checkout-model';

@Injectable({
  providedIn: 'root',
  useFactory: () =>
    facadeFactory({
      facade: CheckoutPaymentTypeFacade,
      feature: CHECKOUT_CORE_FEATURE,
      methods: [
        'getPaymentTypes',
        'getPaymentTypesState',
        'setPaymentType',
        'getSelectedPaymentTypeState',
        'isAccountPayment',
        'getPurchaseOrderNumberState',
        'getCreditLimit',
        'getCurrentUser',
        'setPaymentTypeAndCreditLimit',
        'intitatePaymentCheckout',
        'retriveOrderStatus',
        'finalizeBankTransfer',
        'creditSimulationCheck',
        'getBankCardUrl',
      ],
    }),
})
export abstract class CheckoutPaymentTypeFacade {
  /**
   * Get payment types state.
   */
  abstract getPaymentTypesState(): Observable<
    QueryState<PaymentType[] | undefined>
  >;

  /**
   * Get payment types.
   */
  abstract getPaymentTypes(): Observable<PaymentType[]>;

  /**
   * Set payment type to cart
   */
  abstract setPaymentType(
    paymentTypeCode: string,
    purchaseOrderNumber?: string
  ): Observable<unknown>;

  /**
   * Get the selected payment type
   */
  abstract getSelectedPaymentTypeState(): Observable<
    QueryState<PaymentType | undefined>
  >;
  /**
   * Get whether the selected payment type is "ACCOUNT" payment
   */
  abstract isAccountPayment(): Observable<boolean>;

  /**
   * Get purchase order number
   */
  abstract getPurchaseOrderNumberState(): Observable<
    QueryState<string | undefined>
  >;

  abstract getCreditLimit(b2bUnitUid: string): Observable<ICreditLimit>;

  abstract getCurrentUser(): Observable<ICurrentUser>;

  abstract setPaymentTypeAndCreditLimit(
    payload: IUrlParams
  ): Observable<ICartwsDTO>;

  abstract intitatePaymentCheckout(
    payload: IPaymentCheckoutParams
  ): Observable<ISessionData>;

  abstract finalizeBankTransfer(
    payload: IBankPaymentParams
  ): Observable<IBankFinalize>;

  abstract retriveOrderStatus(
    payload: IOrderStatusParams
  ): Observable<IRetriveStatus>;

  abstract creditSimulationCheck(payer: string): Observable<IOverdue>;

  abstract getBankCardUrl(): Observable<IadnocConfigs>;
}
