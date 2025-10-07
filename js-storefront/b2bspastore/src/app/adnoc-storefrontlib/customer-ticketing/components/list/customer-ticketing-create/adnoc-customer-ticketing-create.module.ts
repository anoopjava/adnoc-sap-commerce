/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdnocCustomerTicketingCreateComponent } from './adnoc-customer-ticketing-create.component';
import { AdnocCustomerTicketingCreateDialogComponent } from './customer-ticketing-create-dialog/adnoc-customer-ticketing-create-dialog.component';
import { FeaturesConfigModule, I18nModule } from '@spartacus/core';
import {
  FileUploadModule,
  FormErrorsModule,
  IconModule,
  KeyboardFocusModule,
} from '@spartacus/storefront';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    IconModule,
    KeyboardFocusModule,
    ReactiveFormsModule,
    FormErrorsModule,
    FileUploadModule,
    FeaturesConfigModule,
  ],
  declarations: [
    AdnocCustomerTicketingCreateComponent,
    AdnocCustomerTicketingCreateDialogComponent,
  ],
  exports: [
    AdnocCustomerTicketingCreateComponent,
    AdnocCustomerTicketingCreateDialogComponent,
  ],
})
export class AdnocCustomerTicketingCreateModule {}
