/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { provideDefaultConfig } from '@spartacus/core';
import { ReactiveFormsModule } from '@angular/forms';
import { FormErrorsModule } from '@spartacus/storefront';
import {
  CustomerTicketingCloseModule,
  CustomerTicketingMessagesModule,
  CustomerTicketingReopenModule,
  MyAccountV2CustomerTicketingModule,
} from '@spartacus/customer-ticketing/components';
import { AdnocCustomerTicketingFormLayoutConfig } from './shared/customer-ticketing-dialog/adnoc-customer-ticketing-form-layout.config';
import { AdnocCustomerTicketingCreateModule } from './list/customer-ticketing-create/adnoc-customer-ticketing-create.module';
import { AdnocCustomerTicketingListModule } from './list/customer-ticketing-list/adnoc-customer-ticketing-list.module';
import { AdnocCustomerTicketingDetailsModule } from './details/adnoc-customer-ticketing-details/adnoc-customer-ticketing-details.module';

@NgModule({
  imports: [
    ReactiveFormsModule,
    FormErrorsModule,
    AdnocCustomerTicketingDetailsModule,
    CustomerTicketingCloseModule,
    CustomerTicketingReopenModule,
    AdnocCustomerTicketingListModule,
    CustomerTicketingMessagesModule,
    AdnocCustomerTicketingCreateModule,
    MyAccountV2CustomerTicketingModule,
  ],
  providers: [provideDefaultConfig(AdnocCustomerTicketingFormLayoutConfig)],
})
export class AdnocCustomerTicketingComponentsModule {}
