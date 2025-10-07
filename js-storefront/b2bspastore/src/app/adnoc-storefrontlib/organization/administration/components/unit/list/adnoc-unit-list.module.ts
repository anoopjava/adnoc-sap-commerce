/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { I18nModule, UrlModule } from '@spartacus/core';
import { IconModule } from '@spartacus/storefront';
import { AdnocUnitListComponent } from './adnoc-unit-list.component';
import { AdnocListModule } from '../../shared/list/adnoc-list.module';
import { AdnocToggleLinkCellComponent } from './toggle-link/toggle-link-cell.component';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    RouterModule,
    UrlModule,
    IconModule,
    AdnocListModule,
  ],
  declarations: [AdnocUnitListComponent, AdnocToggleLinkCellComponent],
  exports: [AdnocToggleLinkCellComponent]
})
export class AdnocUnitListModule {}
