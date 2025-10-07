/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AdnocTableDataCellComponent } from './table-data-cell.component';
import { RouterModule } from '@angular/router';
import { UrlModule } from '@spartacus/core';

@NgModule({
  imports: [CommonModule, RouterModule, UrlModule],
  declarations: [AdnocTableDataCellComponent],
})
export class AdnocTableDataCellModule {}
