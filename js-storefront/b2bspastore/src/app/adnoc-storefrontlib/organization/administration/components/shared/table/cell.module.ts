/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { I18nModule, UrlModule } from '@spartacus/core';
import { IconModule } from '@spartacus/storefront';
import { AmountCellComponent } from './amount/amount-cell.component';
import { DateRangeCellComponent } from './date-range/date-range-cell.component';
import { LimitCellComponent } from './limit/limit-cell.component';
import { RolesCellComponent } from './roles/roles-cell.component';
import { UnitCellComponent } from './unit/unit-cell.component';
import { ParnterIdCellComponent } from './unit/partner-id.component';

@NgModule({
  imports: [CommonModule, RouterModule, UrlModule, I18nModule, IconModule],
  declarations: [
    AmountCellComponent,
    DateRangeCellComponent,
    LimitCellComponent,
    RolesCellComponent,
    UnitCellComponent,
    ParnterIdCellComponent
  ],
})
export class CellModule {}
