/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { I18nModule } from '@spartacus/core';
import { SubListModule } from '../../../shared/sub-list/sub-list.module';
import { UnitChildrenComponent } from './unit-children.component';
import { AdnocListModule } from '../../../shared/list/adnoc-list.module';
import { DisableInfoModule } from '../../../shared/detail/disable-info/disable-info.module';

@NgModule({
  imports: [
    AdnocListModule,
    I18nModule,
    RouterModule,
    SubListModule,
    CommonModule,
    DisableInfoModule,
  ],
  declarations: [UnitChildrenComponent],
})
export class UnitChildrenModule {}
