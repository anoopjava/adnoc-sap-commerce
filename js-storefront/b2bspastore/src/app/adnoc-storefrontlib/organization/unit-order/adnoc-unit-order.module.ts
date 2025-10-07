/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { UnitOrderCoreModule } from '@spartacus/organization/unit-order/core';
import { UnitOrderOccModule } from '@spartacus/organization/unit-order/occ';
import { AdnocUnitOrderComponentsModule } from './components/adnoc-unit-order-components.module';

@NgModule({
  imports: [
    UnitOrderCoreModule.forRoot(),
    UnitOrderOccModule,
    AdnocUnitOrderComponentsModule,
  ],
})
export class AdnocUnitOrderModule {}
