/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import {
  BudgetComponentsModule,
  CostCenterComponentsModule,
  PermissionComponentsModule,
  UserGroupComponentsModule,
} from '@spartacus/organization/administration/components';
import { AdnocUserComponentsModule } from './user/adnoc-user-components.module';
import { AdnocUnitsComponentsModule } from './unit/adnoc-units-components.module';

@NgModule({
  imports: [
    BudgetComponentsModule,
    CostCenterComponentsModule,
    AdnocUnitsComponentsModule,
    UserGroupComponentsModule,
    AdnocUserComponentsModule,
    PermissionComponentsModule,
  ],
})
export class AdnocAdministrationComponentsModule {}
