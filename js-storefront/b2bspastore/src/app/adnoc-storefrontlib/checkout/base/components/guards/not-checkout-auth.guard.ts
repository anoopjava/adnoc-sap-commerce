/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { getLastValueSync, SemanticPathService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { AdnocAuthService } from '../../../../../core/src/auth/user-auth/facade/adnoc-auth.service';

@Injectable({
  providedIn: 'root',
})
export class NotCheckoutAuthGuard {
  constructor(
    protected authService: AdnocAuthService,
    protected activeCartFacade: AdnocActiveCartFacade,
    protected semanticPathService: SemanticPathService,
    protected router: Router
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.authService.isUserLoggedIn().pipe(
      map((isLoggedIn) => {
        if (isLoggedIn) {
          return this.router.parseUrl(
            this.semanticPathService.get('home') ?? ''
          );
        } else if (!!getLastValueSync(this.activeCartFacade.isGuestCart())) {
          return this.router.parseUrl(
            this.semanticPathService.get('cart') ?? ''
          );
        }
        return !isLoggedIn;
      })
    );
  }
}
