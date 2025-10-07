/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';
import {
  FormErrorsModule,
  MediaModule,
} from '@spartacus/storefront';
import { AdnocCancelOrReturnItemsComponent } from './adnoc-amend-order-items.component';
import { ItemCounterModule } from '../../../../shared/components/item-counter/item-counter.module';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    I18nModule,
    MediaModule,
    ItemCounterModule,
    FormErrorsModule,
  ],
  declarations: [AdnocCancelOrReturnItemsComponent],
  exports: [AdnocCancelOrReturnItemsComponent],
})
export class AmendOrderItemsModule {}
