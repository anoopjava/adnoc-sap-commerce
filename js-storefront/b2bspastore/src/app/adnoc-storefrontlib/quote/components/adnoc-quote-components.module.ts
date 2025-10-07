/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { provideDefaultConfig } from '@spartacus/core';
import { ListNavigationModule } from '@spartacus/storefront';
import { AdnocQuoteItemsModule } from './adnoc-items/adnoc-quote-items.module';
import {
  QuoteLinksModule,
  QuoteCommentsModule,
  QuoteHeaderBuyerEditModule,
  QuoteHeaderOverviewModule,
} from '@spartacus/quote/components';
import { defaultQuoteUIConfig } from './config/default-quote-ui.config';
import { AdnocQuoteSummaryModule } from './summary/adnoc-quote-summary.module';
import { AdnocQuoteSummaryActionsModule } from './summary/adnoc-actions/adnoc-quote-summary-actions.module';
import { AdnocQuoteSummaryPricesModule } from './summary/adnoc-prices/adnoc-quote-summary-prices.module';
import { AdnocQuoteSummarySellerEditModule } from './summary/adnoc-seller-edit/adnoc-quote-summary-seller-edit.module';
import { AdnocQuoteConfirmDialogModule } from './adnoc-confirm-dialog/adnoc-quote-confirm-dialog.module';
import { QuoteListModule } from './list/quote-list.module';

@NgModule({
  imports: [
    CommonModule,
    ListNavigationModule,
    AdnocQuoteConfirmDialogModule,
    QuoteLinksModule,
    QuoteCommentsModule,
    QuoteHeaderBuyerEditModule,
    QuoteHeaderOverviewModule,
    AdnocQuoteItemsModule,
    QuoteListModule,
    AdnocQuoteSummaryModule,
    AdnocQuoteSummaryActionsModule,
    AdnocQuoteSummaryPricesModule,
    AdnocQuoteSummarySellerEditModule,
  ],
  providers: [provideDefaultConfig(defaultQuoteUIConfig)],
})
export class AdnocQuoteComponentsModule {}
