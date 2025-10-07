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
import {
  IconModule,
  PaginationModule,
  SortingModule,
} from '@spartacus/storefront';
import { AdnocAdminGuard } from '../../../../administration/core/guards';
import { AdnocAccountSummaryDocumentComponent } from './adnoc-account-summary-document.component';
import { AdnocAccountSummaryDocumentFilterModule } from './filter/adnoc-account-summary-document-filter.module';

export const accountSummaryDocumentCmsConfig: CmsConfig = {
  cmsComponents: {
    AccountSummaryDocumentComponent: {
      component: AdnocAccountSummaryDocumentComponent,
      guards: [AuthGuard, AdnocAdminGuard],
    },
  },
};

@NgModule({
  declarations: [AdnocAccountSummaryDocumentComponent],
  imports: [
    AdnocAccountSummaryDocumentFilterModule,
    CommonModule,
    I18nModule,
    SortingModule,
    PaginationModule,
    IconModule,
  ],
  providers: [provideDefaultConfig(accountSummaryDocumentCmsConfig)],
})
export class AdnocAccountSummaryDocumentModule {}
