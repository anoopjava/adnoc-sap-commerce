/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { SavedCartFacade } from '@spartacus/cart/saved-cart/root';
import { RoutingService } from '@spartacus/core';
import { Observable } from 'rxjs';
import {
  distinctUntilChanged,
  filter,
  map,
  shareReplay,
  switchMap,
  tap,
} from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AdnocSavedCartDetailsService {
  protected savedCartId$: Observable<string>;
  protected savedCart$: Observable<Cart | undefined>;

  constructor(
    protected routingService: RoutingService,
    protected savedCartService: SavedCartFacade
  ) {
    this.savedCartId$ = this.routingService.getRouterState().pipe(
      map((routingData) => routingData.state.params['savedCartId']),
      distinctUntilChanged()
    );

    this.savedCart$ = this.savedCartId$.pipe(
      filter((cartId) => Boolean(cartId)),
      tap((savedCartId: string) =>
        this.savedCartService.loadSavedCart(savedCartId)
      ),
      switchMap((savedCartId: string) =>
        this.savedCartService.get(savedCartId)
      ),
      shareReplay({ bufferSize: 1, refCount: true })
    );
  }

  getSavedCartId(): Observable<string> {
    return this.savedCartId$;
  }

  getCartDetails(): Observable<Cart | undefined> {
    return this.savedCart$;
  }
}
