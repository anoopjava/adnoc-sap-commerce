/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component, OnDestroy } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { AuthRedirectService } from '@spartacus/core';
import { CustomFormValidators } from '@spartacus/storefront';
import { Subscription } from 'rxjs';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';

@Component({
    selector: 'cx-checkout-login',
    templateUrl: './checkout-login.component.html',
    standalone: false
})
export class CheckoutLoginComponent implements OnDestroy {
  checkoutLoginForm: UntypedFormGroup;
  sub!: Subscription;

  constructor(
    protected formBuilder: UntypedFormBuilder,
    protected authRedirectService: AuthRedirectService,
    protected activeCartFacade: AdnocActiveCartFacade
  ) {
    this.checkoutLoginForm = this.formBuilder.group(
      {
        email: ['', [Validators.required, CustomFormValidators.emailValidator]],
        emailConfirmation: ['', [Validators.required]],
      },
      {
        validators: CustomFormValidators.emailsMustMatch(
          'email',
          'emailConfirmation'
        ),
      }
    );
  }

  onSubmit() {
    if (this.checkoutLoginForm.valid) {
      const email = this.checkoutLoginForm.get('email')?.value;
      this.activeCartFacade.addEmail(email);

      if (!this.sub) {
        this.sub = this.activeCartFacade.isGuestCart().subscribe((isGuest) => {
          if (isGuest) {
            this.authRedirectService.redirect();
          }
        });
      }
    } else {
      this.checkoutLoginForm.markAllAsTouched();
    }
  }

  ngOnDestroy() {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }
}
