/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { AdnocCustomerTicketingComponentsModule } from './components/adnoc-customer-ticketing-components.module';
import {
  CustomerTicketingCoreModule,
  CustomerTicketingService,
} from '@spartacus/customer-ticketing/core';
import { AdnocCustomerTicketingOccModule } from './occ/adnoc-customer-ticketing-occ.module';
import { CustomerTicketingFacade } from '@spartacus/customer-ticketing/root';

@NgModule({
  providers: [
    {
      provide: CustomerTicketingFacade,
      useExisting: CustomerTicketingService,
    },
  ],
  imports: [
    AdnocCustomerTicketingComponentsModule,
    CustomerTicketingCoreModule,
    AdnocCustomerTicketingOccModule,
  ],
})
export class AdnocCustomerTicketingModule {}
