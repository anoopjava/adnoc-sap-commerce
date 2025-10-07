/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  CmsConfig,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { CartNotEmptyGuard } from '../../guards/cart-not-empty.guard';
import { CheckoutAuthGuard } from '../../guards/checkout-auth.guard';
import { CheckoutStepsSetGuard } from '../../guards/checkout-steps-set.guard';
import { AdnocCheckoutProgressMobileTopComponent } from './adnoc-checkout-progress-mobile-top.component';

@NgModule({
  imports: [CommonModule, UrlModule, I18nModule, RouterModule],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CheckoutProgressMobileTop: {
          component: AdnocCheckoutProgressMobileTopComponent,
          guards: [CheckoutAuthGuard, CartNotEmptyGuard, CheckoutStepsSetGuard],
        },
      },
    }),
  ],
  declarations: [AdnocCheckoutProgressMobileTopComponent],
  exports: [AdnocCheckoutProgressMobileTopComponent],
})
export class AdnocCheckoutProgressMobileTopModule {}
