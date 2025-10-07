/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  CartNotEmptyGuard,
  CheckoutAuthGuard,
} from '@spartacus/checkout/base/components';
import {
  CmsConfig,
  I18nModule,
  UrlModule,
  provideDefaultConfig,
} from '@spartacus/core';
import {
  CardModule,
  IconModule,
  OutletModule,
  PromotionsModule,
} from '@spartacus/storefront';
import { AdnocB2BCheckoutReviewSubmitComponent } from './adnoc-checkout-review-submit.component';
import { AdnocCheckoutPaymentTypeGuard } from '../adnoc-checkout-payment-type/adnoc-checkout-payment-type.guard';

@NgModule({
  imports: [
    CommonModule,
    CardModule,
    I18nModule,
    UrlModule,
    RouterModule,
    PromotionsModule,
    IconModule,
    OutletModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CheckoutReviewOrder: {
          component: AdnocB2BCheckoutReviewSubmitComponent,
          guards: [CheckoutAuthGuard, CartNotEmptyGuard, AdnocCheckoutPaymentTypeGuard],
        },
      },
    }),
  ],
  declarations: [AdnocB2BCheckoutReviewSubmitComponent],
  exports: [AdnocB2BCheckoutReviewSubmitComponent],
})
export class AdnocB2BCheckoutReviewSubmitModule {}
