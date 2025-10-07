/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CartValidationGuard } from '@spartacus/cart/base/core';
import {
  CartNotEmptyGuard,
  CheckoutAuthGuard,
} from '@spartacus/checkout/base/components';
import { CmsConfig, ConfigModule, I18nModule, UrlModule } from '@spartacus/core';
import { AdnocCheckoutCostCenterComponent } from './checkout-cost-center.component';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    UrlModule,
    ConfigModule.withConfig(<CmsConfig>{
      cmsComponents: {
        CheckoutCostCenterComponent: {
          component: AdnocCheckoutCostCenterComponent,
          guards: [CheckoutAuthGuard, CartNotEmptyGuard, CartValidationGuard],
        },
      },
    }),
  ],
  declarations: [AdnocCheckoutCostCenterComponent],
})
export class CheckoutCostCenterModule {}
