/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdnocProductSummaryComponent } from './adnoc-product-summary.component';
import { OutletModule, PromotionsModule } from '@spartacus/storefront';
import {
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
} from '@spartacus/core';

@NgModule({
  declarations: [AdnocProductSummaryComponent],
  imports: [
    CommonModule,
    OutletModule,
    I18nModule,
    PromotionsModule,
    FeaturesConfigModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ProductSummaryComponent: {
          component: AdnocProductSummaryComponent,
        },
      },
    }),
  ],
  exports: [AdnocProductSummaryComponent],
})
export class AdnocProductSummaryModule {}
