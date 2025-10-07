/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  AuthGuard,
  CmsConfig,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { ListNavigationModule } from '@spartacus/storefront';
import { AdnocOrderReturnRequestListComponent } from './adnoc-order-return-request-list.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    ListNavigationModule,
    UrlModule,
    I18nModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        OrderReturnRequestListComponent: {
          component: AdnocOrderReturnRequestListComponent,
          guards: [AuthGuard],
        },
      },
    }),
  ],
  declarations: [AdnocOrderReturnRequestListComponent],
  exports: [AdnocOrderReturnRequestListComponent],
})
export class AdnocReturnRequestListModule {}
