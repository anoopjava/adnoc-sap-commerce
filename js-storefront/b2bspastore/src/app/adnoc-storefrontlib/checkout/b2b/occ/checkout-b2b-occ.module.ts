/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { provideDefaultConfig } from '@spartacus/core';
import { OccCheckoutCostCenterAdapter } from './adapters/occ-checkout-cost-center.adapter';
import { OccCheckoutPaymentTypeAdapter } from './adapters/occ-checkout-payment-type.adapter';
import { defaultOccCheckoutB2BConfig } from './config/default-occ-checkout-b2b-config';
import { CheckoutPaymentTypeAdapter } from '../core/connectors/checkout-payment-type/checkout-payment-type.adapter';
import { CheckoutCostCenterAdapter } from '../core/connectors/checkout-cost-center/checkout-cost-center.adapter';
@NgModule({
  imports: [CommonModule],
  providers: [
    provideDefaultConfig(defaultOccCheckoutB2BConfig),
    {
      provide: CheckoutPaymentTypeAdapter,
      useClass: OccCheckoutPaymentTypeAdapter,
    },
    {
      provide: CheckoutCostCenterAdapter,
      useClass: OccCheckoutCostCenterAdapter,
    },
  ],
})
export class CheckoutB2BOccModule {}
