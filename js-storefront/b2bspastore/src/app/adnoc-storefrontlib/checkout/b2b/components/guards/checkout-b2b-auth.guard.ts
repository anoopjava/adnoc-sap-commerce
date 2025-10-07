/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import {
  CheckoutAuthGuard,
  CheckoutConfigService,
} from '@spartacus/checkout/base/components';
import {
  AuthRedirectService,
  B2BUser,
  B2BUserRole,
  GlobalMessageType,
  SemanticPathService,
} from '@spartacus/core';
import { User, UserAccountFacade } from '@spartacus/user/account/root';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { AdnocAuthService } from '../../../../../core/src/auth/user-auth/facade/adnoc-auth.service';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

@Injectable({
  providedIn: 'root',
})
export class CheckoutB2BAuthGuard extends CheckoutAuthGuard {
  constructor(
    protected override authService: AdnocAuthService,
    protected override authRedirectService: AuthRedirectService,
    protected override checkoutConfigService: CheckoutConfigService,
    protected override activeCartFacade: AdnocActiveCartFacade,
    protected override semanticPathService: SemanticPathService,
    protected override router: Router,
    protected userAccountFacade: UserAccountFacade,
    protected globalMessageService: AdnocGlobalMessageService
  ) {
    super(
      authService,
      authRedirectService,
      checkoutConfigService,
      activeCartFacade,
      semanticPathService,
      router
    );
  }

  override canActivate(): Observable<boolean | UrlTree> {
    return combineLatest([
      this.authService.isUserLoggedIn(),
      this.activeCartFacade.isGuestCart(),
      this.userAccountFacade.get(),
      this.activeCartFacade.isStable(),
    ]).pipe(
      map(([isLoggedIn, isGuestCart, user, isStable]) => ({
        isLoggedIn,
        isGuestCart,
        user,
        isStable,
      })),
      filter((data) => data.isStable),
      // if the user is authenticated and we have their data, OR if the user is anonymous
      filter((data) => (!!data.user && data.isLoggedIn) || !data.isLoggedIn),
      map((data) => {
        if (!data.isLoggedIn) {
          return data.isGuestCart ? true : this.handleAnonymousUser();
        } else if (data.user && 'roles' in data.user) {
          return this.handleUserRole(data.user);
        }
        return data.isLoggedIn;
      })
    );
  }

  protected handleUserRole(user: User): boolean | UrlTree {
    const roles = (<B2BUser>user).roles;
    if (roles?.includes(B2BUserRole.CUSTOMER)) {
      return true;
    }
    this.globalMessageService.add(
      { key: 'checkoutB2B.invalid.accountType' },
      GlobalMessageType.MSG_TYPE_WARNING
    );
    return this.router.parseUrl(this.semanticPathService.get('home') ?? '');
  }
}
