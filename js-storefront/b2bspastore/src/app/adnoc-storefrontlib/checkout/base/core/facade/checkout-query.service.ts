/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import {
  CheckoutQueryReloadEvent,
  CheckoutQueryResetEvent,
  CheckoutState,
} from '@spartacus/checkout/base/root';
import {
  OCC_USER_ID_ANONYMOUS,
  Query,
  QueryNotifier,
  QueryState,
  UserIdService,
} from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { CheckoutConnector } from '../connectors/checkout/checkout.connector';
import { CheckoutQueryFacade } from '../../root/facade/checkout-query.facade';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { QueryService } from '../../../../../core/src/util/query.service';

@Injectable({
  providedIn: 'root',
})
export class CheckoutQueryService implements CheckoutQueryFacade {
  /**
   * Returns the reload events for the checkout query.
   */
  protected getCheckoutQueryReloadEvents(): QueryNotifier[] {
    return [CheckoutQueryReloadEvent];
  }
  /**
   * Returns the reset events for the checkout query.
   */
  protected getCheckoutQueryResetEvents(): QueryNotifier[] {
    return [CheckoutQueryResetEvent];
  }

  protected checkoutQuery$: Query<CheckoutState | undefined>;

  constructor(
    protected activeCartFacade: AdnocActiveCartFacade,
    protected userIdService: UserIdService,
    protected queryService: QueryService,
    protected checkoutConnector: CheckoutConnector
  ) {
    this.checkoutQuery$ =
    this.queryService.create<CheckoutState | undefined>(
      () =>
        this.checkoutPreconditions().pipe(
          switchMap(([userId, cartId]) =>
            this.checkoutConnector.getCheckoutDetails(userId, cartId)
          )
        ),
      {
        reloadOn: this.getCheckoutQueryReloadEvents(),
        resetOn: this.getCheckoutQueryResetEvents(),
      }
    );
  }

  /**
   * Performs the necessary checkout preconditions.
   */
  protected checkoutPreconditions(): Observable<[string, string]> {
    return combineLatest([
      this.userIdService.takeUserId(),
      this.activeCartFacade.takeActiveCartId(),
      this.activeCartFacade.isGuestCart(),
    ]).pipe(
      take(1),
      map(([userId, cartId, isGuestCart]) => {
        if (
          !userId ||
          !cartId ||
          (userId === OCC_USER_ID_ANONYMOUS && !isGuestCart)
        ) {
          throw new Error('Checkout conditions not met');
        }
        return [userId, cartId];
      })
    );
  }

  getCheckoutDetailsState(): Observable<QueryState<CheckoutState | undefined>> {
    return this.checkoutQuery$.getState();
  }
}
