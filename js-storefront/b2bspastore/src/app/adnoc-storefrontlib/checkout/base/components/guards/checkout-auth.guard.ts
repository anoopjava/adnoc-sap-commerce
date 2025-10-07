/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { AuthRedirectService, SemanticPathService } from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CheckoutConfigService } from '../services/checkout-config.service';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { AdnocAuthService } from '../../../../../core/src/auth/user-auth/facade/adnoc-auth.service';

@Injectable({
  providedIn: 'root',
})
export class CheckoutAuthGuard {
  constructor(
    protected authService: AdnocAuthService,
    protected authRedirectService: AuthRedirectService,
    protected checkoutConfigService: CheckoutConfigService,
    protected activeCartFacade: AdnocActiveCartFacade,
    protected semanticPathService: SemanticPathService,
    protected router: Router
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return combineLatest([
      this.authService.isUserLoggedIn(),
      this.activeCartFacade.isGuestCart(),
      this.activeCartFacade.isStable(),
    ]).pipe(
      map(([isLoggedIn, isGuestCart, isStable]) => ({
        isLoggedIn,
        isGuestCart,
        isStable,
      })),
      filter((data) => data.isStable),
      map((data) => {
        if (!data.isLoggedIn) {
          return data.isGuestCart ? true : this.handleAnonymousUser();
        }
        return data.isLoggedIn;
      })
    );
  }

  protected handleAnonymousUser(): boolean | UrlTree {
    this.authRedirectService.saveCurrentNavigationUrl();
    if (this.checkoutConfigService.isGuestCheckout()) {
      return this.router.createUrlTree(
        [this.semanticPathService.get('login')],
        { queryParams: { forced: true } }
      );
    } else {
      return this.router.parseUrl(this.semanticPathService.get('login') ?? '');
    }
  }
}
