/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { provideDefaultConfig } from '@spartacus/core';
import { AccountSummaryAdapter,  } from '@spartacus/organization/account-summary/core';
import { defaultOccAccountSummaryConfig } from './config/default-occ-account-summary-config';
import { OccAccountSummaryAdapter } from '@spartacus/organization/account-summary/occ';

@NgModule({
  providers: [
    provideDefaultConfig(defaultOccAccountSummaryConfig),
    { provide: AccountSummaryAdapter, useClass: OccAccountSummaryAdapter },
  ],
})
export class AdnocAccountSummaryOccModule {}
