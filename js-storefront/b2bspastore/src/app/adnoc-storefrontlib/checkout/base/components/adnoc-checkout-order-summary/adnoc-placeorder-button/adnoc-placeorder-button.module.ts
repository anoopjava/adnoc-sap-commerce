/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CartValidationGuard } from '@spartacus/cart/base/core';
import {
  CartNotEmptyGuard,
  CheckoutAuthGuard,
} from '@spartacus/checkout/base/components';
import {
  CmsConfig,
  ConfigModule,
  FeaturesConfigModule,
  I18nModule,
  UrlModule,
} from '@spartacus/core';
import { SpinnerModule } from '@spartacus/storefront';
import { AdnocPlaceorderButtonComponent } from './adnoc-placeorder-button.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    SpinnerModule,
    FeaturesConfigModule,
    UrlModule,
    RouterModule,
  ],
  declarations: [AdnocPlaceorderButtonComponent],
  exports: [AdnocPlaceorderButtonComponent],
})
export class AdnocPlaceorderButtonModule {}
