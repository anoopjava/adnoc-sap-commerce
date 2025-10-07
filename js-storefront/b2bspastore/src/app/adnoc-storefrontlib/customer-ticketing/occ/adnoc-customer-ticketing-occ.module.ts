/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideDefaultConfig } from '@spartacus/core';
import { CustomerTicketingAdapter } from '@spartacus/customer-ticketing/core';
import { OccCustomerTicketingAdapter } from '@spartacus/customer-ticketing/occ';
import { defaultOccCustomerTicketingConfig } from './config/default-occ-customer-ticketing-config';

@NgModule({
  imports: [CommonModule],
  providers: [
    provideDefaultConfig(defaultOccCustomerTicketingConfig),
    {
      provide: CustomerTicketingAdapter,
      useClass: OccCustomerTicketingAdapter,
    },
  ],
})
export class AdnocCustomerTicketingOccModule {}
