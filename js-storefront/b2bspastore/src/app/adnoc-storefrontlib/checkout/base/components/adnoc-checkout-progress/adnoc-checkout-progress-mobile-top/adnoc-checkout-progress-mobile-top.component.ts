/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { CheckoutStep } from '@spartacus/checkout/base/root';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CheckoutStepService } from '../../services/checkout-step.service';
import { useFeatureStyles } from '@spartacus/core';
import { AdnocActiveCartFacade } from '../../../../../cart/base/root/facade/adnoc-active-cart.facade';

@Component({
  selector: 'cx-checkout-progress-mobile-top',
  templateUrl: './adnoc-checkout-progress-mobile-top.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocCheckoutProgressMobileTopComponent {
  private _steps$: BehaviorSubject<CheckoutStep[]>;
  cart$: Observable<Cart>;
  activeStepIndex!: number;
  activeStepIndex$: Observable<number>;

  constructor(
    protected activeCartFacade: AdnocActiveCartFacade,
    protected checkoutStepService: CheckoutStepService
  ) {
    useFeatureStyles('a11yTruncatedTextForResponsiveView');

    this._steps$ = this.checkoutStepService.steps$;
    this.cart$ = this.activeCartFacade.getActive();
    this.activeStepIndex$ = this.checkoutStepService.activeStepIndex$.pipe(
      tap((index) => (this.activeStepIndex = index))
    );
  }

  get steps$(): Observable<CheckoutStep[]> {
    return this._steps$.asObservable();
  }
}
