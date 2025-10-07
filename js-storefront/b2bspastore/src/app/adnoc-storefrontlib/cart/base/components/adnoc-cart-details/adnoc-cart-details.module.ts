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
import { PromotionsModule } from '@spartacus/storefront';
import { AdnocCartSharedModule } from '../cart-shared/adnoc-cart-shared.module';
import { AdnocCartDetailsComponent } from './adnoc-cart-details.component';
import { CartValidationWarningsModule } from '@spartacus/cart/base/components';
import { AdnocCartCouponModule } from '../cart-coupon/adnoc-cart-coupon.module';

@NgModule({
  imports: [
    AdnocCartSharedModule,
    CommonModule,
    AdnocCartCouponModule,
    RouterModule,
    UrlModule,
    PromotionsModule,
    I18nModule,
    CartValidationWarningsModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CartComponent: {
          component: AdnocCartDetailsComponent,
        },
      },
    }),
  ],
  declarations: [AdnocCartDetailsComponent],
  exports: [AdnocCartDetailsComponent],
})
export class AdnocCartDetailsModule {}
