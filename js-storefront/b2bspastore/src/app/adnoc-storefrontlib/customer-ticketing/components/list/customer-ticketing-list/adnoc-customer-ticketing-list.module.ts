/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdnocCustomerTicketingListComponent } from './adnoc-customer-ticketing-list.component';
import { RouterModule } from '@angular/router';
import {
  I18nModule,
  UrlModule,
  provideDefaultConfig,
  CmsConfig,
  AuthGuard,
} from '@spartacus/core';
import {
  CardModule,
  IconModule,
  ListNavigationModule,
  SpinnerModule,
} from '@spartacus/storefront';
import { AdnocCustomerTicketingCreateModule } from '../customer-ticketing-create/adnoc-customer-ticketing-create.module';

@NgModule({
  imports: [
    AdnocCustomerTicketingCreateModule,
    CommonModule,
    I18nModule,
    UrlModule,
    CardModule,
    IconModule,
    ListNavigationModule,
    RouterModule,
    SpinnerModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        SupportTicketHistoryComponent: {
          component: AdnocCustomerTicketingListComponent,
          guards: [AuthGuard],
        },
      },
    }),
  ],
  declarations: [AdnocCustomerTicketingListComponent],
  exports: [AdnocCustomerTicketingListComponent],
})
export class AdnocCustomerTicketingListModule {}
