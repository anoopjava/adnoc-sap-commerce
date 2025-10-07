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
import { CartProceedToCheckoutComponent } from './adnoc-cart-proceed-to-checkout.component';
import { ProgressButtonModule } from '../../../../shared/progress-button/progress-button.module';

@NgModule({
  imports: [
    CommonModule,
    ProgressButtonModule,
    RouterModule,
    I18nModule,
    UrlModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CartProceedToCheckoutComponent: {
          component: CartProceedToCheckoutComponent,
        },
      },
    }),
  ],
  declarations: [CartProceedToCheckoutComponent],
  exports: [CartProceedToCheckoutComponent],
})
export class CartProceedToCheckoutModule {}
