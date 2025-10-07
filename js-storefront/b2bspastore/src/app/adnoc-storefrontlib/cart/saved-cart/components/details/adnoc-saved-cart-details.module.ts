/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AddToCartModule } from '@spartacus/cart/base/components/add-to-cart';
import {
  AuthGuard,
  CmsConfig,
  ConfigModule,
  FeaturesConfigModule,
  I18nModule,
  UrlModule,
} from '@spartacus/core';
import {
  CardModule,
  IconModule,
  MediaModule,
  OutletModule,
  SpinnerModule,
} from '@spartacus/storefront';
import { AdnocSavedCartDetailsItemsComponent } from './adnoc-saved-cart-details-items/adnoc-saved-cart-details-items.component';
import { AdnocSavedCartDetailsOverviewComponent } from './adnoc-saved-cart-details-overview/adnoc-saved-cart-details-overview.component';
import { AdnocSavedCartDetailsActionComponent } from './adnoc-saved-cart-details-action/adnoc-saved-cart-details-action.component';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    UrlModule,
    RouterModule,
    CardModule,
    MediaModule,
    IconModule,
    SpinnerModule,
    OutletModule,
    AddToCartModule,
    ConfigModule.withConfig(<CmsConfig>{
      cmsComponents: {
        SavedCartDetailsOverviewComponent: {
          component: AdnocSavedCartDetailsOverviewComponent,
          guards: [AuthGuard],
        },
        SavedCartDetailsItemsComponent: {
          component: AdnocSavedCartDetailsItemsComponent,
          guards: [AuthGuard],
        },
        SavedCartDetailsActionComponent: {
          component: AdnocSavedCartDetailsActionComponent,
          guards: [AuthGuard],
        },
      },
    }),
    FeaturesConfigModule,
  ],
  declarations: [
    AdnocSavedCartDetailsOverviewComponent,
    AdnocSavedCartDetailsActionComponent,
    AdnocSavedCartDetailsItemsComponent,
  ],
  exports: [
    AdnocSavedCartDetailsOverviewComponent,
    AdnocSavedCartDetailsActionComponent,
    AdnocSavedCartDetailsItemsComponent,
  ],
})
export class AdnocSavedCartDetailsModule {}
