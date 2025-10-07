/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CheckoutStep } from '@spartacus/checkout/base/root';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CheckoutStepService } from '../../services/checkout-step.service';

@Component({
  selector: 'cx-checkout-progress-mobile-bottom',
  templateUrl: './checkout-progress-mobile-bottom.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class CheckoutProgressMobileBottomComponent {
  private _steps$: BehaviorSubject<CheckoutStep[]>;
  activeStepIndex!: number;
  activeStepIndex$: Observable<number>;

  constructor(protected checkoutStepService: CheckoutStepService) {
    this._steps$ = this.checkoutStepService.steps$;
    this.activeStepIndex$ = this.checkoutStepService.activeStepIndex$.pipe(
      tap((index) => (this.activeStepIndex = index))
    );
  }

  get steps$(): Observable<CheckoutStep[]> {
    return this._steps$.asObservable();
  }
}
