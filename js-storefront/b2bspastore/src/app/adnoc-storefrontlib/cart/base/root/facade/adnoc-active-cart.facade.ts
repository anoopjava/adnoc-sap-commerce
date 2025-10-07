/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import {
  ActiveCartFacade,
  CART_BASE_CORE_FEATURE,
} from '@spartacus/cart/base/root';
import { facadeFactory, User } from '@spartacus/core';
import { Observable } from 'rxjs';
import {
  AdnocCartConfig,
  IAddress,
  ICartModification,
  ICartQuotes,
  IncoTermList,
  IOrderEntries,
  IPayer,
  IpointOfServices,
  pointOfServices,
} from '../../../../../core/model/adnoc-cart.model';
import { OccQuote } from '@spartacus/quote/root';

@Injectable({
  providedIn: 'root',
  useFactory: () =>
    facadeFactory({
      facade: AdnocActiveCartFacade,
      feature: CART_BASE_CORE_FEATURE,
      methods: [
        'getActive',
        'takeActive',
        'getActiveCartId',
        'takeActiveCartId',
        'getEntries',
        'getLastEntry',
        'getLoading',
        'isStable',
        'addEntry',
        'removeEntry',
        'updateEntry',
        'getEntry',
        'addEmail',
        'getAssignedUser',
        'isGuestCart',
        'addEntries',
        'requireLoadedCart',
        'reloadActiveCart',
        'hasPickupItems',
        'hasDeliveryItems',
        'getPickupEntries',
        'getDeliveryEntries',
        'cartConfig',
        'getPayerListCall',
        'getAddressForEntries',
        'getPickupAddress',
        'updateforcheckout',
        'getIncotermsForEntries',
      ],
      async: true,
    }),
})
export abstract class AdnocActiveCartFacade extends ActiveCartFacade {
  abstract override updateEntry(
    entryNumber: number,
    quantity: number,
    pickupStore?: string,
    pickupToDelivery?: boolean
  ): void;

  abstract cartConfig(): Observable<AdnocCartConfig>;
  abstract getPayerListCall(customerId: string): Observable<IPayer>;
  abstract getAddressForEntries(
    divisionId: string,
    incoTerms: string
  ): Observable<IAddress>;
  abstract  getPickupAddress(productCode: string): Observable<IpointOfServices>;
  abstract updateforcheckout(
    payload: IOrderEntries,
    cartId: string
  ): Observable<ICartModification>;

  abstract getIncotermsForEntries(
    divisionId: string,
    pickup: boolean
  ): Observable<IncoTermList>;
}
