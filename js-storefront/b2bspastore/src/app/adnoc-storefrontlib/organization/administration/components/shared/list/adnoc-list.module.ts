/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FeaturesConfigModule, I18nModule, UrlModule } from '@spartacus/core';
import {
  IconModule,
  KeyboardFocusModule,
  PaginationModule,
  PopoverModule,
  SplitViewModule,
} from '@spartacus/storefront';
import { AdnocListComponent } from './adnoc-list.component';
import { AdnocTableModule } from '../../../../../shared/table/table.module';
import { MessageModule } from '../message/message.module';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    SplitViewModule,
    AdnocTableModule,
    IconModule,
    UrlModule,
    I18nModule,
    PaginationModule,
    NgSelectModule,
    FormsModule,
    MessageModule,
    KeyboardFocusModule,
    PopoverModule,
    FeaturesConfigModule,
  ],
  declarations: [AdnocListComponent],
  exports: [AdnocListComponent],
})
export class AdnocListModule {}
