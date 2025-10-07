/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nModule, UrlModule } from '@spartacus/core';
import { AdnocQuoteSummaryActionsComponent } from './adnoc-quote-summary-actions.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [CommonModule, I18nModule,  UrlModule, RouterModule],
  declarations: [AdnocQuoteSummaryActionsComponent],
  exports: [AdnocQuoteSummaryActionsComponent],
})
export class AdnocQuoteSummaryActionsModule {}
