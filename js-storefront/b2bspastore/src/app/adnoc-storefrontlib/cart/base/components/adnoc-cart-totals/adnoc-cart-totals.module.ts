/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CmsConfig, provideDefaultConfig } from '@spartacus/core';
import { AdnocCartTotalsComponent } from './adnoc-cart-totals.component';
import { AdnocCartSharedModule } from '../cart-shared';

@NgModule({
  imports: [CommonModule, AdnocCartSharedModule],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CartTotalsComponent: {
          component: AdnocCartTotalsComponent,
        },
      },
    }),
  ],
  declarations: [AdnocCartTotalsComponent],
  exports: [AdnocCartTotalsComponent],
})
export class AdnocCartTotalsModule {}
