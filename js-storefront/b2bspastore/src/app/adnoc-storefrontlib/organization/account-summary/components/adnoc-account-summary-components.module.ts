/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { AdnocAccountSummaryListModule } from './list/adnoc-account-summary-list.module';
import { AdnocAccountSummaryHeaderModule } from './details/header/adnoc-account-summary-header.module';
import { AdnocAccountSummaryDocumentModule } from './details/document';

@NgModule({
  declarations: [],
  imports: [
    AdnocAccountSummaryListModule,
    AdnocAccountSummaryHeaderModule,
    AdnocAccountSummaryDocumentModule,
  ],
})
export class AdnocAccountSummaryComponentsModule {}
