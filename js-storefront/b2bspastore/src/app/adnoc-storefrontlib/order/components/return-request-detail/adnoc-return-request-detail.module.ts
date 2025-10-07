/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  CmsConfig,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { MediaModule } from '@spartacus/storefront';
import { AdnocReturnRequestItemsComponent } from './adnoc-return-request-items/adnoc-return-request-items.component';
import { AdnocReturnRequestOverviewComponent } from './adnoc-return-request-overview/adnoc-return-request-overview.component';
import { AdnocReturnRequestTotalsComponent } from './adnoc-return-request-totals/adnoc-return-request-totals.component';

const components = [
  AdnocReturnRequestOverviewComponent,
  AdnocReturnRequestItemsComponent,
  AdnocReturnRequestTotalsComponent,
];

@NgModule({
  imports: [CommonModule, RouterModule, UrlModule, I18nModule, MediaModule],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ReturnRequestOverviewComponent: {
          component: AdnocReturnRequestOverviewComponent,
        },
        ReturnRequestItemsComponent: {
          component: AdnocReturnRequestItemsComponent,
        },
        ReturnRequestTotalsComponent: {
          component: AdnocReturnRequestTotalsComponent,
        },
      },
    }),
  ],
  declarations: [...components],
  exports: [...components],
})
export class AdnocReturnRequestDetailModule {}
