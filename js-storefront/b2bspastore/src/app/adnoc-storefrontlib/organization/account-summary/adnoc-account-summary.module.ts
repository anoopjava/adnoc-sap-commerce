/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { AccountSummaryCoreModule } from '@spartacus/organization/account-summary/core';
import { AdnocAccountSummaryComponentsModule } from './components/adnoc-account-summary-components.module';
import { AdnocAdministrationModule } from '../administration/adnoc-administration.module';
import { NgModule } from '@angular/core';
import { AdnocAccountSummaryOccModule } from './occ/account-summary-occ.module';

@NgModule({
  declarations: [],
  imports: [
    AccountSummaryCoreModule,
    AdnocAccountSummaryOccModule,
    AdnocAccountSummaryComponentsModule,
    AdnocAdministrationModule,
  ],
})
export class AdnocAccountSummaryModule {}
