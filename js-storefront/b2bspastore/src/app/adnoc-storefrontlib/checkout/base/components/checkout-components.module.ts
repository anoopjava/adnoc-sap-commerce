/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CheckoutDeliveryAddressModule } from './adnoc-checkout-delivery-address/checkout-delivery-address.module';
import { CheckoutDeliveryModeModule } from './adnoc-checkout-delivery-mode/checkout-delivery-mode.module';
import { CheckoutLoginModule } from './adnoc-checkout-login/checkout-login.module';
import { CheckoutOrchestratorModule } from './adnoc-checkout-orchestrator/checkout-orchestrator.module';
import { CheckoutOrderSummaryModule } from './adnoc-checkout-order-summary/checkout-order-summary.module';
import { CheckoutPaymentMethodModule } from './checkout-payment-method/checkout-payment-method.module';
import { AdnocCheckoutPlaceOrderModule } from './adnoc-checkout-place-order/adnoc-checkout-place-order.module';
import { CheckoutProgressMobileBottomModule } from './adnoc-checkout-progress/adnoc-checkout-progress-mobile-bottom/checkout-progress-mobile-bottom.module';
import { AdnocCheckoutProgressMobileTopModule } from './adnoc-checkout-progress/adnoc-checkout-progress-mobile-top/adnoc-checkout-progress-mobile-top.module';
import { CheckoutProgressModule } from './adnoc-checkout-progress/adnoc-checkout-progress.module';
import { AdnocCheckoutReviewSubmitModule } from './adnoc-checkout-review-submit/adnoc-checkout-review-submit.module';
import { CheckoutReviewOverviewModule } from './checkout-review/adnoc-checkout-review-overview/checkout-review-overview.module';
import { CheckoutReviewPaymentModule } from './checkout-review/adnoc-checkout-review-payment/checkout-review-payment.module';
import { CheckoutReviewShippingModule } from './checkout-review/adnoc-checkout-review-shipping/checkout-review-shipping.module';

@NgModule({
  imports: [
    CheckoutOrchestratorModule,
    CheckoutOrderSummaryModule,
    CheckoutProgressModule,
    AdnocCheckoutProgressMobileTopModule,
    CheckoutProgressMobileBottomModule,
    CheckoutDeliveryModeModule,
    CheckoutPaymentMethodModule,
    AdnocCheckoutPlaceOrderModule,
    AdnocCheckoutReviewSubmitModule,
    CheckoutReviewPaymentModule,
    CheckoutReviewShippingModule,
    CheckoutReviewOverviewModule,
    CheckoutDeliveryAddressModule,
    CheckoutLoginModule,
  ],
})
export class CheckoutComponentsModule {}
