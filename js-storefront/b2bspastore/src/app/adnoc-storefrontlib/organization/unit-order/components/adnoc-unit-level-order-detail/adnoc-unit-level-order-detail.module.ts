/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthGuard, CmsConfig, provideDefaultConfig } from '@spartacus/core';
import { UnitLevelOrdersViewerGuard } from '@spartacus/organization/unit-order/core';
import { UnitLevelOrderDetailService } from '@spartacus/organization/unit-order/components';
import {
  AdnocOrderDetailItemsComponent,
  AdnocOrderDetailTotalsComponent,
} from '../../../../order/components/order-details';
import { OrderDetailsService } from '../../../../order/components/order-details/order-details.service';
import { AdnocUnitLevelOrderOverviewComponent } from './adnoc-unit-level-order-overview/adnoc-unit-level-order-overview.component';
import { AdnocUnitLevelOrderOverviewModule } from './adnoc-unit-level-order-overview';

@NgModule({
  imports: [CommonModule, AdnocUnitLevelOrderOverviewModule],
  providers: [
    provideDefaultConfig({
      cmsComponents: {
        UnitLevelOrderDetailsOverviewComponent: {
          component: AdnocUnitLevelOrderOverviewComponent,
          guards: [AuthGuard, UnitLevelOrdersViewerGuard],
        },
        UnitLevelOrderDetailsItemsComponent: {
          component: AdnocOrderDetailItemsComponent,
          guards: [AuthGuard, UnitLevelOrdersViewerGuard],
          providers: [
            {
              provide: OrderDetailsService,
              useExisting: UnitLevelOrderDetailService,
            },
          ],
        },
        UnitLevelOrderDetailsTotalsComponent: {
          component: AdnocOrderDetailTotalsComponent,
          guards: [AuthGuard, UnitLevelOrdersViewerGuard],
          providers: [
            {
              provide: OrderDetailsService,
              useExisting: UnitLevelOrderDetailService,
            },
          ],
        },
      },
    } as CmsConfig),
  ],
})
export class AdnocUnitLevelOrderDetailModule {}
