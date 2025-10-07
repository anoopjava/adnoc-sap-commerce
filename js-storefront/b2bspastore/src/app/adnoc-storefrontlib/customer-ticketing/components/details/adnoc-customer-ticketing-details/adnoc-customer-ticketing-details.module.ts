/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardModule, SpinnerModule } from '@spartacus/storefront';
import {
  AuthGuard,
  CmsConfig,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { AdnocCustomerTicketingDetailsComponent } from './adnoc-customer-ticketing-details.component';

@NgModule({
  imports: [CommonModule, I18nModule, UrlModule, CardModule, SpinnerModule],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        SupportTicketDetailsComponent: {
          component: AdnocCustomerTicketingDetailsComponent,
          guards: [AuthGuard],
        },
      },
    }),
  ],
  declarations: [AdnocCustomerTicketingDetailsComponent],
  exports: [AdnocCustomerTicketingDetailsComponent],
})
export class AdnocCustomerTicketingDetailsModule {}
