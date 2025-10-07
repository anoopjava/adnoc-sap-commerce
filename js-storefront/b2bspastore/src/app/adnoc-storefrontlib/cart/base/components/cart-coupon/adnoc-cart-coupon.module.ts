/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import {
  CmsConfig,
  ConfigModule,
  FeaturesConfigModule,
  I18nModule,
} from '@spartacus/core';
import { FormErrorsModule, IconModule } from '@spartacus/storefront';
import { AdnocAppliedCouponsComponent } from './adnoc-applied-coupons/adnoc-applied-coupons.component';
import { AdnocCartCouponComponent } from './adnoc-cart-coupon.component';

@NgModule({
  declarations: [AdnocCartCouponComponent, AdnocAppliedCouponsComponent],
  exports: [AdnocCartCouponComponent, AdnocAppliedCouponsComponent],
  imports: [
    CommonModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    I18nModule,
    IconModule,
    FormErrorsModule,
    FeaturesConfigModule,
    ConfigModule.withConfig(<CmsConfig>{
      cmsComponents: {
        CartApplyCouponComponent: {
          component: AdnocCartCouponComponent,
        },
      }})
  ],
})
export class AdnocCartCouponModule {}
