/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import {
  CardModule,
  KeyboardFocusModule,
  PaginationModule,
  TableModule,
} from '@spartacus/storefront';
import { AssignCellComponent } from './assign-cell.component';
import { SubListComponent } from './sub-list.component';
import { MessageModule } from '../message/message.module';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    CardModule,
    TableModule,
    PaginationModule,
    MessageModule,
    KeyboardFocusModule,
  ],
  declarations: [SubListComponent, AssignCellComponent],
  exports: [SubListComponent],
})
export class SubListModule {}
