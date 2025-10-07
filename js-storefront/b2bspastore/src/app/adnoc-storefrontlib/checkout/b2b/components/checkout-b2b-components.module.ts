/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  CheckoutAuthGuard,
  CheckoutStepsSetGuard,
} from '@spartacus/checkout/base/components';
import { CheckoutCostCenterModule } from './adnoc-checkout-cost-center/checkout-cost-center.module';
import { B2BCheckoutDeliveryAddressModule } from './adnoc-checkout-delivery-address/checkout-delivery-address.module';
import { CheckoutPaymentTypeModule } from './adnoc-checkout-payment-type/checkout-payment-type.module';
import { AdnocB2BCheckoutReviewSubmitModule } from './adnoc-checkout-review-submit/adnoc-checkout-review-submit.module';
import { CheckoutB2BAuthGuard } from './guards/checkout-b2b-auth.guard';
import { CheckoutB2BStepsSetGuard } from './guards/checkout-b2b-steps-set.guard';
import { StoreModule } from '@ngrx/store';
import { creditLimitReducer } from '../b2b-store/reducer/credit-limit.reducer';

@NgModule({
  imports: [
    CommonModule,
    CheckoutCostCenterModule,
    CheckoutPaymentTypeModule,
    B2BCheckoutDeliveryAddressModule,
    AdnocB2BCheckoutReviewSubmitModule,
    StoreModule.forFeature('creditLimit', creditLimitReducer), 
  ],
  providers: [
    {
      provide: CheckoutAuthGuard,
      useExisting: CheckoutB2BAuthGuard,
    },
    {
      provide: CheckoutStepsSetGuard,
      useExisting: CheckoutB2BStepsSetGuard,
    },
  ],
})
export class AdnocCheckoutB2BComponentsModule {}
