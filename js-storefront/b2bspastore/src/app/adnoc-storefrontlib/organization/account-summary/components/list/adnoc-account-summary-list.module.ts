/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { AdnocAccountSummaryListComponent } from './adnoc-account-summary-list.component';
import {
  I18nModule,
  provideDefaultConfig,
  provideDefaultConfigFactory,
} from '@spartacus/core';
import { AdnocListModule } from '../../../administration/components/shared';
import {
  accountSummaryListCmsConfig,
  accountSummaryUnitsTableConfigFactory,
} from './adnoc-account-summary-list-config';

@NgModule({
  declarations: [AdnocAccountSummaryListComponent],
  providers: [
    provideDefaultConfig(accountSummaryListCmsConfig),
    provideDefaultConfigFactory(accountSummaryUnitsTableConfigFactory),
  ],
  imports: [I18nModule, AdnocListModule],
  exports: [AdnocAccountSummaryListComponent],
})
export class AdnocAccountSummaryListModule {}
