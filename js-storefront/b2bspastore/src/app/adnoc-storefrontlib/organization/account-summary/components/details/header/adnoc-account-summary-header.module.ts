/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdnocAccountSummaryHeaderComponent } from './adnoc-account-summary-header.component';
import { AuthGuard, CmsConfig, I18nModule, provideDefaultConfig } from '@spartacus/core';
import { CardModule } from '@spartacus/storefront';
import { AdnocAdminGuard } from '../../../../administration/core/guards/adnocAdmin.guard';

export const adnocAccountSummaryHeaderCmsConfig: CmsConfig = {
  cmsComponents: {
    AccountSummaryHeaderComponent: {
      component: AdnocAccountSummaryHeaderComponent,
      guards: [AuthGuard, AdnocAdminGuard],
    },
  },
};

@NgModule({
  declarations: [AdnocAccountSummaryHeaderComponent],
  imports: [CardModule, CommonModule, I18nModule],
  providers: [provideDefaultConfig(adnocAccountSummaryHeaderCmsConfig)],
})
export class AdnocAccountSummaryHeaderModule {}
