/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CmsConfig, provideDefaultConfig } from '@spartacus/core';
import { OutletModule } from '@spartacus/storefront';
import { CheckoutOrderSummaryComponent } from './checkout-order-summary.component';
import { AdnocPlaceorderButtonComponent } from './adnoc-placeorder-button/adnoc-placeorder-button.component';
import { AdnocPlaceorderButtonModule } from './adnoc-placeorder-button/adnoc-placeorder-button.module';

@NgModule({
  imports: [CommonModule, OutletModule, AdnocPlaceorderButtonModule],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CheckoutOrderSummary: {
          component: CheckoutOrderSummaryComponent,
        },
      },
    }),
  ],
  declarations: [CheckoutOrderSummaryComponent],
  exports: [CheckoutOrderSummaryComponent],
})
export class CheckoutOrderSummaryModule {}
