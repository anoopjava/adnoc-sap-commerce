/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import { CheckoutStep, CheckoutStepState } from '@spartacus/checkout/base/root';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CheckoutStepService } from '../services/checkout-step.service';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';

@Component({
    selector: 'cx-checkout-progress',
    templateUrl: './adnoc-checkout-progress.component.html',
    styleUrl: './adnoc-checkout-progress.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None,
    standalone: false
})
export class CheckoutProgressComponent {
  private _steps$: BehaviorSubject<CheckoutStep[]>;
  cartActiveId: Observable<string>; 
  activeStepIndex$: Observable<number>;
  activeStepIndex!: number;

  constructor(
    protected checkoutStepService: CheckoutStepService,
    protected activeCartFacade: AdnocActiveCartFacade
  ) {
    this._steps$ = this.checkoutStepService.steps$;
    this.cartActiveId = this.activeCartFacade.getActiveCartId();
    this.activeStepIndex$ =
    this.checkoutStepService.activeStepIndex$.pipe(
      tap((index) => (this.activeStepIndex = index))
    );
  }

  get steps$(): Observable<CheckoutStep[]> {
    return this._steps$.asObservable();
  }

  getTabIndex(stepIndex: number): number {
    return !this.isActive(stepIndex) && !this.isDisabled(stepIndex) ? 0 : -1;
  }

  isActive(index: number): boolean {
    return index === this.activeStepIndex;
  }

  isDisabled(index: number): boolean {
    return index > this.activeStepIndex;
  }

  getStepState(index: number): CheckoutStepState {
    if (this.isDisabled(index)) {
      return CheckoutStepState.DISABLED;
    }
    if (this.isActive(index)) {
      return CheckoutStepState.SELECTED;
    }
    return CheckoutStepState.COMPLETED;
  }
}
