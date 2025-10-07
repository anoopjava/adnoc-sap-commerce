/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  provideDefaultConfig,
  provideDefaultConfigFactory,
} from '@spartacus/core';
import { AdnocUnitListModule } from './list/adnoc-unit-list.module';
import {
  UnitApproverListModule,
  UnitChildrenModule,
  UnitCostCenterListModule,
} from '@spartacus/organization/administration/components';
import {
  adnocUnitsCmsConfig,
  unitsTableConfigFactory,
} from './adnoc-units.config';
import { AdnocUnitFormModule } from './form/adnoc-unit-form.module';
import { AdnocUnitDetailsModule } from './adnoc-details';
import { UnitAddressModule, UnitUsersModule } from './links';

@NgModule({
  imports: [
    RouterModule,
    AdnocUnitListModule,
    AdnocUnitDetailsModule,
    AdnocUnitFormModule,
    UnitChildrenModule,
    UnitApproverListModule,
    UnitUsersModule,
    UnitCostCenterListModule,
    UnitAddressModule,
  ],
  providers: [
    provideDefaultConfig(adnocUnitsCmsConfig),
    provideDefaultConfigFactory(unitsTableConfigFactory),
  ],
})
export class AdnocUnitsComponentsModule {}
