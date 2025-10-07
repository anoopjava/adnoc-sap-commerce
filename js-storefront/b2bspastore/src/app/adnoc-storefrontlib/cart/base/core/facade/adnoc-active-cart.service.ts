/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { OccEndpointsService, UserIdService, WindowRef } from '@spartacus/core';
import { ActiveCartService } from '@spartacus/cart/base/core';
import { AdnocActiveCartFacade } from '../../root/facade/adnoc-active-cart.facade';
import { HttpClient, HttpResponse } from '@angular/common/http';
import {
  AdnocCartConfig,
  AdnocOrderEntry,
  IAddress,
  ICartModification,
  ICartQuotes,
  IncoTermList,
  IOrderEntries,
  IPayer,
  IpointOfServices,
  payerInfo,
  pointOfServices,
} from '../../../../../core/model/adnoc-cart.model';
import {
  BehaviorSubject,
  Observable,
  Subject,
  take,
  withLatestFrom,
} from 'rxjs';
import { CART_KEY } from '../../../../shared/constants';
import { MultiCartFacade } from '../../root/facade/adnoc-multi-cart.facade';
import { INestedData } from '../../components/cart-shared';
import { AdnocStatementOfAccount } from '../../../../user/profile/components/adnoc-statement-of-account-component/adnoc-statement-of-account.model';
import { OccQuote } from '@spartacus/quote/root';

@Injectable({ providedIn: 'root' })
export class AdnocActiveCartService
  extends ActiveCartService
  implements AdnocActiveCartFacade
{
  cartEntriesStore$ = new Subject<INestedData>();
  cartDateAndAddress$ = new BehaviorSubject<{
    btnDisable: boolean;
    data: AdnocOrderEntry[];
  }>({ btnDisable: true, data: [] });
  creditLimitFlow$ = new Subject<{
    isChecked: boolean;
    creditLimitValue: number;
  }>();
  constructor(
    protected override multiCartFacade: MultiCartFacade,
    protected override userIdService: UserIdService,
    protected override winRef: WindowRef,
    protected OccEndpointsService: OccEndpointsService,
    protected http: HttpClient
  ) {
    super(multiCartFacade, userIdService, winRef);
  }

  /**
   * Update entry
   *
   * @param entryNumber
   * @param quantity
   * @param pickupStore
   * @param pickupToDelivery
   */
  override updateEntry(
    entryNumber: number,
    quantity?: number,
    pickupStore?: string,
    pickupToDelivery: boolean = false
  ): void {
    this.activeCartId$
      .pipe(withLatestFrom(this.userIdService.getUserId()), take(1))
      .subscribe(([cartId, userId]) => {
        this.multiCartFacade.updateEntry(
          userId,
          cartId,
          entryNumber,
          quantity,
          pickupStore,
          pickupToDelivery
        );
      });
  }

  cartConfig(): Observable<AdnocCartConfig> {
    let configKey = CART_KEY.minMaxDeliveryDaysKey;
    let url = this.OccEndpointsService.buildUrl('adnocConfig', {
      urlParams: {
        configKey,
      },
    });
    return this.http.get<AdnocCartConfig>(url);
  }

  getPayerListCall(customerId: string): Observable<IPayer> {
    let url = this.OccEndpointsService.buildUrl('getPayerList', {
      urlParams: {
        customerId,
      },
    });
    return this.http.get<IPayer>(url);
  }

  getAddressForEntries(
    divisionId: string,
    incoTerms: string
  ): Observable<IAddress> {
    let url = this.OccEndpointsService.buildUrl('getAddressForEntries', {
      urlParams: {
        divisionId,
        incoTerms,
      },
    });
    return this.http.get<IAddress>(url);
  }
  //pickupAddress
  getPickupAddress(productCode: string): Observable<IpointOfServices> {
    let url = this.OccEndpointsService.buildUrl('pickupAddress', {
      urlParams: {
        productCode,
      },
    });
    return this.http.get<IpointOfServices>(url);
  }

  getIncotermsForEntries(
    divisionId: string,
    pickup: boolean
  ): Observable<IncoTermList> {
    let url = this.OccEndpointsService.buildUrl('incoTerms', {
      urlParams: {
        divisionId,
        pickup,
      },
    });
    return this.http.get<IncoTermList>(url);
  }

  getPayersList(): Observable<IPayer> {
    let url = this.OccEndpointsService.buildUrl('getPayersList');
    return this.http.get<IPayer>(url);
  }

  createPayer(payerId: string): Observable<payerInfo[]> {
    let url = this.OccEndpointsService.buildUrl('createPayer', {
      urlParams: {
        payerId,
      },
    });
    return this.http.post<payerInfo[]>(url, null);
  }

  statementOfAccount(
    payload: AdnocStatementOfAccount
  ): Observable<HttpResponse<Blob>> {
    let url = this.OccEndpointsService.buildUrl('statementOfAccount');
    return this.http.post(url, payload, {
      responseType: 'blob',
      observe: 'response',
    });
  }

  updateforcheckout(
    payload: IOrderEntries,
    cartId: string
  ): Observable<ICartModification> {
    let url = this.OccEndpointsService.buildUrl('updateforcheckout', {
      urlParams: {
        cartId,
      },
    });
    return this.http.put<ICartModification>(url, payload);
  }
}
