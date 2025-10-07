/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  AuthGuard,
  CmsConfig,
  I18nModule,
  provideDefaultConfig,
} from '@spartacus/core';
import { AdnocQuoteSummaryComponent } from './adnoc-quote-summary.component';
import { AdnocQuoteSummaryPricesModule } from './adnoc-prices/adnoc-quote-summary-prices.module';
import { AdnocQuoteSummarySellerEditModule } from './adnoc-seller-edit/adnoc-quote-summary-seller-edit.module';
import { AdnocQuoteSummaryActionsModule } from './adnoc-actions/adnoc-quote-summary-actions.module';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    AdnocQuoteSummaryPricesModule,
    AdnocQuoteSummarySellerEditModule,
    AdnocQuoteSummaryActionsModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        QuoteSummaryComponent: {
          component: AdnocQuoteSummaryComponent,
          guards: [AuthGuard],
        },
      },
    }),
  ],
  declarations: [AdnocQuoteSummaryComponent],
  exports: [AdnocQuoteSummaryComponent],
})
export class AdnocQuoteSummaryModule {}
