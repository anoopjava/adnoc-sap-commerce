/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdnocAddToCartComponent } from './adnoc-add-to-cart.component';
import { CmsConfig, I18nModule, provideDefaultConfig } from '@spartacus/core';
import { ReactiveFormsModule } from '@angular/forms';
import {
  IconModule,
  OutletModule,
} from '@spartacus/storefront';
import { ItemCounterModule } from '../../../../shared/components/item-counter/item-counter.module';

@NgModule({
  declarations: [AdnocAddToCartComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    I18nModule,
    IconModule,
    ItemCounterModule,
    OutletModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ProductAddToCartComponent: {
          component: AdnocAddToCartComponent,
          data: {
            inventoryDisplay: false,
          },
        },
      },
    }),
  ],
  exports: [AdnocAddToCartComponent],
})
export class AdnocAddToCartModule {}
