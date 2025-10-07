/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UnitOrderDetailsOrderEntriesContextToken } from '@spartacus/organization/unit-order/root';
import { UnitOrderDetailsOrderEntriesContext } from '@spartacus/organization/unit-order/components';
import { AdnocUnitLevelOrderDetailModule } from './adnoc-unit-level-order-detail/adnoc-unit-level-order-detail.module';
import { AdnocUnitLevelOrderHistoryModule } from './adnoc-unit-level-order-history/adnoc-unit-level-order-history.module';

@NgModule({
  imports: [
    RouterModule,
    AdnocUnitLevelOrderHistoryModule,
    AdnocUnitLevelOrderDetailModule,
  ],
  providers: [
    {
      provide: UnitOrderDetailsOrderEntriesContextToken,
      useExisting: UnitOrderDetailsOrderEntriesContext,
    },
  ],
})
export class AdnocUnitOrderComponentsModule {}
