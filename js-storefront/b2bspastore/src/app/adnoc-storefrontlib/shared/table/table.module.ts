/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { provideDefaultConfig, UrlModule } from '@spartacus/core';
import { defaultTableConfig } from './config/default-table.config';
import { AdnocTableDataCellModule } from './table-data-cell/table-data-cell.module';
import { AdnocTableHeaderCellModule } from './table-header-cell/table-header-cell.module';
import { AdnocTableComponent } from './table.component';
import { OutletModule } from '@spartacus/storefront';
import { RouterModule } from '@angular/router';

/**
 * The TableModule provides a table component that is driven by (responsible) configuration.
 */
@NgModule({
  imports: [
    CommonModule,
    OutletModule,
    RouterModule,
    UrlModule,
    AdnocTableHeaderCellModule,
    AdnocTableDataCellModule,
  ],
  declarations: [AdnocTableComponent],
  exports: [AdnocTableComponent],
  providers: [provideDefaultConfig(defaultTableConfig)],
})
export class AdnocTableModule {}
