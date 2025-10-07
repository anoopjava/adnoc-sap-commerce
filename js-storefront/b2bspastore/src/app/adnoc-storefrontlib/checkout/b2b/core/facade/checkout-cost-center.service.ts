/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { CheckoutCostCenterSetEvent } from '@spartacus/checkout/b2b/root';
import {
  Command,
  CommandService,
  CommandStrategy,
  CostCenter,
  EventService,
  OCC_USER_ID_ANONYMOUS,
  QueryState,
  UserIdService,
} from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { map, switchMap, take, tap } from 'rxjs/operators';
import { CheckoutCostCenterConnector } from '../connectors/checkout-cost-center/checkout-cost-center.connector';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { CheckoutCostCenterFacade } from '../../root/facade/checkout-cost-center.facade';
import { CheckoutQueryFacade } from '../../../base/root/facade/checkout-query.facade';

@Injectable()
export class CheckoutCostCenterService implements CheckoutCostCenterFacade {
  protected setCostCenterCommand: Command<string, Cart>;

  constructor(
    protected activeCartFacade: AdnocActiveCartFacade,
    protected userIdService: UserIdService,
    protected commandService: CommandService,
    protected checkoutCostCenterConnector: CheckoutCostCenterConnector,
    protected checkoutQueryFacade: CheckoutQueryFacade,
    protected eventService: EventService
  ) {
    this.setCostCenterCommand = this.commandService.create<string, Cart>(
      (payload) =>
        this.checkoutPreconditions().pipe(
          switchMap(([userId, cartId]) =>
            this.checkoutCostCenterConnector
              .setCostCenter(userId, cartId, payload)
              .pipe(
                tap(() =>
                  this.eventService.dispatch(
                    {
                      cartId,
                      userId,
                      code: payload,
                    },
                    CheckoutCostCenterSetEvent
                  )
                )
              )
          )
        ),
      {
        strategy: CommandStrategy.CancelPrevious,
      }
    );
  }

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

  getCostCenterState(): Observable<QueryState<CostCenter | undefined>> {
    return this.checkoutQueryFacade.getCheckoutDetailsState().pipe(
      map((state) => ({
        ...state,
        data: (state.data as any)?.costCenter,
      }))
    );
  }

  setCostCenter(costCenterId: string): Observable<Cart> {
    return this.setCostCenterCommand.execute(costCenterId);
  }
}
